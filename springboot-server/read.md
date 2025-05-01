# 高并发抢券系统设计文档

## 1. 文档概述

### 1.1 背景
抢券系统是电商、O2O等场景中的典型高并发业务，用户在短时间内集中抢夺限量优惠券。目标是支持10w QPS（每秒查询率），响应时间低于50ms，系统可用性达99.99%，同时保证公平性、安全性和数据一致性。

### 1.2 目标
设计一个高并发、高可用、可扩展的抢券系统，满足以下要求：
- **性能**：支持10w QPS，峰值15w QPS。
- **稳定性**：系统无单点故障，异常场景可快速恢复。
- **安全性**：防止刷券、作弊行为，确保公平。
- **一致性**：重复请求不导致重复发券或库存错误。

### 1.3 范围
本文档涵盖系统架构、核心模块设计（限流、防刷、幂等性）、技术选型、性能估算、运维保障和扩展性分析。

## 2. 系统架构

### 2.1 总体架构
```
用户 -> CDN/负载均衡(Nginx) -> 网关(Zuul) -> 应用服务(Spring Boot) -> 缓存(Redis Cluster) -> 数据库(MySQL) / 消息队列(Kafka)
       ↳ 风控服务(实时分析)   ↳ 日志服务(ELK)         ↳ 监控(Prometheus+Grafana)
```

- **CDN**：静态资源加速，降低后端压力。
- **负载均衡**：Nginx基于L7分发，动态调整流量。
- **网关**：限流、请求校验、路由。
- **应用服务**：业务逻辑处理，分布式部署。
- **Redis Cluster**：高并发库存管理，用户抢券记录。
- **MySQL**：持久化数据，主从+分库分表。
- **Kafka**：异步削峰，日志收集。
- **风控服务**：实时检测刷券行为。
- **监控/日志**：全链路监控，异常告警。

### 2.2 核心流程
1. 用户通过客户端发起抢券请求。
2. 网关校验请求（限流、防刷），转发至应用服务。
3. 应用服务查询Redis库存，执行原子扣减。
4. 成功抢券后，异步写入Kafka，更新MySQL。
5. 返回结果给用户。

## 3. 核心模块设计

### 3.1 限流设计

#### 3.1.1 目标
控制瞬时流量，保护系统在10w QPS下稳定运行，峰值支持15w QPS。

#### 3.1.2 策略
1. **网关层限流**：
   - **算法**：令牌桶（基于Redis实现）。
   - **配置**：每秒10w令牌，突发流量缓存1w令牌。
   - **实现**：
      - Redis存储令牌数，键为`rate_limit:coupon:activity_id`。
      - Lua脚本执行原子操作，判断令牌是否足够。
      - 超限请求返回429状态码，提示“系统繁忙，请稍后重试”。

2. **服务层限流**：
   - **工具**：Sentinel（阿里巴巴开源）。
   - **配置**：单节点QPS上限1w，集群10节点共10w QPS。
   - **降级**：超限时返回预设降级响应，优先保障核心链路。

3. **热点隔离**：
   - **场景**：限量券（如100张）引发热点请求。
   - **方案**：为热点券分配独立Redis实例，使用一致性哈希分片。
   - **实现**：券ID取模，映射到不同Redis节点。

#### 3.1.3 伪代码
```lua
-- Redis Lua脚本：令牌桶限流
local key = KEYS[1] -- rate_limit:coupon:activity_id
local rate = tonumber(ARGV[1]) -- 每秒令牌数
local now = tonumber(ARGV[2]) -- 当前时间戳
local bucket = redis.call('GET', key)
if bucket == false then
    redis.call('SET', key, rate)
    return 1
end
if tonumber(bucket) > 0 then
    redis.call('DECR', key)
    return 1
else
    return 0
end
```

#### 3.1.4 效果
- **吞吐量**：10w QPS下，响应时间<50ms。
- **稳定性**：超载请求被快速拒绝，系统无雪崩风险。

### 3.2 防刷设计

#### 3.2.1 目标
防止恶意用户通过脚本批量抢券，保证公平性。

