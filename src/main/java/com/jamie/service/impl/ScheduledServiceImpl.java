package com.jamie.service.impl;

import com.jamie.enums.ResponseEnum;
import com.jamie.enums.ScheduledEnum;
import com.jamie.form.ScheduledForm;
import com.jamie.scheduled.ScheduledFutureHolder;
import com.jamie.scheduled.AutoTestTask;
import com.jamie.service.IScheduledService;
import com.jamie.vo.ResponseVo;
import com.jamie.vo.ScheduledVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Service
@Slf4j
public class ScheduledServiceImpl implements IScheduledService {

    @Resource
    private AutoTestTask autoTestTask;
    @Resource
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;
    // 有多个任务的时候会用到,根据任务名(key)去拿到对应的任务信息.
    private final Map<String, ScheduledFutureHolder> scheduleMap = new HashMap<>();

    @Override
    public ResponseVo<ScheduledVo> startTask(ScheduledForm form) {

        String corn = form.getCorn();
        if (StringUtils.isEmpty(corn) || StringUtils.isBlank(corn)) {
            return ResponseVo.error(ResponseEnum.CORN_NOT_EXIST);
        }
        // 将任务交给任务调度器执行
        ScheduledFuture<?> schedule = threadPoolTaskScheduler.schedule(autoTestTask, new CronTrigger(corn));
        // 将任务包装成ScheduledFutureHolder
        ScheduledFutureHolder scheduledFutureHolder = new ScheduledFutureHolder();
        scheduledFutureHolder.setScheduledFuture(schedule);
        scheduledFutureHolder.setRunnableClass(autoTestTask.getClass());
        scheduledFutureHolder.setCorn(corn);
        scheduleMap.put("task", scheduledFutureHolder);
        // 提交给前端的数据
        ScheduledVo scheduledVo = new ScheduledVo();
        scheduledVo.setClassName(autoTestTask.getClass().getName());
        scheduledVo.setCorn(corn);
        scheduledVo.setStatus(ScheduledEnum.START.getCode());
        scheduledVo.setMessage(ScheduledEnum.START.getMessage());

        return ResponseVo.success(scheduledVo);

    }

    @Override
    public ResponseVo<ScheduledVo> stopTask() {

        ScheduledFutureHolder scheduledFutureHolder = scheduleMap.get("task");
        if (scheduledFutureHolder.getScheduledFuture() == null) {
            return ResponseVo.error(ResponseEnum.INVALID_ARGUMENT);
        }
        ScheduledFuture<?> scheduledFuture = scheduledFutureHolder.getScheduledFuture();
        // 终止任务
        scheduledFuture.cancel(true);
        // 提交给前端的数据
        ScheduledVo scheduledVo = new ScheduledVo();
        scheduledVo.setClassName(scheduledFutureHolder.getRunnableClass().getName());
        scheduledVo.setCorn(scheduledFutureHolder.getCorn());
        scheduledVo.setStatus(ScheduledEnum.STOP.getCode());
        scheduledVo.setMessage(ScheduledEnum.STOP.getMessage());

        return ResponseVo.success(scheduledVo);

    }

    @Override
    public ResponseVo<ScheduledVo> restartTask(ScheduledForm form) {

        String corn = form.getCorn();
        if (StringUtils.isEmpty(corn) || StringUtils.isBlank(corn)) {
            return ResponseVo.error(ResponseEnum.CORN_NOT_EXIST);
        }

        ScheduledFutureHolder scheduledFutureHolder = scheduleMap.get("task");
        if (scheduledFutureHolder.getScheduledFuture() == null) {
            return ResponseVo.error(ResponseEnum.INVALID_ARGUMENT);
        }
        ScheduledFuture<?> scheduledFuture = scheduledFutureHolder.getScheduledFuture();
        // 终止任务
        scheduledFuture.cancel(true);
        Runnable runnable;
        try {
            runnable = scheduledFutureHolder.getRunnableClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        // 重启任务
        ScheduledFuture<?> schedule = threadPoolTaskScheduler.schedule(runnable, new CronTrigger(corn));
        scheduledFutureHolder.setScheduledFuture(schedule);
        scheduledFutureHolder.setCorn(corn);
        scheduleMap.put(scheduledFutureHolder.getRunnableClass().getName(), scheduledFutureHolder);
        // 提交给前端的数据
        ScheduledVo scheduledVo = new ScheduledVo();
        scheduledVo.setClassName(scheduledFutureHolder.getRunnableClass().getName());
        scheduledVo.setCorn(corn);
        scheduledVo.setStatus(ScheduledEnum.RESTART.getCode());
        scheduledVo.setMessage(ScheduledEnum.RESTART.getMessage());

        return ResponseVo.success(scheduledVo);

    }

    @Override
    public ResponseVo<ScheduledVo> queryTask() {

        ScheduledVo scheduledVo = new ScheduledVo();
        if (scheduleMap.isEmpty()) {
            return ResponseVo.success(scheduledVo);
        }
        scheduleMap.forEach((k,v)->{
            scheduledVo.setClassName(v.getRunnableClass().getName());
            scheduledVo.setCorn(v.getCorn());
            if (v.getScheduledFuture().isCancelled()) {
                scheduledVo.setStatus(ScheduledEnum.STOP.getCode());
                scheduledVo.setMessage(ScheduledEnum.STOP.getMessage());
            } else {
                scheduledVo.setStatus(ScheduledEnum.START.getCode());
                scheduledVo.setMessage(ScheduledEnum.START.getMessage());
            }
        });
        return ResponseVo.success(scheduledVo);

    }

}
