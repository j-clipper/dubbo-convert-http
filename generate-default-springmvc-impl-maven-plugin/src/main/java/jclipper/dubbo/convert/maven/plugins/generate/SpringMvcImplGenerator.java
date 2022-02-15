package jclipper.dubbo.convert.maven.plugins.generate;

import jclipper.dubbo.proxy.http.DefaultRequestMappingGuesser;
import jclipper.dubbo.proxy.http.RequestMappingGuesser;
import org.apache.maven.plugin.MojoExecutionException;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.*;

import static jclipper.dubbo.convert.maven.plugins.generate.AnnotationTextBuilder.*;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/24 00:15.
 */
public class SpringMvcImplGenerator {
    public static final Map<Class<?>, String> PRIMITIVE_RETURN_VALUES = new HashMap<>();

    private final RequestMappingGuesser guesser;


    public SpringMvcImplGenerator() {
        guesser = new DefaultRequestMappingGuesser(DefaultRequestMappingGuesser.DEFAULT_HTTP_METHOD_KEYWORDS, Collections.emptyMap());
    }

    static {
        PRIMITIVE_RETURN_VALUES.put(Boolean.TYPE, "return false;");
        PRIMITIVE_RETURN_VALUES.put(Character.TYPE, "return 0;");
        PRIMITIVE_RETURN_VALUES.put(Byte.TYPE, "return 0;");
        PRIMITIVE_RETURN_VALUES.put(Short.TYPE, "return 0;");
        PRIMITIVE_RETURN_VALUES.put(Integer.TYPE, "return 0;");
        PRIMITIVE_RETURN_VALUES.put(Long.TYPE, "return 0;");
        PRIMITIVE_RETURN_VALUES.put(Float.TYPE, "return 0;");
        PRIMITIVE_RETURN_VALUES.put(Double.TYPE, "return 0;");
        PRIMITIVE_RETURN_VALUES.put(Void.TYPE, "");
    }

    public void generateToPrintWriter(Class<?> interfaceClass, String implClassName, String realTargetPackage, PrintWriter printWriter) throws MojoExecutionException {
        if (!interfaceClass.isInterface()) {
            throw new MojoExecutionException("The specified class is not an interface: " + interfaceClass.getName());
        }

        if (realTargetPackage.length() > 0) {
            printWriter.printf("package %s;%n", realTargetPackage);
        }
        printWriter.println("import org.springframework.web.bind.annotation.*;");
        printWriter.println("import io.swagger.annotations.Api;");
        printWriter.println("import io.swagger.annotations.ApiOperation;");
        printWriter.println();
        printWriter.println();

        printWriter.printf("%s\n", generateApiAnnotation(interfaceClass));
        printWriter.printf("public class %s implements %s {\n", implClassName, interfaceClass.getCanonicalName());
        Map<String, Integer> methodNameMap = new HashMap<>(interfaceClass.getMethods().length);

        for (Method method : interfaceClass.getDeclaredMethods()) {
            Integer count = methodNameMap.getOrDefault(method.getName(), 0);
            methodNameMap.put(method.getName(), ++count);
        }
        for (Method method : interfaceClass.getMethods()) {
            String returnTypeName = method.getGenericReturnType().getTypeName();
            String methodId = method.getName();
            if (methodNameMap.get(methodId) > 1) {
                methodId = methodId + "_" + String.join("_", Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).toArray(String[]::new));
            }

            printWriter.printf("      @Override\n");
            printWriter.printf("      %s\n", generateApiOperationAnnotation(method, methodId));
            RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
            RequestMethod requestMethod;
            String[] urls;
            if (requestMapping != null && requestMapping.path().length > 0) {
                requestMethod = requestMapping.method()[0];
                urls = requestMapping.path();
            } else {
                requestMethod = guesser.guess(method);
                urls = new String[]{methodId};
            }

            printWriter.printf("      %s\n", generateRequestMappingAnnotation(requestMethod, urls));
            printWriter.printf("      @ResponseBody\n");
            printWriter.printf("      public %s %s(", returnTypeName, method.getName());
            java.lang.reflect.Parameter[] parameters = method.getParameters();

            List<String> annotations = guesser.completeParametersMvcAnnotation(method, requestMethod);

            for (int i = 0; i < parameters.length; i++) {
                java.lang.reflect.Parameter parameter = parameters[i];
                parameter.getParameterizedType();
                printWriter.printf("%s %s %s", annotations.get(i), parameter.getParameterizedType().getTypeName(), parameter.getName());
                if (i < (parameters.length - 1)) {
                    printWriter.print(", ");
                }
            }

            printWriter.print(")");
            for (int i = 0; i < method.getExceptionTypes().length; i++) {
                if (i == 0) {
                    printWriter.print(" throws ");
                }

                printWriter.printf(method.getExceptionTypes()[i].getName());
                if (i < (method.getExceptionTypes().length - 1)) {
                    printWriter.print(", ");
                }
            }

            printWriter.print(" {");
            Class<?> returnType = method.getReturnType();

            printWriter.print(PRIMITIVE_RETURN_VALUES.getOrDefault(returnType, "return null;"));

            printWriter.println("}\n");
        }

        printWriter.println("}");
    }
}
