package com.jamie.jmeter.pojo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Dashboard {

    private Integer id;
    private String batchNo;
    private String projectName;
    private String env;
    private Integer type;
    private Integer caseNum;
    private Integer casePassNum;
    private Integer caseFailNum;
    private BigDecimal casePassRate;
    private Integer newlyFailNum;
    private Integer keepFailingNum;
    private Long projectStartTime;
    private Long projectEndTime;
    private Long projectDuration;

}