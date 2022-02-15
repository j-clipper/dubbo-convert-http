package jclipper.dubbo.proxy.core;

import jclipper.dubbo.proxy.finder.ServiceFinder;
import org.apache.dubbo.common.URL;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/18 14:09.
 */
public class MethodInterceptorDubboProxy extends DubboProxy implements MethodInterceptor {


    public MethodInterceptorDubboProxy(Class<?> target, ServiceFinder finder, String providerName) {
        super(target, finder, providerName);
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) {
        return invoke(method, args);
    }

    /**
     * 该函数用于生成代理
     */
    public static <T> T createProxy(Class<T> clazz, ServiceFinder finder, String providerName) {
        return createProxy(clazz, finder, providerName,null);
    }

    public static <T> T createProxy(Class<T> clazz, ServiceFinder finder, String providerName, URL url) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        MethodInterceptorDubboProxy interceptor = new MethodInterceptorDubboProxy(clazz, finder, providerName);
        interceptor.setDirectUrl(url);

        enhancer.setCallback(interceptor);
        return (T)enhancer.create();
    }

}
