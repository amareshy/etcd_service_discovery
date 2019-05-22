package com.etcd.client.utils;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class CustomScheduleExecuterThreadPool
    extends ScheduledThreadPoolExecutor
{
    public CustomScheduleExecuterThreadPool(int corePoolSize,
        ThreadFactory threadFactory)
    {
	super(corePoolSize, threadFactory);
    }
}
