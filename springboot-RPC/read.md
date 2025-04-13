# 使用
```shell
cd ..
mvn clean install -pl springboot-RPC/rpc-core,springboot-RPC/rpc-client,springboot-RPC/rpc-server -am -Dmaven.test.skip=true
```

# 案例
```shell
cd ..
mvn clean install -pl springboot-RPC/rpc-example-api -am -Dmaven.test.skip=true
```
# RPC 框架设计文档

## 1. 概述

本文档描述了一个基于 **Spring Boot**、**Netty** 和 **etcd** 实现的远程过程调用（RPC）框架的设计。该框架旨在提供高效、可扩展、可靠的分布式服务通信，将远程调用抽象为本地方法调用。

### 1.1 目标
- **简单性**：提供易用的 API，方便开发者定义和调用远程服务。
- **高性能**：利用 Netty 实现高性能网络通信。
- **可扩展性**：通过 etcd 实现服务发现，支持动态扩展。
- **可定制性**：支持序列化、负载均衡和容错机制的自定义。
- **集成性**：与 Spring Boot 无缝集成，支持依赖注入和配置管理。

### 1.2 技术栈
- **Spring Boot**：用于配置、依赖管理和应用生命周期管理。
- **Netty**：异步、事件驱动的网络框架，用于客户端-服务器通信。
- **etcd**：分布式键值存储，用于服务发现和配置管理。
- **Java**：核心编程语言。
- **Maven**：依赖管理和构建工具。

## 2. 系统架构

RPC 框架由以下组件构成：

### 2.1 客户端
- **服务代理**：使用 Java 动态代理为远程接口生成代理。
- **服务发现**：从 etcd 查询可用的服务实例。
- **负载均衡**：根据配置策略（如轮询、随机）选择服务端实例。
- **Netty 客户端**：建立连接、发送请求并处理响应。
- **请求编码/解码**：序列化请求和反序列化响应（默认使用 JSON，支持 Protobuf）。

### 2.2 服务端
- **Netty 服务端**：监听请求并分发到服务实现。
- **服务注册**：将服务元数据（主机、端口、服务名）注册到 etcd。
- **请求处理器**：通过反射将请求映射到对应的服务方法。
- **响应编码/解码**：序列化响应和反序列化请求。

### 2.3 etcd 集成
- **服务注册**：服务端启动时将端点（host:port）注册到 etcd。
- **服务发现**：客户端从 etcd 获取可用服务端点。
- **健康检查**：通过 etcd 租约机制确保只有健康的服务端可被发现。

### 2.4 数据流程
1. 客户端通过代理调用方法。
2. 代理序列化请求（方法名、参数），通过 Netty 发送到负载均衡选择的服务器。
3. 服务端反序列化请求，调用对应方法，序列化响应。
4. 客户端接收并反序列化响应，返回结果。

## 3. 设计细节

### 3.1 服务定义
服务通过 Java 接口定义，使用自定义注解 `@RpcService` 标记。实现类为普通的 Spring Bean。

```java
@RpcService
public interface HelloService {
    String sayHello(String name);
}
```

### 3.2 服务注册
- **服务端启动**：
    - 扫描带有 `@RpcService` 注解的接口。
    - 将服务元数据（接口、主机、端口）注册到 etcd，并绑定租约。
    - 启动 Netty 服务端监听请求。
- **etcd 结构**：
  ```
  /rpc/services/<服务名>/<主机:端口>
  ```
  示例：`/rpc/services/HelloService/192.168.1.1:8080`

### 3.3 服务发现
- **客户端初始化**：
    - 扫描带有 `@RpcReference` 注解的字段或 Bean。
    - 从 etcd 查询服务端点。
    - 为每个服务接口创建动态代理。
- **动态更新**：
    - 监听 etcd 的变化（如新增服务、失效服务）。
    - 动态更新负载均衡的服务端列表。

### 3.4 通信协议
- **请求格式**：
  ```json
  {
    "requestId": "uuid",
    "serviceName": "HelloService",
    "methodName": "sayHello",
    "parameterTypes": ["java.lang.String"],
    "parameters": ["Alice"]
  }
  ```
- **响应格式**：
  ```json
  {
    "requestId": "uuid",
    "result": "Hello, Alice",
    "error": null
  }
  ```
- **序列化**：默认使用 JSON，支持 Protobuf 或自定义序列化器。

### 3.5 负载均衡
- **策略**：
    - 轮询（默认）。
    - 随机。
    - 一致性哈希（适用于有状态服务）。
- **实现**：可插拔接口 `LoadBalancer`，提供默认实现。

