package com.stu.spring.codeTest;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ChildBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.StaticApplicationContext;

//测试方法
public class BeanDefinitionTest {
    public static void main(String[] args) throws Exception {
        // 创建BeanFactory容器
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        // 创建一个BeanDefinition
        RootBeanDefinition definition = new RootBeanDefinition();
        definition.setBeanClass(BeanDefinitionFactoryBeanDemo.class);
        factory.registerBeanDefinition("beanDefinitionDemo", definition);


        BeanDefinitionFactoryBeanDemo factoryBean = factory.getBean("&beanDefinitionDemo",BeanDefinitionFactoryBeanDemo.class);
        BeanDefinitionDemo user = factory.getBean("beanDefinitionDemo", BeanDefinitionDemo.class);
        System.out.println("工厂Bean：" + factoryBean + ", 创建的实例：" + factoryBean.getObject());
        System.out.println("直接获取普通Bean: " + user);

        // 创建一个BeanDefinition
        RootBeanDefinition definition1 = new RootBeanDefinition();
        definition1.setBeanClass(BeanDefinitionDemo.class);
        definition1.setPropertyValues(new MutablePropertyValues().add("name", "beanDefinitionDemo1"));
        factory.registerBeanDefinition("beanDefinitionDemo1", definition1);
        BeanDefinitionDemo user1 = factory.getBean("beanDefinitionDemo", BeanDefinitionDemo.class);
        System.out.println("直接获取普通Bean: " + user1);
        System.out.println(definition1);

        BeanDefinition bd = BeanDefinitionBuilder.childBeanDefinition("fooService")
                .setScope(BeanDefinition.SCOPE_PROTOTYPE)
                .addPropertyValue("id", "456")
                .getBeanDefinition();

        factory.registerBeanDefinition("beanDefinitionDemo1", bd);


        StaticApplicationContext parent = new StaticApplicationContext();
        StaticApplicationContext child = new StaticApplicationContext(parent);

        RootBeanDefinition pbd = new RootBeanDefinition();
        pbd.setBeanClass(Service.class);
        pbd.setScope(BeanDefinition.SCOPE_SINGLETON);
        pbd.getPropertyValues().add("id", "123");
        pbd.getPropertyValues().add("name", "zhangsan");
        parent.registerBeanDefinition("fooService", pbd);

        ChildBeanDefinition cbd = new ChildBeanDefinition("fooService");
        cbd.setBeanClass(Service.class);
        cbd.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        cbd.getPropertyValues().add("id", "456");
        child.registerBeanDefinition("fooService", cbd);

// 通过子容器来获取 bean
        Service bean1 = child.getBean(Service.class);
        Service bean2 = child.getBean(Service.class);
        System.out.println(bean1 + ",id=" + bean1.getId() + ",name=" + bean1.getName());
        System.out.println(bean2 + ",id=" + bean2.getId() + ",name=" + bean2.getName());


    }

}
