package jclipper.dubbo.proxy.finder;

import jclipper.dubbo.proxy.model.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/17 15:09.
 */
public class SpringCloudServiceFinder extends ServiceFinder {

    private final DiscoveryClient discoveryClient;

    public SpringCloudServiceFinder(InstanceChooser chooser,DiscoveryClient discoveryClient) {
        super(chooser);
        this.discoveryClient = discoveryClient;
    }

    @Override
    public List<ServiceInstance> getInstances(String service) {
        List<org.springframework.cloud.client.ServiceInstance> instances = discoveryClient.getInstances(service);
        if (instances == null || instances.isEmpty()) {
            return Collections.emptyList();
        }
        return instances.stream().map(i -> {
            ServiceInstance e = new ServiceInstance();
            e.setId(i.getInstanceId());
            e.setIp(i.getHost());
            e.setPort(i.getPort());
            e.setMetadata(i.getMetadata());
            return e;
        }).collect(Collectors.toList());
    }
}
