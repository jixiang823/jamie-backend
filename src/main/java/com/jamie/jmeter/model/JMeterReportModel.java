package com.jamie.jmeter.model;

import com.jamie.jmeter.pojo.Dashboard;
import lombok.Data;

import java.util.List;


@Data
public class JMeterReportModel {

    private Dashboard dashboard; // 看板信息
    private List<TestCaseModel> testCaseModels; // 测试用例信息

}
