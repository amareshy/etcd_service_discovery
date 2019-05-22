package com.etcd.client.service;

import com.etcd.client.service.impl.EntityKeyImpl.EntityKeyBuilder;
import com.etcd.client.service.impl.QueryOption;

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
