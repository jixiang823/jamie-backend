package com.jamie.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ftp")
public class FtpConfigProperties {

    private String host; // FTP服务地址
    private Integer port; // 默认端口21
    private String username; // FTP登录用户
    private String password; // FTP登录密码
    private String fileHome; // 文件存放目录

}