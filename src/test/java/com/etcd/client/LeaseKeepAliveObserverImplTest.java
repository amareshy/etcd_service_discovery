package com.etcd.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.etcd.client.client.service.LeaseKeepAliveObserver;

import io.etcd.jetcd.lease.LeaseKeepAliveResponse;

public class LeaseKeepAliveObserverImplTest implements LeaseKeepAliveObserver
{
    private static final Logger LOGGER = LoggerFactory
        .getLogger(LeaseKeepAliveObserverImplTest.class);

    @Override
    public void onNext(LeaseKeepAliveResponse value)
    {
	LOGGER.info("onNext LeaseKeepAliveResponse :: id :" + value.getID()
	    + " TTL: " + value.getTTL());
    }

    @Override
    public void onError(Throwable t)
    {
	LOGGER.error("onError LeaseKeepAliveResponse :: ", t);
    }

    @Override
    public void onCompleted()
    {
	LOGGER.info("onCompleted LeaseKeepAliveResponse ");
    }

}
