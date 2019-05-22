package com.etcd.client.cache;

public interface Cache<K, V>
{
    V get(String k);
}
