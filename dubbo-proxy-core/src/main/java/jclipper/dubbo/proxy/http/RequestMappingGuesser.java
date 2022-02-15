package jclipper.dubbo.proxy.http;

import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.List;

/**
 * RequestMappingGuesser，用于猜测方法的请求方式和补全方法参数的注解
 *
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/18 23:37.
 */
public interface RequestMappingGuesser {
    /**
     * 猜测方法的请求方式
     *
     * @param method
     * @return
     */
    RequestMethod guess(Method method);

    /**
     * 补全方法参数的注解
     *
     * @param method
     * @param httpMethod
     * @return annotations
     */
    List<String> completeParametersMvcAnnotation(Method method, RequestMethod httpMethod);
}
