package com.stu.spring.annotation.bean_;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Test_BeanNotInConfiguration {
    @Autowired
    private BeanNotInConfiguration beanNotInConfiguration;
}
