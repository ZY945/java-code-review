package com.example.springscaffoldbase.aspect;

import com.example.springscaffoldbase.utils.MDCTraceUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * MDC 日志切面，用于记录服务方法的执行信息
 */
@Aspect
@Component
@Slf4j
public class MDCLogAspect {

    /**
     * 定义切点：所有 service 包下的方法
     */
    @Pointcut("execution(* com.example.springscaffoldbase.service..*.*(..))")
    public void servicePointcut() {
    }

    /**
     * 环绕通知：记录方法执行前后的信息
     *
     * @param joinPoint 连接点
     * @return 方法执行结果
     * @throws Throwable 可能抛出的异常
     */
    @Around("servicePointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();
        
        // 记录方法开始执行
        log.debug("开始执行: {}.{} [traceId: {}]", className, methodName, MDCTraceUtil.getTraceId());
        
        long startTime = System.currentTimeMillis();
        Object result;
        
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 记录方法执行成功
            log.debug("执行成功: {}.{} (耗时 {} 毫秒) [traceId: {}]", 
                    className, methodName, executionTime, MDCTraceUtil.getTraceId());
            
            return result;
        } catch (Throwable e) {
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 记录方法执行异常
            log.error("执行异常: {}.{} (耗时 {} 毫秒) [traceId: {}], 异常: {}", 
                    className, methodName, executionTime, MDCTraceUtil.getTraceId(), e.getMessage());
            
            throw e;
        }
    }
}
