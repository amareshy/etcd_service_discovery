package com.etcd.client.client.service;

import java.util.function.Consumer;

import io.etcd.jetcd.watch.WatchResponse;

@FunctionalInterface
public interface EtcdWatcher extends Consumer<WatchResponse>
{

}
