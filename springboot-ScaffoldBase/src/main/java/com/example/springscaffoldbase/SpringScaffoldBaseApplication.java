package com.example.springscaffoldbase;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Spring Boot 主应用程序类
 */
@SpringBootApplication
@EnableAsync
@MapperScan("com.example.springscaffoldbase.repository")
public class SpringScaffoldBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringScaffoldBaseApplication.class, args);
    }
}
