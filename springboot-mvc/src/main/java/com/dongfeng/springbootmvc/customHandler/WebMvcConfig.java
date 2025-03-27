package com.dongfeng.springbootmvc.customHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 定义一个自定义的 HandlerAdapter
//    @Bean
//    public HandlerAdapter customHandlerAdapter() {
//        return new HandlerAdapter() {
//            @Override
//            public boolean supports(Object handler) {
//                // 指定支持的 handler 类型，例如只支持特定的控制器
//                return handler instanceof AbstractController;
//            }
//
//            @Override
//            public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//                // 自定义处理逻辑
//                if (handler instanceof AbstractController) {
//                    return ((AbstractController) handler).handleRequest(request, response);
//                }
//                return null;
//            }
//
//            @Override
//            public long getLastModified(HttpServletRequest request, Object handler) {
//                // 返回最后修改时间，-1 表示不支持
//                return -1;
//            }
//        };
//    }

    // 自定义 HandlerMapping，将特定路径映射到 CustomHandler
    @Bean
    public HandlerMapping customHandlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        Map<String, Object> urlMap = new HashMap<>();
        urlMap.put("/custom", new CustomHandler()); // 将 /custom 路径映射到 CustomHandler
        mapping.setUrlMap(urlMap);
        mapping.setOrder(0); // 设置优先级高于默认的 HandlerMapping
        return mapping;
    }
}
