package com.jamie.jmeter.pojo;

import lombok.Data;

@Data
public class TestCase {

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