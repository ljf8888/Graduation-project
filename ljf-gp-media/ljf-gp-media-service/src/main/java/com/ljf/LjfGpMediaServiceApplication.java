package com.ljf;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScans({
        @MapperScan("com.ljf.dao"),
        @MapperScan("com.ljf.config")
})
public class LjfGpMediaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LjfGpMediaServiceApplication.class, args);
    }

}
