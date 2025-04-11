package com.dongfeng.springboot.Proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// 定义一个接口
interface Hello {
    void sayHello();
}

// 目标类，实现接口
class HelloImpl implements Hello {
    @Override
    public void sayHello() {
        System.out.println("Hello from HelloImpl!");
    }
}

// InvocationHandler 实现
class MyInvocationHandler implements InvocationHandler {
    private final Object target;

    public MyInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Before method: " + method.getName());
        Object result = method.invoke(target, args);
        System.out.println("After method: " + method.getName());
        return result;
    }
}

// 测试代码
public class JdkProxyExample {
    public static void main(String[] args) {
        HelloImpl helloImpl = new HelloImpl();
        MyInvocationHandler handler = new MyInvocationHandler(helloImpl);

        // 生成代理对象
        Hello proxy = (Hello) Proxy.newProxyInstance(
                HelloImpl.class.getClassLoader(),
                HelloImpl.class.getInterfaces(),
                handler
        );
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

        // 调用代理方法以验证功能
        proxy.sayHello();
    }
}