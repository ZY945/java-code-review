package com.web.rpc.core.serialize;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import com.web.rpc.core.RpcRequest;
import com.web.rpc.core.RpcResponse;
import com.web.rpc.core.protocol.MessageType;
import com.web.rpc.core.protocol.RpcMessage;
import com.web.rpc.core.protocol.RpcStatus;
import com.web.rpc.core.protocol.proto.RpcMessageProto;
import com.web.rpc.core.protocol.proto.RpcRequestProto;
import com.web.rpc.core.protocol.proto.RpcResponseProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

/**
 * Protobuf序列化实现
 */
public class ProtobufSerializer implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(ProtobufSerializer.class);
    private final JsonFormat.Parser parser;
    private final JsonFormat.Printer printer;

    public ProtobufSerializer() {
        this.parser = JsonFormat.parser().ignoringUnknownFields();
        this.printer = JsonFormat.printer().omittingInsignificantWhitespace();
    }

    @Override
    public byte[] serialize(Object obj) throws Exception {
        try {
            if (obj instanceof RpcRequest) {
                return serializeRequest((RpcRequest) obj);
            } else if (obj instanceof RpcResponse) {
                return serializeResponse((RpcResponse) obj);
            } else if (obj instanceof RpcMessage) {
                return serializeMessage((RpcMessage) obj);
            } else {
                throw new IllegalArgumentException("Unsupported object type: " + obj.getClass());
            }
        } catch (Exception e) {
            logger.error("Protobuf serialization failed for object: {}", obj, e);
            throw e;
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        try {
            if (clazz == RpcRequest.class) {
                return (T) deserializeRequest(bytes);
            } else if (clazz == RpcResponse.class) {
                return (T) deserializeResponse(bytes);
            } else if (clazz == RpcMessage.class) {
                return (T) deserializeMessage(bytes);
            } else {
                throw new IllegalArgumentException("Unsupported class type: " + clazz);
            }
        } catch (Exception e) {
            logger.error("Protobuf deserialization failed for class: {}", clazz.getName(), e);
            throw e;
        }
    }

    private byte[] serializeRequest(RpcRequest request) {
        RpcRequestProto.Builder builder = RpcRequestProto.newBuilder()
                .setRequestId(request.getRequestId())
                .setServiceName(request.getServiceName())
                .setMethodName(request.getMethodName())
                .setGroup(request.getGroup())
                .setVersion(request.getVersion())
                .putAllHeaders(request.getHeaders())
                .setTraceId(Objects.toString(request.getTraceId(), ""))
                .setTimestamp(request.getTimestamp())
                .setOneWay(request.isOneWay());

        // 序列化参数类型
        if (request.getParameterTypes() != null) {
            Arrays.stream(request.getParameterTypes())
                    .map(Class::getName)
                    .forEach(builder::addParameterTypes);
        }

        // 序列化参数值
        if (request.getParameters() != null) {
            Arrays.stream(request.getParameters())
                    .map(this::objectToBytes)
                    .map(com.google.protobuf.ByteString::copyFrom)
                    .forEach(builder::addParameterValues);
        }

        return builder.build().toByteArray();
    }

    private byte[] serializeResponse(RpcResponse response) {
        RpcResponseProto.Builder builder = RpcResponseProto.newBuilder()
                .setRequestId(response.getRequestId())
                .setStatusCode(response.getStatus().getCode())
                .setTimestamp(response.getTimestamp());

        if (response.getErrorMessage() != null) {
            builder.setErrorMessage(response.getErrorMessage());
        }

        if (response.getResult() != null) {
            try {
                // 将结果转换为字节数组
                byte[] resultBytes = objectToBytes(response.getResult());
                builder.setResult(com.google.protobuf.ByteString.copyFrom(resultBytes));
            } catch (Exception e) {
                logger.warn("Failed to serialize result: {}", e.getMessage());
            }
        }

        if (response.getError() != null) {
            try {
                // 将错误转换为字节数组
                byte[] errorBytes = objectToBytes(response.getError());
                builder.setErrorDetails(com.google.protobuf.ByteString.copyFrom(errorBytes));
            } catch (Exception e) {
                logger.warn("Failed to serialize error: {}", e.getMessage());
            }
        }

        return builder.build().toByteArray();
    }

    private byte[] serializeMessage(RpcMessage message) {
        RpcMessageProto.Builder builder = RpcMessageProto.newBuilder()
                .setMagicNumber(0x10)
                .setVersion(message.getVersion())
                .setMessageType(message.getMessageType().getType())
                .setSerializationType(message.getSerializationType())
                .setCompressionType(message.getCompressionType())
                .setRequestId(message.getRequestId());

        if (message.getData() != null) {
            try {
                // 将数据转换为字节数组
                byte[] dataBytes = objectToBytes(message.getData());
                builder.setData(com.google.protobuf.ByteString.copyFrom(dataBytes));
            } catch (Exception e) {
                logger.warn("Failed to serialize message data: {}", e.getMessage());
            }
        }

        return builder.build().toByteArray();
    }

    private RpcRequest deserializeRequest(byte[] bytes) throws InvalidProtocolBufferException {
        RpcRequestProto proto = RpcRequestProto.parseFrom(bytes);
        RpcRequest request = new RpcRequest();
        request.setRequestId(proto.getRequestId());
        request.setServiceName(proto.getServiceName());
        request.setMethodName(proto.getMethodName());
        request.setGroup(proto.getGroup());
        request.setVersion(proto.getVersion());
        request.setHeaders(proto.getHeadersMap());
        request.setTraceId(proto.getTraceId());
        request.setTimestamp(proto.getTimestamp());
        request.setOneWay(proto.getOneWay());

        // 反序列化参数类型
        Class<?>[] parameterTypes = proto.getParameterTypesList().stream()
                .map(this::loadClass)
                .toArray(Class<?>[]::new);
        request.setParameterTypes(parameterTypes);

        // 反序列化参数值
        Object[] parameters = new Object[proto.getParameterValuesCount()];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = bytesToObject(proto.getParameterValues(i).toByteArray());
        }
        request.setParameters(parameters);

        return request;
    }

    private RpcResponse deserializeResponse(byte[] bytes) throws InvalidProtocolBufferException {
        RpcResponseProto proto = RpcResponseProto.parseFrom(bytes);
        RpcResponse response = new RpcResponse();
        response.setRequestId(proto.getRequestId());
        
        // 设置状态码
        response.setStatus(RpcStatus.fromCode(proto.getStatusCode()));
        
        // 设置时间戳
        response.setTimestamp(proto.getTimestamp());
        
        // 设置错误信息
        if (!proto.getErrorMessage().isEmpty()) {
            response.setErrorMessage(proto.getErrorMessage());
        }
        
        // 设置结果
        if (proto.getResult() != null && !proto.getResult().isEmpty()) {
            try {
                // 尝试解析结果
                Object result = deserializeAnyField(proto.getResult());
                response.setResult(result);
            } catch (Exception e) {
                logger.warn("Failed to deserialize result: {}", e.getMessage());
            }
        }
        
        // 设置错误详情
        if (proto.getErrorDetails() != null && !proto.getErrorDetails().isEmpty()) {
            try {
                Throwable error = (Throwable) deserializeAnyField(proto.getErrorDetails());
                response.setError(error);
            } catch (Exception e) {
                logger.warn("Failed to deserialize error details: {}", e.getMessage());
            }
        }
        
        return response;
    }

    private RpcMessage deserializeMessage(byte[] bytes) throws InvalidProtocolBufferException {
        RpcMessageProto proto = RpcMessageProto.parseFrom(bytes);
        RpcMessage message = new RpcMessage();
        
        // 设置版本
        message.setVersion((byte)proto.getVersion());
        
        // 设置消息类型
        message.setMessageType(MessageType.fromType(proto.getMessageType()));
        
        // 设置序列化类型
        message.setSerializationType((byte)proto.getSerializationType());
        
        // 设置压缩类型
        message.setCompressionType((byte)proto.getCompressionType());
        
        // 设置请求ID
        message.setRequestId(proto.getRequestId());
        
        // 设置数据
        if (proto.getData() != null && !proto.getData().isEmpty()) {
            try {
                Object data = deserializeAnyField(proto.getData());
                message.setData(data);
            } catch (Exception e) {
                logger.warn("Failed to deserialize message data: {}", e.getMessage());
            }
        }
        
        return message;
    }

    private byte[] objectToBytes(Object obj) {
        if (obj instanceof Message) {
            return ((Message) obj).toByteArray();
        }
        
        // 处理其他类型的对象序列化
        try {
            if (obj == null) {
                return new byte[0];
            } else if (obj instanceof String) {
                return ((String) obj).getBytes();
            } else if (obj instanceof byte[]) {
                return (byte[]) obj;
            } else if (obj instanceof Throwable) {
                // 对于异常，我们序列化异常消息
                return ((Throwable) obj).getMessage().getBytes();
            } else if (obj instanceof Number || obj instanceof Boolean) {
                // 对于基本类型，转换为字符串再序列化
                return obj.toString().getBytes();
            } else {
                // 对于复杂对象，尝试使用JSON格式进行序列化
                String json = printer.print(toMessageBuilder(obj));
                return json.getBytes();
            }
        } catch (Exception e) {
            logger.error("Failed to serialize object: {}", obj, e);
            return new byte[0];
        }
    }

    private Object bytesToObject(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        
        try {
            // 首先尝试作为Protobuf消息解析
            try {
                // 尝试从Any类型解析
                return deserializeAnyField(com.google.protobuf.ByteString.copyFrom(bytes));
            } catch (InvalidProtocolBufferException e) {
                // 不是Any类型，继续尝试其他方式
            }
            
            // 尝试作为字符串解析
            try {
                String str = new String(bytes);
                
                // 尝试解析为基本类型
                if (str.equals("true")) return Boolean.TRUE;
                if (str.equals("false")) return Boolean.FALSE;
                
                try {
                    return Integer.parseInt(str);
                } catch (NumberFormatException e1) {
                    try {
                        return Long.parseLong(str);
                    } catch (NumberFormatException e2) {
                        try {
                            return Double.parseDouble(str);
                        } catch (NumberFormatException e3) {
                            // 不是数字，就当作普通字符串返回
                            return str;
                        }
                    }
                }
            } catch (Exception e) {
                // 解析字符串失败，返回原始字节数组
                return bytes;
            }
        } catch (Exception e) {
            logger.error("Failed to deserialize bytes", e);
            return null;
        }
    }

    private Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load class: " + className, e);
        }
    }
    
    /**
     * 从ByteString反序列化对象
     */
    private Object deserializeAnyField(com.google.protobuf.ByteString byteString) throws InvalidProtocolBufferException {
        if (byteString == null || byteString.isEmpty()) {
            return null;
        }
        
        try {
            // 首先尝试作为字节数组处理
            byte[] bytes = byteString.toByteArray();
            return bytesToObject(bytes);
        } catch (Exception e) {
            logger.warn("Failed to deserialize ByteString: {}", e.getMessage());
            return byteString.toByteArray();
        }
    }
    
    /**
     * 将普通对象转换为MessageOrBuilder
     * 这是一个简化实现，实际应用中可能需要更复杂的转换逻辑
     */
    private MessageOrBuilder toMessageBuilder(Object obj) {
        // 如果已经是MessageOrBuilder类型，直接返回
        if (obj instanceof MessageOrBuilder) {
            return (MessageOrBuilder) obj;
        }
        
        // 创建一个通用的DynamicMessage
        try {
            // 这里我们创建一个空的Any消息作为容器
            Any.Builder builder = Any.newBuilder();
            
            // 设置类型URL
            String typeUrl = "type.googleapis.com/" + obj.getClass().getName();
            builder.setTypeUrl(typeUrl);
            
            // 将对象转换为JSON字符串，然后设置为值
            // 注意：这里我们假设对象可以转换为字符串
            String json = obj.toString();
            builder.setValue(com.google.protobuf.ByteString.copyFromUtf8(json));
            
            return builder;
        } catch (Exception e) {
            logger.error("Failed to convert object to MessageOrBuilder: {}", obj, e);
            // 如果转换失败，返回一个空的Any消息
            return Any.getDefaultInstance();
        }
    }
}
