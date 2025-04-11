package com.stu.spring.SpringBootCore_Bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class CustomApplicationListener implements ApplicationListener<ApplicationEvent> {

    private static final Logger logger = LoggerFactory.getLogger(CustomApplicationListener.class);

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationStartingEvent) {
            logger.info("监听到 ApplicationStartingEvent - Spring Boot开始启动...");
        } else if (event instanceof ApplicationEnvironmentPreparedEvent) {
            logger.info("监听到 ApplicationEnvironmentPreparedEvent - 环境准备完成...");
        } else if (event instanceof ApplicationContextInitializedEvent) {
            logger.info("监听到 ApplicationContextInitializedEvent - 应用上下文初始化完成...");
        } else if (event instanceof ApplicationStartedEvent) {
            logger.info("监听到 ApplicationStartedEvent - Spring Boot启动完成...");
        } else if (event instanceof ApplicationFailedEvent) {
            logger.info("监听到 ApplicationFailedEvent - Spring Boot启动失败...");
        }
    }
}