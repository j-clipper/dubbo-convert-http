package jclipper.dubbo.proxy.finder;

import jclipper.dubbo.proxy.model.ServiceInstance;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/25 13:42.
 */
public class DirectIpInstanceChooser implements InstanceChooser {

    private static String getHeaderFromHttpRequest(String key) {
        HttpServletRequest request = httpServletRequest();
        if (request == null) {
            return null;
        }
        return request.getHeader(key);
    }

    private static String getTargetIpFromHttpRequest() {
        return getHeaderFromHttpRequest("X-Dubbo-Proxy-Ip");
    }

    private static HttpServletRequest httpServletRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }
            return attributes.getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public ServiceInstance choose(List<ServiceInstance> instances) {
        ServiceInstance instance;
        if (instances == null || instances.isEmpty()) {
            return null;
        }
        String ip = getTargetIpFromHttpRequest();
        if (ip != null) {
            instance = instances.stream().filter(i -> i.getIp().equalsIgnoreCase(ip)).findFirst().orElse(null);
            if (instance != null) {
                return instance;
            }

        }
        return instances.get(0);
    }
}
