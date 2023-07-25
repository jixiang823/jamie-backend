package com.jamie.jmeter.dao;

import com.jamie.jmeter.pojo.Dashboard;

import java.util.List;
import java.util.Map;

public interface DashboardMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Dashboard row);

    int insertSelective(Dashboard row);

    Dashboard selectByPrimaryKey(Integer id);

    List<Map<String,Integer>> selectCaseResultTrend(); // line-chart趋势图(记录最近7次)

    List<Map<String,Integer>> selectCaseTotalPassTrend(); // total-pass趋势图(记录最近7次)

    List<Map<String,Integer>> selectCaseTotalFailTrend(); // total-fail趋势图(记录最近7次)

    List<Map<String,Integer>> selectCaseNewlyFailTrend(); // newly-fail趋势图(记录最近7次)

    List<Map<String,Integer>> selectCaseKeepFailingTrend(); // keep-failing趋势图(记录最近7次)

    Dashboard selectLatest(); // 查询最新的看板数据

    String selectMaxBatchNo(); // 查询当日最大批次号

    List<Map<String,String>> selectFailCaseHistoryResults(String batchNo); // 获取最新执行失败用例的历史测试结果

    int updateByPrimaryKeySelective(Dashboard row);

    int updateByPrimaryKey(Dashboard row);
}