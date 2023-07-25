package com.jamie.jmeter.service.impl;

import com.jamie.jmeter.service.IProcessService;
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
public class ProcessServiceImpl implements IProcessService {

    static Boolean isSomeProcessRun = false;
    Semaphore semaphore = new Semaphore(1);

    @Override
    public String runProcess(String script) {
        if (isSomeProcessRun) {
            return "有程序正在计算，" + script + " 无法启动";
        }
        try {
            isSomeProcessRun = true;
            semaphore.acquire();
            run(script);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return script + "后台已调用";
    }

    protected void run(String script) {
        Thread thread = new Thread(() -> {
            List<String> scriptPaths = new ArrayList<>(); // 保存所有执行脚本的路径
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(script);
            //将错误输出流转移到标准输出流中
            processBuilder.redirectErrorStream(true);
            try {
                Process start = processBuilder.start();
                InputStream inputStream = start.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "gbk");
                char[] chs = new char[1024];
                int len;
                while ((len = inputStreamReader.read(chs)) != -1) {
                    // TODO 得到了文件路径数据,暂时没用.
                    scriptPaths.add(new String(chs, 0, len));
                    log.info("scriptPaths: {}", scriptPaths);
                }
                inputStreamReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            semaphore.release();
            isSomeProcessRun = false;
        });
        thread.start();
    }

}
