package com.jamie.jmeter.controller;

import com.jamie.jmeter.form.ScheduledForm;
import com.jamie.jmeter.service.IScheduledService;
import com.jamie.jmeter.vo.ResponseVo;
import com.jamie.jmeter.vo.ScheduledVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class ScheduledController {

    @Resource
    private IScheduledService scheduledService;

    @PostMapping("/jmeter/scheduled/start")
    public ResponseVo<ScheduledVo> startTask(@RequestBody ScheduledForm form) {
        return scheduledService.startTask(form);
    }

    @PostMapping("/jmeter/scheduled/stop")
    public ResponseVo<ScheduledVo> stopTask(){
        return scheduledService.stopTask();
    }

    @PostMapping("/jmeter/scheduled/restart")
    public ResponseVo<ScheduledVo> restartTask(@RequestBody ScheduledForm form) {
        return scheduledService.restartTask(form);
    }

    @GetMapping("/jmeter/scheduled/query")
    public ResponseVo<ScheduledVo> queryTask() {
        return scheduledService.queryTask();
    }

}