#### 3.2.2 策略
1. **设备指纹**：
   - **实现**：收集UA、IP、设备ID，生成MD5哈希作为指纹。
   - **存储**：Redis键`device:coupon:activity_id:{fingerprint}`，TTL 1分钟。
   - **限制**：同一指纹每秒最多5次请求，超限加入黑名单。

2. **验证码机制**：
   - **场景**：高峰期或疑似刷券用户。
   - **实现**：集成图形验证码（如腾讯防水墙），验证通过后生成Token。
   - **存储**：Redis键`captcha:token:{user_id}`，TTL 30秒。

3. **风控系统**：
   - **模型**：基于Flink实时分析请求特征（IP分布、请求频率、行为模式）。
   - **规则**：
      - IP请求频率>100次/秒，标记为高风险。
      - 单用户重复请求>10次/秒，触发验证码。
   - **动作**：高风险用户进入人工审核或降低抢券优先级。

#### 3.2.3 伪代码
```java
// 防刷校验（Java伪代码）
public boolean checkAntiBrush(Request request) {
    String fingerprint = generateFingerprint(request.getUa(), request.getIp(), request.getDeviceId());
    String key = "device:coupon:" + activityId + ":" + fingerprint;
    Long count = redis.incr(key);
    redis.expire(key, 60); // TTL 1分钟
    if (count > 5) {
        blacklist.add(fingerprint);
        return false;
    }
    if (riskEngine.isHighRisk(fingerprint)) {
        return captchaService.verify(request.getUserId());
    }
    return true;
}
```

#### 3.2.4 效果
- **公平性**：普通用户抢券体验不受影响。
- **安全性**：刷券行为拦截率>95%，黑产攻击成本大幅提高。

### 3.3 幂等性设计

#### 3.3.1 目标
确保重复请求不导致重复发券或库存错误。

#### 3.3.2 策略
1. **请求唯一标识**：
   - **实现**：请求携带`request_id`（UUID或`user_id:coupon_id:timestamp`）。
   - **存储**：Redis键`idempotent:coupon:{request_id}`，TTL 1小时。
   - **校验**：网关层检查`request_id`是否已处理。

2. **库存扣减**：
   - **实现**：Redis原子操作`DECR`扣减库存。
   - **存储**：键`coupon:stock:{coupon_id}`，初始化为券总量。
   - **逻辑**：若`DECR`返回负值，库存不足，回滚。

3. **异步落库**：
   - **流程**：抢券成功后，写入Kafka（主题`coupon_issue`）。
   - **消费者**：异步更新MySQL，记录用户券关系。
   - **一致性**：使用分布式事务（如Seata）或补偿机制。

#### 3.3.3 表结构
```sql
-- MySQL表：优惠券库存
CREATE TABLE coupon_stock (
    coupon_id BIGINT PRIMARY KEY,
    total INT NOT NULL,
    remain INT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- MySQL表：用户券记录
CREATE TABLE user_coupon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    coupon_id BIGINT NOT NULL,
    request_id VARCHAR(64) NOT NULL,
    status ENUM('ISSUED', 'USED', 'EXPIRED') DEFAULT 'ISSUED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_request_id (request_id)
);
```

#### 3.3.4 伪代码
```java
// 抢券逻辑（Java伪代码）
public Result grabCoupon(Request request) {
    String requestId = request.getRequestId();
    String idempotentKey = "idempotent:coupon:" + requestId;
    if (redis.exists(idempotentKey)) {
        return getCachedResult(requestId);
    }
    String stockKey = "coupon:stock:" + request.getCouponId();
    Long remain = redis.decr(stockKey);
    if (remain < 0) {
        redis.incr(stockKey); // 回滚
        return Result.fail("库存不足");
    }
    redis.set(idempotentKey, "SUCCESS", 3600); // 标记已处理
    kafka.send("coupon_issue", new CouponRecord(request));
    return Result.success();
}
```

#### 3.3.5 效果
- **一致性**：重复请求返回相同结果，无重复发券。
- **可靠性**：异步落库降低主流程压力，故障可重试。

## 4. 性能估算

