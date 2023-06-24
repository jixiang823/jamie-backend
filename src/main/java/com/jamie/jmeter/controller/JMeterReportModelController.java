package com.jamie.jmeter.controller;

import com.github.pagehelper.PageInfo;
import com.jamie.jmeter.model.JMeterReportModel;
import com.jamie.jmeter.pojo.ApiObject;
import com.jamie.jmeter.pojo.Dashboard;
import com.jamie.jmeter.pojo.Testcase;
import com.jamie.jmeter.service.IJMeterReportModelService;
import com.jamie.jmeter.vo.ResponseVo;
import com.jamie.jmeter.vo.TestcaseFilterVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
public class JMeterReportModelController {

    @Resource
    private IJMeterReportModelService jMeterReportModelService;

    // 存储JMeter测试结果
    @PostMapping("/jmeter/report/save")
    public ResponseVo<JMeterReportModel> add(@RequestBody String reportData) {
        return jMeterReportModelService.add(reportData);
    }

    // 获取看板最新数据
    @GetMapping("/jmeter/report/dashboard")
    public ResponseVo<Dashboard> getDashboard() {
        return jMeterReportModelService.getDashboard();
    }

    // 获取最新执行失败用例的历史测试结果
    @GetMapping("/jmeter/report/dashboard/fail/results")
    public ResponseVo<List<Map<String, String>>> getFailCaseHistoryResults() {
        return jMeterReportModelService.getFailCaseHistoryResults();
    }

    @PostMapping("/jmeter/report/testcase/list")
    public ResponseVo<PageInfo<Testcase>> list(@RequestBody TestcaseFilterVo testcaseFilterVo) {
        return jMeterReportModelService.list(testcaseFilterVo);
    }

    // 获取用例步骤
    @GetMapping("/jmeter/report/testcase/{caseId}")
    public ResponseVo<List<ApiObject>> getStepsById(@PathVariable Integer caseId) {
        return jMeterReportModelService.steps(caseId);
    }

}
