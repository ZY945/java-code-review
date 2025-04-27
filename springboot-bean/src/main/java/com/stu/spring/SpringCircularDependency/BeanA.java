package com.stu.spring.SpringCircularDependency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BeanA {
    private BeanB beanB;

    @Autowired
    public void setBeanB(BeanB beanB) {
        this.beanB = beanB;
    }

    @Override
    public String toString() {
        return "BeanA{beanB=" + (beanB != null ? beanB.getClass().getSimpleName() : "null") + "}"; // 避免调用 beanB.toString()
    }
}