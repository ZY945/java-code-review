package com.dongfeng.springbootmvc.controller;


import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;

//@RequestMapping("/request")
@RestController
public class RequestController {


    // 1.请求类型
    @RequestMapping("/hello")
    public String hello() {
        return "GET,POST等请求方式都可以";
    }

    @RequestMapping(value = "/hi", method = {RequestMethod.GET})
    public String hi() {
        return "限定请求方式为GET,其他请求方式不允许,405 Method Not Allowed";
    }

    // 2.参数获取,默认是通过地址栏中的参数赋值,也就是通过@RequestParam注解来获取参数
    // 如果需要json格式的参数,可以使用@RequestBody注解
    // RequestParam注解可以设置默认值,如果没有传递参数,则使用默认值
    @RequestMapping(value = "/param", method = {RequestMethod.POST})
    public String param(String name, @RequestParam(value = "name", defaultValue = "defaultValue") String test, @RequestBody String json) {
        // 这里name和test都是请求参数name的值
        // Spring MVC 会根据地址栏中输入的参数列表自动实现参数对象的赋值操作。
        // 例如一个对象user，那么Spring MVC会自动将地址栏中的参数赋值给user对象的属性name。
        return "RequestParam可以绑定变量名,如果没有添加则按变量名来进行获取\n" +
                "获取参数name=" + name + ",\ntest(请求地址栏中的参数名为name)=" + test + ",\njson=" + json;
    }

    // 3.路径参数,path路径的参数,可以通过@PathVariable注解来获取
    @RequestMapping(value = "/path/{path}/{name}", method = {RequestMethod.GET})
    public String paths(@PathVariable String path, @PathVariable("name") String name) {
        return "路由为/path/{path}/{name}。\n路径参数path=" + path + ",name=" + name;
    }

    @RequestMapping(value = "/path/1/{name}", method = {RequestMethod.GET})
    public String path(@PathVariable String name) {
        return "路由为/path/1/{name}。\n路径参数name=" + name;
    }

    // 4.请求头信息
    @RequestMapping(value = "/header", method = {RequestMethod.GET})
    public String header(@RequestHeader("User-Agent") String userAgent) {
        return "请求头信息User-Agent=" + userAgent;
    }

    // 5.获取cookie
    // @CookieValue注解可以获取cookie中的值
    // @CookieValue("JSESSIONID") String sessionId
    // @CookieValue(value = "JSESSIONID",required = false,defaultValue = "defaultValue") String sessionId
    // required = false表示cookie中没有JSESSIONID时不会报错
    // defaultValue = "defaultValue"表示cookie中没有JSESSIONID时默认值为defaultValue
    // 如果cookie中没有JSESSIONID,则会报错
    @RequestMapping(value = "/cookie", method = {RequestMethod.GET})
    public String cookie(@CookieValue(value = "JSESSIONID", required = false, defaultValue = "defaultValue") String sessionId) {
        return "cookie中的JSESSIONID=" + sessionId;
    }

    // 6.获取session
    // 通过HttpServletRequest获取session
    @RequestMapping(value = "/getSessions", method = {RequestMethod.GET})
    public String getSessions(HttpServletRequest request) {
        return "session=" + request.getSession().getId();
    }

    // 如果没有sessionKey,则会报错400 Bad Request
    @RequestMapping(value = "/getSession", method = {RequestMethod.GET})
    public String getSession(@SessionAttribute("sessionKey") String value) {
        return "session:Key=sessionKey,Value=" + value;
    }

    @RequestMapping(value = "/setSession", method = {RequestMethod.GET})
    public String setSession(HttpServletRequest request) {
        request.getSession().setAttribute("sessionKey", "dongfeng");
        return "session无法手动直接添加,只能通过代码进行,因此比cookie安全";
    }


}
