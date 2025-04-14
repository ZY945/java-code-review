# 解决依赖问题
```shell
rpc-example-server $ mvn dependency:tree
```
排查发现
[INFO] |  +- com.fasterxml.jackson.core:jackson-databind:jar:2.15.2:compile
[INFO] |  |  +- com.fasterxml.jackson.core:jackson-annotations:jar:2.13.3:compile
[INFO] |  |  \- com.fasterxml.jackson.core:jackson-core:jar:2.13.3:compile
jackson-databind是2.15.2版本，而jackson-annotations和jackson-core是2.13.3版本。
这种混合使用不同版本的Jackson组件会导致类似NoSuchFieldError: READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE的错误，
因为较新版本的databind尝试访问较旧版本的annotations中不存在的字段。

# 构建基本依赖包
```shell
rpc-core $ mvn clean install -DskipTests
```

# grpc
```shell
rpc-core $ clean compile -DskipTests
```