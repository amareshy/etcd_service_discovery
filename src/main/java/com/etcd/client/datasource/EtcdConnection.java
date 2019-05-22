package com.etcd.client.datasource;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.etcd.jetcd.Client;

/**
 * Handle ETCD connection.
 * 
 * @author amaresh
 */
@Component
public class EtcdConnection
{
    private static final Logger LOGGER = LoggerFactory
        .getLogger(EtcdConnection.class);

    @Autowired
    private EtcdClientProperties myConnectionProperties;

    /** Native client. */
    private Client etcdClient;

    public void connect()
    {
	final String etcdUrl = String.format("http://%s:%d",
	    myConnectionProperties.getEtcdServerHost(),
	    myConnectionProperties.getEtcdServerPort());
	LOGGER.info("Initializing connection to ETCD Server");
	LOGGER.info(" + Address '{}'", etcdUrl);
	etcdClient = Client.builder().endpoints(URI.create(etcdUrl)).build();
	LOGGER.info(" + Client created");
    }

    public Client getEtcdClient()
    {
	return etcdClient;
    }
}
