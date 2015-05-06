package com.apigee.sdk.apm.android.metrics;

import java.util.concurrent.ThreadFactory;

public class LowPriorityThreadFactory implements ThreadFactory {
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setPriority(Thread.MIN_PRIORITY);
        return t;
    }
}