package com.etcd.client.service;

import com.etcd.client.service.impl.EntityDataImpl.EntityDataBuilder;

public interface EntityData
{
    /**
     * Returns the entity key.
     *
     * @return the entity key
     */
    EntityKey getEntityKey();

    /**
     * Returns the entity value.
     *
     * @return the entity key
     */
    EntityValue getEntityValue();

    /**
     * Returns the entity property.
     *
     * @return the entity property
     */
    EntityProperty getEntityProperty();

    public static EntityDataBuilder newBuilder()
    {
	return new EntityDataBuilder();
    }

}
