package com.jamie.jmeter.service.impl;

import com.jamie.jmeter.dao.DashboardMapper;
import com.jamie.jmeter.pojo.Dashboard;
import com.jamie.jmeter.service.IGetCallBack;
import com.jamie.jmeter.service.ITestScriptService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

@Service
@Slf4j
public class TestScriptServiceImpl implements ITestScriptService {

    @Resource
    private DashboardMapper dashboardMapper;

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
            run(new IGetCallBack() {
                @Override
                public void scriptCallBack(String scriptLog) {
                    // 获取日志`.jmx`出现的次数(执行了几个jmx文件)
                    int count = StringUtils.countMatches(scriptLog, ".jmx");
                    List<Dashboard> oldDashboardList = dashboardMapper.selectSameBatch(count);
                    // 首次执行的批次号作为当前的批次号(降序排列,所以最后一个元素是首次执行的)
                    List<Dashboard> newSameBatchList = new ArrayList<>();
                    String batchNo = oldDashboardList.get(oldDashboardList.size() - 1).getBatchNo();
                    for (Dashboard dashboard : oldDashboardList) {
                        dashboard.setBatchNo(batchNo);
                        newSameBatchList.add(dashboard);
                    }
                    dashboardMapper.updateBatch(newSameBatchList);
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "脚本已执行";

    }

    protected void run(final IGetCallBack getCallBack) {
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
                    log.info("进程正常结束");
                } else {
                    log.info("进程异常结束");
                }
                inputStreamReader.close();
                inputStream.close();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (getCallBack != null) {
                    getCallBack.scriptCallBack(sb.toString());
                }
            }
            semaphore.release();
            isSomeProcessRun = false;
        });
        thread.start();
    }

}
