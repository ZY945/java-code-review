package com.stu.spring.SpringBootCore_Bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    @Bean(initMethod = "customInit", destroyMethod = "customDestroy")
    public CustomBean customBean1() {
        logger.info("ApplicationConfig 创建CustomBean...");
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new CustomBean();
    }

    @Bean(initMethod = "customInit", destroyMethod = "customDestroy")
    public CustomBean customBean2() {
        logger.info("ApplicationConfig 创建CustomBean...");
        return new CustomBean();
    }
}