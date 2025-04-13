package com.web.rpc.core;


import com.web.rpc.core.serialize.JsonSerializer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsonSerializerTest {
    @Test
    public void testSerializeDeserialize() throws Exception {
        JsonSerializer serializer = new JsonSerializer();
        RpcRequest request = new RpcRequest();
        request.setRequestId("123");
        request.setServiceName("HelloService");

        byte[] bytes = serializer.serialize(request);
        RpcRequest deserialized = serializer.deserialize(bytes, RpcRequest.class);

        assertEquals(request.getRequestId(), deserialized.getRequestId());
        assertEquals(request.getServiceName(), deserialized.getServiceName());
    }
}