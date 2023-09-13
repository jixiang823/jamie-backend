package com.jamie.controller;

import com.jamie.form.UserInfoForm;
import com.jamie.form.UserRegisterForm;
import com.jamie.form.UserLoginForm;
import com.jamie.pojo.User;
import com.jamie.service.IUserService;
import com.jamie.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static com.jamie.constant.UserConst.CURRENT_USER;
import static com.jamie.enums.ResponseEnum.USERNAME_OR_PASSWORD_ERROR;


@RestController
@Slf4j
public class UserController {

    @Resource
    private IUserService userService;

    @PostMapping("/user/register")
    public ResponseVo<User> register(@Valid @RequestBody UserRegisterForm userRegisterForm) {
        if (!userRegisterForm.getPassword().equals(userRegisterForm.getPassword2())) {
            return ResponseVo.error(USERNAME_OR_PASSWORD_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userRegisterForm,user);
        return userService.register(user);
    }

    @PostMapping("/user/login")
    public ResponseVo<User> login(@Valid @RequestBody UserLoginForm userLoginForm, HttpSession httpSession) {
        ResponseVo<User> userResponseVo = userService.login(userLoginForm.getUsername(), userLoginForm.getPassword());
        httpSession.setAttribute(CURRENT_USER, userResponseVo.getData());
        return userResponseVo;
    }

    @GetMapping("/user/info")
    public ResponseVo<User> userInfo(HttpSession session) {
        log.info("/user sessionId={}", session.getId());
        User user = (User)session.getAttribute(CURRENT_USER);
        return ResponseVo.success(user);

    }

    @PostMapping("/user/logout")
    public ResponseVo<User> logout(HttpSession session) {
        log.info("/user/logout sessionId={}", session.getId());
        session.removeAttribute(CURRENT_USER);
        return ResponseVo.success();

    }

    @PutMapping("/user/update")
    public ResponseVo<User> update(@Valid @RequestBody UserInfoForm userInfoForm, HttpSession session) {
        User user = (User)session.getAttribute(CURRENT_USER);
        return userService.update(user.getId(), userInfoForm);
    }

}
