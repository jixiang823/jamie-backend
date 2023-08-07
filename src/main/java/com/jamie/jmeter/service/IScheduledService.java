package com.jamie.jmeter.service;

import com.jamie.jmeter.form.ScheduledForm;
import com.jamie.jmeter.vo.ResponseVo;
import com.jamie.jmeter.vo.ScheduledVo;

import java.util.List;

public interface IScheduledService {

    ResponseVo<ScheduledVo> startTask(ScheduledForm form);

    ResponseVo<ScheduledVo> stopTask();

    ResponseVo<ScheduledVo> restartTask(ScheduledForm form);

    ResponseVo<ScheduledVo> queryTask();

}
