package com.etcd.client.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.etcd.client.datasource.EtcdConnection;
import com.etcd.client.service.EntityData;
import com.etcd.client.service.EntityKey;
import com.etcd.client.service.EntityValue;
import com.etcd.client.service.EtcdDataAccessorService;
import com.etcd.client.service.EtcdWatcher;
import com.google.common.base.Charsets;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.lease.LeaseGrantResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.options.WatchOption;

/**
 * Handle operation around ETCD (read, write).
 * 
 * @author amaresh
 */
@Repository
public class EtcdDataAccessorServiceImpl implements EtcdDataAccessorService
{
    private static final Logger LOGGER = LoggerFactory
        .getLogger(EtcdDataAccessorServiceImpl.class);

    @Autowired
    private EtcdConnection etcdConnection;

    @PostConstruct
    public void connect()
    {
	etcdConnection.connect();
	LOGGER.info("Connection etablished to ETCD Server");
    }

    public void register(EntityData entityData)
    {
	KV kvClient = etcdConnection.getEtcdClient().getKVClient();
	Lease lease = null;
	ByteSequence lKey = entityData.getEntityKey().toByteSequence();
	ByteSequence lValue = entityData.getEntityValue().toByteSequence();
	try
	{
	    PutOption.Builder builder = PutOption.newBuilder();

	    LeaseGrantResponse leaseGrantResponse = null;
	    if (entityData.getEntityProperty() != null
	        && entityData.getEntityProperty().getTTL() > 0)
	    {
		// Create lease with TTL value, and attach the lease to put
		// option.
		lease = etcdConnection.getEtcdClient().getLeaseClient();
		leaseGrantResponse = lease
		    .grant(entityData.getEntityProperty().getTTL()).get();
		builder.withLeaseId(leaseGrantResponse.getID());
	    }

	    // put the key-value
	    PutOption option = builder.withPrevKV().build();
	    kvClient.put(lKey, lValue, option).get();

	    if (entityData.getEntityProperty() != null
	        && entityData.getEntityProperty()
	            .getLeaseKeepAliveObserver() != null
	        && leaseGrantResponse != null)
	    {
		lease.keepAlive(leaseGrantResponse.getID(),
		    entityData.getEntityProperty().getLeaseKeepAliveObserver());
	    }

	} catch (InterruptedException | ExecutionException e)
	{
	    throw new IllegalStateException(
	        "Exception occurred during saving key-value", e);
	}
    }

    @Override
    public List<EntityData> read(EntityKey entityKey)
    {
	List<EntityData> result = new ArrayList<>();
	KV kvClient = etcdConnection.getEtcdClient().getKVClient();

	CompletableFuture<GetResponse> getFuture = null;
	if (entityKey.getQueryOption() != null)
	{
	    GetOption option = GetOption.newBuilder()
	        .withSortOrder(entityKey.getQueryOption().getSortOrder())
	        .withPrefix(ByteSequence
	            .from(entityKey.getQueryOption().getPrefix().getBytes()))
	        .build();

	    // get the CompletableFuture with query option
	    getFuture = kvClient.get(entityKey.toByteSequence(), option);
	} else
	{
	    getFuture = kvClient.get(entityKey.toByteSequence());
	}

	// get the value from CompletableFuture
	GetResponse gResponse = null;
	try
	{
	    gResponse = getFuture.get();
	    if (CollectionUtils.isEmpty(gResponse.getKvs()))
	    {
		LOGGER.info("Get response is empty!!");
		return result;
	    }
	    gResponse.getKvs().forEach((keyValue) ->
	    {
		LOGGER.info("GetResponse Key: "
		    + keyValue.getKey().toString(Charsets.UTF_8) + " Value: "
		    + keyValue.getValue().toString(Charsets.UTF_8));

		EntityData entityData = EntityData.newBuilder()
		    .withEntityKey(EntityKey.newBuilder()
		        .withKeyName(keyValue.getKey().toString(Charsets.UTF_8))
		        .build())
		    .withEntityValue(EntityValue.newBuilder()
		        .withValue(keyValue.getValue().toString(Charsets.UTF_8))
		        .build())
		    .build();

		result.add(entityData);
	    });

	} catch (InterruptedException | ExecutionException e)
	{
	    throw new IllegalStateException("Read has error!!", e);
	}
	return result;
    }

    public void watch(EntityKey entityKey, EtcdWatcher watcher)
    {
	Watch watchClient = etcdConnection.getEtcdClient().getWatchClient();
	WatchOption option = WatchOption.newBuilder()
	    .withPrefix(ByteSequence
	        .from(entityKey.getQueryOption().getPrefix().getBytes()))
	    .withPrevKV(true).build();
	watchClient.watch(entityKey.toByteSequence(), option, watcher);
    }

    @Override
    public void delete(EntityKey entityKey)
    {

    }
}
