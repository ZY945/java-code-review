package com.stu.spring.SpringCircularDependency;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.stu.spring.SpringCircularDependency")
public class AppConfig {
}