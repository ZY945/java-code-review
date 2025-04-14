package com.web.rpc.core.compress;

/**
 * 压缩器接口
 */
public interface Compressor {
    /**
     * 压缩数据
     *
     * @param bytes 原始数据
     * @return 压缩后的数据
     */
    byte[] compress(byte[] bytes) throws Exception;

    /**
     * 解压数据
     *
     * @param bytes 压缩的数据
     * @return 原始数据
     */
    byte[] decompress(byte[] bytes) throws Exception;
}
