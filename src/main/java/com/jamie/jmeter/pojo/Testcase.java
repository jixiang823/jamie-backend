package com.jamie.jmeter.pojo;

import lombok.Data;

@Data
public class Testcase {

    private Integer id;
    private String batchNo;
    private String caseName;
    private String moduleName;
    private String caseOwner;
    private Integer caseStepNum;
    private Boolean result;
    private Long caseStartTime;
    private Long caseEndTime;
    private Long caseDuration;

}