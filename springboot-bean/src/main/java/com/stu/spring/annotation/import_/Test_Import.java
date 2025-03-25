package com.stu.spring.annotation.import_;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({Bean_Demo_1.class, Bean_Demo_2.class})
public class Test_Import {

    @Bean
    public Bean_Demo_3 beanDemo3() {
        return new Bean_Demo_3();
    }
}
