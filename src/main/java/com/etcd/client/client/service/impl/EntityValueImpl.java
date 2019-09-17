package com.etcd.client.client.service.impl;

import com.etcd.client.client.service.EntityValue;

import io.etcd.jetcd.ByteSequence;

public class EntityValueImpl implements EntityValue
{
    private final String value;

    private EntityValueImpl(EntityValueBuilder builder)
    {
	this.value = builder.value;
    }

    @Override
    public String getValue()
    {
	return this.value;
    }

    public static class EntityValueBuilder
    {
	private String value;

	public EntityValueBuilder withValue(String servicePath)
	{
	    this.value = servicePath;
	    return this;
	}

	public EntityValue build()
	{
	    return new EntityValueImpl(this);
	}
    }

    @Override
    public ByteSequence toByteSequence()
    {
	return ByteSequence.from(getValue().getBytes());
    }

    @Override
    public int hashCode()
    {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((value == null) ? 0 : value.hashCode());
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
	EntityValueImpl other = (EntityValueImpl) obj;
	if (value == null)
	{
	    if (other.value != null)
		return false;
	} else if (!value.equals(other.value))
	    return false;
	return true;
    }

    @Override
    public String toString()
    {
	return String.format("EntityValueImpl [value=%s]", value);
    }

}
