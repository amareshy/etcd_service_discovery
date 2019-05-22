package com.etcd.client.service.impl;

import com.etcd.client.service.EntityProperty;
import com.etcd.client.service.LeaseKeepAliveObserver;
import com.etcd.client.service.impl.EntityKeyImpl.EntityKeyBuilder;

public class EntityPropertyImpl implements EntityProperty
{
    private final int ttl;
    private final LeaseKeepAliveObserver keepAliveObserver;

    private EntityPropertyImpl(EntityPropertyBuilder builder)
    {
	this.ttl = builder.ttl;
	this.keepAliveObserver = builder.keepAliveObserver;
    }

    @Override
    public int getTTL()
    {
	return ttl;
    }

    @Override
    public LeaseKeepAliveObserver getLeaseKeepAliveObserver()
    {
	return keepAliveObserver;
    }

    public static class EntityPropertyBuilder
    {
	private int ttl;
	private LeaseKeepAliveObserver keepAliveObserver;

	/**
	 * Set the TTL for a key in ETCD store.
	 * 
	 * @param ttl
	 * @return {@link EntityKeyBuilder}
	 */
	public EntityPropertyBuilder withTtl(int ttl)
	{
	    this.ttl = ttl;
	    return this;
	}

	/**
	 * Refresh the lease of key by provided TTL value.
	 * 
	 * @param observer to get update for lease refersh.
	 * @return {@link EntityKeyBuilder}
	 */
	public EntityPropertyBuilder withLeaseKeepAliveObserver(
	    LeaseKeepAliveObserver observer)
	{
	    this.keepAliveObserver = observer;
	    return this;
	}

	public EntityProperty build()
	{
	    return new EntityPropertyImpl(this);
	}
    }

    @Override
    public String toString()
    {
	return String.format(
	    "EntityPropertyImpl [ttl=%s, keepAliveObserver=%s]", ttl,
	    keepAliveObserver);
    }

}
