package jclipper.dubbo.proxy.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/6/29 21:00.
 */
public class MapBuilder {
    private Map<String, Object> map;

    public static MapBuilder newInstance() {
        return new MapBuilder();
    }

    public static MapBuilder of(Object... array) {
        return new MapBuilder(array);
    }

    public MapBuilder(Object... array) {
        this.map = convert(array);
    }

    public MapBuilder add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

    public MapBuilder add(Object... array) {
        this.map.putAll(convert(array));
        return this;
    }

    public MapBuilder addIfAbsent(String key, Object value) {
        this.map.putIfAbsent(key, value);
        return this;
    }

    public Map<String, Object> build() {
        return this.map;
    }

    private Map<String, Object> convert(Object... array) {
        if (array.length % 2 != 0) {
            throw new IllegalArgumentException();
        }
        if (array.length == 0) {
            return new HashMap<>(8);
        }
        int size = array.length / 2;
        Map<String, Object> map = new HashMap<>(size);

        for (int i = 0; i < array.length; i += 2) {
            map.put((String) array[i], array[i + 1]);
        }
        return map;
    }
}
