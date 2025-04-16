# springboot-MCP注意

主模块版本是8，当前模块需要jdk17，通过命令执行比较方便
## 启动服务
```shell
# 换成本地java路径,这里是mac版
JAVA_HOME=/Users/dongfeng/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home mvn spring-boot:run
```
## 测试类
```shell
JAVA_HOME=/Users/dongfeng/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home mvn test -Dtest=McpClientTest#testNumServiceDirectly
```