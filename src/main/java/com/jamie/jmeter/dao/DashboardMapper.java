package com.jamie.jmeter.dao;

import com.jamie.jmeter.pojo.Dashboard;

import java.util.List;
import java.util.Map;

public interface DashboardMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Dashboard row);

    int insertSelective(Dashboard row);

    Dashboard selectByPrimaryKey(Integer id);

    Dashboard selectLatest(); // 查询最新的看板数据

    String selectMaxBatchNo(); // 查询当日最大批次号

    List<Map<String,String>> selectFailCaseHistoryResults(String batchNo); // 获取最新执行失败用例的历史测试结果

    int updateByPrimaryKeySelective(Dashboard row);

    int updateByPrimaryKey(Dashboard row);
}