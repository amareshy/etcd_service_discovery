package com.etcd.client.service;

import com.etcd.client.service.impl.EntityPropertyImpl.EntityPropertyBuilder;

public interface EntityProperty
{
    int getTTL();

    /**
     * To keep alive the associated lease. This will work only if TTL value is
     * provided. Observer to get lease keep alive updates.
     * 
     * @return instance of LeaseKeepAliveObserver implementation
     */
    LeaseKeepAliveObserver getLeaseKeepAliveObserver();

    public static EntityPropertyBuilder newBuilder()
    {
	return new EntityPropertyBuilder();
    }

}
