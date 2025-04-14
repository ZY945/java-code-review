package com.web.rpc.core.serialize;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.web.rpc.core.RpcRequest;
import com.web.rpc.core.RpcResponse;
import com.web.rpc.core.protocol.RpcMessage;
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
            builder.setResult(Any.pack((Message) response.getResult()).toByteString());
        }

        if (response.getError() != null) {
            builder.setErrorDetails(Any.pack((Message) response.getError()).toByteString());
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
            builder.setData(Any.pack((Message) message.getData()).toByteString());
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
        // TODO: 设置其他字段
        return response;
    }

    private RpcMessage deserializeMessage(byte[] bytes) throws InvalidProtocolBufferException {
        RpcMessageProto proto = RpcMessageProto.parseFrom(bytes);
        RpcMessage message = new RpcMessage();
        // TODO: 设置消息字段
        return message;
    }

    private byte[] objectToBytes(Object obj) {
        if (obj instanceof Message) {
            return ((Message) obj).toByteArray();
        }
        // TODO: 处理其他类型的对象序列化
        return new byte[0];
    }

    private Object bytesToObject(byte[] bytes) {
        // TODO: 实现对象反序列化
        return null;
    }

    private Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load class: " + className, e);
        }
    }
}
