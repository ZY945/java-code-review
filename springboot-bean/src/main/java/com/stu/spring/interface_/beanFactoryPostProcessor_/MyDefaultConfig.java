package com.stu.spring.interface_.beanFactoryPostProcessor_;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class MyDefaultConfig {
	@Bean
	public User user(){
		return new User();
	}

	// 如果有多个BeanFactoryPostProcessor，可以通过@Order注解来指定执行顺序
	@Order(0)
	@Bean
	public MyBeanFactoryPostProcessor myBeanFactoryPostProcessor(){
		return new MyBeanFactoryPostProcessor();
	}

	@Order(0)
	@Bean
	public MyBeanFactoryPostProcessor2 myBeanFactoryPostProcessor2(){
		return new MyBeanFactoryPostProcessor2();
	}

}
