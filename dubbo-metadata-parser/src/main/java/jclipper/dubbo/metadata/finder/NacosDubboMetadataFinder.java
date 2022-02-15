package jclipper.dubbo.metadata.finder;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.client.naming.NacosNamingService;
import jclipper.dubbo.metadata.discovery.NacosServiceInstanceDiscovery;
import jclipper.dubbo.metadata.discovery.ServiceInstanceDiscovery;
import jclipper.dubbo.metadata.model.SelectRequest;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2022/2/10 16:16.
 */
public class NacosDubboMetadataFinder extends AbstractDubboMetadataFinder implements DubboMetadataFinder {
    private static final Map<String, ServiceInstanceDiscovery> CACHE = new ConcurrentHashMap<>();

    @Override
    public ServiceInstanceDiscovery getServiceInstanceDiscovery(SelectRequest request) {
        String serverId = buildServerId(request);
        return CACHE.computeIfAbsent(serverId, k -> new NacosServiceInstanceDiscovery(createNamingService(request)));
    }

    private String buildServerId(SelectRequest request) {
        return String.format("%s#%s", request.getServerAddress(), request.getNamespace());
    }

    private NamingService createNamingService(SelectRequest request) {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, request.getServerAddress());
        properties.setProperty(PropertyKeyConst.NAMESPACE, request.getNamespace());
        try {
            return new NacosNamingService(properties);
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return null;
    }

}
