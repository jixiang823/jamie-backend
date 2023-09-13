package com.jamie.dao;

import com.jamie.pojo.ApiInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApiInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ApiInfo row);

    int batchInsert(@Param("caseStepsList") List<List<ApiInfo>> caseStepsList); // 避免循环体内操作数据库

    int insertSelective(ApiInfo row);

    ApiInfo selectByPrimaryKey(Integer id);

    List<ApiInfo> selectByCaseId(Integer caseId); //查询用例的steps数据

    int updateByPrimaryKeySelective(ApiInfo row);

    int updateByPrimaryKey(ApiInfo row);
}