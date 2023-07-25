package com.jamie.jmeter.service;

import com.github.pagehelper.PageInfo;
import com.jamie.jmeter.pojo.ApiObject;
import com.jamie.jmeter.pojo.Dashboard;
import com.jamie.jmeter.vo.TableVo;
import com.jamie.jmeter.vo.PanelGroupVo;
import com.jamie.jmeter.vo.TestcaseFilterVo;
import com.jamie.jmeter.vo.ResponseVo;

import java.util.List;
import java.util.Map;

public interface IJMeterReportModelService {

    void save(String reportData); // JMeter测试结果入库

    ResponseVo<Dashboard> getDashboard(); // pie-chart

//    ResponseVo<Dashboard> updateDashboard(); // pie-chart

    ResponseVo<List<TableVo>> latestList(); // table-list

    ResponseVo<List<Map<String,Integer>>> getCaseResultTrend(); // result-trend

    ResponseVo<PanelGroupVo> getPanelGroup(); // total-pass-trend

    // 用于统计执行失败的测试用例的历史执行结果 (未使用)
    ResponseVo<List<Map<String, String>>> getFailCaseHistoryResults();

    // pageinfo-table-list
    ResponseVo<PageInfo<TableVo>> list(TestcaseFilterVo testcaseFilterVo);

    // case-steps
    ResponseVo<List<ApiObject>> steps(Integer caseId);

}
