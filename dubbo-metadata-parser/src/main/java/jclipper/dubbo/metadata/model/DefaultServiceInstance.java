package jclipper.dubbo.metadata.model;

import lombok.Data;
import org.springframework.cloud.client.ServiceInstance;

import java.net.URI;
import java.util.Map;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2022/2/10 16:03.
 */
@Data
public class DefaultServiceInstance implements ServiceInstance {
    private String serviceId;

    private String host;

    private int port;

    private boolean secure;

    private Map<String, String> metadata;


    @Override
    public URI getUri() {
        return org.springframework.cloud.client.DefaultServiceInstance.getUri(this);
    }
}
