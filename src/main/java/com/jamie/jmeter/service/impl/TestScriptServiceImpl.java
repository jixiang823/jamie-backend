package com.jamie.jmeter.service.impl;

import com.jamie.jmeter.service.ITestScriptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

@Service
@Slf4j
public class TestScriptServiceImpl implements ITestScriptService {

    static Boolean isSomeProcessRun = false;
    Semaphore semaphore = new Semaphore(1);

    @Override
    public String runScript(String scriptPath) {

        if (isSomeProcessRun) {
            return "有程序正在运行,无法启动";
        }
        try {
            isSomeProcessRun = true;
            semaphore.acquire();
            run(scriptPath);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "脚本已执行";

    }

    protected void run(String scriptPath) {
        Thread thread = new Thread(() -> {
            // 日志记录
            StringBuilder scriptLog = new StringBuilder();
            // shell命令执行jmx脚本
            List<String> commandList = new ArrayList<>();
            commandList .add("jmeter");
            commandList .add("-n");
            commandList .add("-t");
            commandList .add(scriptPath); // 所在服务器的路径
            ProcessBuilder processBuilder = new ProcessBuilder(commandList);
            processBuilder.redirectErrorStream(true);
            try {
                Process start = processBuilder.start();
                InputStream inputStream = start.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "gbk");
                char[] chs = new char[1024];
                int len;
                while ((len = inputStreamReader.read(chs)) != -1) {
                    scriptLog.append(new String(chs, 0, len));
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
    }

}
