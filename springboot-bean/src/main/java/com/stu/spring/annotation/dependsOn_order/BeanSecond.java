package com.stu.spring.annotation.dependsOn_order;


import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Data
@DependsOn("beanFirst") // CommandLineRunner的run方法并不能保证按照@DependsOn的顺序执行
//@Order(2)//
public class BeanSecond implements CommandLineRunner {

    private BeanFirst bean;

    // 1.@Component注解的类，会在springboot启动时自动加载构造方法
    // 2.CommandLineRunner接口的实现类，会在springboot启动时自动加载run方法
    public BeanSecond(final BeanFirst bean) {
        this.bean = bean;
        System.out.println("CommandLineRunnerSecond constructor");
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("CommandLineRunnerSecond run bean:" + bean + " name:" + bean.getName());
    }
}
