package com.jamie.jmeter.dao;

import com.jamie.jmeter.pojo.ApiObject;

import java.util.List;

public interface ApiObjectMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ApiObject row);

    int insertSelective(ApiObject row);

    ApiObject selectByPrimaryKey(Integer id);

    List<ApiObject> selectByCaseId(Integer caseId); //查询用例的steps数据

    int updateByPrimaryKeySelective(ApiObject row);

    int updateByPrimaryKey(ApiObject row);
}