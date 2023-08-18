package com.jamie.jmeter.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jamie.jmeter.constant.PageConst;
import com.jamie.jmeter.dao.ApiObjectMapper;
import com.jamie.jmeter.dao.DashboardMapper;
import com.jamie.jmeter.dao.TestCaseMapper;
import com.jamie.jmeter.enums.ResponseEnum;
import com.jamie.jmeter.model.JMeterReportModel;
import com.jamie.jmeter.model.TestCaseModel;
import com.jamie.jmeter.pojo.ApiObject;
import com.jamie.jmeter.pojo.Dashboard;
import com.jamie.jmeter.vo.TableVo;
import com.jamie.jmeter.pojo.TestCase;
import com.jamie.jmeter.service.ITestReportService;
import com.jamie.jmeter.utils.GsonUtil;
import com.jamie.jmeter.vo.PanelGroupVo;
import com.jamie.jmeter.vo.TestcaseFilterVo;
import com.jamie.jmeter.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@Transactional
public class TestReportServiceImpl implements ITestReportService {

    @Resource
    private ApiObjectMapper apiObjectMapper;
    @Resource
    private DashboardMapper dashboardMapper;
    @Resource
    private TestCaseMapper testcaseMapper;

    /**
     * 获得dashboard最新数据
     * @return dashboard
     */
    public ResponseVo<Dashboard> getDashboard() {
        Dashboard dashboard = dashboardMapper.selectLatestSummary();
        if (dashboard == null) {
            return ResponseVo.error(ResponseEnum.ARGUMENT_NOT_EXIST);
        }
        return ResponseVo.success(dashboard);
    }

    /**
     * table list
     * @return 用例信息列表
     */
    @Override
    public ResponseVo<List<TableVo>> getLatestCaseList() {
        List<TableVo> tableList = dashboardMapper.selectLatestCaseList();
        if (tableList.isEmpty()) {
            return ResponseVo.error(ResponseEnum.ARGUMENT_NOT_EXIST);
        }
        return ResponseVo.success(tableList);
    }

    /**
     * line-chart
     * @return 用例结果的趋势(最近7次)
     */
    @Override
    public ResponseVo<List<Map<String, Integer>>> getCaseResultTrend() {
        List<Map<String, Integer>> caseResultList = dashboardMapper.selectCaseResultTrend();
        if (caseResultList.isEmpty()) {
            return ResponseVo.error(ResponseEnum.ARGUMENT_NOT_EXIST);
        }
        return ResponseVo.success(caseResultList);
    }

    /**
     * panel-group
     * @return totalPass,totalFail,NewlyFail,KeepFailing
     */
    @Override
    public ResponseVo<PanelGroupVo> getPanelGroup() {

        PanelGroupVo panelGroupVo = new PanelGroupVo();

        List<Map<String, Integer>> totalPassList = dashboardMapper.selectCaseTotalPassTrend();
        if (totalPassList.isEmpty()) {
            return ResponseVo.error(ResponseEnum.ARGUMENT_NOT_EXIST);
        }
        panelGroupVo.setTotalPass(totalPassList);

        List<Map<String, Integer>> totalFailList = dashboardMapper.selectCaseTotalFailTrend();
        if (totalFailList.isEmpty()) {
            return ResponseVo.error(ResponseEnum.ARGUMENT_NOT_EXIST);
        }
        panelGroupVo.setTotalFail(totalFailList);

        List<Map<String, Integer>> newlyFailList = dashboardMapper.selectCaseNewlyFailTrend();
        if (newlyFailList.isEmpty()) {
            return ResponseVo.error(ResponseEnum.ARGUMENT_NOT_EXIST);
        }
        panelGroupVo.setNewlyFail(newlyFailList);

        List<Map<String, Integer>> keepFailingList = dashboardMapper.selectCaseKeepFailingTrend();
        if (keepFailingList.isEmpty()) {
            return ResponseVo.error(ResponseEnum.ARGUMENT_NOT_EXIST);
        }
        panelGroupVo.setKeepFailing(keepFailingList);

        return ResponseVo.success(panelGroupVo);

    }

    @Override
    public ResponseVo<List<Map<String, String>>> getFailCaseHistoryResults() {
        String batchNo = dashboardMapper.selectLatestSummary().getBatchNo();
        if (batchNo == null) {
            return ResponseVo.error(ResponseEnum.ARGUMENT_NOT_EXIST);
        }
        return ResponseVo.success(dashboardMapper.selectFailCaseHistoryResults(batchNo));
    }

    /**
     * page-info list
     *
     * @param testcaseFilterVo 查询条件
     * @return Testcase
     */
    @Override
    public ResponseVo<PageInfo<TableVo>> list(TestcaseFilterVo testcaseFilterVo) {

        PageHelper.startPage(testcaseFilterVo.getPageNum(), testcaseFilterVo.getPageSize());
        if (testcaseFilterVo.getPageNum() == null || testcaseFilterVo.getPageSize() == null) {
            testcaseFilterVo.setPageNum(PageConst.PAGE_NUM);
            testcaseFilterVo.setPageSize(PageConst.PAGE_SIZE);
        }
        Map<String, Object> queryKeywords = new HashMap<>();
        queryKeywords.put("batchNo", testcaseFilterVo.getBatchNo());
        queryKeywords.put("featureName", testcaseFilterVo.getFeatureName());
        queryKeywords.put("storyName", testcaseFilterVo.getStoryName());
        queryKeywords.put("caseName", testcaseFilterVo.getCaseName());
        queryKeywords.put("caseOwner", testcaseFilterVo.getCaseOwner());
        queryKeywords.put("caseResult", testcaseFilterVo.getCaseResult());
        queryKeywords.put("sort", testcaseFilterVo.getSort());
        List<TableVo> pageVoList = testcaseMapper.page(queryKeywords);
        PageInfo<TableVo> pageInfo = new PageInfo<>(pageVoList);
        pageInfo.setList(pageVoList);
        return ResponseVo.success(pageInfo);

    }

    /**
     * case steps
     *
     * @param caseId 用例ID
     * @return CaseSteps
     */
    @Override
    public ResponseVo<List<ApiObject>> steps(Integer caseId) {
        List<ApiObject> apiObjects = apiObjectMapper.selectByCaseId(caseId);
        if (apiObjects.isEmpty()) {
            return ResponseVo.error(ResponseEnum.ARGUMENT_NOT_EXIST);
        }
        return ResponseVo.success(apiObjects);
    }

}
