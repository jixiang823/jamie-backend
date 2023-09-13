package com.jamie.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class User {

    private Integer id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String avatar;
    private Integer role;
    private Date createTime;
    private Date updateTime;

}