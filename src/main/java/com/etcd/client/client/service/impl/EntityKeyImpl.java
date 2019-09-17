package com.etcd.client.client.service.impl;

import com.etcd.client.client.service.EntityKey;

import io.etcd.jetcd.ByteSequence;

public class EntityKeyImpl implements EntityKey
{
    private final String keyName;
    private final QueryOption queryOption;

    private EntityKeyImpl(EntityKeyBuilder builder)
    {
	this.keyName = builder.keyName;
	this.queryOption = builder.queryOption;
    }

    @Override
    public String getKeyName()
    {
	return this.keyName;
    }

    @Override
    public QueryOption getQueryOption()
    {
	return queryOption;
    }

    public static class EntityKeyBuilder
    {
	private String keyName;
	private QueryOption queryOption;

	public EntityKeyBuilder()
	{

	}

	/**
	 * Key name
	 * 
	 * @param serviceName
	 * @return {@link EntityKeyBuilder}
	 */
	public EntityKeyBuilder withKeyName(String serviceName)
	{
	    this.keyName = serviceName;
	    return this;
	}

	/**
	 * Set Query options
	 * 
	 * @param option
	 * @return
	 */
	public EntityKeyBuilder withQueryOption(QueryOption option)
	{
	    this.queryOption = option;
	    return this;
	}

	public EntityKey build()
	{
	    return new EntityKeyImpl(this);
	}
    }

    @Override
    public ByteSequence toByteSequence()
    {
	return ByteSequence.from(getKeyName().getBytes());
    }

    @Override
    public int hashCode()
    {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((keyName == null) ? 0 : keyName.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj)
    {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	EntityKeyImpl other = (EntityKeyImpl) obj;
	if (keyName == null)
	{
	    if (other.keyName != null)
		return false;
	} else if (!keyName.equals(other.keyName))
	    return false;
	return true;
    }

    @Override
    public String toString()
    {
	return String.format("EntityKeyImpl [keyName=%s, queryOption=%s]",
	    keyName, queryOption);
    }

}
