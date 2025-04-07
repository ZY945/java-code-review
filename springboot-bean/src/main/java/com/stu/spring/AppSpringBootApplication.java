package com.stu.spring;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(scanBasePackages = "com.stu.spring.annotation.import_")
// scanBasePackages: 扫描指定包下的所有类，不指定默认扫描当前包及其子包
//@SpringBootApplication(scanBasePackages = "com.stu.spring.annotation.commandLineRunner_")

// springboot-bean
@SpringBootApplication(scanBasePackages = "com.stu.spring.SpringBootCore_Bean")
@Slf4j
public class AppSpringBootApplication {

    public static void main(String[] args) {
        log.info("Spring Boot应用开始启动...");
        SpringApplication app = new SpringApplication(AppSpringBootApplication.class);
        log.info("SpringApplication对象创建完成，开始run方法...");
        app.run(args);
        log.info("Spring Boot应用启动完成！");

    }

}