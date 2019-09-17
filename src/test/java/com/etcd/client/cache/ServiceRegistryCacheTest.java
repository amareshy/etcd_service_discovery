package com.etcd.client.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.etcd.client.client.cache.ServiceRegistryCache;
import com.etcd.client.client.service.EtcdDataAccessorService;

@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan("com.etcd.client")
@EnableAutoConfiguration
public class ServiceRegistryCacheTest
{
    private static final Logger LOGGER = LoggerFactory
        .getLogger(ServiceRegistryCacheTest.class);

    @Autowired
    private EtcdDataAccessorService myDataAccessorService;

    @Autowired
    private ServiceRegistryCache myServiceRegistryCache;

    @Test
    public void initCacheTest()
    {
	// createData();
	// myServiceRegistryCache.initCache();
    }
}
