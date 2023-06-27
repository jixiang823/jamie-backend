package com.jamie.jmeter.model;


import com.jamie.jmeter.pojo.ApiObject;
import com.jamie.jmeter.pojo.TestCase;
import lombok.Data;

import java.util.List;

@Data
public class TestCaseModel {

    private TestCase caseInfo; // 测试用例信息
    private List<ApiObject> caseSteps; // 测试用例内的接口信息

}
