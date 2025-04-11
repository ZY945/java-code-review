package com.stu.spring.interface_.beanFactoryPostProcessor_;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class MyBeanFactoryPostProcessor2 implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        // 修改bean的属性
        BeanDefinition userBeanDefinition = beanFactory.getBeanDefinition("user");
        MutablePropertyValues propertyValues = userBeanDefinition.getPropertyValues();
        propertyValues.add("type", "teacher1");

    }
}
