package com.stu.spring.interface_.factoryBean_;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

public class BeanDefinitionAndBeanDefinitionRegistryTest {

    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerBeanDefinition("proxyFactoryBean", new RootBeanDefinition(ProxyFactoryBean.class));
        System.out.println(beanFactory.getBean("proxyFactoryBean"));//com.stu.spring.interface_.factoryBean_.OldBean@69b794e2
        System.out.println(beanFactory.getBean("&proxyFactoryBean"));//com.stu.spring.interface_.factoryBean_.ProxyFactoryBean@f0f2775

    }
}