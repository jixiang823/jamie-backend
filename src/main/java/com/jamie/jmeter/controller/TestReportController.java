package com.jamie.jmeter.controller;

import com.github.pagehelper.PageInfo;
import com.jamie.jmeter.pojo.ApiObject;
import com.jamie.jmeter.pojo.Dashboard;
import com.jamie.jmeter.vo.TableVo;
import com.jamie.jmeter.service.ITestReportService;
import com.jamie.jmeter.vo.PanelGroupVo;
import com.jamie.jmeter.vo.ResponseVo;
import com.jamie.jmeter.vo.TestcaseFilterVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
public class TestReportController {

    @Resource
    private ITestReportService testReportService;

    // 存储JMeter测试结果,由backend-listener调用. (TODO 之后单独弄一个controller)
    @PostMapping("/jmeter/report/save")
    public void save(@RequestBody String reportData) {
        testReportService.save(reportData);
    }

    // 获取看板最新数据 (pie)
    @GetMapping("/jmeter/report/info")
    public ResponseVo<Dashboard> getDashboard() {
        return testReportService.getDashboard();
    }

//    @GetMapping("/jmeter/report/info/update")
//    public ResponseVo<Dashboard> updateDashboard() {
//        return jMeterReportModelService.updateDashboard();
//    }

    // line-chart
    @GetMapping("/jmeter/report/trend/case-result")
    public ResponseVo<List<Map<String,Integer>>> getCaseResultTrend() {
        return testReportService.getCaseResultTrend();
    }

    // panel-group total-pass
    @GetMapping("/jmeter/report/trend/panel-group")
    public ResponseVo<PanelGroupVo> getPanelGroup() {
        return testReportService.getPanelGroup();
    }

    // 当前批次用例表单 (table-list)
    @GetMapping("/jmeter/report/list/latest")
    public ResponseVo<List<TableVo>> getLatestCaseList() {
        return testReportService.getLatestCaseList();
    }

    // 获取用例步骤 (table-timeline)
    @GetMapping("/jmeter/report/detail/{caseId}")
    public ResponseVo<List<ApiObject>> steps(@PathVariable Integer caseId) {
        return testReportService.steps(caseId);
    }

    // 获取最新执行失败用例的历史测试结果 (未使用)
    @GetMapping("/jmeter/report/dashboard/fail/results")
    public ResponseVo<List<Map<String, String>>> getFailCaseHistoryResults() {
        return testReportService.getFailCaseHistoryResults();
    }

    // table-list 分页多查询条件
    @PostMapping("/jmeter/report/list/total")
    public ResponseVo<PageInfo<TableVo>> list(@RequestBody TestcaseFilterVo testcaseFilterVo) {
        return testReportService.list(testcaseFilterVo);
    }

}
