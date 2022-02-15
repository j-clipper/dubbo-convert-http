package jclipper.dubbo.proxy.core.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2020/8/11 10:24.
 */
public interface BaseCode<C, N> {
    /**
     * 错误码
     *
     * @return
     */
    C getCode();

    /**
     * 错误信息
     *
     * @return
     */
    N getName();

    /**
     * 根据ID查找具体的枚举值
     *
     * @param clazz 枚举类型
     * @param code  code
     */
    static <E extends Enum<E> & BaseCode, C> E getByCode(Class<E> clazz, C code) {
        return Arrays.stream(clazz.getEnumConstants())
                .filter(t -> t.getCode().equals(code))
                .findFirst().orElse(null);
    }

    /**
     * 将枚举类转为Map
     *
     * @param clazz 枚举类型
     */
    static <E extends Enum<E> & BaseCode<K, V>, K, V> Map<K, V> toMap(Class<E> clazz) {
        E[] array = clazz.getEnumConstants();
        Map<K, V> map = new HashMap<>(array.length);
        for (E e : array) {
            map.put(e.getCode(), e.getName());
        }
        return map;
    }
    /**
     * ToString ,格式：
     * code1:name1;code2:name2;....
     */
    static <E extends Enum<E> & BaseCode> String toString(Class<E> clazz) {
        return Arrays.stream(clazz.getEnumConstants())
                .map(e -> e.getCode() + ":" + e.getName() + ";")
                .collect(Collectors.joining());
    }

}
