package com.jamie.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jmeter")
public class JMeterProperties {

    private String jmeterHome;
    private String scriptPath;
    private String javaHome;

}
