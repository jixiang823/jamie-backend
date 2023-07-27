package com.jamie.jmeter.controller;

import com.jamie.jmeter.service.ITestScriptService;
import com.jamie.jmeter.vo.ResponseVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController

public class TestScriptController {

    @Resource
    private ITestScriptService scriptService;

    // 命令行执行jmeter脚本(全量执行)
    @PostMapping("/jmeter/script/run")
    public ResponseVo<String> run() {
        return ResponseVo.success(scriptService.runScript());
    }

}
