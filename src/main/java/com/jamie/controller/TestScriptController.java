package com.jamie.controller;

import com.jamie.constant.UserConst;
import com.jamie.enums.ResponseEnum;
import com.jamie.form.ScriptForm;
import com.jamie.pojo.User;
import com.jamie.properties.FtpConfigProperties;
import com.jamie.service.ITestScriptService;
import com.jamie.utils.FTPUtil;
import com.jamie.vo.ResponseVo;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
public class TestScriptController {

    @Resource
    private ITestScriptService scriptService;

    @Resource
    private FtpConfigProperties properties;

    // 执行脚本
    @PostMapping("/script/run")
    public ResponseVo<String> run(@RequestBody ScriptForm scriptForm, HttpSession session) {
        User user = (User)session.getAttribute(UserConst.CURRENT_USER);
        String scriptPath = scriptForm.getScriptPath();
        if (scriptPath == null || scriptPath.isEmpty()) {
            return ResponseVo.error(ResponseEnum.INVALID_ARGUMENT);
        }
        return ResponseVo.success(scriptService.runScript(user.getId().toString(), scriptForm));
    }

    // 上传脚本
    @PostMapping("/script/upload")
    public ResponseVo<String> uploadFtpFile(@RequestParam("file") MultipartFile file){
        try {
            FTPClient ftpClient = FTPUtil.connectFtp(
                    properties.getHost(),
                    properties.getPort(),
                    properties.getUsername(),
                    properties.getPassword()
            );
            boolean result = FTPUtil.uploadFile(
                    ftpClient,
                    properties.getFileHome(),
                    file.getOriginalFilename(),
                    file.getInputStream()
            );
            if (!result) {
                return ResponseVo.error(ResponseEnum.UPLOAD_FAIL);
            }
            return ResponseVo.success();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
