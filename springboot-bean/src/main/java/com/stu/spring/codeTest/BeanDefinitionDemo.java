package com.stu.spring.codeTest;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class BeanDefinitionDemo {
    @Value("张三")
    private String name;

}
