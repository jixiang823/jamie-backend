package com.jamie.jmeter.pojo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Dashboard {

    private Integer id;
    private String batchNo;
    private String featureName;
    private String buildEnv;
    private Integer buildType;
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