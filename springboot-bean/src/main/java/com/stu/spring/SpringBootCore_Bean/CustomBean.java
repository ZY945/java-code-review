package com.stu.spring.SpringBootCore_Bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

//@Component
public class CustomBean implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(CustomBean.class);

    public CustomBean() {
        logger.info("CustomBean构造方法执行...");
    }

    @PostConstruct
    public void postConstruct() {
        logger.info("CustomBean @PostConstruct 初始化方法执行...");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("CustomBean InitializingBean#afterPropertiesSet 执行...");
    }

    public void customInit() {
        logger.info("CustomBean 自定义init方法执行...");
    }

    @PreDestroy
    public void preDestroy() {
        logger.info("CustomBean @PreDestroy 销毁方法执行...");
    }

    @Override
    public void destroy() throws Exception {
        logger.info("CustomBean DisposableBean#destroy 执行...");
    }

    public void customDestroy() {
        logger.info("CustomBean 自定义destroy方法执行...");
    }
}