package jclipper.dubbo.proxy.core;

import jclipper.dubbo.proxy.finder.ServiceFinder;
import org.apache.dubbo.common.URL;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/18 14:33.
 */
public class DubboProxyFactory {

    public static <T> T create(Class<T> target, ServiceFinder finder, String providerName) {
        return create(target, finder, providerName, null);
    }

    public static <T> T create(Class<T> target, ServiceFinder finder, String providerName, URL url) {
        return MethodInterceptorDubboProxy.createProxy(target, finder, providerName, url);
    }

}
