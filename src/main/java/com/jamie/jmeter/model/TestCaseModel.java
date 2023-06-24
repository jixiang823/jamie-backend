package com.jamie.jmeter.model;


import com.jamie.jmeter.form.ApiObjectForm;
import com.jamie.jmeter.form.TestCaseForm;
import lombok.Data;

import java.util.List;

@Data
public class TestCaseModel {

    private TestCaseForm caseInfo; // 测试用例信息
    private List<ApiObjectForm> caseSteps; // 测试用例内的接口信息

}
