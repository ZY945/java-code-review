// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: rpc_message.proto

package com.web.rpc.core.protocol.proto;

/**
 * <pre>
 * RPC统一消息格式
 * </pre>
 *
 * Protobuf type {@code com.web.rpc.core.protocol.RpcMessageProto}
 */
public final class RpcMessageProto extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:com.web.rpc.core.protocol.RpcMessageProto)
    RpcMessageProtoOrBuilder {
private static final long serialVersionUID = 0L;
  // Use RpcMessageProto.newBuilder() to construct.
  private RpcMessageProto(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private RpcMessageProto() {
    data_ = com.google.protobuf.ByteString.EMPTY;
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new RpcMessageProto();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.web.rpc.core.protocol.proto.RpcMessage.internal_static_com_web_rpc_core_protocol_RpcMessageProto_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.web.rpc.core.protocol.proto.RpcMessage.internal_static_com_web_rpc_core_protocol_RpcMessageProto_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.web.rpc.core.protocol.proto.RpcMessageProto.class, com.web.rpc.core.protocol.proto.RpcMessageProto.Builder.class);
  }

  public static final int MAGIC_NUMBER_FIELD_NUMBER = 1;
  private int magicNumber_ = 0;
  /**
   * <code>int32 magic_number = 1;</code>
   * @return The magicNumber.
   */
  @java.lang.Override
  public int getMagicNumber() {
    return magicNumber_;
  }

  public static final int VERSION_FIELD_NUMBER = 2;
  private int version_ = 0;
  /**
   * <code>int32 version = 2;</code>
   * @return The version.
   */
  @java.lang.Override
  public int getVersion() {
    return version_;
  }

  public static final int MESSAGE_TYPE_FIELD_NUMBER = 3;
  private int messageType_ = 0;
  /**
   * <code>int32 message_type = 3;</code>
   * @return The messageType.
   */
  @java.lang.Override
  public int getMessageType() {
    return messageType_;
  }

  public static final int SERIALIZATION_TYPE_FIELD_NUMBER = 4;
  private int serializationType_ = 0;
  /**
   * <code>int32 serialization_type = 4;</code>
   * @return The serializationType.
   */
  @java.lang.Override
  public int getSerializationType() {
    return serializationType_;
  }

  public static final int COMPRESSION_TYPE_FIELD_NUMBER = 5;
  private int compressionType_ = 0;
  /**
   * <code>int32 compression_type = 5;</code>
   * @return The compressionType.
   */
  @java.lang.Override
  public int getCompressionType() {
    return compressionType_;
  }

  public static final int REQUEST_ID_FIELD_NUMBER = 6;
  private long requestId_ = 0L;
  /**
   * <code>int64 request_id = 6;</code>
   * @return The requestId.
   */
  @java.lang.Override
  public long getRequestId() {
    return requestId_;
  }

  public static final int DATA_FIELD_NUMBER = 7;
  private com.google.protobuf.ByteString data_ = com.google.protobuf.ByteString.EMPTY;
  /**
   * <code>bytes data = 7;</code>
   * @return The data.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString getData() {
    return data_;
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (magicNumber_ != 0) {
      output.writeInt32(1, magicNumber_);
    }
    if (version_ != 0) {
      output.writeInt32(2, version_);
    }
    if (messageType_ != 0) {
      output.writeInt32(3, messageType_);
    }
    if (serializationType_ != 0) {
      output.writeInt32(4, serializationType_);
    }
    if (compressionType_ != 0) {
      output.writeInt32(5, compressionType_);
    }
    if (requestId_ != 0L) {
      output.writeInt64(6, requestId_);
    }
    if (!data_.isEmpty()) {
      output.writeBytes(7, data_);
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (magicNumber_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(1, magicNumber_);
    }
    if (version_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, version_);
    }
    if (messageType_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(3, messageType_);
    }
    if (serializationType_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(4, serializationType_);
    }
    if (compressionType_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(5, compressionType_);
    }
    if (requestId_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(6, requestId_);
    }
    if (!data_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(7, data_);
    }
    size += getUnknownFields().getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof com.web.rpc.core.protocol.proto.RpcMessageProto)) {
      return super.equals(obj);
    }
    com.web.rpc.core.protocol.proto.RpcMessageProto other = (com.web.rpc.core.protocol.proto.RpcMessageProto) obj;

    if (getMagicNumber()
        != other.getMagicNumber()) return false;
    if (getVersion()
        != other.getVersion()) return false;
    if (getMessageType()
        != other.getMessageType()) return false;
    if (getSerializationType()
        != other.getSerializationType()) return false;
    if (getCompressionType()
        != other.getCompressionType()) return false;
    if (getRequestId()
        != other.getRequestId()) return false;
    if (!getData()
        .equals(other.getData())) return false;
    if (!getUnknownFields().equals(other.getUnknownFields())) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + MAGIC_NUMBER_FIELD_NUMBER;
    hash = (53 * hash) + getMagicNumber();
    hash = (37 * hash) + VERSION_FIELD_NUMBER;
    hash = (53 * hash) + getVersion();
    hash = (37 * hash) + MESSAGE_TYPE_FIELD_NUMBER;
    hash = (53 * hash) + getMessageType();
    hash = (37 * hash) + SERIALIZATION_TYPE_FIELD_NUMBER;
    hash = (53 * hash) + getSerializationType();
    hash = (37 * hash) + COMPRESSION_TYPE_FIELD_NUMBER;
    hash = (53 * hash) + getCompressionType();
    hash = (37 * hash) + REQUEST_ID_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getRequestId());
    hash = (37 * hash) + DATA_FIELD_NUMBER;
    hash = (53 * hash) + getData().hashCode();
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.web.rpc.core.protocol.proto.RpcMessageProto parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.web.rpc.core.protocol.proto.RpcMessageProto parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.web.rpc.core.protocol.proto.RpcMessageProto parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.web.rpc.core.protocol.proto.RpcMessageProto parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.web.rpc.core.protocol.proto.RpcMessageProto parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.web.rpc.core.protocol.proto.RpcMessageProto parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.web.rpc.core.protocol.proto.RpcMessageProto parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.web.rpc.core.protocol.proto.RpcMessageProto parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public static com.web.rpc.core.protocol.proto.RpcMessageProto parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }

  public static com.web.rpc.core.protocol.proto.RpcMessageProto parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.web.rpc.core.protocol.proto.RpcMessageProto parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.web.rpc.core.protocol.proto.RpcMessageProto parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.web.rpc.core.protocol.proto.RpcMessageProto prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * <pre>
   * RPC统一消息格式
   * </pre>
   *
   * Protobuf type {@code com.web.rpc.core.protocol.RpcMessageProto}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:com.web.rpc.core.protocol.RpcMessageProto)
      com.web.rpc.core.protocol.proto.RpcMessageProtoOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.web.rpc.core.protocol.proto.RpcMessage.internal_static_com_web_rpc_core_protocol_RpcMessageProto_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.web.rpc.core.protocol.proto.RpcMessage.internal_static_com_web_rpc_core_protocol_RpcMessageProto_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.web.rpc.core.protocol.proto.RpcMessageProto.class, com.web.rpc.core.protocol.proto.RpcMessageProto.Builder.class);
    }

    // Construct using com.web.rpc.core.protocol.proto.RpcMessageProto.newBuilder()
    private Builder() {

    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);

    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      magicNumber_ = 0;
      version_ = 0;
      messageType_ = 0;
      serializationType_ = 0;
      compressionType_ = 0;
      requestId_ = 0L;
      data_ = com.google.protobuf.ByteString.EMPTY;
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.web.rpc.core.protocol.proto.RpcMessage.internal_static_com_web_rpc_core_protocol_RpcMessageProto_descriptor;
    }

    @java.lang.Override
    public com.web.rpc.core.protocol.proto.RpcMessageProto getDefaultInstanceForType() {
      return com.web.rpc.core.protocol.proto.RpcMessageProto.getDefaultInstance();
    }

    @java.lang.Override
    public com.web.rpc.core.protocol.proto.RpcMessageProto build() {
      com.web.rpc.core.protocol.proto.RpcMessageProto result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.web.rpc.core.protocol.proto.RpcMessageProto buildPartial() {
      com.web.rpc.core.protocol.proto.RpcMessageProto result = new com.web.rpc.core.protocol.proto.RpcMessageProto(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(com.web.rpc.core.protocol.proto.RpcMessageProto result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.magicNumber_ = magicNumber_;
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.version_ = version_;
      }
      if (((from_bitField0_ & 0x00000004) != 0)) {
        result.messageType_ = messageType_;
      }
      if (((from_bitField0_ & 0x00000008) != 0)) {
        result.serializationType_ = serializationType_;
      }
      if (((from_bitField0_ & 0x00000010) != 0)) {
        result.compressionType_ = compressionType_;
      }
      if (((from_bitField0_ & 0x00000020) != 0)) {
        result.requestId_ = requestId_;
      }
      if (((from_bitField0_ & 0x00000040) != 0)) {
        result.data_ = data_;
      }
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.web.rpc.core.protocol.proto.RpcMessageProto) {
        return mergeFrom((com.web.rpc.core.protocol.proto.RpcMessageProto)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.web.rpc.core.protocol.proto.RpcMessageProto other) {
      if (other == com.web.rpc.core.protocol.proto.RpcMessageProto.getDefaultInstance()) return this;
      if (other.getMagicNumber() != 0) {
        setMagicNumber(other.getMagicNumber());
      }
      if (other.getVersion() != 0) {
        setVersion(other.getVersion());
      }
      if (other.getMessageType() != 0) {
        setMessageType(other.getMessageType());
      }
      if (other.getSerializationType() != 0) {
        setSerializationType(other.getSerializationType());
      }
      if (other.getCompressionType() != 0) {
        setCompressionType(other.getCompressionType());
      }
      if (other.getRequestId() != 0L) {
        setRequestId(other.getRequestId());
      }
      if (other.getData() != com.google.protobuf.ByteString.EMPTY) {
        setData(other.getData());
      }
      this.mergeUnknownFields(other.getUnknownFields());
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 8: {
              magicNumber_ = input.readInt32();
              bitField0_ |= 0x00000001;
              break;
            } // case 8
            case 16: {
              version_ = input.readInt32();
              bitField0_ |= 0x00000002;
              break;
            } // case 16
            case 24: {
              messageType_ = input.readInt32();
              bitField0_ |= 0x00000004;
              break;
            } // case 24
            case 32: {
              serializationType_ = input.readInt32();
              bitField0_ |= 0x00000008;
              break;
            } // case 32
            case 40: {
              compressionType_ = input.readInt32();
              bitField0_ |= 0x00000010;
              break;
            } // case 40
            case 48: {
              requestId_ = input.readInt64();
              bitField0_ |= 0x00000020;
              break;
            } // case 48
            case 58: {
              data_ = input.readBytes();
              bitField0_ |= 0x00000040;
              break;
            } // case 58
            default: {
              if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                done = true; // was an endgroup tag
              }
              break;
            } // default:
          } // switch (tag)
        } // while (!done)
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.unwrapIOException();
      } finally {
        onChanged();
      } // finally
      return this;
    }
    private int bitField0_;

    private int magicNumber_ ;
    /**
     * <code>int32 magic_number = 1;</code>
     * @return The magicNumber.
     */
    @java.lang.Override
    public int getMagicNumber() {
      return magicNumber_;
    }
    /**
     * <code>int32 magic_number = 1;</code>
     * @param value The magicNumber to set.
     * @return This builder for chaining.
     */
    public Builder setMagicNumber(int value) {

      magicNumber_ = value;
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>int32 magic_number = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearMagicNumber() {
      bitField0_ = (bitField0_ & ~0x00000001);
      magicNumber_ = 0;
      onChanged();
      return this;
    }

    private int version_ ;
    /**
     * <code>int32 version = 2;</code>
     * @return The version.
     */
    @java.lang.Override
    public int getVersion() {
      return version_;
    }
    /**
     * <code>int32 version = 2;</code>
     * @param value The version to set.
     * @return This builder for chaining.
     */
    public Builder setVersion(int value) {

      version_ = value;
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>int32 version = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearVersion() {
      bitField0_ = (bitField0_ & ~0x00000002);
      version_ = 0;
      onChanged();
      return this;
    }

    private int messageType_ ;
    /**
     * <code>int32 message_type = 3;</code>
     * @return The messageType.
     */
    @java.lang.Override
    public int getMessageType() {
      return messageType_;
    }
    /**
     * <code>int32 message_type = 3;</code>
     * @param value The messageType to set.
     * @return This builder for chaining.
     */
    public Builder setMessageType(int value) {

      messageType_ = value;
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     * <code>int32 message_type = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearMessageType() {
      bitField0_ = (bitField0_ & ~0x00000004);
      messageType_ = 0;
      onChanged();
      return this;
    }

    private int serializationType_ ;
    /**
     * <code>int32 serialization_type = 4;</code>
     * @return The serializationType.
     */
    @java.lang.Override
    public int getSerializationType() {
      return serializationType_;
    }
    /**
     * <code>int32 serialization_type = 4;</code>
     * @param value The serializationType to set.
     * @return This builder for chaining.
     */
    public Builder setSerializationType(int value) {

      serializationType_ = value;
      bitField0_ |= 0x00000008;
      onChanged();
      return this;
    }
    /**
     * <code>int32 serialization_type = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearSerializationType() {
      bitField0_ = (bitField0_ & ~0x00000008);
      serializationType_ = 0;
      onChanged();
      return this;
    }

    private int compressionType_ ;
    /**
     * <code>int32 compression_type = 5;</code>
     * @return The compressionType.
     */
    @java.lang.Override
    public int getCompressionType() {
      return compressionType_;
    }
    /**
     * <code>int32 compression_type = 5;</code>
     * @param value The compressionType to set.
     * @return This builder for chaining.
     */
    public Builder setCompressionType(int value) {

      compressionType_ = value;
      bitField0_ |= 0x00000010;
      onChanged();
      return this;
    }
    /**
     * <code>int32 compression_type = 5;</code>
     * @return This builder for chaining.
     */
    public Builder clearCompressionType() {
      bitField0_ = (bitField0_ & ~0x00000010);
      compressionType_ = 0;
      onChanged();
      return this;
    }

    private long requestId_ ;
    /**
     * <code>int64 request_id = 6;</code>
     * @return The requestId.
     */
    @java.lang.Override
    public long getRequestId() {
      return requestId_;
    }
    /**
     * <code>int64 request_id = 6;</code>
     * @param value The requestId to set.
     * @return This builder for chaining.
     */
    public Builder setRequestId(long value) {

      requestId_ = value;
      bitField0_ |= 0x00000020;
      onChanged();
      return this;
    }
    /**
     * <code>int64 request_id = 6;</code>
     * @return This builder for chaining.
     */
    public Builder clearRequestId() {
      bitField0_ = (bitField0_ & ~0x00000020);
      requestId_ = 0L;
      onChanged();
      return this;
    }

    private com.google.protobuf.ByteString data_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>bytes data = 7;</code>
     * @return The data.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString getData() {
      return data_;
    }
    /**
     * <code>bytes data = 7;</code>
     * @param value The data to set.
     * @return This builder for chaining.
     */
    public Builder setData(com.google.protobuf.ByteString value) {
      if (value == null) { throw new NullPointerException(); }
      data_ = value;
      bitField0_ |= 0x00000040;
      onChanged();
      return this;
    }
    /**
     * <code>bytes data = 7;</code>
     * @return This builder for chaining.
     */
    public Builder clearData() {
      bitField0_ = (bitField0_ & ~0x00000040);
      data_ = getDefaultInstance().getData();
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:com.web.rpc.core.protocol.RpcMessageProto)
  }

  // @@protoc_insertion_point(class_scope:com.web.rpc.core.protocol.RpcMessageProto)
  private static final com.web.rpc.core.protocol.proto.RpcMessageProto DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.web.rpc.core.protocol.proto.RpcMessageProto();
  }

  public static com.web.rpc.core.protocol.proto.RpcMessageProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<RpcMessageProto>
      PARSER = new com.google.protobuf.AbstractParser<RpcMessageProto>() {
    @java.lang.Override
    public RpcMessageProto parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      Builder builder = newBuilder();
      try {
        builder.mergeFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(builder.buildPartial());
      } catch (com.google.protobuf.UninitializedMessageException e) {
        throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(e)
            .setUnfinishedMessage(builder.buildPartial());
      }
      return builder.buildPartial();
    }
  };

  public static com.google.protobuf.Parser<RpcMessageProto> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<RpcMessageProto> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.web.rpc.core.protocol.proto.RpcMessageProto getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

