package com.jamie.vo;

import lombok.Data;

// tb_test_summary table list
@Data
public class TableVo {

    // tb_test_summary
    private String batchNo;
    private String featureName;
    private String buildEnv;
    // tb_testcase_info
    private Integer id;
    private String storyName;
    private String caseName;
    private String caseOwner;
    private Integer caseStepNum;
    private Boolean caseResult;
    private Boolean newlyFail;
    private Boolean keepFailing;
    private Long startTime;
    private Long duration;

}