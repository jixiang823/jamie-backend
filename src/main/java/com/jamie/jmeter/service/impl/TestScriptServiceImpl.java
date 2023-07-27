package com.jamie.jmeter.service.impl;

import com.jamie.jmeter.service.ITestScriptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Semaphore;

@Service
@Slf4j
public class TestScriptServiceImpl implements ITestScriptService {

    static Boolean isSomeProcessRun = false;
    Semaphore semaphore = new Semaphore(1);

    @Override
    public String runScript() {

        if (isSomeProcessRun) {
            return "有程序正在运行,无法启动";
        }
        try {
            isSomeProcessRun = true;
            semaphore.acquire();
            run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "脚本已执行";

    }

    protected void run() {
        Thread thread = new Thread(() -> {
            StringBuilder sb = new StringBuilder();
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("scripts.sh");
            processBuilder.redirectErrorStream(true);
            try {
                Process start = processBuilder.start();
                InputStream inputStream = start.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "gbk");
                char[] chs = new char[1024];
                int len;
                while ((len = inputStreamReader.read(chs)) != -1) {
                    sb.append(new String(chs, 0, len));
                }
                //阻塞当前线程，直到进程退出为止
                start.waitFor();
                if (start.exitValue() == 0) {
                    log.info("进程正常结束 {}", sb);
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
