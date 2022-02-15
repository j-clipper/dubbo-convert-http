package jclipper.dubbo.proxy.core;

import jclipper.dubbo.proxy.finder.ServiceFinder;
import lombok.SneakyThrows;
import org.apache.dubbo.common.URL;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/16 15:08.
 */
public class InvocationHandlerDubboProxy extends DubboProxy implements InvocationHandler {

    public InvocationHandlerDubboProxy(Class<?> target, ServiceFinder finder, String providerName) {
        super(target, finder, providerName);
    }

    @Override
    @SneakyThrows
    public Object invoke(Object proxy, Method method, Object[] args) {
        return invoke(method, args);
    }


    public static <T> T createProxy(Class<T> target, ServiceFinder finder, String providerName) {
        return createProxy(target, finder, providerName, null);
    }

    public static <T> T createProxy(Class<T> target, ServiceFinder finder, String providerName, URL url) {
        InvocationHandlerDubboProxy handler = new InvocationHandlerDubboProxy(target, finder, providerName);
        handler.setDirectUrl(url);
        return (T) Proxy.newProxyInstance(target.getClassLoader(), new Class<?>[]{target}, handler);
    }

}
