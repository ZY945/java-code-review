package com.dongfeng.springbootmvc.common.idempotency;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TokenController {
    @Autowired
    private TokenUtilService tokenService;

    /**
     * 获取 Token 接口
     *
     * @return Token 串
     */
    @GetMapping("/token")
    public String getToken(@RequestHeader(value = "X-User-Id") String userId,
                           @RequestHeader(value = "X-Service-Name") String serviceName) {
        // 获取辅助验证数据（这里使用模拟数据,例如服务名,活动id,用户id,商品id）
        // - 1)、使用"token"验证 Redis 中是否存在对应的 Key
        // - 2)、使用"用户信息"验证 Redis 的 Value 是否匹配。
        String userInfo = serviceName + ":" + userId;
        // 获取 Token 字符串，并返回
        return tokenService.generateToken(userInfo);
    }
//    /**
//     * 接口幂等性测试接口,可以通过注解+AOP来实现全局管理
//     *
//     * @param token 幂等 Token 串
//     * @return 执行结果
//     */
//    @PostMapping("/test")
//    public String test(@RequestHeader(value = "token") String token) {
//        // 获取用户信息（这里使用模拟数据）
//        String userInfo = "mydlq";
//        // 根据 Token 和与用户相关的信息到 Redis 验证是否存在对应的信息
//        boolean result = tokenService.validToken(token, userInfo);
//        // 根据验证结果响应不同信息
//        return result ? "正常调用" : "重复调用";
//    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 接口幂等性测试接口,可以通过注解+AOP来实现全局管理
     *
     * @return 执行结果
     */
    @PostMapping("/test")
    @ApiIdempotent(value = "test", message = "请勿重复调用")
    public String test(@RequestParam("key") String key) {
        Boolean delResult = redisTemplate.delete(key);

        // 根据验证结果响应不同信息
        return delResult + "";
    }
}