package com.web.rpc.core.codec;

import com.web.rpc.core.RpcRequest;
import com.web.rpc.core.RpcResponse;
import com.web.rpc.core.compress.Compressor;
import com.web.rpc.core.compress.CompressorFactory;
import com.web.rpc.core.constants.RpcConstants;
import com.web.rpc.core.protocol.MessageType;
import com.web.rpc.core.protocol.RpcMessage;
import com.web.rpc.core.serialize.Serializer;
import com.web.rpc.core.serialize.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RPC消息解码器
 * 使用LengthFieldBasedFrameDecoder来处理类似于TCP粘包问题
 */
public class RpcDecoder extends LengthFieldBasedFrameDecoder {
    private static final Logger logger = LoggerFactory.getLogger(RpcDecoder.class);

    private static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024; // 8MB
    private static final int LENGTH_FIELD_OFFSET = RpcMessageCodec.HEADER_LENGTH - 4; // 长度字段偏移量
    private static final int LENGTH_FIELD_LENGTH = 4; // 长度字段大小
    private static final int LENGTH_ADJUSTMENT = 0;
    private static final int INITIAL_BYTES_TO_STRIP = 0;

    public RpcDecoder() {
        super(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH,
                LENGTH_ADJUSTMENT, INITIAL_BYTES_TO_STRIP);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        try {
            // 1. 解码消息头部
            RpcMessage message = RpcMessageCodec.decode(frame);

            // 2. 如果有消息体，先解压缩，再反序列化
            if (message.getData() != null) {
                byte[] data = (byte[]) message.getData();
                
                // 如果启用了压缩，先进行解压缩
                if (message.getCompressionType() != RpcConstants.CompressType.NONE) {
                    Compressor compressor = CompressorFactory.getCompressor(message.getCompressionType());
                    data = compressor.decompress(data);
                }
                
                Serializer serializer = SerializerFactory.getSerializer(message.getSerializationType());

                // 根据消息类型选择目标类
                Class<?> targetClass;
                if (message.getMessageType() == MessageType.REQUEST) {
                    targetClass = RpcRequest.class;
                } else if (message.getMessageType() == MessageType.RESPONSE) {
                    targetClass = RpcResponse.class;
                } else {
                    throw new IllegalArgumentException("Unknown message type: " + message.getMessageType());
                }

                Object obj = serializer.deserialize(data, targetClass);
                message.setData(obj);
            }

            logger.debug("Decoded message, type: {}, requestId: {}",
                    message.getMessageType(), message.getRequestId());
            return message;

        } catch (Exception e) {
            logger.error("Decode error", e);
            throw e;
        } finally {
            frame.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("RpcDecoder exception", cause);
        ctx.fireExceptionCaught(cause);
    }
}