package DesignPattern.Proxy.StaticProxy;

import DesignPattern.Proxy.StaticProxy.Service.ServiceImpl;

/**
 * @author dongfeng
 * @date 2023/4/4 14:05
 */
public class Main {
    public static void main(String[] args) {
        ServiceStaticProxy serviceStaticProxy = new ServiceStaticProxy(new ServiceImpl());
        serviceStaticProxy.test();
    }
}
