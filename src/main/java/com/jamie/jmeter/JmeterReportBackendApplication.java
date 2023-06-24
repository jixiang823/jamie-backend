package com.jamie.jmeter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.jamie.jmeter.dao")
public class JmeterReportBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmeterReportBackendApplication.class, args);
    }

}
