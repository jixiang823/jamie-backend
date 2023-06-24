package com.jamie.jmeter.enums;

import lombok.Getter;

@Getter
public enum ResponseEnum {

    SERVICE_ERROR(-1, "服务异常"),
    ARGUMENT_NOT_EXIST(0, "未查询到数据"),
    SUCCESS(0, "请求成功"),
    ;

    final Integer code;
    final String message;

    ResponseEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
