package com.web.rpc.core.compress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZIP压缩实现
 */
public class GzipCompressor implements Compressor {
    private static final Logger logger = LoggerFactory.getLogger(GzipCompressor.class);

    @Override
    public byte[] compress(byte[] bytes) throws Exception {
        if (bytes == null || bytes.length == 0) {
            return bytes;
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(bytes);
            gzip.finish();
            byte[] result = out.toByteArray();
            logger.debug("Compressed size: {} -> {}", bytes.length, result.length);
            return result;
        } catch (IOException e) {
            logger.error("Gzip compress error", e);
            throw e;
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) throws Exception {
        if (bytes == null || bytes.length == 0) {
            return bytes;
        }
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes);
             GZIPInputStream gzip = new GZIPInputStream(in);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int n;
            while ((n = gzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            byte[] result = out.toByteArray();
            logger.debug("Decompressed size: {} -> {}", bytes.length, result.length);
            return result;
        } catch (IOException e) {
            logger.error("Gzip decompress error", e);
            throw e;
        }
    }
}
