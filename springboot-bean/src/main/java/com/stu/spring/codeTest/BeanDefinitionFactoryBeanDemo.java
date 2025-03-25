package com.stu.spring.codeTest;

import lombok.Data;
import org.springframework.beans.factory.FactoryBean;

@Data
public class BeanDefinitionFactoryBeanDemo implements FactoryBean<BeanDefinitionDemo> {

    @Override
    public BeanDefinitionDemo getObject() throws Exception {
        return new BeanDefinitionDemo();
    }

    @Override
    public Class<?> getObjectType() {
        return BeanDefinitionDemo.class;
    }
}
