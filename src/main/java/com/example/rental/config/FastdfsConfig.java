package com.example.rental.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "fdfs")
public class FastdfsConfig {
    /**
     * fastdfs对外域名
     */
    private String outurl;
}
