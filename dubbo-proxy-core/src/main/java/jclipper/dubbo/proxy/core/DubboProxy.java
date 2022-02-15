package jclipper.dubbo.proxy.core;

import jclipper.dubbo.proxy.core.discovery.ApplicationLevelUrlDiscovery;
import jclipper.dubbo.proxy.core.discovery.DubboInvokeUrlDiscovery;
import jclipper.dubbo.proxy.finder.ServiceFinder;
import lombok.SneakyThrows;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/18 14:09.
 */
public abstract class DubboProxy {
    private final ServiceFinder finder;

    private final Class<?> target;
    private final String application;
    private URL directUrl;
    private final Class<?> realInterface;

    public DubboProxy(Class<?> target, ServiceFinder finder, String providerName) {
        this.target = target;
        this.finder = finder;
        this.application = providerName;
        this.realInterface = getRealInterface(target);
    }

    public void setDirectUrl(URL directUrl) {
        this.directUrl = directUrl;
    }

    protected Object invoke(Method method, Object[] args) {
        if ("equals".equals(method.getName())) {
            try {
                Object otherHandler = args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) : null;
                return equals(otherHandler);
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else if ("hashCode".equals(method.getName())) {
            return hashCode();
        } else if ("toString".equals(method.getName())) {
            return toString();
        }
        Supplier<Object> supplier = () -> invokeDubboMethod(method, args);
        return supplier.get();
    }

    @SneakyThrows
    private Object invokeDubboMethod(Method method, Object[] args) {
        ApplicationConfig application = new ApplicationConfig(this.application);

        ReferenceConfig<?> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setApplication(application);
        referenceConfig.setInterface(realInterface);
        referenceConfig.setUrl(buildUrl().toFullString());
        Object obj = referenceConfig.get();
        Method proxyMethod = obj.getClass().getMethod(method.getName(), method.getParameterTypes());
        return proxyMethod.invoke(obj, args);
    }


    public static Class<?> getRealInterface(Class<?> source) {
        return source.isInterface() ? source : source.getInterfaces()[0];
    }


    private URL buildUrl() {
        if (directUrl != null) {
            return directUrl;
        }
        DubboInvokeUrlDiscovery parser = new ApplicationLevelUrlDiscovery(finder, application);
        return parser.getInvokeUrl(realInterface);
    }

    @Override
    public boolean equals(Object obj) {
        return target.equals(obj.getClass());
    }

    @Override
    public int hashCode() {
        return this.target.hashCode();
    }

    @Override
    public String toString() {
        return this.target.toString();
    }

}