### 3.6 容错机制
- **重试机制**：支持配置失败重试次数。
- **熔断器**：通过熔断器模式防止级联失败。
- **超时控制**：配置请求超时，避免长时间挂起。

### 3.7 配置
通过 Spring Boot 属性文件支持自定义：
```properties
rpc.server.port=8080
rpc.etcd.address=http://localhost:2379
rpc.serialization=json
rpc.loadbalancer=round-robin
rpc.retry.count=3
rpc.timeout.ms=5000
```

## 4. 实现计划

### 4.1 模块划分
- **rpc-core**：核心 RPC 逻辑（代理、请求/响应处理）。
- **rpc-netty**：基于 Netty 的传输层。
- **rpc-etcd**：etcd 服务发现与注册。
- **rpc-spring-boot-starter**：Spring Boot 自动配置和注解。

### 4.2 依赖项
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>io.netty</groupId>
        <artifactId>netty-all</artifactId>
        <version>4.1.100.Final</version>
    </dependency>
    <dependency>
        <groupId>io.etcd</groupId>
        <artifactId>jetcd-core</artifactId>
        <version>0.7.5</version>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.15.2</version>
    </dependency>
</dependencies>
```

### 4.3 核心类
- **注解**：
    - `@RpcService`：标记服务接口。
    - `@RpcReference`：注入客户端代理。
- **客户端**：
    - `RpcProxy`：生成动态代理。
    - `NettyClient`：管理连接和发送请求。
    - `EtcdServiceDiscovery`：从 etcd 查询端点。
    - `LoadBalancer`：选择服务端。
- **服务端**：
    - `NettyServer`：监听请求。
    - `RpcRequestHandler`：分发请求到服务方法。
    - `EtcdServiceRegistry`：向 etcd 注册服务。
- **通用**：
    - `RpcRequest`：封装请求数据。
    - `RpcResponse`：封装响应数据。
    - `Serializer`：处理序列化/反序列化。

### 4.4 开发阶段
1. **阶段 1：核心 RPC 逻辑**（2 周）
    - 实现代理、请求/响应处理和序列化。
    - 进行本地测试（无网络）。
2. **阶段 2：Netty 集成**（2 周）
    - 添加 Netty 客户端和服务端。
    - 测试客户端-服务端通信。
3. **阶段 3：etcd 集成**（1 周）
    - 实现服务注册和发现。
    - 测试动态服务发现。
4. **阶段 4：Spring Boot Starter**（1 周）
    - 实现自动配置和注解。
    - 测试与 Spring Boot 的集成。
5. **阶段 5：高级特性**（2 周）
    - 添加负载均衡、重试和超时机制。
    - 测试容错和可扩展性。
6. **阶段 6：文档与测试**（1 周）
    - 编写用户指南和 API 文档。
    - 进行集成测试和性能测试。

## 5. 测试策略

### 5.1 单元测试
- 测试代理生成、序列化和请求处理。
- 模拟 Netty 和 etcd 依赖。

### 5.2 集成测试
- 使用真实 Netty 配置测试客户端-服务端通信。
- 使用本地 etcd 实例测试服务注册和发现。

### 5.3 性能测试
- 测量高负载下的吞吐量和延迟。
- 测试多服务端和客户端的扩展性。

### 5.4 容错测试
- 模拟服务端故障和网络问题。
- 验证重试、超时和熔断器的行为。

## 6. 部署考虑

- **etcd 集群**：部署高可用的 etcd 集群。
- **监控**：集成 Prometheus/ELK 收集指标和日志。
- **安全性**：
    - 为 etcd 访问添加认证。
    - 支持 Netty 的 TLS 加密通信。
- **扩展性**：使用 Kubernetes 或类似工具实现动态扩展。

## 7. 风险与应对

- **风险**：Netty 学习曲线。
    - **应对**：参考官方文档和社区示例。
- **风险**：etcd 可用性问题。
    - **应对**：实现端点缓存等降级机制。
- **风险**：性能瓶颈。
    - **应对**：优化序列化、网络和反射调用。

## 8. 未来改进

- **gRPC 支持**：兼容 gRPC 协议。
- **异步调用**：支持异步方法调用。
- **指标监控**：提供详细的性能指标。
- **自定义协议**：支持更多序列化格式（如 Avro）。

## 9. 结论

该 RPC 框架利用 Spring Boot 的易用性、Netty 的高性能和 etcd 的可扩展性，模块化设计确保了扩展能力，分阶段实现计划降低了开发风险。最终将交付一个适用于分布式系统的健壮 RPC 解决方案。

---
