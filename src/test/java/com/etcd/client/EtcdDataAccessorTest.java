package com.etcd.client;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.etcd.client.client.service.EntityData;
import com.etcd.client.client.service.EntityKey;
import com.etcd.client.client.service.EntityProperty;
import com.etcd.client.client.service.EntityValue;
import com.etcd.client.client.service.EtcdDataAccessorService;
import com.etcd.client.client.service.EtcdWatcher;
import com.etcd.client.client.service.impl.QueryOption;
import com.google.common.base.Charsets;

import io.etcd.jetcd.watch.WatchResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan
@EnableAutoConfiguration
public class EtcdDataAccessorTest
{
    private static final Logger LOGGER = LoggerFactory
        .getLogger(EtcdDataAccessorTest.class);
    @Autowired
    private EtcdDataAccessorService myDataAccessorServ;

    @Test
    public void testWriteKV()
    {
	EntityKey entityKey = EntityKey.newBuilder()
	    .withKeyName("user-management-service").build();
	EntityValue entityValue = EntityValue.newBuilder()
	    .withValue("/services/userManagement").build();

	myDataAccessorServ.register(EntityData.newBuilder()
	    .withEntityKey(entityKey).withEntityValue(entityValue).build());
	List<EntityData> resultData = myDataAccessorServ.read(entityKey);

	assertFalse(resultData.isEmpty());
	assertEquals(resultData.get(0).getEntityValue(), entityValue);

	LOGGER.info("testWriteKV completed !!");
    }

    @Test
    public void testWriteKVWithTTL()
    {
	EntityProperty property = EntityProperty.newBuilder().withTtl(2)
	    .build();
	EntityKey entityKey = EntityKey.newBuilder().withKeyName("mykey_ttl")
	    .build();
	EntityValue entityValue = EntityValue.newBuilder()
	    .withValue("/services/userManagement").build();

	// Write Key-Value with TTL
	myDataAccessorServ.register(EntityData.newBuilder()
	    .withEntityKey(entityKey).withEntityValue(entityValue)
	    .withEntityProperty(property).build());

	// Read result before TTL expires should not be empty.
	List<EntityData> result = myDataAccessorServ.read(entityKey);
	Assert.assertFalse(result.isEmpty());
	result.forEach((entitydata) ->
	{
	    Assert.assertEquals(entitydata.getEntityKey(), entityKey);
	    Assert.assertEquals(entitydata.getEntityValue(), entityValue);

	});
	try
	{
	    Thread.sleep(1000 * 3);
	} catch (InterruptedException e)
	{
	    e.printStackTrace();
	}
	// Read result after TTL reached. Key-value should be deleted.
	result = myDataAccessorServ.read(entityKey);
	Assert.assertTrue(result.isEmpty());

	LOGGER.info("testWriteKVWithTTL completed !!");
    }

    @Test
    public void testWriteKVWithTTLAndKeepAliveTTL()
    {
	EntityProperty property = EntityProperty.newBuilder().withTtl(1)
	    .withLeaseKeepAliveObserver(new LeaseKeepAliveObserverImplTest())
	    .build();
	EntityKey entityKey = EntityKey.newBuilder()
	    .withKeyName("mykey_keep_alive").build();
	EntityValue entityValue = EntityValue.newBuilder()
	    .withValue("/services/userManagement").build();

	// Write Key-Value with TTL
	myDataAccessorServ.register(EntityData.newBuilder()
	    .withEntityKey(entityKey).withEntityValue(entityValue)
	    .withEntityProperty(property).build());

	try
	{
	    Thread.sleep(1000 * 3);
	} catch (InterruptedException e)
	{
	    e.printStackTrace();
	}

	// Read result after TTL reached. Key-value should not be deleted.
	List<EntityData> result = myDataAccessorServ.read(entityKey);
	Assert.assertFalse(result.isEmpty());
	LOGGER.info("testWriteKVWithTTLAndKeepAliveTTL completed !!");
    }

