package com.jamie.controller;

import com.jamie.constant.UserConst;
import com.jamie.enums.ResponseEnum;
import com.jamie.form.ScriptForm;
import com.jamie.pojo.User;
import com.jamie.service.ITestScriptService;
import com.jamie.vo.ResponseVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController

public class TestScriptController {

    @Resource
    private ITestScriptService scriptService;

    // 执行jmeter脚本
    @PostMapping("/script/run")
    public ResponseVo<String> run(@RequestBody ScriptForm scriptForm, HttpSession session) {
        User user = (User)session.getAttribute(UserConst.CURRENT_USER);
        String scriptPath = scriptForm.getScriptPath();
        if (scriptPath == null || scriptPath.isEmpty()) {
            return ResponseVo.error(ResponseEnum.INVALID_ARGUMENT);
        }
        return ResponseVo.success(scriptService.runScript(user.getId().toString(), scriptForm));
    }

}
