package com.dongfeng.springbootmvc.customHandler;

// 自定义Handler
public class CustomHandler {

    public String handle(String type) {
        System.out.println("自定义handle逻辑...入参|type:" + type);
        return "自定义handle返回的数据.type为" + type;
    }

}
