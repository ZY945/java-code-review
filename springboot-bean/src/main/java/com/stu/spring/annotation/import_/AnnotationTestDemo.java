package com.stu.spring.annotation.import_;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 打印容器中的组件测试
 */
public class AnnotationTestDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Test_Import.class);  //这里的参数代表要做操作的类

        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String name : beanDefinitionNames) {
            System.out.println(name);
        }
        //test_Import
        //com.stu.spring.annotation.import_.Bean_Demo_1
        //com.stu.spring.annotation.import_.Bean_Demo_2
        //beanDemo3

    }
}