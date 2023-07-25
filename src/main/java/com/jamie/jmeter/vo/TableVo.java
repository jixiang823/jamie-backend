package com.jamie.jmeter.vo;

import lombok.Data;

// dashboard table list
@Data
public class TableVo {

    // tb_dashboard
    private String batchNo;
    private String projectName;
    private String buildEnv;
    // tb_testcase
    private Integer id;
    private String caseName;
    private String moduleName;
    private String caseOwner;
    private Integer caseStepNum;
    private Boolean caseResult;
    private Boolean newlyFail;
    private Boolean keepFailing;
    private Long caseStartTime;
    private Long caseDuration;

}