package com.jamie.jmeter.service.impl;

import com.jamie.jmeter.dao.UserMapper;
import com.jamie.jmeter.enums.RoleEnum;
import com.jamie.jmeter.form.UserInfoForm;
import com.jamie.jmeter.pojo.User;
import com.jamie.jmeter.service.IUserService;
import com.jamie.jmeter.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static com.jamie.jmeter.enums.ResponseEnum.*;


@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public ResponseVo<User> register(User user) {
        //username不能重复
        int countByUsername = userMapper.countByUsername(user.getUsername());
        if (countByUsername > 0) {
            return ResponseVo.error(USERNAME_EXIST);
        }
        //设置角色为普通用户
        user.setRole(RoleEnum.TESTER.getCode());
        //MD5摘要算法(Spring自带)
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));

        //写入数据库
        int resultCount = userMapper.insertSelective(user);
        if (resultCount == 0) {
            //返回0说明没有写入数据库
            return ResponseVo.error(ERROR);
        }

        return ResponseVo.success(user);
    }

    @Override
    public ResponseVo<User> login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            // 用户不存在(返回: 用户名或密码错误)
            return ResponseVo.error(USERNAME_OR_PASSWORD_ERROR);
        }
        if (!user.getPassword().equalsIgnoreCase(DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)))) {
            // 密码错误(返回: 用户名或密码错误)
            return ResponseVo.error(USERNAME_OR_PASSWORD_ERROR);
        }
        user.setPassword("");
        return ResponseVo.success(user);
    }

    @Override
    public ResponseVo<User> update(Integer id, UserInfoForm userInfoForm) {

        User user = new User();
        BeanUtils.copyProperties(userInfoForm, user);
        user.setId(id);
        user.setUpdateTime(new Date());
        if (!StringUtils.isEmpty(userInfoForm.getPassword())) {
            user.setPassword(DigestUtils.md5DigestAsHex(userInfoForm.getPassword().getBytes(StandardCharsets.UTF_8)));
        }
        int row = userMapper.updateByPrimaryKeySelective(user);
        if (row == 0) {
            return ResponseVo.error(UPDATE_FAIL);
        }
        return ResponseVo.success(user);
    }

}
