package jclipper.dubbo.metadata.proxy;

import com.alibaba.cloud.dubbo.service.DubboMetadataService;
import com.alibaba.cloud.dubbo.util.JSONUtils;
import jclipper.dubbo.proxy.core.DubboProxyFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.URL;
import org.springframework.cloud.client.ServiceInstance;

import java.util.List;
import java.util.Map;

import static org.apache.dubbo.common.constants.CommonConstants.APPLICATION_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.VERSION_KEY;
import static org.apache.dubbo.registry.client.metadata.ServiceInstanceMetadataUtils.METADATA_SERVICE_URLS_PROPERTY_NAME;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2022/2/10 15:45.
 */
@Slf4j
public class SimpleDubboMetadataServiceProxy {
    private final JSONUtils jsonUtils;

    public SimpleDubboMetadataServiceProxy() {
        this(new JSONUtils());
    }

    public SimpleDubboMetadataServiceProxy(JSONUtils jsonUtils) {
        this.jsonUtils = jsonUtils;
    }

    /**
     * Get the proxy of {@link DubboMetadataService} if possible.
     *
     * @param serviceInstance the instances of {@link DubboMetadataService}
     * @return <code>null</code> if initialization can't be done
     */
    public DubboMetadataService getProxy(ServiceInstance serviceInstance) {

        List<URL> dubboMetadataServiceURLs = getDubboMetadataServiceURLs(serviceInstance);

        for (URL dubboMetadataServiceURL : dubboMetadataServiceURLs) {
            DubboMetadataService dubboMetadataService = createProxyIfAbsent(
                    dubboMetadataServiceURL);
            if (dubboMetadataService != null) {
                return dubboMetadataService;
            }
        }

        return null;
    }


    public List<URL> getDubboMetadataServiceURLs(ServiceInstance serviceInstance) {
        Map<String, String> metadata = serviceInstance.getMetadata();
        String dubboURLsJSON = metadata.get(METADATA_SERVICE_URLS_PROPERTY_NAME);
        return jsonUtils.toURLs(dubboURLsJSON);
    }


    /**
     * Create a {@link DubboMetadataService}'s Proxy If abstract.
     *
     * @param dubboMetadataServiceURL the {@link URL} of {@link DubboMetadataService}
     * @return a {@link DubboMetadataService} proxy
     */
    private DubboMetadataService createProxyIfAbsent(URL dubboMetadataServiceURL) {
        String serviceName = dubboMetadataServiceURL.getParameter(APPLICATION_KEY);
        String version = dubboMetadataServiceURL.getParameter(VERSION_KEY);

        // Initialize DubboMetadataService with right version
        return DubboProxyFactory.create(DubboMetadataService.class, null, serviceName, dubboMetadataServiceURL);
    }
}
