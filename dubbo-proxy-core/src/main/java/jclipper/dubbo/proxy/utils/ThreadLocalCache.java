package jclipper.dubbo.proxy.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/25 13:44.
 */
public class ThreadLocalCache {
    private static final ThreadLocal<Map<String, Object>> CACHE = new InheritableThreadLocal<>();

    static {
        CACHE.set(new ConcurrentHashMap<>());
    }


    public static <T> void set(String key, T value) {
        Map<String, Object> map = CACHE.get();
        map.put(key, value);
    }

    public static <T> T get(String key) {
        Map<String, Object> map = CACHE.get();
        return (T) map.get(key);
    }
}
