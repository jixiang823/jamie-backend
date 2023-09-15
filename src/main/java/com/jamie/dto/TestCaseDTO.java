package com.jamie.dto;


import com.jamie.pojo.ApiInfo;
import com.jamie.pojo.TestCaseInfo;
import lombok.Data;

import java.util.List;

@Data
public class TestCaseDTO {

    private TestCaseInfo caseInfo; // 测试用例信息
    private List<ApiInfo> caseSteps; // 测试用例内的接口信息

}
