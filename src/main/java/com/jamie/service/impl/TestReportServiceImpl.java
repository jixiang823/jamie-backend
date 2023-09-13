package com.jamie.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jamie.constant.PageConst;
import com.jamie.dao.ApiInfoMapper;
import com.jamie.dao.TestCaseInfoMapper;
import com.jamie.dao.TestSummaryMapper;
import com.jamie.enums.ResponseEnum;
import com.jamie.pojo.ApiInfo;
import com.jamie.pojo.TestSummary;
import com.jamie.vo.TableVo;
import com.jamie.service.ITestReportService;
import com.jamie.vo.PanelGroupVo;
import com.jamie.vo.TestcaseFilterVo;
import com.jamie.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
@Transactional
public class TestReportServiceImpl implements ITestReportService {

    @Resource
    private ApiInfoMapper apiInfoMapper;
    @Resource
    private TestSummaryMapper testSummaryMapper;
    @Resource
    private TestCaseInfoMapper testcaseInfoMapper;

    /**
     * 获得TestSummary最新数据
     * @return TestSummary
     */
    public ResponseVo<TestSummary> getTestSummary() {
        TestSummary testSummary = testSummaryMapper.selectLatestSummary();
        if (testSummary == null) {
            return ResponseVo.error(ResponseEnum.ARGUMENT_NOT_EXIST);
        }
        return ResponseVo.success(testSummary);
    }

    /**
     * table list
     * @return 用例信息列表
     */
    @Override
    public ResponseVo<List<TableVo>> getLatestCaseList() {
        List<TableVo> tableList = testSummaryMapper.selectLatestCaseList();
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
        List<Map<String, Integer>> caseResultList = testSummaryMapper.selectCaseResultTrend();
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

        List<Map<String, Integer>> totalPassList = testSummaryMapper.selectCaseTotalPassTrend();
        if (totalPassList.isEmpty()) {
            return ResponseVo.error(ResponseEnum.ARGUMENT_NOT_EXIST);
        }
        panelGroupVo.setTotalPass(totalPassList);

        List<Map<String, Integer>> totalFailList = testSummaryMapper.selectCaseTotalFailTrend();
        if (totalFailList.isEmpty()) {
            return ResponseVo.error(ResponseEnum.ARGUMENT_NOT_EXIST);
        }
        panelGroupVo.setTotalFail(totalFailList);

        List<Map<String, Integer>> newlyFailList = testSummaryMapper.selectCaseNewlyFailTrend();
        if (newlyFailList.isEmpty()) {
            return ResponseVo.error(ResponseEnum.ARGUMENT_NOT_EXIST);
        }
        panelGroupVo.setNewlyFail(newlyFailList);

        List<Map<String, Integer>> keepFailingList = testSummaryMapper.selectCaseKeepFailingTrend();
        if (keepFailingList.isEmpty()) {
            return ResponseVo.error(ResponseEnum.ARGUMENT_NOT_EXIST);
        }
        panelGroupVo.setKeepFailing(keepFailingList);

        return ResponseVo.success(panelGroupVo);

    }

    @Override
    public ResponseVo<List<Map<String, String>>> getFailCaseHistoryResults() {
        String batchNo = testSummaryMapper.selectLatestSummary().getBatchNo();
        if (batchNo == null) {
            return ResponseVo.error(ResponseEnum.ARGUMENT_NOT_EXIST);
        }
        return ResponseVo.success(testSummaryMapper.selectFailCaseHistoryResults(batchNo));
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
        List<TableVo> pageVoList = testcaseInfoMapper.page(queryKeywords);
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
    public ResponseVo<List<ApiInfo>> steps(Integer caseId) {
        List<ApiInfo> apiInfos = apiInfoMapper.selectByCaseId(caseId);
        if (apiInfos.isEmpty()) {
            return ResponseVo.error(ResponseEnum.ARGUMENT_NOT_EXIST);
        }
        return ResponseVo.success(apiInfos);
    }

}
