package com.jamie.service;

import com.jamie.form.ScheduledForm;
import com.jamie.vo.ResponseVo;
import com.jamie.vo.ScheduledVo;

public interface IScheduledService {

    ResponseVo<ScheduledVo> startTask(ScheduledForm form);

    ResponseVo<ScheduledVo> stopTask();

    ResponseVo<ScheduledVo> restartTask(ScheduledForm form);

    ResponseVo<ScheduledVo> queryTask();

}
