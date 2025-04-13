package com.web.rpc.client.netty;

import com.web.rpc.core.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyClientEncoder extends MessageToByteEncoder<Object> {
    private final Serializer serializer;

    public NettyClientEncoder(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] data = serializer.serialize(msg);
        out.writeBytes(data);
    }
}