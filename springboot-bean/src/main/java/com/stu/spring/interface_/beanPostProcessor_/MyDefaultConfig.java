package com.stu.spring.interface_.beanPostProcessor_;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyDefaultConfig {

	@Bean
	public User user(){
		return new User();
	}

	@Bean
	public User user2(){
		return new User();
	}

	@Bean
	public MyBeanPostProcessor myBeanPostProcessor(){
		return new MyBeanPostProcessor();
	}


}
