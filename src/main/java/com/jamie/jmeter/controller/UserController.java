package com.jamie.jmeter.controller;

import com.jamie.jmeter.form.UserLoginForm;
import com.jamie.jmeter.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;


@RestController
@Slf4j
public class UserController {

    @PostMapping("/user/login")
    public UserLoginForm login() {
        UserLoginForm userLoginForm = new UserLoginForm();
        userLoginForm.setCode(20000);
        userLoginForm.setData("aabbcc");
        return userLoginForm;
    }

    @GetMapping("/user/info")
    public UserLoginForm info() {
        UserLoginForm userLoginForm = new UserLoginForm();
        userLoginForm.setCode(20000);
        userLoginForm.setData("aabbcc");
        return userLoginForm;
    }

}
