package com.etcd.client.client.service;

import com.etcd.client.client.service.impl.EntityValueImpl.EntityValueBuilder;

import io.etcd.jetcd.ByteSequence;

public interface EntityValue
{
    String getValue();

    public ByteSequence toByteSequence();

    public static EntityValueBuilder newBuilder()
    {
	return new EntityValueBuilder();
    }
}
