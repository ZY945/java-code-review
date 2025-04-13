package com.web.rpc.example.client;

import com.web.rpc.core.annotation.RpcService;
import com.web.rpc.example.api.HelloService;

@RpcService
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "Hello, " + name + "!";
    }
}