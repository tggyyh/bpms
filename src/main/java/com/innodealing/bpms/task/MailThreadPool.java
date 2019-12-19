package com.innodealing.bpms.task;

import com.innodealing.bpms.common.model.ConstantUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MailThreadPool {
    private static ExecutorService pool;
    private MailThreadPool(){}
    public static synchronized ExecutorService getPool() {
        if (pool == null) {
            pool = Executors.newFixedThreadPool(ConstantUtil.MAIL_THREAD_COUNT);
        }
        return pool;
    }
}
