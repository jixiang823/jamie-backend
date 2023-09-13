package com.jamie.service.impl;

import com.jamie.enums.ResponseEnum;
import com.jamie.enums.RoleEnum;
import com.jamie.form.UserInfoForm;
import com.jamie.dao.UserMapper;
import com.jamie.pojo.User;
import com.jamie.service.IUserService;
import com.jamie.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;


@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public ResponseVo<User> register(User user) {
        //username不能重复
        int countByUsername = userMapper.countByUsername(user.getUsername());
        if (countByUsername > 0) {
            return ResponseVo.error(ResponseEnum.USERNAME_EXIST);
        }
        if (user.getPassword().length() < 6 ) {
            return ResponseVo.error(ResponseEnum.INVALID_PASSWORD);
        }
        //设置角色为普通用户
        user.setRole(RoleEnum.TESTER.getCode());
        //MD5摘要算法(Spring自带)
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));

        //写入数据库
        int resultCount = userMapper.insertSelective(user);
        if (resultCount == 0) {
            //返回0说明没有写入数据库
            return ResponseVo.error(ResponseEnum.ERROR);
        }

        return ResponseVo.success(user);
    }

    @Override
    public ResponseVo<User> login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            // 用户不存在(返回: 用户名或密码错误)
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }
        if (!user.getPassword().equalsIgnoreCase(DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)))) {
            // 密码错误(返回: 用户名或密码错误)
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }
        user.setPassword("");
        return ResponseVo.success(user);
    }

    @Override
    public ResponseVo<User> update(Integer id, UserInfoForm userInfoForm) {

        User user = new User();
        String password = userInfoForm.getPassword();
        if (password.length() < 6 && !StringUtils.isBlank(password) && !StringUtils.isEmpty(password)) {
            return ResponseVo.error(ResponseEnum.INVALID_PASSWORD);
        }
        if (StringUtils.isBlank(password) || StringUtils.isEmpty(password)) {
            BeanUtils.copyProperties(userInfoForm, user, "password");
        } else {
            BeanUtils.copyProperties(userInfoForm, user);
            user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)));
        }
        user.setId(id);
        user.setUpdateTime(new Date());
        int row = userMapper.updateByPrimaryKeySelective(user);
        if (row == 0) {
            return ResponseVo.error(ResponseEnum.UPDATE_FAIL);
        }
        return ResponseVo.success(user);

    }

}
