package com.web.rpc.example.api;

import java.util.List;

/**
 * 示例服务接口
 * 客户端和服务端共享此接口
 */
public interface HelloService {
    /**
     * 简单问候
     *
     * @param name 名字
     * @return 问候语
     */
    String sayHello(String name);

    /**
     * 带有延迟的问候，用于测试超时
     *
     * @param name        名字
     * @param delayMillis 延迟毫秒数
     * @return 问候语
     */
    String helloWithDelay(String name, long delayMillis);

    /**
     * 批量问候
     *
     * @param names 名字列表
     * @return 问候语列表
     */
    List<String> batchHello(List<String> names);

    /**
     * 获取服务器信息
     *
     * @return 服务器信息
     */
    ServerInfo getServerInfo();
}