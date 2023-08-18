package com.jamie.jmeter.service.impl;

import com.jamie.jmeter.dao.ApiObjectMapper;
import com.jamie.jmeter.dao.DashboardMapper;
import com.jamie.jmeter.dao.TestCaseMapper;
import com.jamie.jmeter.model.JMeterReportModel;
import com.jamie.jmeter.model.TestCaseModel;
import com.jamie.jmeter.pojo.ApiObject;
import com.jamie.jmeter.pojo.Dashboard;
import com.jamie.jmeter.pojo.TestCase;
import com.jamie.jmeter.service.IRawDataService;
import com.jamie.jmeter.utils.GsonUtil;
import com.jamie.jmeter.vo.TableVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RawDataServiceImpl implements IRawDataService {

    @Resource
    private ApiObjectMapper apiObjectMapper;
    @Resource
    private DashboardMapper dashboardMapper;
    @Resource
    private TestCaseMapper testcaseMapper;

    /**
     * JMeter数据入库
     *
     * @param rawData 测试数据
     */
    @Override
    public void save(String rawData) {

        JMeterReportModel jMeterReportModel = GsonUtil.jsonToBean(rawData, JMeterReportModel.class);
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
        Integer id = dashboardMapper.selectLatestSummary().getId();
        dashboard.setId(id);
        dashboardMapper.updateByPrimaryKeySelective(dashboard);

        // tb_testcase更新newly_fail和Keep_failing
        List<TableVo> tableVos = dashboardMapper.selectLatestCaseList();
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
}
