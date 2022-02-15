package jclipper.dubbo.proxy.core;

import jclipper.dubbo.proxy.core.model.Page;
import jclipper.dubbo.proxy.core.model.Result;
import jclipper.dubbo.proxy.core.model.UserInfo;
import jclipper.dubbo.proxy.core.model.UserRequest;
import jclipper.dubbo.proxy.utils.AnnotationBuilder;
import lombok.SneakyThrows;
import org.junit.Test;
import xdean.jex.util.reflect.AnnotationUtil;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/19 15:26.
 */
public class JavasistUtilsTests {
    public Result<Page<UserInfo>> getUserList(UserRequest request) {
        return null;
    }

    @SneakyThrows
    @Test
    public void testAddAnnoInMethod() {
        Method method = JavasistUtilsTests.class.getMethod("getUserList", UserRequest.class);
        Annotation[] annotations = method.getAnnotations();
        System.out.println("before:" + annotations.length);

        AnnotationBuilder.addResponeBodyIfNeed(method);
        Annotation[] annotations1 = method.getAnnotations();
        System.out.println("after1:" + annotations1.length);
    }


    @SneakyThrows
    @Test
    public void testAddAnnoInFiled() {
        Field filed = UserRequest.class.getDeclaredField("name");
        Annotation[] annotations = filed.getAnnotations();
        System.out.println("before:" + annotations.length);

        AnnotationUtil.addAnnotation(filed, AnnotationUtil.createAnnotationFromMap(Nullable.class, Collections.emptyMap()));

        Annotation[] annotations1 = filed.getAnnotations();
        System.out.println("after1:" + annotations1.length);

    }
}
