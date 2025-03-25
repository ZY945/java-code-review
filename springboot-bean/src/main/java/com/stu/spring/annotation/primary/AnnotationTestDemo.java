package com.stu.spring.annotation.primary;

import com.stu.spring.annotation.import_.Test_Import;
import com.stu.spring.interface_.factoryBean_.ProxyFactoryBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 打印容器中的组件测试
 */
public class AnnotationTestDemo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext=new AnnotationConfigApplicationContext(Bean_Primary.class);  //这里的参数代表要做操作的类
//        AnnotationConfigApplicationContext applicationContext=new AnnotationConfigApplicationContext("");  //这里的参数代表scan的包路径

//        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String name : beanDefinitionNames){
            System.out.println(name);
        }

        System.out.println(applicationContext.getBean("primaryClassDemo1", PrimaryClassDemo.class));
        System.out.println(applicationContext.getBean("primaryClassDemo2", PrimaryClassDemo.class));


//        PrimaryClassDemo bean = applicationContext.getBean(PrimaryClassDemo.class);// NoUniqueBeanDefinitionException
//        System.out.println(bean);


        //test_Import
        //com.stu.spring.annotation.import_.Bean_Demo_1
        //com.stu.spring.annotation.import_.Bean_Demo_2
        //beanDemo3

    }
}