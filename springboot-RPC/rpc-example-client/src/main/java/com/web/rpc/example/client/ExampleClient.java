package com.web.rpc.example.client;

import com.web.rpc.client.RpcClientFactory;
import com.web.rpc.core.constants.RpcConstants;
import com.web.rpc.example.api.HelloService;
import com.web.rpc.example.api.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * RPC示例客户端
 */
public class ExampleClient {
    private static final Logger logger = LoggerFactory.getLogger(ExampleClient.class);

    public static void main(String[] args) {
        // 创建RPC客户端工厂，显式指定使用JSON序列化
        RpcClientFactory clientFactory = new RpcClientFactory(
                RpcConstants.DEFAULT_VERSION,
                RpcConstants.DEFAULT_TIMEOUT,
                TimeUnit.MILLISECONDS,
                RpcConstants.SerializationType.JSON,
                RpcConstants.CompressType.GZIP,
                "http://localhost:2379");
        // 指定版本号为1.0.0，与服务端保持一致
        String version = "1.0.0";

        try {
            // 获取HelloService的代理对象，明确指定版本号
            HelloService helloService = clientFactory.createService(HelloService.class, version);

            // 测试简单调用
            testSimpleCall(helloService);

            // 测试延迟调用（测试超时）
            testDelayedCall(helloService);

            // 测试批量调用
            testBatchCall(helloService);

            // 获取服务器信息
            testServerInfo(helloService);

        } catch (Exception e) {
            logger.error("Error in RPC client", e);
        } finally {
            // 关闭客户端
            clientFactory.shutdown();
        }
    }

    private static void testSimpleCall(HelloService helloService) {
        logger.info("Testing simple call...");
        String result = helloService.sayHello("World");
        logger.info("Result: {}", result);
    }

    private static void testDelayedCall(HelloService helloService) {
        logger.info("Testing delayed call...");
        try {
            // 设置2秒延迟
            String result = helloService.helloWithDelay("Delayed World", 2000);
            logger.info("Delayed result: {}", result);
        } catch (Exception e) {
            logger.error("Delayed call failed", e);
        }
    }

    private static void testBatchCall(HelloService helloService) {
        logger.info("Testing batch call...");
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        List<String> results = helloService.batchHello(names);
        logger.info("Batch results: {}", results);
    }

    private static void testServerInfo(HelloService helloService) {
        logger.info("Getting server info...");
        ServerInfo serverInfo = helloService.getServerInfo();
        logger.info("Server info: {}", serverInfo);
        logger.info("Server uptime: {} seconds",
                TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - serverInfo.getStartTime()));
    }
}
