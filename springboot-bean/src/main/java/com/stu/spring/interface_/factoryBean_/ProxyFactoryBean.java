package com.stu.spring.interface_.factoryBean_;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

//@Component()
public class ProxyFactoryBean implements FactoryBean<OldBean> {

    public void say() {
        System.out.println("This is a ProxyFactoryBean");
    }

    @Override
    public OldBean getObject() throws Exception {
        return new OldBean();
    }

    @Override
    public Class<?> getObjectType() {
        return OldBean.class;
    }
}
