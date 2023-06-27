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
import com.jamie.jmeter.pojo.TestCase;
import com.jamie.jmeter.service.IJMeterReportModelService;
import com.jamie.jmeter.utils.GsonUtil;
import com.jamie.jmeter.vo.TestcaseFilterVo;
import com.jamie.jmeter.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class JMeterReportModelServiceImpl implements IJMeterReportModelService {

    @Resource
    private ApiObjectMapper apiObjectMapper;
    @Resource
    private DashboardMapper dashboardMapper;
    @Resource
    private TestCaseMapper testcaseMapper;

    /**
     * 解析JMeter测试结果并入库
     *
     * @param reportData 测试数据
     * @return 数据入库
     */
    @Override
    public ResponseVo<JMeterReportModel> add(String reportData) {

        JMeterReportModel jMeterReportModel = GsonUtil.jsonToBean(reportData, JMeterReportModel.class);
        // dashboard信息
        Dashboard dashboard = jMeterReportModel.getDashboard();
        String batchNo = generateBatchNo();
        dashboard.setBatchNo(batchNo);
        int rowForDashboard = dashboardMapper.insert(dashboard);
        if (rowForDashboard <= 0) {
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        // case信息
        List<TestCaseModel> testCaseModels = jMeterReportModel.getTestCaseModels();
        // caseInfo
        List<TestCase> caseInfoList = buildCaseInfoList(testCaseModels, batchNo);
        int rowForTestCase = testcaseMapper.batchInsert(caseInfoList);
        if (rowForTestCase <= 0) {
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        // caseSteps
        List<List<ApiObject>> caseStepsList = buildCaseStepsList(testCaseModels, batchNo);
        int rowForApiObject = apiObjectMapper.batchInsert(caseStepsList);
        if (rowForApiObject <= 0) {
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        return ResponseVo.success(jMeterReportModel);

    }

    /**
     * 生成BatchNo,格式如20230627001
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
     * 构造CaseInfoList
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
     * 获取看板数据,统计'新增失败'和'持续失败'数据.
     *
     * @return 看板数据
     */
    @Override
    public ResponseVo<Dashboard> getDashboard() {

        Dashboard dashboard = dashboardMapper.selectLatest();
        Integer newlyFailNum = dashboard.getNewlyFailNum();
        Integer keepFailingNum = dashboard.getKeepFailingNum();
        String batchNo = dashboard.getBatchNo();
        List<Map<String, String>> results = dashboardMapper.selectFailCaseHistoryResults(batchNo);
        for (Map<String, String> result : results) {
            String[] historyResults = result.get("historyResults").split(",");
            if (historyResults[0].equals("0") && historyResults[1].equals("1")) {
                newlyFailNum += 1;
                dashboard.setNewlyFailNum(newlyFailNum);
            }
            if (historyResults[0].equals("0") && historyResults[1].equals("0")) {
                keepFailingNum += 1;
                dashboard.setKeepFailingNum(keepFailingNum);
            }
        }
        return ResponseVo.success(dashboard);

    }

    @Override
    public ResponseVo<List<Map<String, String>>> getFailCaseHistoryResults() {
        String batchNo = dashboardMapper.selectLatest().getBatchNo();
        return ResponseVo.success(dashboardMapper.selectFailCaseHistoryResults(batchNo));
    }

    /**
     * 分页查询
     *
     * @param testcaseFilterVo 查询条件
     * @return Testcase
     */
    @Override
    public ResponseVo<PageInfo<TestCase>> list(TestcaseFilterVo testcaseFilterVo) {

        PageHelper.startPage(testcaseFilterVo.getPageNum(), testcaseFilterVo.getPageSize());
        if (testcaseFilterVo.getPageNum() == null || testcaseFilterVo.getPageSize() == null) {
            testcaseFilterVo.setPageNum(PageConst.PAGE_NUM);
            testcaseFilterVo.setPageSize(PageConst.PAGE_SIZE);
        }
        Map<String, Object> queryKeywords = new HashMap<>();
        queryKeywords.put("batchNo", testcaseFilterVo.getBatchNo());
        queryKeywords.put("caseName", testcaseFilterVo.getCaseName());
        queryKeywords.put("moduleName", testcaseFilterVo.getModuleName());
        queryKeywords.put("caseOwner", testcaseFilterVo.getCaseOwner());
        queryKeywords.put("caseResult", testcaseFilterVo.getCaseResult());
        queryKeywords.put("sort", testcaseFilterVo.getSort());
        List<TestCase> page = testcaseMapper.page(queryKeywords);
        PageInfo<TestCase> pageInfo = new PageInfo<>();
        pageInfo.setList(page);
        return ResponseVo.success(pageInfo);

    }

    /**
     * 通过用例ID查询CaseSteps
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
