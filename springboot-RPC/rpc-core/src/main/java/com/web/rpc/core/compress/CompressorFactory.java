package com.web.rpc.core.compress;

import com.web.rpc.core.constants.RpcConstants;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 压缩器工厂，用于管理不同的压缩实现
 */
public class CompressorFactory {
    private static final ConcurrentHashMap<Byte, Compressor> COMPRESSOR_MAP = new ConcurrentHashMap<>();

    static {
        // 注册压缩实现
        registerCompressor(RpcConstants.CompressType.GZIP, new GzipCompressor());
    }

    /**
     * 获取压缩器
     *
     * @param compressType 压缩类型
     * @return Compressor实例
     * @throws IllegalArgumentException 如果压缩类型不支持
     */
    public static Compressor getCompressor(byte compressType) {
        Compressor compressor = COMPRESSOR_MAP.get(compressType);
        if (compressor == null) {
            throw new IllegalArgumentException("Compressor not found for type: " + compressType);
        }
        return compressor;
    }

    /**
     * 注册压缩器
     *
     * @param compressType 压缩类型
     * @param compressor   压缩器实例
     */
    public static void registerCompressor(byte compressType, Compressor compressor) {
        COMPRESSOR_MAP.put(compressType, compressor);
    }
}
