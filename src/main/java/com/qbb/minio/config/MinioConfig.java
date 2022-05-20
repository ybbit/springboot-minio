package com.qbb.minio.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author QiuQiu&LL (个人博客:https://www.cnblogs.com/qbbit)
 * @version 1.0
 * @date 2022-05-20  18:57
 * @Description:
 */
@ConfigurationProperties(prefix = "app.minio")
@EnableConfigurationProperties
@Configuration
@Data
public class MinioConfig {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;
}
