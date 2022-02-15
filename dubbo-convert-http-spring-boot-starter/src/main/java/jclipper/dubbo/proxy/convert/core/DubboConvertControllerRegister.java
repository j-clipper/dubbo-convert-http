package jclipper.dubbo.proxy.convert.core;

import com.google.common.collect.Lists;
import jclipper.dubbo.proxy.convert.configuration.DubboConvertProperties;
import jclipper.dubbo.proxy.core.DubboProxy;
import jclipper.dubbo.proxy.core.DubboProxyFactory;
import jclipper.dubbo.proxy.file.ClassScannerUtils;
import jclipper.dubbo.proxy.finder.ServiceFinder;
import jclipper.dubbo.proxy.http.RequestMappingGuesser;
import jclipper.dubbo.proxy.utils.AnnotationBuilder;
import jclipper.dubbo.proxy.utils.MapBuilder;
import io.swagger.annotations.Api;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import xdean.jex.util.reflect.AnnotationUtil;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/16 15:41.
 */
@Component
@Slf4j
public class DubboConvertControllerRegister {

    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    @Resource
    private ServiceFinder serviceFinder;
    @Resource
    private DubboConvertProperties properties;
    @Resource
    private RequestMappingGuesser guesser;


    @PostConstruct
    @SneakyThrows
    public void register() {
        if (log.isInfoEnabled()) {
            log.info("start register dubbo api to springmvc");
        }
        if (!properties.isEnabled() || properties.getScanPackages() == null || properties.getScanPackages().length == 0) {
            log.warn("No registration required");
            return;
        }
        Set<Class<?>> classSet = ClassScannerUtils.searchClasses(Lists.newArrayList(properties.getScanPackages()));
        for (Class<?> clazz : classSet) {
            if (clazz.isInterface()) {
                continue;
            }
            if (log.isInfoEnabled()) {
                log.info("register dubbo interface [{}] to http ", clazz.getInterfaces()[0].getName());
            }
            register(clazz);
        }
    }

    @SneakyThrows
    public void register(Class<?> clazz) {
        Object mvc = DubboProxyFactory.create(clazz, serviceFinder, properties.getApplication());

        Class<?> realInterface = DubboProxy.getRealInterface(clazz);

        Api api = realInterface.getAnnotation(Api.class);
        if (api == null) {
            String tagPrefix = properties.getSwaggerTagPrefix() + realInterface.getSimpleName();
            Map<String, Object> map = MapBuilder.newInstance()
                    .add("tags", new String[]{"dubbo", tagPrefix}).build();
            api = AnnotationUtil.createAnnotationFromMap(Api.class, map);
            AnnotationUtil.addAnnotation(realInterface, api);
        }

        Map<String, Integer> methodNameMap = new HashMap<>(realInterface.getMethods().length);
        for (Method method : realInterface.getDeclaredMethods()) {
            Integer count = methodNameMap.getOrDefault(method.getName(), 0);
            methodNameMap.put(method.getName(), ++count);
        }

        for (Method method : clazz.getDeclaredMethods()) {
            String name = method.getName();
            if (!methodNameMap.containsKey(name)) {
                continue;
            }
            Method proxyMethod = mvc.getClass().getMethod(method.getName(), method.getParameterTypes());

            String prefix = properties.getHttpPathPrefix() + realInterface.getName();

            if (methodNameMap.get(name) > 1) {
                name = name + "_" + String.join("_", Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).toArray(String[]::new));
            }

            AnnotationBuilder.addResponeBodyIfNeed(method);

            RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
            RequestMethod requestMethod;
            String[] paths;

            if (requestMapping != null) {
                RequestMethod[] methods = requestMapping.method();
                requestMethod = methods.length == 0 ? RequestMethod.GET : methods[0];
                paths = requestMapping.path();

                AnnotationUtil.removeAnnotation(method, RequestMapping.class);
                AnnotationUtil.removeAnnotation(proxyMethod, RequestMapping.class);
            } else {
                requestMethod = guesser.guess(method);
                paths = new String[]{name};
            }

            AnnotationBuilder.addApiOperationIfNeed(proxyMethod, name);

            requestMapping = AnnotationBuilder.createRequestMapping(requestMethod, prefix, paths);
            Field field = RequestMappingHandlerMapping.class.getDeclaredField("config");
            field.setAccessible(true);
            RequestMappingInfo.BuilderConfiguration configuration = (RequestMappingInfo.BuilderConfiguration) field.get(requestMappingHandlerMapping);

            RequestMappingInfo.Builder builder = RequestMappingInfo
                    .paths(requestMapping.path())
                    .methods(requestMapping.method())
                    .params(requestMapping.params())
                    .headers(requestMapping.headers())
                    .consumes(requestMapping.consumes())
                    .produces(requestMapping.produces())
                    .mappingName(requestMapping.name());
            builder.options(configuration);
            requestMappingHandlerMapping.registerMapping(builder.build(), mvc, method);
        }

    }


}
