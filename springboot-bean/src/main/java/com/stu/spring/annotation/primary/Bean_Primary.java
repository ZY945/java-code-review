package com.stu.spring.annotation.primary;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class Bean_Primary {

    @Bean
    public PrimaryClassDemo primaryClassDemo1() {
        return new PrimaryClassDemo("primaryClassDemo1");
    }

    @Bean
    public PrimaryClassDemo primaryClassDemo2() {
        return new PrimaryClassDemo("primaryClassDemo2");
    }

}
