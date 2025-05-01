package com.example.springscaffoldbase.interceptor;

import com.example.springscaffoldbase.utils.MDCTraceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用于记录请求详情的拦截器
 */
@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTRIBUTE = "startTime";

    /**
     * 预处理方法，用于记录请求详情和记录开始时间
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);
        
        // 记录请求信息，包含 traceId
        log.info("请求开始: [{}] {} (来自 {}) [traceId: {}]",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr(),
                MDCTraceUtil.getTraceId());
        
        return true;
    }

    /**
     * 后处理方法，用于记录请求完成和执行时间
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // Nothing to do here
    }

    /**
     * 完成后方法，用于记录请求完成和执行时间
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime != null) {
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 记录请求完成信息，包含 traceId 和执行时间
            log.info("请求完成: [{}] {} - 状态码: {} (耗时 {} 毫秒) [traceId: {}]",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    executionTime,
                    MDCTraceUtil.getTraceId());
            
            if (executionTime > 1000) {
                // 记录慢请求警告，包含 traceId
                log.warn("检测到慢请求: [{}] {} 耗时 {} 毫秒 [traceId: {}]",
                        request.getMethod(),
                        request.getRequestURI(),
                        executionTime,
                        MDCTraceUtil.getTraceId());
            }
        }
    }
}
