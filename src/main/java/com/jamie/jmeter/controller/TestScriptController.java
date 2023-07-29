package com.jamie.jmeter.controller;

import com.jamie.jmeter.service.ITestScriptService;
import com.jamie.jmeter.vo.ResponseVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController

public class TestScriptController {

    @Resource
    private ITestScriptService scriptService;

    // 执行jmeter脚本
    @PostMapping("/jmeter/script/run")
    public ResponseVo<String> run(@RequestParam("script") String scriptPath) {
        return ResponseVo.success(scriptService.runScript(scriptPath));
    }

}
