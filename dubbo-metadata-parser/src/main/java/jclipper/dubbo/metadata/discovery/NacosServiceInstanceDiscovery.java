package jclipper.dubbo.metadata.discovery;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2022/2/10 15:52.
 */
public class NacosServiceInstanceDiscovery implements ServiceInstanceDiscovery {

    private final NamingService namingService;

    public NacosServiceInstanceDiscovery(NamingService namingService) {
        this.namingService = namingService;
    }

    @Override
    public ServiceInstance discovery(String name, String ip, Integer port) {
        try {
            List<Instance> instances = namingService.getAllInstances(name);
            if (instances == null || instances.size() == 0) {
                return null;
            }
            Optional<Instance> find = instances.stream().filter(instance -> {
                boolean b = instance.getIp().equals(ip);
                if (port == null) {
                    return b;
                }
                return b && instance.getPort() == port;
            }).findFirst();
            if (find.isPresent()) {
                return hostToServiceInstance(find.get(), name);
            }
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ServiceInstance hostToServiceInstance(Instance instance,
                                                        String serviceId) {
        if (instance == null || !instance.isEnabled() || !instance.isHealthy()) {
            return null;
        }
        Map<String, String> metadata = new HashMap<>(8);
        metadata.put("nacos.instanceId", instance.getInstanceId());
        metadata.put("nacos.weight", instance.getWeight() + "");
        metadata.put("nacos.healthy", instance.isHealthy() + "");
        metadata.put("nacos.cluster", instance.getClusterName() + "");
        if (instance.getMetadata() != null) {
            metadata.putAll(instance.getMetadata());
        }
        metadata.put("nacos.ephemeral", String.valueOf(instance.isEphemeral()));
        boolean secure = false;
        if (metadata.containsKey("secure")) {
            secure = Boolean.parseBoolean(metadata.get("secure"));
        }

        return new DefaultServiceInstance(instance.getInstanceId(), serviceId, instance.getIp(), instance.getPort(), secure, metadata);
    }
}
