package com.jamie.jmeter.dao;

import com.jamie.jmeter.pojo.TestCase;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TestCaseMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TestCase row);

    int batchInsert(@Param("caseInfoList") List<TestCase> caseInfoList); // 避免循环体内操作数据库

    List<Integer> selectIdByBatchNo(String batchNo); // 根据batchNo拿到caseId集合(ASC排列)

    int insertSelective(TestCase row);

    TestCase selectByPrimaryKey(Integer id);

    List<TestCase> page(Map<String, Object> queryKeywords); // 分页多条件查询

    int updateByPrimaryKeySelective(TestCase row);

    int updateByPrimaryKey(TestCase row);
}