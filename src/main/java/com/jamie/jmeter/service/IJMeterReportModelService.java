package com.jamie.jmeter.service;

import com.github.pagehelper.PageInfo;
import com.jamie.jmeter.model.JMeterReportModel;
import com.jamie.jmeter.pojo.ApiObject;
import com.jamie.jmeter.pojo.Dashboard;
import com.jamie.jmeter.pojo.Testcase;
import com.jamie.jmeter.vo.TestcaseFilterVo;
import com.jamie.jmeter.vo.ResponseVo;

import java.util.List;
import java.util.Map;

public interface IJMeterReportModelService {

    // JMeter测试结果入库
    ResponseVo<JMeterReportModel> add(String reportData);
    // 获取看板页数据
    ResponseVo<Dashboard> getDashboard();
    // 用于统计执行失败的测试用例的历史执行结果
    ResponseVo<List<Map<String, String>>> getFailCaseHistoryResults();
    // 获取用例列表数据
    ResponseVo<PageInfo<Testcase>> list(TestcaseFilterVo testcaseFilterVo);
    // 获取用例详情数据
    ResponseVo<List<ApiObject>> steps(Integer caseId);

}
