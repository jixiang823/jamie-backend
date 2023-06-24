package com.jamie.jmeter.form;

import lombok.Data;

@Data
public class ApiObjectForm {

    private String apiName; // API名称
    private String requestUrl; // 请求url
    private String requestMethod; // 请求方法
    private String requestHeader; // 请求头
    private String requestBody; // 请求体
    private String responseHeader; // 响应头
    private String responseBody; // 响应
    private String responseCode; // 响应码
    private String assertMessage; // 接口断言信息
    private Boolean apiResult; // 接口是否执行成功 0:成功 1:失败
    private Long apiStartTime; // 接口执行开始时间
    private Long apiEndTime; // 接口执行结束时间
    private Long apiDuration; // 接口执行持续时间

}
