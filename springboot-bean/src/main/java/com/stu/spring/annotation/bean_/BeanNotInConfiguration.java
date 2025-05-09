package com.stu.spring.annotation.bean_;

import org.springframework.context.annotation.Bean;

// @Bean不搭配@Configuration注解，会报错
//@Configuration
public class BeanNotInConfiguration {

    @Bean("test")
    public BeanNotInConfiguration beanNotInConfiguration() {
        return new BeanNotInConfiguration();
    }
}
