package com.jamie.dao;

import com.jamie.pojo.TestSummary;
import com.jamie.vo.TableVo;

import java.util.List;
import java.util.Map;

public interface TestSummaryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TestSummary row);

    int insertSelective(TestSummary row);

    TestSummary selectByPrimaryKey(Integer id);

    List<Map<String,Integer>> selectCaseResultTrend(); // line-chart趋势图(记录最近7次)

    List<Map<String,Integer>> selectCaseTotalPassTrend(); // total-pass趋势图(记录最近7次)

    List<Map<String,Integer>> selectCaseTotalFailTrend(); // total-fail趋势图(记录最近7次)

    List<Map<String,Integer>> selectCaseNewlyFailTrend(); // newly-fail趋势图(记录最近7次)

    List<Map<String,Integer>> selectCaseKeepFailingTrend(); // keep-failing趋势图(记录最近7次)

    TestSummary selectLatestSummary(); // 查询最新的看板数据

    List<TableVo> selectLatestCaseList(); // 获取最新批次的用例信息 (放到testSummary里)

//    List<Dashboard> selectSameBatch(@Param("pageSize") Integer pageSize); // 废弃 查询最新的看板数据(指定数量) 用于更新batch_no为同一批次

    String selectMaxBatchNo(); // 获取最新批次号

    List<Map<String,String>> selectFailCaseHistoryResults(String batchNo); // 获取最新执行失败用例的历史测试结果

    int updateByPrimaryKeySelective(TestSummary row);

    int updateByPrimaryKey(TestSummary row);

    int updateBatch(List<TestSummary> testSummaries); // 批量更新
}