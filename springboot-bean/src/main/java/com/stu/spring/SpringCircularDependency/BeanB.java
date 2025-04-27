package com.stu.spring.SpringCircularDependency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BeanB {
    private BeanA beanA;

    @Autowired
    public void setBeanA(BeanA beanA) {
        this.beanA = beanA;
    }

    @Override
    public String toString() {
        return "BeanB{beanA=" + (beanA != null ? beanA.getClass().getSimpleName() : "null") + "}"; // 避免调用 beanA.toString()
    }
}