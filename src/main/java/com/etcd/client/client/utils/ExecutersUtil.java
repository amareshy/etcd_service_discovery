package com.etcd.client.client.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutersUtil
{
    private ExecutersUtil()
    {
    }

    public static CustomScheduleExecuterThreadPool getHeartBeatExecutor()
    {
	CustomScheduleExecuterThreadPool executor = new CustomScheduleExecuterThreadPool(
	    15, new ThreadFactory()
	    {
	        @Override
	        public Thread newThread(Runnable r)
	        {
		    AtomicInteger counter = new AtomicInteger(0);
		    return new Thread(r, "Micro-Services-HeartBeat-{"
		        + counter.getAndIncrement() + "}");
	        }
	    });
	return executor;
    }
}
