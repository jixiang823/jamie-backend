package com.jamie.pojo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TestSummary {

    private Integer id;
    private String batchNo;
    private String featureName;
    private String buildEnv;
    private Integer caseNum;
    private Integer casePassNum;
    private Integer caseFailNum;
    private BigDecimal casePassRate;
    private Integer newlyFailNum;
    private Integer keepFailingNum;
    private Long startTime;
    private Long endTime;
    private Long duration;

}