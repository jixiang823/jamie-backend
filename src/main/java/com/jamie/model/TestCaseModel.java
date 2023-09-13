package com.jamie.model;


import com.jamie.pojo.ApiInfo;
import com.jamie.pojo.TestCaseInfo;
import lombok.Data;

import java.util.List;

@Data
public class TestCaseModel {

    private TestCaseInfo caseInfo; // 测试用例信息
    private List<ApiInfo> caseSteps; // 测试用例内的接口信息

}
