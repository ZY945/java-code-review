package com.stu.spring.interface_.beanFactoryPostProcessor_;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BeanFactoryPostProcessorTest {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext
                = new AnnotationConfigApplicationContext(MyDefaultConfig.class);

        // 通过BeanFactoryPostProcessor修改属性值
        User user = applicationContext.getBean("user", User.class);
        System.out.println(user);

        // 通过BeanFactoryPostProcessor注册新的bean
        User worker = applicationContext.getBean("worker", User.class);
        System.out.println(worker);
    }
}
