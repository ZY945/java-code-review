package com.stu.spring.annotation.all_;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.PostConstruct;

//@Component
@Slf4j
public class Test implements CommandLineRunner, InitializingBean, DisposableBean {

    private String name;

    @Autowired
    public void setName(String name) {
        // 2.@Autowired注解的方法，会在springboot启动时自动加载
        log.info("--- @Autowired setName 属性注入方法");
        this.name = name;
    }

    @PostConstruct
    public void postConstruct() {
        log.info("--- @PostConstruct");
    }

    public Test() {
        // 1.@Component注解的类，会在springboot启动时自动加载构造方法
        log.info("--- constructor()");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 3.InitializingBean接口的实现类，会在springboot启动时自动加载afterPropertiesSet方法
        log.info("--- implements InitializingBean afterPropertiesSet()");
    }

    private void initMethodBean() {
        log.info("--- @BeanDemo initMethod()");
    }

    @Override
    public void destroy() throws Exception {
        // DisposableBean接口的实现类，会在springboot关闭时自动加载destroy方法
        // 该函数内容一般为客户端的关闭，例如关闭数据库连接
        log.info("--- implements DisposableBean destroy()");
    }

    @Override
    public void run(String... args) throws Exception {
        // 4.CommandLineRunner接口的实现类，会在springboot启动时自动加载run方法
        log.info("--- implements CommandLineRunner run()");
    }
}
