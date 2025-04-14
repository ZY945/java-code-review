package com.web.rpc.core.codec;

import com.web.rpc.core.compress.Compressor;
import com.web.rpc.core.compress.CompressorFactory;
import com.web.rpc.core.constants.RpcConstants;
import com.web.rpc.core.protocol.RpcMessage;
import com.web.rpc.core.serialize.Serializer;
import com.web.rpc.core.serialize.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RPC消息编码器
 */
public class RpcEncoder extends MessageToByteEncoder<RpcMessage> {
    private static final Logger logger = LoggerFactory.getLogger(RpcEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf out) {
        try {
            // 1. 使用对应的序列化器序列化消息体
            byte[] data = null;
            if (msg.getData() != null) {
                Serializer serializer = SerializerFactory.getSerializer(msg.getSerializationType());
                data = serializer.serialize(msg.getData());

                // 如果启用了压缩，对序列化后的数据进行压缩
                if (msg.getCompressionType() != RpcConstants.CompressType.NONE) {
                    Compressor compressor = CompressorFactory.getCompressor(msg.getCompressionType());
                    data = compressor.compress(data);
                }
                msg.setData(data);
            }

            // 2. 编码消息
            RpcMessageCodec.encode(msg, out);

            logger.debug("Encoded message, type: {}, requestId: {}, length: {}",
                    msg.getMessageType(),
                    msg.getRequestId(),
                    data != null ? data.length : 0);
        } catch (Exception e) {
            logger.error("Encode error for message: {}", msg, e);
            ctx.fireExceptionCaught(e);
        }
    }
}