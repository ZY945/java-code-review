package com.dongfeng.springboot.rpc;

import com.web.rpc.core.annotation.RpcReference;
import com.web.rpc.example.api.HelloService;
import com.web.rpc.example.api.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(ServiceTest.class);

    @RpcReference(version = "1.0.0")
    private HelloService helloService;

    public void test() {

        try {

            // 测试简单调用
            testSimpleCall();

            // 测试延迟调用（测试超时）
            testDelayedCall();

            // 测试批量调用
            testBatchCall();

            // 获取服务器信息
            testServerInfo();

        } catch (Exception e) {
            logger.error("Error in RPC client", e);
        }
    }

    private void testSimpleCall() {
        logger.info("Testing simple call...");
        String result = helloService.sayHello("World");
        logger.info("Result: {}", result);
    }

    private void testDelayedCall() {
        logger.info("Testing delayed call...");
        try {
            // 设置2秒延迟
            String result = helloService.helloWithDelay("Delayed World", 2000);
            logger.info("Delayed result: {}", result);
        } catch (Exception e) {
            logger.error("Delayed call failed", e);
        }
    }

    private void testBatchCall() {
        logger.info("Testing batch call...");
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        List<String> results = helloService.batchHello(names);
        logger.info("Batch results: {}", results);
    }

    private void testServerInfo() {
        logger.info("Getting server info...");
        ServerInfo serverInfo = helloService.getServerInfo();
        logger.info("Server info: {}", serverInfo);
        logger.info("Server uptime: {} seconds",
                TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - serverInfo.getStartTime()));
    }
}
