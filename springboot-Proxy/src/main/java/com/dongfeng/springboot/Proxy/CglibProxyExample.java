package com.dongfeng.springboot.Proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

// final 目标类
class Target {
    public void sayHello() {
        System.out.println("Hello from Target!");
    }
}

// MethodInterceptor 实现（同上）
class MyMethodInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("Before method: " + method.getName());
        return proxy.invokeSuper(obj, args);
    }
}

// 测试代码
public class CglibProxyExample {
    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Target.class);
        enhancer.setCallback(new MyMethodInterceptor());

        // 尝试生成代理对象
        Target proxy = (Target) enhancer.create();
// 获取代理类的 Class 对象
        Class<?> proxyClass = proxy.getClass();

        // 获取代理类的名称
        System.out.println("代理类名: " + proxyClass.getName());

        // 获取代理类的父类
        Class<?> superClass = proxyClass.getSuperclass();
        System.out.println("代理类的父类: " + superClass.getName());

        // 检查是否有父类
        System.out.println("代理类继承于" + superClass);

        // 获取代理类实现的接口
        Class<?>[] interfaces = proxyClass.getInterfaces();
        System.out.println("代理类实现的接口: ");
        for (Class<?> iface : interfaces) {
            System.out.println("  - " + iface.getName());
        }
        proxy.sayHello();
    }
}