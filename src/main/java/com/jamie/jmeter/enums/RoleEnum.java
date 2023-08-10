package com.jamie.jmeter.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {
    ADMIN(0),
    TESTER(1),
    ;

    RoleEnum(Integer code) {
        this.code = code;
    }

    Integer code;

}
