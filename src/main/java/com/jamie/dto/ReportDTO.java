package com.jamie.dto;

import com.jamie.pojo.TestSummary;
import lombok.Data;

import java.util.List;


@Data
public class ReportDTO {

    private TestSummary testSummary; // 概述
    private List<TestCaseDTO> testCaseDTOS; // 测试用例信息

}
