package com.jamie.jmeter.controller;

import com.jamie.jmeter.form.UserLoginForm;
import com.jamie.jmeter.form.UserRegisterForm;
import com.jamie.jmeter.pojo.User;
import com.jamie.jmeter.service.IUserService;
import com.jamie.jmeter.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static com.jamie.jmeter.constant.UserConst.CURRENT_USER;


@RestController
@Slf4j
public class UserController {

    @Resource
    private IUserService userService;

    /**
     * 注册
     * @param userRegisterForm
     * @return
     */
    @PostMapping("/user/register")
    public ResponseVo<User> register(@Valid @RequestBody UserRegisterForm userRegisterForm) {
        User user = new User();
        BeanUtils.copyProperties(userRegisterForm,user);
        return userService.register(user);
    }

    /**
     * 登录
     * @param userLoginForm
     * @param httpSession
     * @return
     */
    @PostMapping("/user/login")
    public ResponseVo<User> login(@Valid @RequestBody UserLoginForm userLoginForm,
                                  HttpSession httpSession) {
        ResponseVo<User> userResponseVo = userService.login(userLoginForm.getUsername(), userLoginForm.getPassword());
        //设置session
        httpSession.setAttribute(CURRENT_USER, userResponseVo.getData());
        return userResponseVo;
    }

    /**
     * 用户信息
     * @param session
     * @return
     */
    @GetMapping("/user/info")
    public ResponseVo<User> userInfo(HttpSession session) {
        log.info("/user sessionId={}", session.getId());
        User user = (User)session.getAttribute(CURRENT_USER);
        return ResponseVo.success(user);

    }

    /**
     * 登出
     */
    @PostMapping("/user/logout")
    public ResponseVo<User> logout(HttpSession session) {
        log.info("/user/logout sessionId={}", session.getId());
        session.removeAttribute(CURRENT_USER);
        return ResponseVo.success();

    }
}
