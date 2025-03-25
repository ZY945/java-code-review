package com.stu.spring.annotation.commandLineRunner_;


import lombok.Data;
import org.springframework.boot.CommandLineRunner;

//@Component
@Data
public class CommandLineRunnerSecond implements CommandLineRunner {

    private CommandLineRunnerFirst bean;

    // 1.@Component注解的类，会在springboot启动时自动加载构造方法
    // 2.CommandLineRunner接口的实现类，会在springboot启动时自动加载run方法
    public CommandLineRunnerSecond(final CommandLineRunnerFirst bean) {
        this.bean = bean;
        System.out.println("CommandLineRunnerSecond constructor");
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("CommandLineRunnerSecond run bean:" + bean + " name:" + bean.getName());
    }
}
