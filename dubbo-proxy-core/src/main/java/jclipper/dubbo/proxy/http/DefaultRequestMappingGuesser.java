package jclipper.dubbo.proxy.http;

import com.google.common.collect.Sets;
import jclipper.dubbo.proxy.utils.ReflectUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;

import static jclipper.dubbo.proxy.utils.AnnotationBuilder.addRequestBodyIfNeed;
import static jclipper.dubbo.proxy.utils.AnnotationBuilder.addRequestParamIfNeed;


/**
 * 默认的RequestMappingGuesser
 *
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/18 23:17.
 */
public class DefaultRequestMappingGuesser implements RequestMappingGuesser {

    public static final Map<RequestMethod, Set<String>> DEFAULT_HTTP_METHOD_KEYWORDS = new TreeMap<>();

    static {
        DEFAULT_HTTP_METHOD_KEYWORDS.put(RequestMethod.GET, Sets.newHashSet("get", "query", "find", "select", "count", "check", "show", "page", "batchGet", "batchQuery", "batchFind", "batchSelect"));
        DEFAULT_HTTP_METHOD_KEYWORDS.put(RequestMethod.POST, Sets.newHashSet("new", "insert", "add", "create", "save", "batchAdd", "batchInsert", "batchCreate", "batchSave", "login", "logout", "post"));
        DEFAULT_HTTP_METHOD_KEYWORDS.put(RequestMethod.PUT, Sets.newHashSet("update", "mod", "batchUpdate", "batchMod", "change", "batchChange", "put"));
        DEFAULT_HTTP_METHOD_KEYWORDS.put(RequestMethod.DELETE, Sets.newHashSet("del", "remove", "batchDel", "batchRemove"));
        DEFAULT_HTTP_METHOD_KEYWORDS.put(RequestMethod.HEAD, Sets.newHashSet());
        DEFAULT_HTTP_METHOD_KEYWORDS.put(RequestMethod.PATCH, Sets.newHashSet());
        DEFAULT_HTTP_METHOD_KEYWORDS.put(RequestMethod.OPTIONS, Sets.newHashSet());
    }


    private final Map<RequestMethod, Set<String>> requestMethodKeywords;

    private final Map<RequestMethod, Set<String>> extendRequestMethodKeywords;

    public DefaultRequestMappingGuesser(Map<RequestMethod, Set<String>> requestMethodKeywords, Map<RequestMethod, Set<String>> extendRequestMethodKeywords) {
        this.requestMethodKeywords = requestMethodKeywords;
        this.extendRequestMethodKeywords = extendRequestMethodKeywords;
    }


    @Override
    public RequestMethod guess(Method method) {
        RequestMethod hm = guessMethodByName(method, requestMethodKeywords);
        if (hm == null && extendRequestMethodKeywords != null && !extendRequestMethodKeywords.isEmpty()) {
            hm = guessMethodByName(method, extendRequestMethodKeywords);
        }
        if (hm == null) {
            return guessMethodByParameters(method);
        }
        return hm;
    }

    protected RequestMethod guessMethodByName(Method method, Map<RequestMethod, Set<String>> requestMethodKeywords) {
        if (requestMethodKeywords == null || requestMethodKeywords.isEmpty()) {
            return null;
        }
        String name = method.getName().toLowerCase();
        for (Map.Entry<RequestMethod, Set<String>> entry : requestMethodKeywords.entrySet()) {
            Set<String> keywords = entry.getValue();
            if (keywords == null || keywords.isEmpty()) {
                continue;
            }
            if (keywords.stream().anyMatch(k -> name.startsWith(k.toLowerCase()))) {
                return entry.getKey();
            }
        }
        return null;
    }


    protected RequestMethod guessMethodByParameters(Method method) {
        Parameter[] parameters = method.getParameters();
        //1. 无参方法，则使用GET
        if (parameters.length == 0) {
            return RequestMethod.GET;
        }
        //2. 方法参数长度为1时
        if (parameters.length == 1) {
            Parameter parameter = parameters[0];
            return guessMethodByOnlyOneParameter(parameter);
        } else {
            //3. 方法参数长度大于1时
            return guessMethodByParameters(parameters);
        }
    }

    private RequestMethod guessMethodByParameters(Parameter[] parameters) {
        //a. 所有参数都是基本类型，则用GET
        if (Arrays.stream(parameters).allMatch(p -> ReflectUtils.isBasicType(p.getType()))) {
            return RequestMethod.GET;
        }
        //b. 带有Map参数则用POST
        if (Arrays.stream(parameters).anyMatch(p -> ReflectUtils.isMapType(p.getType()))) {
            return RequestMethod.POST;
        }
        //c. 有任何自定义类型，并且自定义类型的字段里面带有复杂类型则用POST
        if (Arrays.stream(parameters).anyMatch(p -> ReflectUtils.isCustomType(p) && !ReflectUtils.isCustomTypeAndAllBasicFields(p))) {
            return RequestMethod.POST;
        }
        //d. 有任何泛型类型，并且泛型的实际类型的的字段里面带有复杂类型则用POST
        if (Arrays.stream(parameters).anyMatch(p -> ReflectUtils.isParameterizedType(p.getType()) && !ReflectUtils.hasComplexFields((Class<?>) ReflectUtils.getParameterizedActualType(p.getType())))) {
            return RequestMethod.POST;
        }
        //e. 有任何数组，并且数组类型的字段里面带有复杂类型则用POST
        if (Arrays.stream(parameters).anyMatch(p -> ReflectUtils.isArrayType(p) && !ReflectUtils.hasComplexFields(ReflectUtils.getArrayType(p)))) {
            return RequestMethod.POST;
        }
        //f. 其他情况使用GET
        return RequestMethod.GET;
    }

