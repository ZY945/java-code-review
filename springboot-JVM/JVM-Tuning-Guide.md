# JVM Tuning Guide for Spring Boot Applications

This document provides JVM tuning parameters for optimizing the performance of Spring Boot applications under different load scenarios.

## JVM Parameters Explanation

| Parameter | Description |
|-----------|-------------|
| `-Xms` | Initial heap size |
| `-Xmx` | Maximum heap size |
| `-XX:MetaspaceSize` | Initial metaspace size |
| `-XX:MaxMetaspaceSize` | Maximum metaspace size |
| `-XX:+UseG1GC` | Use G1 Garbage Collector |
| `-XX:MaxGCPauseMillis` | Target maximum GC pause time |
| `-XX:InitiatingHeapOccupancyPercent` | Percentage of heap occupancy that triggers a concurrent GC cycle |
| `-XX:+ParallelRefProcEnabled` | Enable parallel reference processing |
| `-XX:+UseStringDeduplication` | Enable string deduplication (G1GC only) |
| `-XX:+DisableExplicitGC` | Disable explicit GC calls |
| `-XX:+HeapDumpOnOutOfMemoryError` | Create heap dump on OutOfMemoryError |
| `-XX:HeapDumpPath` | Path for heap dumps |
| `-Xlog:gc*` | GC logging (Java 11+) |
| `-XX:+PrintGCDetails` | Print detailed GC information (Java 8) |
| `-XX:+PrintGCDateStamps` | Print GC date stamps (Java 8) |

## Tuning Profiles

### 1. Development Profile
```
-Xms256m -Xmx512m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m -XX:+UseG1GC
```

### 2. Low Load Profile (< 100 concurrent users)
```
-Xms512m -Xmx1g -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+DisableExplicitGC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heapdump.hprof
```

### 3. Medium Load Profile (100-500 concurrent users)
```
-Xms1g -Xmx2g -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=45 -XX:+ParallelRefProcEnabled -XX:+DisableExplicitGC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heapdump.hprof
```

### 4. High Load Profile (500-1000 concurrent users)
```
-Xms2g -Xmx4g -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=40 -XX:+ParallelRefProcEnabled -XX:+UseStringDeduplication -XX:+DisableExplicitGC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heapdump.hprof
```

### 5. Very High Load Profile (1000+ concurrent users)
```
-Xms4g -Xmx8g -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=1g -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:InitiatingHeapOccupancyPercent=35 -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=60 -XX:G1HeapRegionSize=16m -XX:+ParallelRefProcEnabled -XX:+UseStringDeduplication -XX:+DisableExplicitGC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heapdump.hprof
```

## GC Logging Profiles

### Basic GC Logging (Java 8)
```
-XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:./gc.log
```

### Advanced GC Logging (Java 11+)
```
-Xlog:gc*=info:file=./gc.log:time,uptime,level,tags:filecount=5,filesize=100m
```

## Memory Optimization Tips

1. **Right-size your heap**: Set `-Xms` and `-Xmx` to the same value to avoid heap resizing.
2. **Monitor GC behavior**: Use GC logging to identify potential memory issues.
3. **Tune the G1GC collector**: Adjust `MaxGCPauseMillis` and `InitiatingHeapOccupancyPercent` based on your application's needs.
4. **Consider application characteristics**: CPU-bound vs. memory-bound applications need different tuning strategies.
5. **Benchmark and test**: Always test your JVM settings under realistic load conditions.

## Running with Different Profiles

To run the application with a specific profile:

```bash
java [JVM_PARAMETERS] -jar springboot-JVM.jar
```

Example for medium load:
```bash
java -Xms1g -Xmx2g -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:InitiatingHeapOccupancyPercent=45 -XX:+ParallelRefProcEnabled -XX:+DisableExplicitGC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heapdump.hprof -jar springboot-JVM.jar
```
