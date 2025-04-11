package com.dongfeng.springboot.Proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// 未实现接口的目标类
class NoInterfaceTarget {
    public void sayHello() {
        System.out.println("Hello from NoInterfaceTarget!");
    }
}

// InvocationHandler 实现（同上）
class MyInvocationFailHandler implements InvocationHandler {
    private final Object target;

    public MyInvocationFailHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Before method: " + method.getName());
        return method.invoke(target, args);
    }
}

// 测试代码
public class JdkProxyFailExample {
    public static void main(String[] args) {
        NoInterfaceTarget target = new NoInterfaceTarget();
        MyInvocationFailHandler handler = new MyInvocationFailHandler(target);

        // 尝试生成代理对象
        Object proxy = Proxy.newProxyInstance(
                NoInterfaceTarget.class.getClassLoader(),
                NoInterfaceTarget.class.getInterfaces(), // 返回空数组，因为没有接口
                handler
        );

        // 无法转换为 NoInterfaceTarget 类型，因为没有接口
        // ((NoInterfaceTarget) proxy).sayHello(); // 这行会编译失败
    }
}