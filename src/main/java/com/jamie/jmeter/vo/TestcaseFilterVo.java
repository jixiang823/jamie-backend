package com.jamie.jmeter.vo;

import lombok.Data;

@Data
public class TestcaseFilterVo {

    private String batchNo;
    private String caseName;
    private String moduleName;
    private String caseOwner;
    private Boolean result;
    private Integer pageNum;
    private Integer pageSize;
    private String sort;

}
