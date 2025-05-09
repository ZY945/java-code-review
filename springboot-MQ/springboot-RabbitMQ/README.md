# SpringBoot RabbitMQ 消息幂等性处理方案

## 项目介绍

本项目实现了基于 RabbitMQ 的消息幂等性处理方案，结合抽奖场景，通过 Redis 去重和数据库唯一约束的组合方案，确保消息不会被重复消费。

## 幂等性实现思路

在 RabbitMQ 中，防止消息重复消费的核心是实现幂等性，本项目采用了多层防护机制：

### 1. Redis 消息 ID 去重（第一层防护）

- 使用 Redis 的 `setIfAbsent` (SETNX) 特性，为每个消息 ID 设置一个缓存标记
- 消费者处理消息前，先检查该消息 ID 是否已存在于 Redis 中
- 如果存在，说明消息已处理过，直接跳过
- 如果不存在，则继续处理，并在处理完成后设置缓存标记

### 2. 数据库消息 ID 唯一索引（第二层防护）

- 在用户抽奖记录表中，为 `message_id` 字段创建唯一索引
- 插入记录时，如果消息 ID 已存在，会触发唯一约束异常
- 捕获异常并视为消息已处理，避免重复消费

### 3. 业务字段唯一约束（第三层防护）

- 基于业务唯一标识（如 `user_id + activity_id + prize_id`）创建唯一索引
- 确保同一用户不会重复领取同一活动的同一奖品
- 这一层防护更符合业务语义，确保业务一致性

### 4. 消费端配置优化

- 使用手动确认模式 (Manual Acknowledgment)
- 设置预取数量 (prefetch) 为 1，确保一次只处理一条消息
- 配置死信队列，处理失败的消息
- 启用重试机制，但限制最大重试次数

## 项目结构

```
├── src/main/java/com/dongfeng/springboot
│   ├── config                      # 配置类
│   │   ├── RabbitMQConfig.java     # RabbitMQ 配置
│   │   └── RedisConfig.java        # Redis 配置
│   ├── consumer                    # 消息消费者
│   │   └── LotteryConsumer.java    # 抽奖消息消费者
│   ├── controller                  # 控制器
│   │   └── LotteryController.java  # 抽奖控制器
│   ├── entity                      # 实体类
│   │   ├── LotteryActivity.java    # 抽奖活动
│   │   ├── LotteryPrize.java       # 奖品
│   │   ├── User.java               # 用户
│   │   └── UserLotteryRecord.java  # 用户抽奖记录
│   ├── mapper                      # Mapper 接口
│   │   └── UserLotteryRecordMapper.java  # 用户抽奖记录 Mapper
│   ├── message                     # 消息对象
│   │   └── LotteryMessage.java     # 抽奖消息
│   ├── service                     # 服务接口
│   │   ├── LotteryService.java     # 抽奖服务接口
│   │   └── impl                    # 服务实现
│   │       └── LotteryServiceImpl.java  # 抽奖服务实现
│   ├── util                        # 工具类
│   │   └── RedisUtil.java          # Redis 工具类
│   ├── vo                          # 值对象
│   │   ├── LotteryRequest.java     # 抽奖请求
│   │   └── Result.java             # 通用响应对象
│   └── SpringbootMvcApplication.java  # 应用程序入口
├── src/main/resources
│   ├── application.yml             # 应用配置
│   ├── mapper                      # MyBatis XML 映射文件
│   │   └── UserLotteryRecordMapper.xml  # 用户抽奖记录映射
│   └── schema.sql                  # 数据库初始化脚本
└── src/test
    └── java/com/dongfeng/springboot
        └── LotteryServiceTest.java  # 测试类
```

## 使用方法

### 环境准备

1. 安装并启动 MySQL 数据库
2. 安装并启动 Redis 服务
3. 安装并启动 RabbitMQ 服务

### 配置修改

根据实际环境修改 `application.yml` 中的配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lottery?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: root
  
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
  
  redis:
    host: localhost
    port: 6379
    password:
```

### 启动应用

```bash
mvn spring-boot:run
```

### API 接口

#### 发起抽奖请求

- URL: `POST /lottery/draw`
- 请求体:
  ```json
  {
    "userId": 1001,
    "activityId": 101,
    "prizeId": 201
  }
  ```
- 响应:
  ```json
  {
    "code": 0,
    "message": "成功",
    "data": "抽奖请求已受理，正在处理中"
  }
  ```

## 测试幂等性

可以通过运行测试类 `LotteryServiceTest` 中的 `testIdempotence()` 方法来验证幂等性处理。该方法会连续发送 3 次相同参数的抽奖消息，通过观察日志可以看到系统对重复消息的处理情况。

## 幂等性处理流程

1. 消息生产者发送抽奖消息到 RabbitMQ
2. 消费者接收到消息后，首先检查 Redis 缓存是否存在该消息 ID
3. 如果 Redis 中已存在，直接确认消息，跳过处理
4. 如果 Redis 中不存在，查询数据库是否已有该消息 ID 的记录
5. 如果数据库中已存在，设置 Redis 缓存并确认消息
6. 如果数据库中不存在，查询是否存在相同用户-活动-奖品的记录
7. 如果业务记录已存在，设置 Redis 缓存并确认消息
8. 如果都不存在，插入新记录并处理业务逻辑
9. 处理完成后，设置 Redis 缓存并确认消息
10. 如果插入过程中触发数据库唯一约束异常，捕获异常并视为消息已处理

## 注意事项

1. Redis 缓存设置了 24 小时的过期时间，可根据业务需求调整
2. 数据库唯一约束是最终的保障，确保即使 Redis 失效也不会重复处理
3. 手动确认模式下，需要确保消息处理的异常被正确捕获，避免消息丢失
4. 死信队列用于处理多次重试后仍失败的消息，需要配置适当的监控和告警
