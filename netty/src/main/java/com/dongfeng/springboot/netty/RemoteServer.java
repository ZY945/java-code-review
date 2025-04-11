package com.dongfeng.springboot.netty;

public interface RemoteServer {
    /**
     * 启动远程服务
     */
    void start();

    /**
     * 停止远程服务
     */
    void stop();

    /**
     * 远程服务是否启动
     *
     * @return true表示启动，false表示未启动
     */
    boolean isStarted();

}
