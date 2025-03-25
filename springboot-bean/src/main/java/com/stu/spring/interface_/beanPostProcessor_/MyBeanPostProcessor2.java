package com.stu.spring.interface_.beanPostProcessor_;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class MyBeanPostProcessor2 implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("MyBeanPostProcessor2 - postProcessBeforeInitialization - Bean Name: " + beanName + ", Bean: " + bean);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("MyBeanPostProcessor2 - postProcessAfterInitialization - Bean Name: " + beanName + ", Bean: " + bean);
        return bean;
    }
}
