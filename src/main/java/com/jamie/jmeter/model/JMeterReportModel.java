package com.jamie.jmeter.model;

import com.jamie.jmeter.form.DashboardForm;
import lombok.Data;

import java.util.List;


@Data
public class JMeterReportModel {

    private DashboardForm dashboard; // 看板信息
    private List<TestCaseModel> testCaseModels; // 测试用例信息

}
