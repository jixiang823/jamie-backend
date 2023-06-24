package com.jamie.jmeter.dao;

import com.jamie.jmeter.pojo.Testcase;

import java.util.List;
import java.util.Map;

public interface TestcaseMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Testcase row);

    int insertSelective(Testcase row);

    Testcase selectByPrimaryKey(Integer id);

    List<Testcase> page(Map<String, Object> queryKeywords); // 分页多条件查询

    int updateByPrimaryKeySelective(Testcase row);

    int updateByPrimaryKey(Testcase row);
}