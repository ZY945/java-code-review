package com.stu.spring.interface_.beanPostProcessor_;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BeanPostProcessorTest {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext
                = new AnnotationConfigApplicationContext(MyDefaultConfig.class);


        // 通过BeanFactoryPostProcessor注册新的bean
        User worker = applicationContext.getBean("user", User.class);
        System.out.println(worker);
    }
}
