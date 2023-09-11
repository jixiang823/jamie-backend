package com.jamie.jmeter.scheduled;


import com.jamie.jmeter.properties.JMeterProperties;
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
        if (isSomeProcessRun) {
            log.info("有程序正在运行,无法启动");
        }
        if (!isSomeProcessRun) {
            try {
                isSomeProcessRun = true;
                semaphore.acquire();
                Thread thread = new Thread(() -> {
                    // 记录日志
                    StringBuilder scriptLog = new StringBuilder();
                    ProcessBuilder processBuilder = new ProcessBuilder();
                    // 配置环境变量
                    Map<String, String> environment = processBuilder.environment();
                    environment.put("JAVA_HOME", jMeterProperties.getJavaHome());
                    // shell命令执行jmx脚本
                    List<String> commandList = new ArrayList<>();
                    commandList.add(jMeterProperties.getJmeterHome());
                    commandList.add("-n");
                    commandList.add("-t");
                    commandList.add(jMeterProperties.getJmxFilePath());
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
                            log.info(new String(chs, 0, len));
                        }
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        log.info("脚本已执行");
    }

}
