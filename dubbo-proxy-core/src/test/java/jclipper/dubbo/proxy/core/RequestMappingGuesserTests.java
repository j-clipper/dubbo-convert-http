package jclipper.dubbo.proxy.core;

import jclipper.dubbo.proxy.core.service.TestService;
import jclipper.dubbo.proxy.http.DefaultRequestMappingGuesser;
import jclipper.dubbo.proxy.http.RequestMappingGuesser;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/25 14:44.
 */
public class RequestMappingGuesserTests {
    private RequestMappingGuesser guesser;

    @Before
    public void init() {
        guesser = new DefaultRequestMappingGuesser(DefaultRequestMappingGuesser.DEFAULT_HTTP_METHOD_KEYWORDS, Collections.emptyMap());
    }



    @Test
    @SneakyThrows
    public void test1() {
        Class clazz = TestService.class;
        Method method = clazz.getDeclaredMethod("select", Collection.class);
        List<String> annotations = guesser.completeParametersMvcAnnotation(method, RequestMethod.GET);
        System.out.println(annotations);
    }
}
