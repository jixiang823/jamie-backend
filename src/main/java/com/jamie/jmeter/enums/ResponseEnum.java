package com.jamie.jmeter.enums;

import lombok.Getter;

@Getter
public enum ResponseEnum {

    ERROR(-1, "服务端异常"),
    ARGUMENT_NOT_EXIST(0, "未查询到数据"),
    INVALID_ARGUMENT(1, "请求参数不合法"),
    CORN_NOT_EXIST(1, "corn不能为空"),
    SUCCESS(20000, "请求成功"),
    ;

    final Integer code;
    final String message;

    ResponseEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
