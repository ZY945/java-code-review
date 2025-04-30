package com.dongfeng.springbootmvc.idempotency;

import com.dongfeng.springbootmvc.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Slf4j
public class ApiIdempotentInterceptor implements HandlerInterceptor {
    /**
     * 存入 Redis 的 Token 键的前缀
     */
    private static final String IDEMPOTENT_TOKEN_PREFIX = "idempotent_token:";
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        ApiIdempotent apiIdempotent = method.getAnnotation(ApiIdempotent.class);
        if (apiIdempotent != null) {
            check(request, apiIdempotent);
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    private void check(HttpServletRequest request, ApiIdempotent apiIdempotent) {
        String token = request.getHeader("token");
        String userId = request.getHeader("X-User-Id");
        String serviceName = request.getHeader("X-Service-Name");
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(userId) || StringUtils.isEmpty(serviceName)) {
            throw new BizException("非法参数");
        }

        // 可以使用token获取value和指定信息进行比较,或者根据token删除
//        String value = serviceName + ":" + userId;
        // 根据 Key 前缀拼接 Key
        String key = IDEMPOTENT_TOKEN_PREFIX + token;
        Boolean delResult = redisTemplate.delete(key);
        log.info("delResult: {}", delResult);
        if (Boolean.FALSE.equals(delResult)) {
            //删除失败
            throw new BizException(apiIdempotent.message());
        }

    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
