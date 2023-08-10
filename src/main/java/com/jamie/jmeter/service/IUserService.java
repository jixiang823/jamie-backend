package com.jamie.jmeter.service;


import com.jamie.jmeter.pojo.User;
import com.jamie.jmeter.vo.ResponseVo;

public interface IUserService {

    ResponseVo<User> register(User user);

    ResponseVo<User> login(String username, String password);

}
