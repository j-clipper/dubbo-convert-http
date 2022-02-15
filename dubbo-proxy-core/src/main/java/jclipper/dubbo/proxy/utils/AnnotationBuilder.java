package jclipper.dubbo.proxy.utils;

import io.swagger.annotations.ApiOperation;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.*;
import xdean.jex.util.reflect.AnnotationUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/19 10:44.
 */
public class AnnotationBuilder {


    public static ResponseBody createResponseBody() {
        return AnnotationUtil.createAnnotationFromMap(ResponseBody.class, Collections.emptyMap());
    }


    public static ApiOperation createApiOperation(String name) {
        return AnnotationUtil.createAnnotationFromMap(ApiOperation.class,
                MapBuilder.newInstance()
                        .add("value", name).build());
    }

    public static RequestMapping createRequestMapping(RequestMethod method, String pathPrefix, String... paths) {
        String[] fullPaths = Arrays.stream(paths).map(p -> pathPrefix + "/" + p).toArray(String[]::new);
        return AnnotationUtil.createAnnotationFromMap(RequestMapping.class,
                MapBuilder.newInstance()
                        .add("path", fullPaths)
                        .add("method", new RequestMethod[]{method}).build());
    }


    public static void addResponeBodyIfNeed(Method method) {
        if (AnnotatedElementUtils.findMergedAnnotation(method, ResponseBody.class) == null) {
            ResponseBody requestBody = createResponseBody();
            AnnotationUtil.addAnnotation(method, requestBody);
        }
    }

    public static void addApiOperationIfNeed(Method method, String name) {
        if (AnnotatedElementUtils.findMergedAnnotation(method, ApiOperation.class) == null) {
            ApiOperation apiOperation = createApiOperation(name);
            AnnotationUtil.addAnnotation(method, apiOperation);
        }
    }

    public static void addRequestBodyIfNeed(Parameter p, List<String> list) {
        if (AnnotatedElementUtils.findMergedAnnotation(p, RequestBody.class) == null) {
            list.add("@RequestBody");
        } else {
            list.add("");
        }
    }

    public static void addRequestParamIfNeed(Parameter p, List<String> list) {
        if (AnnotatedElementUtils.findMergedAnnotation(p, RequestParam.class) == null) {
            list.add("@RequestParam");
        } else {
            list.add("");
        }
    }

}
