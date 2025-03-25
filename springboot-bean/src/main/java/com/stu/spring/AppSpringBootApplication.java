package com.stu.spring;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication(scanBasePackages = "com.stu.spring.annotation.import_")
// scanBasePackages: 扫描指定包下的所有类，不指定默认扫描当前包及其子包
//@SpringBootApplication(scanBasePackages = "com.stu.spring.annotation.commandLineRunner_")
public class AppSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppSpringBootApplication.class, args);

    }

}