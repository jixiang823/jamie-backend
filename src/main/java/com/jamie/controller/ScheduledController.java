package com.jamie.controller;

import com.jamie.form.ScheduledForm;
import com.jamie.service.IScheduledService;
import com.jamie.vo.ResponseVo;
import com.jamie.vo.ScheduledVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class ScheduledController {

    @Resource
    private IScheduledService scheduledService;

    @PostMapping("/scheduled/start")
    public ResponseVo<ScheduledVo> startTask(@RequestBody ScheduledForm form) {
        return scheduledService.startTask(form);
    }

    @PostMapping("/scheduled/stop")
    public ResponseVo<ScheduledVo> stopTask(){
        return scheduledService.stopTask();
    }

    @PostMapping("/scheduled/restart")
    public ResponseVo<ScheduledVo> restartTask(@RequestBody ScheduledForm form) {
        return scheduledService.restartTask(form);
    }

    @GetMapping("/scheduled/query")
    public ResponseVo<ScheduledVo> queryTask() {
        return scheduledService.queryTask();
    }

}
