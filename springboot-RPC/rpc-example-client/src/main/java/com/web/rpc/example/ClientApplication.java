package com.web.rpc.example;

import com.web.rpc.core.annotation.RpcReference;
import com.web.rpc.example.api.HelloService;
import com.web.rpc.example.rpc.ClientConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;

@Component
public class ClientApplication implements CommandLineRunner {

    @RpcReference
    private HelloService helloService;

    public void run(String name) {
        String result = helloService.sayHello(name);
        System.out.println("Result: " + result);
    }

    public static void main(String[] args) {
        SpringApplication.run(ClientConfig.class, args);
    }

    @Override
    public void run(String... args)  {
        run("RPC Test");
    }
}