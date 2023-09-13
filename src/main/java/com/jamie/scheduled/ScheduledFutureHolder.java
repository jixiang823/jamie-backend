package com.jamie.scheduled;

import lombok.Data;

import java.util.concurrent.ScheduledFuture;

@Data
public class ScheduledFutureHolder {

    private ScheduledFuture<?> scheduledFuture;
    private Class<? extends Runnable> runnableClass;
    private String corn;

}
