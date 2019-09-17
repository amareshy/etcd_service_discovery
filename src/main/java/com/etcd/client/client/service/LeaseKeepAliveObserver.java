package com.etcd.client.client.service;

import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.grpc.stub.StreamObserver;

public interface LeaseKeepAliveObserver
    extends StreamObserver<LeaseKeepAliveResponse>
{

}
