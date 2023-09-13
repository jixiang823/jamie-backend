package com.jamie.service;


import com.jamie.form.UserInfoForm;
import com.jamie.pojo.User;
import com.jamie.vo.ResponseVo;

public interface IUserService {

    ResponseVo<User> register(User user);

    ResponseVo<User> login(String username, String password);

    ResponseVo<User> update(Integer id, UserInfoForm userInfoForm);

}
