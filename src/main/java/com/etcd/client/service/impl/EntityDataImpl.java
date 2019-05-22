package com.etcd.client.service.impl;

import com.etcd.client.service.EntityData;
import com.etcd.client.service.EntityKey;
import com.etcd.client.service.EntityProperty;
import com.etcd.client.service.EntityValue;

public class EntityDataImpl implements EntityData
{
    private final EntityKey entityKey;
    private final EntityValue entityValue;
    private final EntityProperty entityProperty;

    private EntityDataImpl(EntityDataBuilder builder)
    {
	this.entityKey = builder.entityKey;
	this.entityValue = builder.entityValue;
	this.entityProperty = builder.entityProperty;
    }

    @Override
    public EntityKey getEntityKey()
    {
	return entityKey;
    }

    @Override
    public EntityValue getEntityValue()
    {
	return entityValue;
    }

    @Override
    public EntityProperty getEntityProperty()
    {
	return entityProperty;
    }

    public static class EntityDataBuilder
    {
	private EntityKey entityKey;
	private EntityValue entityValue;
	private EntityProperty entityProperty;

	public EntityDataBuilder withEntityKey(EntityKey entityKey)
	{
	    this.entityKey = entityKey;
	    return this;
	}

	public EntityDataBuilder withEntityValue(EntityValue entityValue)
	{
	    this.entityValue = entityValue;
	    return this;
	}

	public EntityDataBuilder withEntityProperty(
	    EntityProperty requestProperty)
	{
	    this.entityProperty = requestProperty;
	    return this;
	}

	public EntityData build()
	{
	    return new EntityDataImpl(this);
	}
    }

    @Override
    public String toString()
    {
	return String.format(
	    "EntityDataImpl [entityKey=%s, entityValue=%s, entityProperty=%s]",
	    entityKey, entityValue, entityProperty);
    }

}
