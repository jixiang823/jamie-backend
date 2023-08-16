package com.jamie.jmeter.service.impl;

import com.jamie.jmeter.form.ScriptForm;
import com.jamie.jmeter.service.ITestScriptService;
import com.jamie.jmeter.websocket.WebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

@Service
@Slf4j
public class TestScriptServiceImpl implements ITestScriptService {

    @Resource
    private WebSocket webSocket;

    static Boolean isSomeProcessRun = false;
    Semaphore semaphore = new Semaphore(1);

    @Override
    public String runScript(String userId, ScriptForm scriptForm) {

        if (isSomeProcessRun) {
            return "有程序正在运行,无法启动";
        }
        try {
            isSomeProcessRun = true;
            semaphore.acquire();
            run(userId, scriptForm.getScriptPath());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "脚本已执行";

    }

    protected void run(String userId, String scriptPath) {
        Thread thread = new Thread(() -> {
            // 日志记录
            StringBuilder scriptLog = new StringBuilder();
            // shell命令执行jmx脚本
            List<String> commandList = new ArrayList<>();
            commandList .add("jmeter");
            commandList .add("-n");
            commandList .add("-t");
            commandList .add(scriptPath); // 所在服务器的路径
            commandList.add("-j");
            commandList.add("/dev/stdout"); // 将具体的日志打印出来
            ProcessBuilder processBuilder = new ProcessBuilder(commandList);
            processBuilder.redirectErrorStream(true);
            try {
                Process start = processBuilder.start();
                InputStream inputStream = start.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                char[] chs = new char[1024];
                int len;
                while ((len = inputStreamReader.read(chs)) != -1) {
                    webSocket.sendOneMessage(userId, new String(chs, 0, len));
                }
                webSocket.sendOneMessage(userId,"\n执行完毕!\n");
                //阻塞当前线程，直到进程退出为止
                start.waitFor();
                if (start.exitValue() == 0) {
                    log.info("进程正常结束 {}", scriptLog);
                } else {
                    log.info("进程异常结束");
                }
                inputStreamReader.close();
                inputStream.close();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            semaphore.release();
            isSomeProcessRun = false;
        });
        thread.start();
    }

}
