package jclipper.dubbo.metadata.finder;

import com.alibaba.cloud.dubbo.service.DubboMetadataService;
import jclipper.dubbo.metadata.discovery.ServiceInstanceDiscovery;
import jclipper.dubbo.metadata.model.DubboUrlInfo;
import jclipper.dubbo.metadata.model.SelectRequest;
import jclipper.dubbo.metadata.proxy.SimpleDubboMetadataServiceProxy;
import org.springframework.cloud.client.ServiceInstance;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2022/2/10 16:33.
 */
public abstract class AbstractDubboMetadataFinder implements DubboMetadataFinder {

    protected SimpleDubboMetadataServiceProxy dubboMetadataServiceProxy;

    public AbstractDubboMetadataFinder() {
        this(new SimpleDubboMetadataServiceProxy());
    }

    public AbstractDubboMetadataFinder(SimpleDubboMetadataServiceProxy dubboMetadataServiceProxy) {
        this.dubboMetadataServiceProxy = dubboMetadataServiceProxy;
    }

    /**
     * 获取 ServiceInstanceDiscovery
     *
     * @param request SelectRequest
     * @return
     */
    protected abstract ServiceInstanceDiscovery getServiceInstanceDiscovery(SelectRequest request);

    @Override
    public Map<String, List<DubboUrlInfo>> getDubboMetadata(SelectRequest request) {
        ServiceInstanceDiscovery discovery = getServiceInstanceDiscovery(request);
        ServiceInstance instance = discovery.discovery(request.getServiceName(), request.getIp(), request.getPort());
        if (instance == null) {
            return Collections.emptyMap();
        }
        DubboMetadataService proxy = dubboMetadataServiceProxy.getProxy(instance);
        Map<String, String> urls = proxy.getAllExportedURLs();
        return DubboUrlInfo.parse(urls);
    }
}
