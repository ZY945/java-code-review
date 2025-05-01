package com.example.springscaffoldbase.filter;

import com.example.springscaffoldbase.utils.MDCTraceUtil;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * MDC 链路追踪过滤器
 * 为每个请求生成唯一的 traceId，并在请求完成后清除
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MDCTraceFilter extends OncePerRequestFilter {

    /**
     * 请求头中的追踪ID键名
     */
    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 尝试从请求头中获取 traceId
            String traceId = request.getHeader(TRACE_ID_HEADER);
            
            // 如果请求头中没有 traceId，则生成一个新的
            if (!StringUtils.hasText(traceId)) {
                traceId = MDCTraceUtil.generateTraceId();
            }
            
            // 设置 traceId 到 MDC 上下文
            MDCTraceUtil.setTraceId(traceId);
            
            // 将 traceId 添加到响应头
            response.setHeader(TRACE_ID_HEADER, traceId);
            
            // 继续执行过滤器链
            filterChain.doFilter(request, response);
        } finally {
            // 请求处理完成后清除 MDC 上下文
            MDCTraceUtil.clear();
        }
    }
}
