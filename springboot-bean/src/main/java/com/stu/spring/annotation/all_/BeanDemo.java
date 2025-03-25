package com.stu.spring.annotation.all_;


import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class BeanDemo {

    @Bean
    public String test() {
        return "test";
    }

    @Bean(name = "test2", initMethod = "initMethodBean", destroyMethod = "destroy")
    public Test test2() {
        return new Test();
    }
}
