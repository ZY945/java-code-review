package com.web.rpc.core.registry;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class EtcdServiceRegistry implements ServiceRegistry {
    private static final Logger logger = LoggerFactory.getLogger(EtcdServiceRegistry.class);
    private static final String ETCD_ROOT = "/rpc/services/";
    private static final long LEASE_TTL = 30; // 租约时间，单位秒

    private final Client etcdClient;
    private final KV kvClient;
    private final Lease leaseClient;
    private final ObjectMapper objectMapper;
    private long leaseId;

    public EtcdServiceRegistry(String... endpoints) {
        logger.info("Initializing ETCD client with endpoints: {}", String.join(", ", endpoints));
        
        // 处理端点格式
        for (int i = 0; i < endpoints.length; i++) {
            String endpoint = endpoints[i].trim();
            // 检查是否包含协议前缀
            if (!endpoint.startsWith("http://") && !endpoint.startsWith("https://")) {
                endpoints[i] = "http://" + endpoint;
            }
            // 检查是否包含端口
            if (!endpoint.contains(":")) {
                endpoints[i] = endpoints[i] + ":2379";
            }
            logger.info("Using ETCD endpoint: {}", endpoints[i]);
        }
        
        try {
            this.etcdClient = Client.builder().endpoints(endpoints).build();
            this.kvClient = etcdClient.getKVClient();
            this.leaseClient = etcdClient.getLeaseClient();
            this.objectMapper = new ObjectMapper();
            initLease();
        } catch (Exception e) {
            logger.error("Failed to initialize ETCD client with endpoints: {}", String.join(", ", endpoints), e);
            throw new RuntimeException("Failed to initialize ETCD client", e);
        }
    }

    private void initLease() {
        try {
            this.leaseId = leaseClient.grant(LEASE_TTL).get().getID();
            // 自动续约
            leaseClient.keepAlive(leaseId, new StreamObserver<LeaseKeepAliveResponse>() {
                @Override
                public void onNext(LeaseKeepAliveResponse value) {
                    logger.debug("Lease renewed: {}", leaseId);
                }

                @Override
                public void onError(Throwable t) {
                    logger.error("Error renewing lease", t);
                }

                @Override
                public void onCompleted() {
                    logger.info("Lease complete");
                }
            });
        } catch (Exception e) {
            logger.error("Failed to initialize lease", e);
            throw new RuntimeException("Failed to initialize lease", e);
        }
    }

    @Override
    public void register(String serviceName, String host, int port) {
        try {
            ServiceInfo serviceInfo = new ServiceInfo(host, port);
            String key = ETCD_ROOT + serviceName;
            String value = objectMapper.writeValueAsString(serviceInfo);

            ByteSequence keyBytes = ByteSequence.from(key.getBytes(StandardCharsets.UTF_8));
            ByteSequence valueBytes = ByteSequence.from(value.getBytes(StandardCharsets.UTF_8));

            // 使用租约注册服务
            PutOption putOption = PutOption.newBuilder().withLeaseId(leaseId).build();
            kvClient.put(keyBytes, valueBytes, putOption).get();
            logger.info("Registered service {} at {}:{}", serviceName, host, port);
        } catch (Exception e) {
            logger.error("Failed to register service: " + serviceName, e);
            throw new RuntimeException("Failed to register service", e);
        }
    }

    @Override
    public void unregister(String serviceName, String host, int port) {
        try {
            String key = ETCD_ROOT + serviceName;
            ByteSequence keyBytes = ByteSequence.from(key.getBytes(StandardCharsets.UTF_8));
            kvClient.delete(keyBytes).get();
            logger.info("Unregistered service {} at {}:{}", serviceName, host, port);
        } catch (Exception e) {
            logger.error("Failed to unregister service: " + serviceName, e);
            throw new RuntimeException("Failed to unregister service", e);
        }
    }

    @Override
    public ServiceInfo getService(String serviceName) {
        try {
            String key = ETCD_ROOT + serviceName;
            ByteSequence keyBytes = ByteSequence.from(key.getBytes(StandardCharsets.UTF_8));
            GetOption getOption = GetOption.newBuilder().withPrefix(ByteSequence.from(key.getBytes())).build();

            GetResponse response = kvClient.get(keyBytes, getOption).get();
            if (response.getKvs().isEmpty()) {
                logger.warn("Service not found: {}", serviceName);
                return null;
            }

            String value = response.getKvs().get(0).getValue().toString(StandardCharsets.UTF_8);
            return objectMapper.readValue(value, ServiceInfo.class);
        } catch (Exception e) {
            logger.error("Failed to get service: " + serviceName, e);
            throw new RuntimeException("Failed to get service", e);
        }
    }

    @Override
    public void close() {
        try {
            if (leaseId != 0) {
                leaseClient.revoke(leaseId).get(5, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            logger.error("Error revoking lease", e);
        } finally {
            etcdClient.close();
        }
    }
} 