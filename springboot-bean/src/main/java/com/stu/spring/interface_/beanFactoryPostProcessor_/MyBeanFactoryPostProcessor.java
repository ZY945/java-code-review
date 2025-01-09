package com.stu.spring.interface_.beanFactoryPostProcessor_;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;

public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

		// 修改bean的属性
		BeanDefinition userBeanDefinition = beanFactory.getBeanDefinition("user");
		MutablePropertyValues propertyValues = userBeanDefinition.getPropertyValues();
		propertyValues.add("type", "teacher");


		// 注册一个新的bean
		GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
		genericBeanDefinition.setBeanClass(User.class);
		genericBeanDefinition.getPropertyValues().add("type","worker");


		((DefaultListableBeanFactory)beanFactory).registerBeanDefinition("worker",genericBeanDefinition);

	}
}