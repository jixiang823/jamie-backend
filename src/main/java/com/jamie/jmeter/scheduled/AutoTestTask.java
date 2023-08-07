package com.jamie.jmeter.scheduled;


import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// 定时任务执行脚本
@Slf4j
public class AutoTestTask implements Runnable{

    @Override
    public void run() {
        List<String> commandList = new ArrayList<>();
        commandList .add("jmeter");
        commandList .add("-n");
        commandList .add("-t");
        commandList .add("/Users/jixiang/Downloads/api_auto_test_script.jmx"); // TODO 所在服务器的路径
        ProcessBuilder processBuilder = new ProcessBuilder(commandList);
        try {
            Process start = processBuilder.start();
            start.waitFor();
            if (start.exitValue() == 0) {
                // TODO 发送执行成功邮件
                log.info("成功");
            }
            if (start.exitValue() != 0) {
                // TODO 发送执行失败邮件
                log.error("失败");
            }
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

}