    /**
     * 方法参数长度为1时的请求类型匹配
     *
     * @param parameter Parameter
     * @return
     */
    private RequestMethod guessMethodByOnlyOneParameter(Parameter parameter) {
        Type type = parameter.getParameterizedType();
        //a. 参数类型是基本类型，使用GET
        if (ReflectUtils.isBasicType(type)) {
            return RequestMethod.GET;
        }
        //b. 参数类型是Map，使用GET
        if (ReflectUtils.isMapType(parameter.getType())) {
            return RequestMethod.POST;
        }
        //c. 参数类型是泛型，且泛型的实际类型是基本类型，使用GET，例如List<Long>
        if (ReflectUtils.isParameterizedType(type)) {
            Type actualType = ReflectUtils.getParameterizedActualType(type);
            if (ReflectUtils.isBasicType(actualType)) {
                return RequestMethod.GET;
            }
        }
        //d. 参数类型是数组，且数组的实际类型是基本类型，使用GET，例如String[]
        if (ReflectUtils.isArrayType(parameter)) {
            Class<?> arrayType = ReflectUtils.getArrayType(parameter);
            if (ReflectUtils.isBasicType(arrayType)) {
                return RequestMethod.GET;
            }
        }
        //e. 其他情况使用POST
        return RequestMethod.POST;
    }

    @Override
    public List<String> completeParametersMvcAnnotation(Method method, RequestMethod requestMethod) {
        if (method.getParameterCount() == 0) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        //方法参数长度为1时
        if (method.getParameterCount() == 1) {
            Parameter p = method.getParameters()[0];
            if (!ReflectUtils.isBasicType(p.getType())) {
                if (RequestMethod.GET == requestMethod) {
                    //1. 如果请求方式为GET，并且参数类型是集合或数组，则为参数设置RequestParam注解
                    if (ReflectUtils.isCollectionType(p.getType()) || ReflectUtils.isArrayType(p)) {
                        addRequestParamIfNeed(p, list);
                    }
                } else {
                    //2. 如果请求方式为POST，且参数类型不是基础类型，则为参数设置RequestBody注解
                    addRequestBodyIfNeed(p, list);
                }
            }
            if (list.isEmpty()) {
                list.add("");
            }
            return list;
        }

        // 参数长度大于1时
        if (RequestMethod.GET == requestMethod) {
            for (Parameter p : method.getParameters()) {
                Class<?> type = p.getType();
                if (!ReflectUtils.isBasicType(type)) {
                    //1. 如果请求方式为GET,为所有不是基础类型的参数设置RequestParam注解
                    addRequestParamIfNeed(p, list);
                } else {
                    list.add("");
                }
            }
        } else {
            boolean setRequestBodyCount = false;
            int i = 0;
            for (Parameter p : method.getParameters()) {
                i++;
                Class<?> type = p.getType();
                //a. 参数类型是Map类型,如果所有参数中还没有设置过RequestBody,则为该参数设置RequestBody注解
                if (ReflectUtils.isMapType(type)) {
                    if (!setRequestBodyCount) {
                        setRequestBodyCount = true;
                        addRequestBodyIfNeed(p, list);
                    }
                }
                //b. 参数类型是自定义类型，且自定义类型的参数的所有属性中存在不是基本类型的属性，如果所有参数中还没有设置过RequestBody,则为该参数设置RequestBody注解
                if (ReflectUtils.isCustomType(p) && !ReflectUtils.isCustomTypeAndAllBasicFields(p)) {
                    if (!setRequestBodyCount) {
                        setRequestBodyCount = true;
                        addRequestBodyIfNeed(p, list);
                    }
                }
                //c. 参数类型是泛型，且泛型类型的实际参数类型不是复杂类型
                if (ReflectUtils.isParameterizedType(p.getType()) && !ReflectUtils.hasComplexFields((Class<?>) ReflectUtils.getParameterizedActualType(p.getType()))) {
                    //① 如果所有参数中还没有设置过RequestBody,则为该参数设置RequestBody注解
                    if (!setRequestBodyCount) {
                        setRequestBodyCount = true;
                        addRequestBodyIfNeed(p, list);
                        //② 如果参数类型是集合，则为该参数设置RequestParam注解
                    } else if (ReflectUtils.isCollectionType(p.getType())) {
                        addRequestParamIfNeed(p, list);
                    }
                }
                //d. 参数类型是数组，且泛型类型的实际参数类型不是复杂类型
                if (ReflectUtils.isArrayType(p) && !ReflectUtils.hasComplexFields(ReflectUtils.getArrayType(p))) {
                    //① 如果所有参数中还没有设置过RequestBody,则为该参数设置RequestBody注解
                    if (!setRequestBodyCount) {
                        setRequestBodyCount = true;
                        addRequestBodyIfNeed(p, list);
                    } else {
                        //② 否则为该参数设置RequestParam注解
                        addRequestParamIfNeed(p, list);
                    }
                }
                if (list.size() < i) {
                    list.add("");
                }
            }
        }
        return list;
    }


}
