package com.jamie.pojo;

import lombok.Data;

@Data
public class TestCaseInfo {

    private Integer id;
    private String batchNo;
    private String storyName;
    private String caseName;
    private String caseOwner;
    private Integer caseStepNum;
    private Boolean caseResult;
    private Boolean newlyFail;
    private Boolean keepFailing;
    private Long startTime;
    private Long endTime;
    private Long duration;

}