    @Test
    public void testWriteServicesKVAndReadWithPrefixQueryString()
    {
	// Demonstrate three instances of flight service.

	int applicationUID0 = 0;
	int applicationUID1 = 1;
	int applicationUID2 = 2;
	int applicationUID3 = 3;
	int applicationUID4 = 4;

	String shortName = "flights";
	String shortName2 = "inventory";

	final String serviceKey1 = format(
	    "flightReservationSystem/services/%s/%s", shortName,
	    applicationUID0);
	final String serviceKey2 = format(
	    "flightReservationSystem/services/%s/%s", shortName,
	    applicationUID1);
	final String serviceKey3 = format(
	    "flightReservationSystem/services/%s/%s", shortName,
	    applicationUID2);

	final String serviceKey4 = format(
	    "flightReservationSystem/services/%s/%s", shortName2,
	    applicationUID3);
	final String serviceKey5 = format(
	    "flightReservationSystem/services/%s/%s", shortName2,
	    applicationUID4);

	final String applicationAddr1 = "10.10.100.1:8888";
	final String applicationAddr2 = "10.10.100.2:9988";
	final String applicationAddr3 = "10.10.100.3:8899";

	final String applicationAddr4 = "110.11.101.4:8899";
	final String applicationAddr5 = "110.11.101.5:8899";

	EntityKey entityKey = EntityKey.newBuilder().withKeyName(serviceKey1)
	    .build();
	EntityValue entityValue = EntityValue.newBuilder()
	    .withValue(applicationAddr1).build();
	myDataAccessorServ.register(EntityData.newBuilder()
	    .withEntityKey(entityKey).withEntityValue(entityValue).build());

	entityKey = EntityKey.newBuilder().withKeyName(serviceKey2).build();
	entityValue = EntityValue.newBuilder().withValue(applicationAddr2)
	    .build();
	myDataAccessorServ.register(EntityData.newBuilder()
	    .withEntityKey(entityKey).withEntityValue(entityValue).build());

	entityKey = EntityKey.newBuilder().withKeyName(serviceKey3).build();
	entityValue = EntityValue.newBuilder().withValue(applicationAddr3)
	    .build();
	myDataAccessorServ.register(EntityData.newBuilder()
	    .withEntityKey(entityKey).withEntityValue(entityValue).build());

	entityKey = EntityKey.newBuilder().withKeyName(serviceKey4).build();
	entityValue = EntityValue.newBuilder().withValue(applicationAddr4)
	    .build();
	myDataAccessorServ.register(EntityData.newBuilder()
	    .withEntityKey(entityKey).withEntityValue(entityValue).build());

	entityKey = EntityKey.newBuilder().withKeyName(serviceKey5).build();
	entityValue = EntityValue.newBuilder().withValue(applicationAddr5)
	    .build();
	myDataAccessorServ.register(EntityData.newBuilder()
	    .withEntityKey(entityKey).withEntityValue(entityValue).build());

	// Read All instances of a microService i.e. flights.
	QueryOption option = QueryOption.newBuilder()
	    .withPrefix("flightReservationSystem/services/flights").build();

	EntityKey readKey = EntityKey.newBuilder()
	    .withKeyName("flightReservationSystem/services/flights")
	    .withQueryOption(option).build();
	List<EntityData> resultData = myDataAccessorServ.read(readKey);

	assertFalse(resultData.isEmpty());
	assertEquals(3, resultData.size());

	assertEquals(resultData.get(0).getEntityValue().getValue(),
	    applicationAddr1);
	assertEquals(resultData.get(1).getEntityValue().getValue(),
	    applicationAddr2);
	assertEquals(resultData.get(2).getEntityValue().getValue(),
	    applicationAddr3);

	LOGGER.info(
	    "testWriteServicesKVAndReadWithPrefixQueryString completed !!");
    }

    @Test
    public void testWatchKeyChanges()
    {
	final String applicationAddr1 = "New_IP_10.10.100.1:8888";

	QueryOption option = QueryOption.newBuilder()
	    .withPrefix("flightReservationSystem/services").build();
	EntityKey entityKey = EntityKey.newBuilder()
	    .withKeyName("flightReservationSystem/services")
	    .withQueryOption(option).build();

	myDataAccessorServ.watch(entityKey, new EtcdWatcher()
	{
	    @Override
	    public void accept(WatchResponse response)
	    {
		response.getEvents().forEach((watchEvent) ->
		{
		    LOGGER.info("Watcher...... Event Type : "
		        + watchEvent.getEventType() + " Key :"
		        + watchEvent
		            .getKeyValue().getKey().toString(Charsets.UTF_8)
		        + " Value: "
		        + watchEvent
		            .getKeyValue().getValue().toString(Charsets.UTF_8)
		        + " Previous Key Value : "
		        + watchEvent.getPrevKV().getKey()
		            .toString(Charsets.UTF_8)
		        + " " + watchEvent.getPrevKV().getValue()
		            .toString(Charsets.UTF_8));

		    assertEquals(applicationAddr1, watchEvent.getKeyValue()
		        .getValue().toString(Charsets.UTF_8));

		});
	    }
	});

	new Thread(() ->
	{
	    try
	    {
		Thread.sleep(2000);
	    } catch (InterruptedException e)
	    {
		e.printStackTrace();
	    }

	    int applicationUID0 = 6;
	    String shortName = "flights";
	    final String serviceKey1 = format(
	        "flightReservationSystem/services/%s/%s", shortName,
	        applicationUID0);

	    EntityKey entityKey2 = EntityKey.newBuilder()
	        .withKeyName(serviceKey1).build();
	    EntityValue entityValue = EntityValue.newBuilder()
	        .withValue(applicationAddr1).build();
	    EntityProperty property = EntityProperty.newBuilder().withTtl(1)
	        .build();
	    myDataAccessorServ.register(EntityData.newBuilder()
	        .withEntityKey(entityKey2).withEntityValue(entityValue)
	        .withEntityProperty(property).build());

	}).start();

	try
	{
	    Thread.sleep(6000);
	} catch (InterruptedException e)
	{
	    e.printStackTrace();
	}
    }
}
