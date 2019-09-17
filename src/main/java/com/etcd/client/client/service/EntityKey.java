package com.etcd.client.client.service;

import com.etcd.client.client.service.impl.QueryOption;
import com.etcd.client.client.service.impl.EntityKeyImpl.EntityKeyBuilder;

import io.etcd.jetcd.ByteSequence;

public interface EntityKey
{

    String getKeyName();

    ByteSequence toByteSequence();

    QueryOption getQueryOption();

    public static EntityKeyBuilder newBuilder()
    {
	return new EntityKeyBuilder();
    }
}
