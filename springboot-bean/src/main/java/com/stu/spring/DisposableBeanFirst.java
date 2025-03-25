package com.stu.spring;

import org.springframework.beans.factory.DisposableBean;

public class DisposableBeanFirst implements DisposableBean {

    @Override
    public void destroy() throws Exception {
        System.out.println("DisposableBeanFirst destroy");
    }
}
