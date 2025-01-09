package com.stu.spring.interface_.beanPostProcessor_;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("MyBeanPostProcessor - postProcessBeforeInitialization - Bean Name: " + beanName + ", Bean: " + bean);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("MyBeanPostProcessor - postProcessAfterInitialization - Bean Name: " + beanName + ", Bean: " + bean);
        return bean;
    }
}
