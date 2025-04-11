接口说明：

1. 创建优惠券模板
   路径: POST /api/v1/coupons/templates
   功能: 创建新的优惠券模板
   特点: 参数验证、事务处理
   查询可用优惠券模板
   路径: GET /api/v1/coupons/templates
   功能: 查询所有可用的优惠券模板
   特点: 缓存支持、分页查询
3. 预热优惠券
   路径: POST /api/v1/coupon/seckill/preload/{templateId}
   功能: 将优惠券库存预热到Redis
   特点: Redis预热、异常处理
4. 秒杀抢券
   路径: POST /api/v1/coupon/seckill/{templateId}
   功能: 秒杀抢购优惠券
   特点:
   Redis限流
   分布式锁
   异步处理
   库存预热
   查询我的优惠券
   路径: GET /api/v1/coupons/my
   功能: 查询用户的优惠券列表
   特点: 缓存支持、关联查询
   这些接口实现了：
1. 统一的响应格式
2. 参数验证
   全局异常处理
   性能优化
   高并发支持

死信队列（Dead Letter Queue，DLQ）在消息中间件中是一个重要的设计模式，主要用于处理无法被正常消费的消息。在我们的优惠券系统中使用死信队列有以下几个重要原因：

1. **处理失败消息**

```java
@RabbitListener(queues = RabbitMQConfig.COUPON_QUEUE)
public void handleCouponMessage(CouponMessage message) {
    try {
        couponMessageHandler.handleCouponGrab(message.getTemplateId(), message.getUserId());
    } catch (Exception e) {
        // 当消息处理失败时，会被转发到死信队列
        throw e;
    }
}
```

2. **消息重试机制**

```java
@Bean
public Queue couponQueue() {
    return QueueBuilder.durable(COUPON_QUEUE)
            .withArgument("x-dead-letter-exchange", COUPON_DLX_EXCHANGE)  // 指定死信交换机
            .withArgument("x-dead-letter-routing-key", COUPON_DLX_ROUTING_KEY)  // 指定路由键
            .withArgument("x-message-ttl", 60000)  // 消息过期时间
            .withArgument("x-max-retries", 3)      // 最大重试次数
            .build();
}
```

3. **监控和告警**

```java:src/main/java/com/dongfeng/springbootmvc/coupon/mq/DeadLetterQueueListener.java
@Component
@Slf4j
@RequiredArgsConstructor
public class DeadLetterQueueListener {

    private final AlertService alertService;

    @RabbitListener(queues = RabbitMQConfig.COUPON_DLX_QUEUE)
    public void handleDeadLetter(Message message) {
        try {
            // 1. 记录死信消息
            log.error("收到死信消息: {}", message);
            
            // 2. 获取消息属性
            MessageProperties properties = message.getMessageProperties();
            Map<String, Object> headers = properties.getHeaders();
            int retryCount = (int) headers.getOrDefault("x-retry-count", 0);
            
            // 3. 发送告警
            alertService.sendAlert(
                String.format("优惠券消息处理失败，消息ID: %s, 重试次数: %d", 
                    properties.getMessageId(), retryCount)
            );
            
            // 4. 保存到数据库（可选）
            saveFailedMessage(message);
            
        } catch (Exception e) {
            log.error("处理死信消息异常", e);
        }
    }
}
```

4. **业务补偿机制**

```java:src/main/java/com/dongfeng/springbootmvc/coupon/service/FailedMessageProcessor.java
@Service
@Slf4j
@RequiredArgsConstructor
public class FailedMessageProcessor {

    private final CouponMessageHandler messageHandler;

    @Scheduled(fixedDelay = 300000) // 5分钟执行一次
    public void processFailedMessages() {
        try {
            // 1. 查询需要重试的消息
            List<FailedMessage> messages = findMessagesNeedRetry();
            
            // 2. 重试处理
            for (FailedMessage message : messages) {
                try {
                    messageHandler.handleCouponGrab(
                        message.getTemplateId(), 
                        message.getUserId()
                    );
                    // 处理成功后更新状态
                    markAsProcessed(message);
                } catch (Exception e) {
                    log.error("重试处理消息失败: {}", message, e);
                    updateRetryCount(message);
                }
            }
        } catch (Exception e) {
            log.error("处理失败消息异常", e);
        }
    }
}
```

使用死信队列的主要好处：

1. **可靠性保证**
    - 不会丢失消息
    - 可以追踪每条消息的处理状态
    - 支持消息重试机制

2. **问题诊断**
    - 可以查看失败原因
    - 记录失败时的上下文信息
    - 便于排查问题

3. **业务补偿**
    - 支持手动重试
    - 可以实现定时重试
    - 灵活的补偿策略

4. **监控告警**
    - 及时发现问题
    - 统计失败率
    - 预警异常情况

5. **运维管理**
    - 查看死信消息
    - 手动处理异常
    - 统计分析问题

这样的设计可以确保在分布式系统中消息处理的可靠性，即使出现异常情况，也能保证业务最终一致性。
