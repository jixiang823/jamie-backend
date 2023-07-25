package com.jamie.jmeter.pojo;

import lombok.Data;

@Data
public class ApiObject {

    private Integer id;
    private Integer caseId;
    private String batchNo;
    private String apiName;
    private String requestHost;
    private String requestPath;
    private String requestMethod;
    private String requestHeader;
    private String requestBody;
    private String responseHeader;
    private String responseBody;
    private String responseCode;
    private String assertMessage;
    private Boolean apiResult;
    private Long apiStartTime;
    private Long apiEndTime;
    private Long apiDuration;

}