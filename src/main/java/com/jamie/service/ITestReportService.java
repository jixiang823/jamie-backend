package com.jamie.service;

import com.github.pagehelper.PageInfo;
import com.jamie.pojo.ApiInfo;
import com.jamie.pojo.TestSummary;
import com.jamie.vo.TableVo;
import com.jamie.vo.PanelGroupVo;
import com.jamie.vo.TestcaseFilterVo;
import com.jamie.vo.ResponseVo;

import java.util.List;
import java.util.Map;

public interface ITestReportService {

    ResponseVo<TestSummary> getTestSummary(); // pie-chart

//    ResponseVo<Dashboard> updateDashboard(); // pie-chart

    ResponseVo<List<TableVo>> getLatestCaseList(); // table-list

    ResponseVo<List<Map<String,Integer>>> getCaseResultTrend(); // result-trend

    ResponseVo<PanelGroupVo> getPanelGroup(); // total-pass-trend

    // 用于统计执行失败的测试用例的历史执行结果 (未使用)
    ResponseVo<List<Map<String, String>>> getFailCaseHistoryResults();

    // pageinfo-table-list
    ResponseVo<PageInfo<TableVo>> list(TestcaseFilterVo testcaseFilterVo);

    // case-steps
    ResponseVo<List<ApiInfo>> steps(Integer caseId);

}
