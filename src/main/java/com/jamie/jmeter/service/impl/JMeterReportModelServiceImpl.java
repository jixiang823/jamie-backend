package com.jamie.jmeter.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jamie.jmeter.constant.PageConst;
import com.jamie.jmeter.dao.ApiObjectMapper;
import com.jamie.jmeter.dao.DashboardMapper;
import com.jamie.jmeter.dao.TestcaseMapper;
import com.jamie.jmeter.enums.ResponseEnum;
import com.jamie.jmeter.model.JMeterReportModel;
import com.jamie.jmeter.model.TestCaseModel;
import com.jamie.jmeter.pojo.ApiObject;
import com.jamie.jmeter.pojo.Dashboard;
import com.jamie.jmeter.pojo.Testcase;
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
    private TestcaseMapper testcaseMapper;

    /**
     * JMeter测试结果入库
     * @param reportData 测试数据
     * @return 数据入库
     */
    @Override
    public ResponseVo<JMeterReportModel> add(String  reportData) {

        JMeterReportModel jMeterReportModel = GsonUtil.jsonToBean(reportData, JMeterReportModel.class);

        Dashboard dashboard = jMeterReportModel.getDashboard();
        // 生成批次号
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
        dashboard.setBatchNo(batchNo);
        dashboardMapper.insert(dashboard);

        // TODO 避免循环体里频繁插入数据
        List<TestCaseModel> testCaseModels = jMeterReportModel.getTestCaseModels();
        for (TestCaseModel testCaseModel : testCaseModels) {
            // 插入CaseInfo
            Testcase caseInfo = testCaseModel.getCaseInfo();
            caseInfo.setBatchNo(batchNo);
            testcaseMapper.insert(caseInfo);
            // 插入CaseSteps
            List<ApiObject> caseSteps = testCaseModel.getCaseSteps();
            Integer caseId = caseInfo.getId();
            for (ApiObject caseStep : caseSteps) {
                caseStep.setCaseId(caseId);
                caseStep.setBatchNo(batchNo);
                apiObjectMapper.insert(caseStep);
            }
        }

        return ResponseVo.success(jMeterReportModel);

    }

    // TODO
    private List<ApiObject> buildCaseSteps(String batchNo, List<Integer> caseIdList, List<ApiObject> apiObjectList) {
        ApiObject apiObject = new ApiObject();
        List<ApiObject> list = new ArrayList<>();
        for (int i = 0; i < caseIdList.size(); i++) {
            apiObject.setBatchNo(batchNo);
            apiObject.setCaseId(caseIdList.get(i));
            BeanUtils.copyProperties(apiObjectList.get(i), apiObject);
            list.add(apiObject);
        }
        return list;
    }

    /**
     * 获取看板数据,统计'新增失败'和'持续失败'数据.
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
     * @param testcaseFilterVo 查询条件
     * @return Testcase
     */
    @Override
    public ResponseVo<PageInfo<Testcase>> list(TestcaseFilterVo testcaseFilterVo) {

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
        List<Testcase> page = testcaseMapper.page(queryKeywords);
        PageInfo<Testcase> pageInfo = new PageInfo<>();
        pageInfo.setList(page);
        return ResponseVo.success(pageInfo);

    }

    /**
     * 通过用例ID查询CaseSteps
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
