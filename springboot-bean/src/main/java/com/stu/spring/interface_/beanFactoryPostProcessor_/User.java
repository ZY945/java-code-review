package com.stu.spring.interface_.beanFactoryPostProcessor_;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class User {

    @Value("student")
    private String type;

}
