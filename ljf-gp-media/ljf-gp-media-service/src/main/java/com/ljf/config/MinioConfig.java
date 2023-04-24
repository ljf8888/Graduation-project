package com.ljf.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: minio配置类
 * @author 李炯飞
 * @date: 2023/2/15 10:33
 * @param:
 * @return:
 **/
@Configuration
public class MinioConfig {


    @Bean
    public MinioClient minioClient() {

        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint("http://192.168.10.112:9000")
                        .credentials("minioadmin", "minioadmin")
                        .build();
        return minioClient;
    }
}
