package com.stu.spring;

import org.springframework.beans.factory.InitializingBean;

public class InitializingBeanFirst implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("InitializingBeanFirst afterPropertiesSet");
    }
}
