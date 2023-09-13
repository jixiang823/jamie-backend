package com.jamie.enums;

import lombok.Getter;

@Getter
public enum ResponseEnum {

    ERROR(-1, "服务端异常"),
    ARGUMENT_NOT_EXIST(0, "未查询到数据"),
    INVALID_ARGUMENT(20001, "请求参数不合法"),
    CORN_NOT_EXIST(20002, "corn不能为空"),
    USERNAME_EXIST(20003,"用户名已存在"),
    EMAIL_EXIST(20004,"邮箱已存在"),
    USERNAME_OR_PASSWORD_ERROR(20005,"用户名或密码错误"),
    UPDATE_FAIL(20006, "更新失败"),
    INVALID_PASSWORD(20007, "密码不能小于6位"),
    SUCCESS(20000, "请求成功"),
    ;

    final Integer code;
    final String message;

    ResponseEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
