package com.jamie.scheduled;


import com.jamie.properties.JMeterProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

// 定时任务执行脚本
@Slf4j
@Component
public class AutoTestTask implements Runnable{

    @Resource
    private JMeterProperties jMeterProperties;

    static Boolean isSomeProcessRun = false;
    Semaphore semaphore = new Semaphore(1);

    @Override
    public void run() {
        String[] scriptPaths = getScriptPath().split("\r?\n|\r");
        for (String scriptPath : scriptPaths) {
            log.info("scriptPath: {}", scriptPath);
            if (isSomeProcessRun) {
                log.info("有脚本正在运行,无法启动");
            }
            if (!isSomeProcessRun) {
                try {
                    isSomeProcessRun = true;
                    semaphore.acquire();
                    runJmx(jMeterProperties, scriptPath);
                    log.info("脚本运行完毕");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getScriptPath() {
        String scriptPath = "";
        StringBuilder sb = new StringBuilder();
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(jMeterProperties.getScriptPath());
        processBuilder.redirectErrorStream(true);
        try {
            Process start = processBuilder.start();
            InputStream inputStream = start.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            char[] chs = new char[1024];
            int len;
            while ((len = inputStreamReader.read(chs)) != -1) {
                sb.append(new String(chs, 0, len));
            }
            start.waitFor();
            if (start.exitValue() == 0) {
                scriptPath = sb.toString();
                log.info("进程正常结束,scriptPath: {}", scriptPath);
            } else {
                log.error("进程异常结束,scriptPath: {}", scriptPath);
            }
            inputStreamReader.close();
            inputStream.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return scriptPath;
    }

    private void runJmx(JMeterProperties jMeterProperties, String jmxFilePath) throws InterruptedException {
        Thread thread = new Thread(() -> {
            StringBuilder sb = new StringBuilder();
            ProcessBuilder processBuilder = new ProcessBuilder();
            Map<String, String> environment = processBuilder.environment(); // 配置JAVA_HOME
            environment.put("JAVA_HOME", jMeterProperties.getJavaHome());
            List<String> commandList = new ArrayList<>();
            commandList.add(jMeterProperties.getJmeterHome()); // 配置JMETER_HOME
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
                    sb.append(new String(chs, 0, len));
                }
                start.waitFor();
                if (start.exitValue() == 0) {
                    log.info("进程正常结束 {}", sb);
                } else {
                    log.error("进程异常结束 {}", sb);
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
