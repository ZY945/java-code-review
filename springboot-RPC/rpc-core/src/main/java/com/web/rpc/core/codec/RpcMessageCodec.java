package com.web.rpc.core.codec;

import com.web.rpc.core.protocol.MessageType;
import com.web.rpc.core.protocol.RpcMessage;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * RPC消息编解码工具类
 * 消息格式：
 * +---------------------------------------------------------------+
 * | 魔数 2byte | 版本号 1byte | 消息类型 1byte | 序列化类型 1byte  |
 * +---------------------------------------------------------------+
 * | 压缩类型 1byte | 请求ID 8byte | 消息体长度 4byte | 消息体 bytes |
 * +---------------------------------------------------------------+
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RpcMessageCodec {
    /**
     * 魔数，用于快速识别RPC消息
     */
    public static final short MAGIC_NUMBER = 0x10;

    /**
     * 版本号
     */
    public static final byte VERSION = 1;

    /**
     * 头部长度: 魔数(2) + 版本(1) + 消息类型(1) + 序列化类型(1) + 压缩类型(1) + 请求ID(8) + 消息体长度(4)
     */
    public static final int HEADER_LENGTH = 18;

    /**
     * 编码消息到ByteBuf
     */
    public static void encode(RpcMessage message, ByteBuf out) {
        // 1. 写入魔数
        out.writeShort(MAGIC_NUMBER);
        // 2. 写入版本号
        out.writeByte(VERSION);
        // 3. 写入消息类型
        out.writeByte(message.getMessageType().getType());
        // 4. 写入序列化类型
        out.writeByte(message.getSerializationType());
        // 5. 写入压缩类型
        out.writeByte(message.getCompressionType());
        // 6. 写入请求ID
        out.writeLong(message.getRequestId());
        // 7. 写入消息体（如果有的话）
        if (message.getData() != null) {
            // 预留消息体长度的位置
            int lengthIndex = out.writerIndex();
            out.writeInt(0);
            // 记录开始写入的位置
            int bodyStartIndex = out.writerIndex();
            // 写入消息体
            out.writeBytes((byte[]) message.getData());
            // 计算消息体长度
            int bodyLength = out.writerIndex() - bodyStartIndex;
            // 回写消息体长度
            out.setInt(lengthIndex, bodyLength);
        } else {
            out.writeInt(0);
        }
    }

    /**
     * 从ByteBuf解码消息
     */
    public static RpcMessage decode(ByteBuf in) {
        // 1. 校验魔数
        short magic = in.readShort();
        if (magic != MAGIC_NUMBER) {
            throw new IllegalArgumentException("Unknown magic number: " + magic);
        }

        // 2. 读取版本号
        byte version = in.readByte();
        if (version != VERSION) {
            throw new IllegalArgumentException("Unknown version: " + version);
        }

        // 3. 读取消息类型
        byte messageTypeValue = in.readByte();
        MessageType messageType = MessageType.fromType(messageTypeValue);

        // 4. 读取序列化类型
        byte serializationType = in.readByte();

        // 5. 读取压缩类型
        byte compressionType = in.readByte();

        // 6. 读取请求ID
        long requestId = in.readLong();

        // 7. 读取消息体长度
        int bodyLength = in.readInt();

        // 8. 读取消息体
        byte[] data = null;
        if (bodyLength > 0) {
            data = new byte[bodyLength];
            in.readBytes(data);
        }

        // 9. 构建RpcMessage对象
        RpcMessage message = new RpcMessage();
        message.setMessageType(messageType);
        message.setSerializationType(serializationType);
        message.setCompressionType(compressionType);
        message.setRequestId(requestId);
        message.setData(data);

        return message;
    }
}
