package com.stu.spring.annotation.dependsOn_order;


import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Data
public class BeanFirst implements CommandLineRunner {

    private String name;

    // 1.@Component注解的类，会在springboot启动时自动加载构造方法
    // 2.CommandLineRunner接口的实现类，会在springboot启动时自动加载run方法
    public BeanFirst() {
        this.name = "123";
        System.out.println("CommandLineRunnerFirst constructor");
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("CommandLineRunnerFirst run name:" + name);
    }
}
