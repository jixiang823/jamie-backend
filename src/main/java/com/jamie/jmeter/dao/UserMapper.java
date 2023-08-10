package com.jamie.jmeter.dao;

import com.jamie.jmeter.pojo.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User row);

    int insertSelective(User row);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User row);

    int updateByPrimaryKey(User row);

    int countByUsername(String username); //查询是否有username

    int countByEmail(String email); //查询是否有email

    User selectByUsername(String username);

}