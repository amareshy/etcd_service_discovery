package com.etcd.client.client.service;

import java.util.List;

public interface EtcdDataAccessorService
{
    /**
     * Write key-value in ETCD store.
     * 
     * @param key
     * @param value
     */
    public void register(EntityData entityData);

    public List<EntityData> read(EntityKey entityKey);

    /**
     * Watch Entity key changes in etcd store.
     * 
     * @param entityKey
     */
    public void watch(EntityKey entityKey, EtcdWatcher watcher);

    public void delete(EntityKey entityKey);

}
