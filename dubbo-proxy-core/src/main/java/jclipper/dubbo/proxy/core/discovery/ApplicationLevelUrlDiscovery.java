package jclipper.dubbo.proxy.core.discovery;

import jclipper.dubbo.proxy.core.DubboProxyFactory;
import jclipper.dubbo.proxy.finder.ServiceFinder;
import jclipper.dubbo.proxy.model.ServiceInstance;
import com.alibaba.cloud.dubbo.service.DubboMetadataService;
import com.alibaba.fastjson.JSON;
import org.apache.dubbo.common.URL;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/17 14:47.
 */
public class ApplicationLevelUrlDiscovery implements DubboInvokeUrlDiscovery {
    private static final String DUBBO_METADATA_SERVICE_URLS_KEY = "dubbo.metadata-service.urls";
    private static final String DUBBO_INTERFACE_KEY = "interface";

    private final ServiceFinder finder;
    /**
     * 项目名称
     */
    private final String application;

    private URL metadataUrl;

    private Map<String, URL> serviceMap = Collections.emptyMap();

    public ApplicationLevelUrlDiscovery(ServiceFinder finder, String application) {
        this.finder = finder;
        this.application = application;
        init();
    }

    @Override
    public URL getInvokeUrl(Class<?> clazz) {
        return serviceMap.get(clazz.getName());
    }

    private void init() {
        ServiceInstance instance = finder.chooseInstance(application);
        if (instance == null || instance.getMetadata() == null) {
            return;
        }
        String data = instance.getMetadata().get(DUBBO_METADATA_SERVICE_URLS_KEY);
        List<String> list = JSON.parseArray(data, String.class);
        if (list.isEmpty()) {
            return;
        }
        String string = list.get(0);
        metadataUrl = URL.valueOf(string);

        parseServiceUrls();
    }

    private void parseServiceUrls() {
        DubboMetadataService instance = DubboProxyFactory.create(DubboMetadataService.class, finder, application, metadataUrl);
        Map<String, String> exportedUrls = instance.getAllExportedURLs();
        if (exportedUrls == null || exportedUrls.isEmpty()) {
            return;
        }
        serviceMap = new HashMap<>(exportedUrls.size());
        exportedUrls.forEach((k, v) -> {
            List<String> list = JSON.parseArray(v, String.class);
            if (list.isEmpty()) {
                return;
            }
            String string = list.get(0);
            URL url = URL.valueOf(string);
            serviceMap.put(getServiceName(url), url);
        });

    }

    private String getServiceName(URL url) {
        return url.getParameter(DUBBO_INTERFACE_KEY);
    }


}
