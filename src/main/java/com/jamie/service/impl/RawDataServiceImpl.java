package com.jamie.service.impl;

import com.jamie.dao.ApiInfoMapper;
import com.jamie.dao.TestCaseInfoMapper;
import com.jamie.dao.TestSummaryMapper;
import com.jamie.model.JMeterReportModel;
import com.jamie.model.TestCaseModel;
import com.jamie.pojo.ApiInfo;
import com.jamie.pojo.TestSummary;
import com.jamie.pojo.TestCaseInfo;
import com.jamie.service.IRawDataService;
import com.jamie.utils.GsonUtil;
import com.jamie.vo.TableVo;
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
    private ApiInfoMapper apiInfoMapper;
    @Resource
    private TestSummaryMapper testSummaryMapper;
    @Resource
    private TestCaseInfoMapper testcaseInfoMapper;

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
        List<TestCaseInfo> caseInfoList = buildCaseInfoList(testCaseModels, batchNo);
        testcaseInfoMapper.batchInsert(caseInfoList);
        // caseSteps
        List<List<ApiInfo>> caseStepsList = buildCaseStepsList(testCaseModels, batchNo);
        apiInfoMapper.batchInsert(caseStepsList);
        // tb_test_summary (newlyFail和keepFailing依赖于caseSteps,所以放最后)
        TestSummary testSummary = jMeterReportModel.getTestSummary();
        testSummary.setBatchNo(batchNo);
        testSummaryMapper.insert(testSummary);
        // 更新Dashboard`新增失败`和`持续失败`的个数. 标记当前用例是否是`新增失败`或`持续失败`
        updateData(testSummary, batchNo);

    }

    /**
     * 处理newlyFail和keepFailing
     *
     */
    private void updateData(TestSummary testSummary, String batchNo) {

        // 记录当前批次下newlyFail为true的caseName
        List<String> newlyFailCaseNames = new ArrayList<>();
        // 记录当前批次下keepFailing为true的caseName
        List<String> keepFailingCaseNames = new ArrayList<>();
        // tb_test_summary表newly_fail_num字段计数
        Integer newlyFailNum = testSummary.getNewlyFailNum();
        // tb_test_summary表keep_failing_num字段计数
        Integer keepFailingNum = testSummary.getKeepFailingNum();
        // 获取当前批次失败用例的历史状态
        List<Map<String, String>> results = testSummaryMapper.selectFailCaseHistoryResults(batchNo);
        for (Map<String, String> result : results) {
            String caseName = result.get("caseName").trim(); // 去除字符串末尾的空格
            String[] historyResults = result.get("historyResults").split(",");
            if (historyResults.length == 1 && historyResults[0].equals("0")) {
                // newFail加1,keepFailing保持默认值0不变
                newlyFailNum += 1;
                newlyFailCaseNames.add(caseName);
                testSummary.setNewlyFailNum(newlyFailNum);
            }
            if (historyResults.length >=2 ) {
                if (historyResults[0].equals("0") && historyResults[1].equals("1")) {
                    // 记录newlyFail的个数
                    newlyFailNum += 1;
                    // 记录newlyFail的用例名
                    newlyFailCaseNames.add(caseName);
                    testSummary.setNewlyFailNum(newlyFailNum);
                }
                if (historyResults[0].equals("0") && historyResults[1].equals("0")) {
                    // 记录keepFailing的个数
                    keepFailingNum += 1;
                    // 记录keepFailing的用例名
                    keepFailingCaseNames.add(caseName);
                    testSummary.setKeepFailingNum(keepFailingNum);
                }
            }
        }
        // tb_test_summary更新newly_fail_num和keep_failing_num
        Integer id = testSummaryMapper.selectLatestSummary().getId();
        testSummary.setId(id);
        testSummaryMapper.updateByPrimaryKeySelective(testSummary);

        // tb_testcase_info更新newly_fail和Keep_failing
        List<TableVo> tableVos = testSummaryMapper.selectLatestCaseList();
        List<TestCaseInfo> testCaseInfoList = new ArrayList<>();
        for (TableVo tableVo : tableVos) {
            TestCaseInfo testCaseInfo = new TestCaseInfo();
            if (newlyFailCaseNames.contains(tableVo.getCaseName())) {
                testCaseInfo.setId(tableVo.getId());
                testCaseInfo.setNewlyFail(true);
                testCaseInfo.setKeepFailing(false);
            }
            if (keepFailingCaseNames.contains(tableVo.getCaseName())) {
                testCaseInfo.setId(tableVo.getId());
                testCaseInfo.setNewlyFail(false);
                testCaseInfo.setKeepFailing(true);
            }
            testCaseInfoList.add(testCaseInfo);
        }
        // tb_testcase_info批量更新
        testcaseInfoMapper.updateBatch(testCaseInfoList);

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
        String maxBatchNo = testSummaryMapper.selectMaxBatchNo();
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
    private List<TestCaseInfo> buildCaseInfoList(List<TestCaseModel> testCaseModels, String batchNo) {
        List<TestCaseInfo> caseInfoList = new ArrayList<>();
        for (TestCaseModel testcaseModel : testCaseModels) {
            TestCaseInfo caseInfo = testcaseModel.getCaseInfo();
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
    private List<List<ApiInfo>> buildCaseStepsList(List<TestCaseModel> testCaseModels, String batchNo) {

        List<List<ApiInfo>> apiInfoList = new ArrayList<>();
        for (TestCaseModel testcaseModel : testCaseModels) {
            List<ApiInfo> apiInfos = testcaseModel.getCaseSteps();
            apiInfoList.add(apiInfos);
        }
        // 根据本次执行的批次号拿到caseId集合
        List<Integer> caseIdList = testcaseInfoMapper.selectIdByBatchNo(batchNo);
        // 把caseId赋值给ApiInfo
        int i = 0;
        List<List<ApiInfo>> caseStepsList = new ArrayList<>();
        for (List<ApiInfo> apiInfos : apiInfoList) {
            List<ApiInfo> caseStepList = new ArrayList<>();
            for (ApiInfo apiInfo : apiInfos) {
                ApiInfo caseStep = new ApiInfo();
                BeanUtils.copyProperties(apiInfo, caseStep);
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
