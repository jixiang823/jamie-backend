package com.jamie.vo;

import lombok.Data;

@Data
public class TestcaseFilterVo {

    private String batchNo;
    private String featureName;
    private String storyName;
    private String caseName;
    private String caseOwner;
    private Boolean caseResult;
    private Integer pageNum;
    private Integer pageSize;
    private String sort;

}
