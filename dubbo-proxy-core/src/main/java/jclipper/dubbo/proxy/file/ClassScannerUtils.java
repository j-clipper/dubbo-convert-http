package jclipper.dubbo.proxy.file;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/23 20:08.
 * @see https://blog.csdn.net/a729913162/article/details/81698109
 */

public class ClassScannerUtils {

    public static Set<Class<?>> searchClasses(String packageName) {
        return searchClasses(packageName, null);
    }

    public static Set<Class<?>> searchClasses(List<String> packageNames, Predicate<Class<?>> predicate) {
        return packageNames.stream().flatMap(p -> searchClasses(p, predicate).stream()).collect(Collectors.toSet());
    }

    public static Set<Class<?>> searchClasses(List<String> packageNames) {
        return searchClasses(packageNames, null);
    }

    public static Set<Class<?>> searchClasses(String packageName, Predicate<Class<?>> predicate) {
        return ScanExecutor.getInstance().search(packageName, predicate);
    }

}
