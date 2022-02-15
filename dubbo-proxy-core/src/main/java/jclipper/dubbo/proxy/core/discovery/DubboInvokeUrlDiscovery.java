package jclipper.dubbo.proxy.core.discovery;


import org.apache.dubbo.common.URL;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/23 21:07.
 */
public interface DubboInvokeUrlDiscovery {

    /**
     * 获取Dubbo接口调用的url地址
     *
     * @param dubboInterface
     * @return
     */
    URL getInvokeUrl(Class<?> dubboInterface);

}
