package com.dongfeng.springbootmvc.customHandler;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CustomHandlerAdapter implements HandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return handler instanceof CustomHandler;
    }

    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        CustomHandler customHandler = (CustomHandler) handler;
        String type = request.getParameter("type");
        String result = customHandler.handle(type);

        if ("view".equals(type)) {
            // 使用ModelAndView需要添加spring-boot-starter-thymeleaf.同时保证在resources存在templates/{viewName}.html
            return new ModelAndView("customView", "result", result);
        } else {
            // 直接写入响应
            response.setContentType("text/plain;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_OK); // 200 OK
            response.getWriter().write(result);
        }
        System.out.println("Handling request for: " + request.getRequestURI());
        return null; // 返回 null 表示已手动处理响应
    }

    @Override
    public long getLastModified(HttpServletRequest request, Object handler) {
        return 0;
    }
}
