package com.example.springscaffoldbase.utils;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * MDC 工具类，用于链路追踪
 */
public class MDCTraceUtil {

    /**
     * 追踪ID的键名
     */
    public static final String TRACE_ID_KEY = "traceId";
    
    /**
     * 用户ID的键名
     */
    public static final String USER_ID_KEY = "userId";
    
    /**
     * 生成追踪ID
     * 
     * @return 生成的追踪ID
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 获取当前追踪ID
     * 
     * @return 当前追踪ID，如果不存在则返回null
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }
    
    /**
     * 设置追踪ID
     * 
     * @param traceId 追踪ID
     */
    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID_KEY, traceId);
    }
    
    /**
     * 清除追踪ID
     */
    public static void clearTraceId() {
        MDC.remove(TRACE_ID_KEY);
    }
    
    /**
     * 设置用户ID
     * 
     * @param userId 用户ID
     */
    public static void setUserId(String userId) {
        if (userId != null) {
            MDC.put(USER_ID_KEY, userId);
        }
    }
    
    /**
     * 获取用户ID
     * 
     * @return 当前用户ID，如果不存在则返回null
     */
    public static String getUserId() {
        return MDC.get(USER_ID_KEY);
    }
    
    /**
     * 清除用户ID
     */
    public static void clearUserId() {
        MDC.remove(USER_ID_KEY);
    }
    
    /**
     * 清除所有MDC数据
     */
    public static void clear() {
        MDC.clear();
    }
}
