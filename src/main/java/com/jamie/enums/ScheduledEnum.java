package com.jamie.enums;

import lombok.Getter;

@Getter
public enum ScheduledEnum {

    START(1, "定时任务已启动"),
    STOP(2, "定时任务已停止"),
    RESTART(3, "定时任务已重启"),
    ;
    final Integer code;
    final String message;

    ScheduledEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
