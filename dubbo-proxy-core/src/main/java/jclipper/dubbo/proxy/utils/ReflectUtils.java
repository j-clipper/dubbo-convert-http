package jclipper.dubbo.proxy.utils;

import com.google.common.collect.Sets;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.temporal.Temporal;
import java.util.*;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/19 00:12.
 */
public class ReflectUtils {
    public static final Set<Type> BASIC_TYPES = Sets.newHashSet(
            Boolean.TYPE, Character.TYPE, Byte.TYPE, Short.TYPE, Integer.TYPE, Float.TYPE, Long.TYPE, Double.TYPE,
            Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Float.class, Long.class, Double.class, String.class
    );


    public static boolean isBasicType(Type type) {
        return BASIC_TYPES.contains(type) || (type instanceof Number) || (type instanceof Date) || (type instanceof Temporal) || (type instanceof Enumeration);
    }

    public static boolean isCollectionType(Class<?> type) {
        return Collection.class.isAssignableFrom(type);
    }

    public static boolean isMapType(Class<?> type) {
        return Map.class.isAssignableFrom(type);
    }

    public static boolean isArrayType(Parameter parameter) {
        return parameter.getType().isArray();
    }

    public static Class<?> getArrayType(Parameter parameter) {
        return parameter.getClass().getComponentType();
    }

    public static boolean isParameterizedType(Type type) {
        return type instanceof ParameterizedType;
    }

    public static Type getParameterizedActualType(Type type) {
        return ((ParameterizedType) type).getActualTypeArguments()[0];
    }

    public static boolean isCustomType(Parameter parameter) {
        Class<?> type = parameter.getType();
        return !isBasicType(type) && !isMapType(type) && !isParameterizedType(type);
    }

    public static boolean isCustomTypeAndAllBasicFields(Parameter parameter) {
        if (!isCustomType(parameter)) {
            return false;
        }
        return hasComplexFields(parameter.getType());
    }

    public static boolean hasComplexFields(Class<?> type) {
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            Class<?> ft = field.getType();
            if (!isBasicType(ft)) {
                return false;
            }
        }
        return true;
    }

//    public static boolean


}
