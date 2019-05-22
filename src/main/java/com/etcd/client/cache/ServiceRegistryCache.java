package com.etcd.client.cache;

import java.lang.ref.SoftReference;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.etcd.client.service.EntityData;
import com.etcd.client.service.EntityKey;
import com.etcd.client.service.EtcdDataAccessorService;
import com.etcd.client.service.impl.QueryOption;
import com.google.common.base.Charsets;

import io.etcd.jetcd.watch.WatchEvent.EventType;

@Component("serviceRegistryCache")
@PropertySource("classpath:etcd-server.properties")
public class ServiceRegistryCache implements Cache<String, Set<String>>
{
    private static final Logger LOGGER = LoggerFactory
        .getLogger(ServiceRegistryCache.class);
    /**
     * Key - URL of the service <br/>
     * Value - List of Running instances
     */
    private Map<String, SoftReference<Set<String>>> cache = new ConcurrentHashMap<>();

    @Autowired
    private EtcdDataAccessorService etcdDataAccessorService;

    @Value("${service-base-url}")
    private String serviceName;

    @PostConstruct
    public void init()
    {
	initCache();
	QueryOption option = QueryOption.newBuilder().withPrefix(serviceName)
	    .build();
	EntityKey entityKey = EntityKey.newBuilder().withKeyName(serviceName)
	    .withQueryOption(option).build();
	watchKeyChangesAndUpdateCache(entityKey);

	/*
	 * try { Thread.sleep(880000); } catch (InterruptedException e) {
	 * e.printStackTrace(); }
	 */

    }

    @Override
    public Set<String> get(String keyName)
    {
	SoftReference<Set<String>> value = cache.get(keyName);
	if (value == null || value.get() == null)
	{
	    refreshCache(keyName);
	    value = cache.get(keyName);
	}

	return (value == null) ? null : value.get();
    }

    /**
     * 
     * @param keyName Ex. flightReservationSystem/services/flights/
     */
    private synchronized void refreshCache(String keyName)
    {
	EntityKey entityKey = getEntityKey(keyName);

	if (cache.get(keyName) == null || cache.get(keyName).get() == null)
	{
	    List<EntityData> result = etcdDataAccessorService.read(entityKey);
	    if (!CollectionUtils.isEmpty(result))
	    {
		Set<String> values = new LinkedHashSet<>();

		result.forEach((entityData) ->
		{
		    values.add(entityData.getEntityValue().getValue());
		});
		cache.put(keyName, new SoftReference<Set<String>>(values));
	    }
	}

    }

    private EntityKey getEntityKey(String keyName)
    {
	QueryOption option = QueryOption.newBuilder().withPrefix(keyName)
	    .build();
	EntityKey entityKey = EntityKey.newBuilder().withKeyName(keyName)
	    .withQueryOption(option).build();
	return entityKey;
    }

    public void initCache()
    {
	LOGGER.info("Cache state before initialization :: " + cache.toString());
	cache.clear();

	EntityKey entityKey = getEntityKey(serviceName);

	List<EntityData> result = etcdDataAccessorService.read(entityKey);
	LOGGER.info("initCache Result from etcd read :: " + result.toString());
	if (!CollectionUtils.isEmpty(result))
	{
	    result.forEach((entityData) ->
	    {
		String newKeyName = createNewKeyName(
		    entityData.getEntityKey().getKeyName());
		if (StringUtils.isEmpty(newKeyName))
		{
		    throw new IllegalArgumentException(
		        "Invalid Key for service registry cache");
		}

		SoftReference<Set<String>> value = cache.get(newKeyName);
		Set<String> values = null;
		if (value == null)
		{
		    values = new LinkedHashSet<>();
		} else
		{
		    values = value.get();
		}
		values.add(entityData.getEntityValue().getValue());
		cache.put(newKeyName, new SoftReference<Set<String>>(values));
	    });
	}
	LOGGER.info("Cache state after initialization :: " + cache.toString());
    }

    /// flightReservationSystem/services/flights/0
    private String createNewKeyName(String Key)
    {
	StringBuilder newKeyName = null;

	String arr[] = Key.split("\\/");
	if (arr != null && arr.length > 0)
	{
	    newKeyName = new StringBuilder();
	    for (int i = 0; i < arr.length - 1; i++)
	    {
		newKeyName.append(arr[i]).append("/");
	    }
	    return newKeyName.toString(); // flightReservationSystem/services/flights/
	}

	return null;

    }

    private void watchKeyChangesAndUpdateCache(EntityKey entityKey)
    {
	etcdDataAccessorService.watch(entityKey, (watcher) ->
	{
	    watcher.getEvents().forEach((watchEvent) ->
	    {
		String eventKey = createNewKeyName(
		    watchEvent.getKeyValue().getKey().toString(Charsets.UTF_8));
		String eventValue = watchEvent.getKeyValue().getValue()
		    .toString(Charsets.UTF_8);

		String prevEventValue = watchEvent.getPrevKV().getValue()
		    .toString(Charsets.UTF_8);

		SoftReference<Set<String>> cachedValuesSoftRef = cache
		    .get(eventKey);

		// eventKey not exists in local cache, so it's new register
		if (cachedValuesSoftRef == null)
		{
		    // New key register
		    Set<String> values = new LinkedHashSet<>();
		    values.add(eventValue);
		    cache.put(eventKey, new SoftReference<Set<String>>(values));
		    cache.entrySet().forEach((entrySet) ->
		    {
			LOGGER
			    .info("Cached value after new key addition :: Key->"
			        + entrySet.getKey() + " Value:"
			        + entrySet.getValue().get().toString());
		    });
		    return;
		}

		// Cases when existing key getting updated/deleted.
		Set<String> cachedValues = cachedValuesSoftRef.get();
		EventType eventType = watchEvent.getEventType();

		switch (eventType)
		{
		    case PUT:
			// Update Case
			if (cachedValues.contains(prevEventValue))
			{
			    cachedValues.remove(prevEventValue);
			    cachedValues.add(eventValue);
			} else
			{
			    cachedValues.add(eventValue);
			}
			break;
		    case DELETE:
			// Delete case
			if (cachedValues.contains(prevEventValue))
			{
			    cachedValues.remove(prevEventValue);
			}
			break;

		    default:
			break;
		}
		cache.entrySet().forEach((entrySet) ->
		{
		    LOGGER.info("Cached value after Modification :: Key->"
		        + entrySet.getKey() + " Value:"
		        + entrySet.getValue().get().toString());
		});
	    });
	});
	cache.entrySet().forEach((entrySet) ->
	{
	    LOGGER.info(
	        "Cache value when starting watcher :: Key->" + entrySet.getKey()
	            + " Value:" + entrySet.getValue().get().toString());
	});

    }
}
