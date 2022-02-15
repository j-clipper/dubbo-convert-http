package jclipper.dubbo.convert.maven.plugins.generate;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/23 23:49.
 */
public class AnnotationTextBuilder {
    public static String generateRequestMappingAnnotation(RequestMethod method, String... urls) {
        String text = String.join(",", Arrays.stream(urls).map(s -> "\"" + s + "\"").collect(Collectors.toSet()));
        return String.format("@RequestMapping(method = RequestMethod.%s ,path={%s})", method.name(), text);
    }


    public static String generateApiAnnotation(Class<?> clazz) {
        List<String> tags = Lists.newArrayList("dubbo", "dubbo-" + clazz.getSimpleName());
        Api a = AnnotatedElementUtils.findMergedAnnotation(clazz, Api.class);
        if (a != null && a.tags().length > 0) {
            for (String tag : a.tags()) {
                if (!Strings.isNullOrEmpty(tag)) {
                    tags.add(tag);
                }
            }
        }
        String text = String.join(",", tags.stream().map(s -> "\"" + s + "\"").collect(Collectors.toSet()));
        return String.format("@Api(tags = {%s})", text);
    }

    public static String generateApiOperationAnnotation(Method method, String id) {
        ApiOperation a = AnnotatedElementUtils.findMergedAnnotation(method, ApiOperation.class);
        String value;
        if (a != null) {
            value = a.value();
        } else {
            value = id;
        }
        return String.format("@ApiOperation(\"%s\")", value);
    }

}
