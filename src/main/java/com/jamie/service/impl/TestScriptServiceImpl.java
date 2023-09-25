package com.jamie.service.impl;

import com.jamie.form.ScriptForm;
import com.jamie.properties.JMeterProperties;
import com.jamie.service.ITestScriptService;
import com.jamie.websocket.WebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

@Service
@Slf4j
public class TestScriptServiceImpl implements ITestScriptService {

    @Resource
    private WebSocket webSocket;

    @Resource
    private JMeterProperties jMeterProperties;

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

    private void run(String userId, String jmxFilePath) {
        Thread thread = new Thread(() -> {
            ProcessBuilder processBuilder = new ProcessBuilder();
            Map<String, String> environment = processBuilder.environment();
            environment.put("JAVA_HOME", jMeterProperties.getJavaHome());
            List<String> commandList = new ArrayList<>();
            commandList.add(jMeterProperties.getJmeterHome());
            commandList.add("-n");
            commandList.add("-t");
            commandList.add(jmxFilePath);
            commandList.add("-j");
            commandList.add("/dev/stdout");
            processBuilder.command(commandList);
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
                    log.info("进程正常结束");
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
        try {
            thread.join(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (thread.isAlive()) {
            thread.interrupt();
        }
    }

}
