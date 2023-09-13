package com.jamie.controller;

import com.github.pagehelper.PageInfo;
import com.jamie.pojo.ApiInfo;
import com.jamie.pojo.TestSummary;
import com.jamie.vo.TableVo;
import com.jamie.service.ITestReportService;
import com.jamie.vo.PanelGroupVo;
import com.jamie.vo.ResponseVo;
import com.jamie.vo.TestcaseFilterVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
public class TestReportController {

    @Resource
    private ITestReportService testReportService;

    // 获取看板最新数据 (pie)
    @GetMapping("/report/info")
    public ResponseVo<TestSummary> getDashboard() {
        return testReportService.getTestSummary();
    }

    // line-chart
    @GetMapping("/report/trend/case-result")
    public ResponseVo<List<Map<String,Integer>>> getCaseResultTrend() {
        return testReportService.getCaseResultTrend();
    }

    // panel-group total-pass
    @GetMapping("/report/trend/panel-group")
    public ResponseVo<PanelGroupVo> getPanelGroup() {
        return testReportService.getPanelGroup();
    }

    // 当前批次用例表单 (table-list)
    @GetMapping("/report/list/latest")
    public ResponseVo<List<TableVo>> getLatestCaseList() {
        return testReportService.getLatestCaseList();
    }

    // 获取用例步骤 (table-timeline)
    @GetMapping("/report/detail/{caseId}")
    public ResponseVo<List<ApiInfo>> steps(@PathVariable Integer caseId) {
        return testReportService.steps(caseId);
    }

    // 获取最新执行失败用例的历史测试结果 (未使用)
    @GetMapping("/report/dashboard/fail/results")
    public ResponseVo<List<Map<String, String>>> getFailCaseHistoryResults() {
        return testReportService.getFailCaseHistoryResults();
    }

    // table-list 分页多查询条件
    @PostMapping("/report/list/total")
    public ResponseVo<PageInfo<TableVo>> list(@RequestBody TestcaseFilterVo testcaseFilterVo) {
        return testReportService.list(testcaseFilterVo);
    }

}
