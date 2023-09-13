package com.jamie.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserInfoForm {

    @NotBlank
    private String username;
    private String password;
    private String email;
    private String phone;
    private String avatar;
    private Integer role;

}