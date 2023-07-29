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
     * JMeter数据入库
     *
     * @param reportData 测试数据
     */
    @Override
    public void save(String reportData) {

        JMeterReportModel jMeterReportModel = GsonUtil.jsonToBean(reportData, JMeterReportModel.class);
        String batchNo = generateBatchNo();
        // case信息
        List<TestCaseModel> testCaseModels = jMeterReportModel.getTestCaseModels();
        // caseInfo
        List<TestCase> caseInfoList = buildCaseInfoList(testCaseModels, batchNo);
        testcaseMapper.batchInsert(caseInfoList);
        // caseSteps
        List<List<ApiObject>> caseStepsList = buildCaseStepsList(testCaseModels, batchNo);
        apiObjectMapper.batchInsert(caseStepsList);
        // dashboard信息 (newlyFail和keepFailing依赖于caseSteps,所以放最后)
        Dashboard dashboard = jMeterReportModel.getDashboard();
        dashboard.setBatchNo(batchNo);
        dashboardMapper.insert(dashboard);
        // 更新Dashboard`新增失败`和`持续失败`的个数. 标记当前用例是否是`新增失败`或`持续失败`
        updateData(dashboard, batchNo);

    }

    /**
     * 处理newlyFail和keepFailing
     *
     */
    private void updateData(Dashboard dashboard, String batchNo) {

        // 记录当前批次下newlyFail为true的caseName
        List<String> newlyFailCaseNames = new ArrayList<>();
        // 记录当前批次下keepFailing为true的caseName
        List<String> keepFailingCaseNames = new ArrayList<>();
        // tb_dashboard表newly_fail_num字段计数
        Integer newlyFailNum = dashboard.getNewlyFailNum();
        // tb_dashboard表keep_failing_num字段计数
        Integer keepFailingNum = dashboard.getKeepFailingNum();
        // 获取当前批次失败用例的历史状态
        List<Map<String, String>> results = dashboardMapper.selectFailCaseHistoryResults(batchNo);
        for (Map<String, String> result : results) {
            String caseName = result.get("caseName").trim(); // 去除字符串末尾的空格
            String[] historyResults = result.get("historyResults").split(",");
            if (historyResults.length == 1 && historyResults[0].equals("0")) {
                // newFail加1,keepFailing保持默认值0不变
                newlyFailNum += 1;
                newlyFailCaseNames.add(caseName);
                dashboard.setNewlyFailNum(newlyFailNum);
            }
            if (historyResults.length >=2 ) {
                if (historyResults[0].equals("0") && historyResults[1].equals("1")) {
                    // 记录newlyFail的个数
                    newlyFailNum += 1;
                    // 记录newlyFail的用例名
                    newlyFailCaseNames.add(caseName);
                    dashboard.setNewlyFailNum(newlyFailNum);
                }
                if (historyResults[0].equals("0") && historyResults[1].equals("0")) {
                    // 记录keepFailing的个数
                    keepFailingNum += 1;
                    // 记录keepFailing的用例名
                    keepFailingCaseNames.add(caseName);
                    dashboard.setKeepFailingNum(keepFailingNum);
                }
            }
        }
        // tb_dashboard更新newly_fail_num和keep_failing_num
        Integer id = dashboardMapper.selectLatest().getId();
        dashboard.setId(id);
        dashboardMapper.updateByPrimaryKeySelective(dashboard);

        // tb_testcase更新newly_fail和Keep_failing
        List<TableVo> tableVos = testcaseMapper.selectLatest();
        List<TestCase> testCaseList = new ArrayList<>();
        for (TableVo tableVo : tableVos) {
            TestCase testCase = new TestCase();
            if (newlyFailCaseNames.contains(tableVo.getCaseName())) {
                testCase.setId(tableVo.getId());
                testCase.setNewlyFail(true);
                testCase.setKeepFailing(false);
            }
            if (keepFailingCaseNames.contains(tableVo.getCaseName())) {
                testCase.setId(tableVo.getId());
                testCase.setNewlyFail(false);
                testCase.setKeepFailing(true);
            }
            testCaseList.add(testCase);
        }
        // tb_testcase批量更新
        testcaseMapper.updateBatch(testCaseList);

    }

    /**
     * 生成批次号,格式20230627001
     * @return BatchNo
     */
    private String generateBatchNo() {
        String batchNo;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String prefix = simpleDateFormat.format(new Date());
        String minBatchNo = prefix + "001";
        String maxBatchNo = dashboardMapper.selectMaxBatchNo();
        if (maxBatchNo == null) {
            batchNo = minBatchNo;
        } else {
            batchNo = String.valueOf(Long.parseLong(maxBatchNo) + 1);
        }
        return batchNo;
    }

    /**
     * table list
     *
     * @param testCaseModels 用例信息
     * @param batchNo        批次号
     * @return caseInfoList
     */
    private List<TestCase> buildCaseInfoList(List<TestCaseModel> testCaseModels, String batchNo) {
        List<TestCase> caseInfoList = new ArrayList<>();
        for (TestCaseModel testcaseModel : testCaseModels) {
            TestCase caseInfo = testcaseModel.getCaseInfo();
            caseInfo.setBatchNo(batchNo);
            caseInfoList.add(caseInfo);
        }
        return caseInfoList;
    }

    /**
     * 构造CaseSteps
     *
     * @param testCaseModels 未对caseId和batchNo赋值的ApiObject集合
     * @param batchNo        本次执行的批次号
     * @return 已对caseId和batchNo赋值的ApiObject集合
     */
    private List<List<ApiObject>> buildCaseStepsList(List<TestCaseModel> testCaseModels, String batchNo) {

        List<List<ApiObject>> apiObjectsList = new ArrayList<>();
        for (TestCaseModel testcaseModel : testCaseModels) {
            List<ApiObject> apiObjects = testcaseModel.getCaseSteps();
            apiObjectsList.add(apiObjects);
        }
        // 根据本次执行的批次号拿到caseId集合
        List<Integer> caseIdList = testcaseMapper.selectIdByBatchNo(batchNo);
        // 把caseId赋值给ApiObject
        int i = 0;
        List<List<ApiObject>> caseStepsList = new ArrayList<>();
        for (List<ApiObject> apiObjects : apiObjectsList) {
            List<ApiObject> caseStepList = new ArrayList<>();
            for (ApiObject apiObject : apiObjects) {
                ApiObject caseStep = new ApiObject();
                BeanUtils.copyProperties(apiObject, caseStep);
                caseStep.setCaseId(caseIdList.get(i));
                caseStep.setBatchNo(batchNo);
                caseStepList.add(caseStep);
            }
            caseStepsList.add(caseStepList);
            i += 1;
        }
        return caseStepsList;

    }

    /**
     * 获得dashboard最新数据
     * @return dashboard
     */
    public ResponseVo<Dashboard> getDashboard() {
        Dashboard dashboard = dashboardMapper.selectLatest();
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
    public ResponseVo<List<TableVo>> latestList() {
        List<TableVo> tableList = testcaseMapper.selectLatest();
        if (tableList.size() == 0) {
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
        if (caseResultList.size() == 0) {
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
        if (totalPassList.size() == 0) {
            return ResponseVo.error(ResponseEnum.ARGUMENT_NOT_EXIST);
        }
        panelGroupVo.setTotalPass(totalPassList);

        List<Map<String, Integer>> totalFailList = dashboardMapper.selectCaseTotalFailTrend();
        if (totalFailList.size() == 0) {
            return ResponseVo.error(ResponseEnum.ARGUMENT_NOT_EXIST);
        }
        panelGroupVo.setTotalFail(totalFailList);

        List<Map<String, Integer>> newlyFailList = dashboardMapper.selectCaseNewlyFailTrend();
        if (newlyFailList.size() == 0) {
            return ResponseVo.error(ResponseEnum.ARGUMENT_NOT_EXIST);
        }
        panelGroupVo.setNewlyFail(newlyFailList);

        List<Map<String, Integer>> keepFailingList = dashboardMapper.selectCaseKeepFailingTrend();
        if (keepFailingList.size() == 0) {
            return ResponseVo.error(ResponseEnum.ARGUMENT_NOT_EXIST);
        }
        panelGroupVo.setKeepFailing(keepFailingList);

        return ResponseVo.success(panelGroupVo);

    }

    @Override
    public ResponseVo<List<Map<String, String>>> getFailCaseHistoryResults() {
        String batchNo = dashboardMapper.selectLatest().getBatchNo();
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
        queryKeywords.put("storyName", testcaseFilterVo.getStoryName());
        queryKeywords.put("caseName", testcaseFilterVo.getCaseName());
        queryKeywords.put("caseOwner", testcaseFilterVo.getCaseOwner());
        queryKeywords.put("caseResult", testcaseFilterVo.getCaseResult());
        queryKeywords.put("sort", testcaseFilterVo.getSort());
        List<TableVo> page = testcaseMapper.page(queryKeywords);
        PageInfo<TableVo> pageInfo = new PageInfo<>();
        pageInfo.setList(page);
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
        if (apiObjects.size() == 0) {
            return ResponseVo.error(ResponseEnum.ARGUMENT_NOT_EXIST);
        }
        return ResponseVo.success(apiObjects);
    }

}