### 4.1 流量估算
- **QPS**：正常10w，峰值15w。
- **请求大小**：约1KB/请求。
- **带宽**：10w QPS × 1KB = 100MB/s，峰值150MB/s。

### 4.2 资源需求
1. **应用服务**：
   - 单节点：1w QPS，4核8G，响应时间<20ms。
   - 集群：15节点，支持15w QPS，预留5节点冗余。

2. **Redis Cluster**：
   - 单节点：5w QPS，4核16G，内存10GB。
   - 集群：6节点（3主3从），支持30w QPS。
   - 热点券：独立2节点，内存5GB。

3. **MySQL**：
   - 主库：8核16G，1TB SSD，QPS 1w。
   - 从库：2个，4核8G，QPS 2w。
   - 分库分表：按券ID分4库，每库4表。

4. **Kafka**：
   - 3节点，4核8G，1TB SSD。
   - 吞吐量：10w 消息/秒，保留1天。

### 4.3 性能测试
- **压测环境**：模拟10w QPS，持续10分钟。
- **结果**：
   - 平均响应时间：30ms。
   - 99分位延迟：50ms。
   - 错误率：<0.01%。

## 5. 运维保障

### 5.1 监控
- **指标**：
   - 系统：QPS、响应时间、错误率。
   - 业务：库存扣减速率、抢券成功率、刷券拦截率。
- **工具**：Prometheus+Grafana，ELK日志分析。
- **告警**：
   - 响应时间>100ms，错误率>1%。
   - Redis内存使用率>80%，Kafka积压>1w消息。

### 5.2 容错
- **降级**：
   - 库存不足：返回“券已抢完”。
   - 系统超载：返回“系统繁忙”。
- **回滚**：
   - 异步落库失败：定时任务重试，记录补偿日志。
- **故障恢复**：
   - Redis故障：切换从节点，重建主节点。
   - MySQL故障：主从切换，延迟<1分钟。

### 5.3 部署建议
- **环境**：Kubernetes集群，自动扩缩容。
- **区域**：多AZ部署，跨机房容灾。
- **预案**：
   - 流量突增：动态扩容应用服务和Redis节点。
   - 活动前：预热Redis库存，压测验证。

## 6. 风险分析

| 风险类型         | 描述                           | 解决方案                                   |
|------------------|-------------------------------|------------------------------------------|
| 流量超预期       | QPS超过15w                   | 动态扩容，降级非核心功能                   |
| 刷券攻击         | 黑产批量抢券                  | 风控实时拦截，验证码强制验证               |
| 数据不一致       | Redis与MySQL库存不一致        | 分布式事务，定时对账补偿                   |
| 单点故障         | Redis或MySQL主节点宕机       | 主从切换，多AZ部署                        |

## 7. 扩展性设计

1. **水平扩展**：
   - 应用服务：无状态设计，随时增加节点。
   - Redis/MySQL：分片扩容，支持更高QPS。

2. **功能扩展**：
   - 支持多类型券：增加券模板表，动态配置规则。
   - 个性化推荐：集成推荐系统，优先推送高意向用户。

3. **AI优化**：
   - 流量预测：基于历史数据，动态调整限流阈值。
   - 风控升级：引入图神经网络，检测团伙作弊。

## 8. SLA指标

- **可用性**：99.99%（年宕机<52.6分钟）。
- **响应时间**：平均30ms，99分位50ms。
- **成功率**：抢券成功率>99.9%（库存充足时）。
- **数据一致性**：最终一致性，延迟<1秒。

## 9. 总结

本设计通过限流、防刷、幂等性三大维度，构建了一个高并发、高可用、可扩展的抢券系统。核心亮点：
- **限流**：令牌桶+热点隔离，保障10w QPS稳定运行。
- **防刷**：设备指纹+验证码+风控，拦截95%恶意请求。
- **幂等性**：唯一ID+原子操作+异步落库，确保数据一致。

未来可进一步优化：
- 引入Serverless架构，降低运维成本。
- 使用AI优化流量分配和风控策略。
- 支持全球化部署，满足多地域需求。