package com.jamie.model;

import com.jamie.pojo.TestSummary;
import lombok.Data;

import java.util.List;


@Data
public class JMeterReportModel {

    private TestSummary testSummary; // 概述
    private List<TestCaseModel> testCaseModels; // 测试用例信息

}
