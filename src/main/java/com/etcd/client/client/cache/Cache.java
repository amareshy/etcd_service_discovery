package com.etcd.client.client.cache;

public interface Cache<K, V>
{
    V get(String k);
}
