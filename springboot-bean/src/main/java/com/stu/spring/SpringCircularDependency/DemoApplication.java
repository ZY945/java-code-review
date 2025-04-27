package com.stu.spring.SpringCircularDependency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        // 创建单一 Spring Boot 应用上下文
        SpringApplication application = new SpringApplication(DemoApplication.class);
        application.setAllowCircularReferences(true); // 启用循环依赖
        ConfigurableApplicationContext context = application.run(args);
        // 从上下文获取 Bean
        BeanA beanA = context.getBean(BeanA.class);
        BeanB beanB = context.getBean(BeanB.class);
        System.out.println("BeanA: " + beanA + ", BeanB: " + beanB);

        // 关闭上下文以注销 MBean（可选，推荐用于调试）
        context.close();
    }
}

