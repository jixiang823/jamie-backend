package com.jamie.jmeter.controller;

import com.jamie.jmeter.enums.ResponseEnum;
import com.jamie.jmeter.form.ScriptForm;
import com.jamie.jmeter.pojo.User;
import com.jamie.jmeter.service.ITestScriptService;
import com.jamie.jmeter.vo.ResponseVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import static com.jamie.jmeter.constant.UserConst.CURRENT_USER;

@RestController

public class TestScriptController {

    @Resource
    private ITestScriptService scriptService;

    // 执行jmeter脚本
    @PostMapping("/jmeter/script/run")
    public ResponseVo<String> run(@RequestBody ScriptForm scriptForm, HttpSession session) {
        User user = (User)session.getAttribute(CURRENT_USER);
        String scriptPath = scriptForm.getScriptPath();
        if (scriptPath == null || scriptPath.isEmpty()) {
            return ResponseVo.error(ResponseEnum.INVALID_ARGUMENT);
        }
        return ResponseVo.success(scriptService.runScript(user.getId().toString(), scriptForm));
    }

}
