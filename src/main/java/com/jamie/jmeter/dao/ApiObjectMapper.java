package com.jamie.jmeter.dao;

import com.jamie.jmeter.pojo.ApiObject;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApiObjectMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ApiObject row);

    int batchInsert(@Param("caseStepsList") List<List<ApiObject>> caseStepsList); // 避免循环体内操作数据库

    int insertSelective(ApiObject row);

    ApiObject selectByPrimaryKey(Integer id);

    List<ApiObject> selectByCaseId(Integer caseId); //查询用例的steps数据

    int updateByPrimaryKeySelective(ApiObject row);

    int updateByPrimaryKey(ApiObject row);
}