package com.stu.spring.codeTest;

import lombok.Data;

@Data
public class Service {
    private String id;
    private String name;
    public void say() {
        System.out.println("This is a Service");
    }
}
