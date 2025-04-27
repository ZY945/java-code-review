# Spring Boot JVM性能测试应用

这个应用程序用于测试不同JVM配置下Spring Boot应用的性能表现，包括QPS和TPS测试接口，以及相应的JMeter测试计划。

## 数据库设置

### 创建MySQL数据库和表

在开始之前，请确保您已安装MySQL数据库服务器。然后，您可以使用以下方法之一创建所需的数据库和表：

#### 方法1：使用提供的SQL脚本

1. 登录到MySQL服务器：
```bash
mysql -u root -p
```

2. 执行项目中的schema.sql脚本：
```bash
source /path/to/springboot-JVM/src/main/resources/schema.sql
```

#### 方法2：手动执行SQL命令

1. 创建数据库：
```sql
CREATE DATABASE IF NOT EXISTS jvm_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 使用数据库：
```sql
USE jvm_test;
```

3. 创建用户表：
```sql
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 配置数据库连接

应用程序的数据库连接已配置在`application.properties`文件中：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/jvm_test?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=root
```

如果您的MySQL用户名和密码不同，请相应地修改这些属性。

## 构建和运行应用

### 构建应用

使用Maven构建应用：

```bash
mvn clean package
```

### 使用不同JVM参数运行应用

您可以使用不同的JVM参数运行应用程序，以测试它们对性能的影响：

#### 低负载配置

```bash
java -Xms512m -Xmx1g -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+DisableExplicitGC -jar target/springboot-JVM.jar
```

#### 中等负载配置

```bash
java -Xms1g -Xmx2g -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=45 -XX:+ParallelRefProcEnabled -jar target/springboot-JVM.jar
```

#### 高负载配置

```bash
java -Xms2g -Xmx4g -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=40 -XX:+ParallelRefProcEnabled -XX:+UseStringDeduplication -jar target/springboot-JVM.jar
```

## 性能测试

### 使用JMeter进行测试

项目包含了三个JMeter测试计划：

1. `qps-test-plan.jmx` - 测试QPS（每秒查询数）
2. `tps-test-plan.jmx` - 测试TPS（每秒事务数）
3. `mixed-load-test-plan.jmx` - 测试混合负载

要运行这些测试，请使用JMeter图形界面或命令行：

```bash
jmeter -n -t jmeter/qps-test-plan.jmx -l results/qps-results.jtl
```

### 自动化测试脚本

项目还包含一个自动化测试脚本`run-jvm-tests.sh`，它可以使用不同的JVM参数运行应用程序并执行JMeter测试：

```bash
chmod +x run-jvm-tests.sh
./run-jvm-tests.sh
```

请确保在运行脚本之前修改JMeter的安装路径。

## API端点

应用程序提供以下API端点：

- `GET /api/users` - 获取所有用户
- `GET /api/users/{id}` - 根据ID获取用户
- `POST /api/users` - 创建新用户
- `PUT /api/users/{id}` - 更新用户
- `DELETE /api/users/{id}` - 删除用户
- `GET /api/users/search?username=xxx` - 按用户名搜索
- `GET /api/users/email-domain?domain=xxx` - 按电子邮件域名搜索
- `GET /api/users/qps-test` - QPS测试端点（简单读操作）
- `POST /api/users/tps-test` - TPS测试端点（写操作）
- `GET /api/users/recent` - 复杂查询测试
- `GET /api/users/metrics` - 获取性能指标
- `POST /api/users/metrics/reset` - 重置性能指标

## JVM调优指南

有关JVM调优的详细信息，请参阅项目中的以下文档：

- `JVM-Tuning-Guide.md` - 英文版JVM调优指南
- `JVM-调优指南.md` - 中文版JVM调优指南
