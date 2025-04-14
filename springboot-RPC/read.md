# RPC Framework Design Document

## 1. 项目概述

这是一个基于Netty的RPC框架实现，支持服务注册发现、负载均衡、序列化、压缩等特性。

## 2. 核心架构

### 2.1 消息体系

框架采用双层消息设计：

#### 业务层消息

- `RpcRequest`: 包含调用的服务名、方法名、参数等业务信息
- `RpcResponse`: 包含调用结果、错误信息等业务响应数据

#### 传输层消息

- `RpcMessage`: 传输协议的封装，处理消息类型、序列化、压缩等传输层面的问题

### 2.2 核心组件

#### 编解码器

- `RpcEncoder`: 将RpcMessage编码为字节流
- `RpcDecoder`: 将字节流解码为RpcMessage
- `RpcMessageCodec`: 处理消息的编解码细节

#### 序列化

- 支持多种序列化方式
- Protobuf序列化实现
- 可扩展的序列化接口

#### 压缩

- `Compressor`: 压缩器接口
- `GzipCompressor`: GZIP压缩实现
- 支持扩展其他压缩算法

#### 服务管理

- `ServiceProvider`: 管理服务实例
- `RpcServer`: 处理服务注册和请求分发

#### 请求处理

- `RpcRequestHandler`: 处理RPC请求的核心组件
- 支持异步处理和超时控制
- 集成心跳检测机制

### 2.3 通信流程

```
客户端                                     服务端
RpcRequest ──┐                         ┌── RpcRequest
              │                         │
              v                         v
RpcMessage ───┼─> 序列化 ─> 网络传输 ─> 反序列化 ──┼─> RpcMessage
              │                         │
              └── 压缩               解压 ─┘
```

## 3. 特性介绍

### 3.1 服务注册与发现

- 基于ETCD的服务注册中心
- 支持服务版本管理
- 自动服务注册和发现

### 3.2 负载均衡

- 支持多种负载均衡策略
- 可扩展的负载均衡接口

### 3.3 高可用特性

- 服务健康检查
- 心跳保活机制
- 自动重连机制

### 3.4 可扩展性

- 插件化的序列化机制
- 可扩展的压缩算法
- 灵活的协议设计

## 4. 使用示例

### 4.1 服务定义

```java
@RpcService(version = "1.0")
public interface HelloService {
    String hello(String name);
}
```

### 4.2 服务实现

```java
@RpcService(version = "1.0")
public class HelloServiceImpl implements HelloService {
    public String hello(String name) {
        return "Hello, " + name;
    }
}
```

### 4.3 服务配置

```yaml
rpc:
  server:
    port: 8888

etcd:
  endpoints: http://localhost:2379
```

### 4.4 构建和运行

```shell
# 构建核心模块
mvn clean install -pl springboot-RPC/rpc-core,springboot-RPC/rpc-client,springboot-RPC/rpc-server -am -Dmaven.test.skip=true

# 构建示例API
mvn clean install -pl springboot-RPC/rpc-example-api -am -Dmaven.test.skip=true
```

## 5. 性能优化

### 5.1 网络优化

- 使用Netty高性能网络框架
- 支持消息压缩
- 连接复用

### 5.2 并发处理

- 异步处理机制
- 线程池优化
- 请求超时控制

## 6. 后续规划

1. 引入熔断降级机制
2. 添加更多序列化和压缩算法
3. 完善监控和追踪功能
4. 支持更多注册中心
5. 优化负载均衡算法

# 测试
