package com.dongfeng.springboot.Proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

// final 目标类
final class FinalFailTarget {
    public void sayHello() {
        System.out.println("Hello from FinalFailTarget!");
    }
}

// MethodInterceptor 实现（同上）
class MyMethodFailInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("Before method: " + method.getName());
        return proxy.invokeSuper(obj, args);
    }
}

// 测试代码
public class CglibProxyFailExample {
    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(FinalFailTarget.class); // 设置 final 类为父类
        enhancer.setCallback(new MyMethodFailInterceptor());

        // 尝试生成代理对象
        FinalFailTarget proxy = (FinalFailTarget) enhancer.create(); // 这行会抛出异常
        proxy.sayHello();
    }
}