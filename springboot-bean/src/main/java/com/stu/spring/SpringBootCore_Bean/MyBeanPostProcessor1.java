package com.stu.spring.SpringBootCore_Bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class MyBeanPostProcessor1 implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MyBeanPostProcessor1.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        logger.info("MyBeanPostProcessor1 - postProcessBeforeInitialization - Bean Name: {}, Bean: {}", beanName, bean);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        logger.info("MyBeanPostProcessor1 - postProcessAfterInitialization - Bean Name: {}, Bean: {}", beanName, bean);
        return bean;
    }
}
