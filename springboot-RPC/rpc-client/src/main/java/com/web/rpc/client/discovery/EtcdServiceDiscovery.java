package com.web.rpc.client.discovery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.rpc.core.registry.ServiceInfo;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 基于ETCD的服务发现实现
 */
public class EtcdServiceDiscovery implements ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(EtcdServiceDiscovery.class);
    private static final String ETCD_ROOT = "/rpc/services/";

    private final Client etcdClient;
    private final ObjectMapper objectMapper;
    private final Map<String, List<ServiceInfo>> serviceCache = new ConcurrentHashMap<>();
    private final LoadBalancer loadBalancer;

    public EtcdServiceDiscovery(String... endpoints) {
        this(new RandomLoadBalancer(), endpoints);
    }

    public EtcdServiceDiscovery(LoadBalancer loadBalancer, String... endpoints) {
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

        this.etcdClient = Client.builder().endpoints(endpoints).build();
        this.objectMapper = new ObjectMapper();
        this.loadBalancer = loadBalancer;
    }

    @Override
    public ServiceInfo discover(String serviceName) {
        List<ServiceInfo> serviceInfos = getServiceInstances(serviceName);
        if (serviceInfos == null || serviceInfos.isEmpty()) {
            throw new RuntimeException("No available service instances for: " + serviceName);
        }

        // 使用负载均衡选择一个服务实例
        ServiceInfo serviceInfo = loadBalancer.select(serviceInfos);
        logger.info("Selected service instance: {}:{} for {}",
                serviceInfo.getHost(), serviceInfo.getPort(), serviceName);

        return serviceInfo;
    }

    @Override
    public List<ServiceInfo> getServiceInstances(String serviceName) {
        // 先从缓存获取
        List<ServiceInfo> serviceInfos = serviceCache.get(serviceName);
        if (serviceInfos == null || serviceInfos.isEmpty()) {
            // 缓存未命中，从ETCD获取
            serviceInfos = fetchServiceInstances(serviceName);
            if (serviceInfos != null && !serviceInfos.isEmpty()) {
                serviceCache.put(serviceName, serviceInfos);
            }
        }
        return serviceInfos;
    }

    private List<ServiceInfo> fetchServiceInstances(String serviceName) {
        try {
            String key = ETCD_ROOT + serviceName;
            ByteSequence keyBytes = ByteSequence.from(key.getBytes(StandardCharsets.UTF_8));
            GetOption getOption = GetOption.newBuilder().withPrefix(ByteSequence.from(key.getBytes())).build();

            GetResponse response = etcdClient.getKVClient().get(keyBytes, getOption).get();
            if (response.getKvs().isEmpty()) {
                logger.warn("No service instances found for: {}", serviceName);
                return new ArrayList<>();
            }

            List<ServiceInfo> serviceInfos = new ArrayList<>(response.getKvs().size());
            for (KeyValue kv : response.getKvs()) {
                String value = kv.getValue().toString(StandardCharsets.UTF_8);
                ServiceInfo serviceInfo = objectMapper.readValue(value, ServiceInfo.class);
                serviceInfos.add(serviceInfo);
            }

            logger.info("Fetched {} service instances for {}", serviceInfos.size(), serviceName);
            return serviceInfos;
        } catch (Exception e) {
            logger.error("Failed to fetch service instances for: " + serviceName, e);
            return new ArrayList<>();
        }
    }

    @Override
    public void refreshService(String serviceName) {
        logger.info("Refreshing service cache for: {}", serviceName);
        List<ServiceInfo> serviceInfos = fetchServiceInstances(serviceName);
        if (serviceInfos != null && !serviceInfos.isEmpty()) {
            serviceCache.put(serviceName, serviceInfos);
        } else {
            serviceCache.remove(serviceName);
        }
    }

    @Override
    public void close() {
        if (etcdClient != null) {
            etcdClient.close();
            logger.info("ETCD client closed");
        }
        serviceCache.clear();
    }

    /**
     * 负载均衡器接口
     */
    public interface LoadBalancer {
        /**
         * 从服务实例列表中选择一个实例
         */
        ServiceInfo select(List<ServiceInfo> instances);
    }

    /**
     * 随机负载均衡器
     */
    public static class RandomLoadBalancer implements LoadBalancer {
        @Override
        public ServiceInfo select(List<ServiceInfo> instances) {
            if (instances == null || instances.isEmpty()) {
                return null;
            }
            int index = ThreadLocalRandom.current().nextInt(instances.size());
            return instances.get(index);
        }
    }
}
