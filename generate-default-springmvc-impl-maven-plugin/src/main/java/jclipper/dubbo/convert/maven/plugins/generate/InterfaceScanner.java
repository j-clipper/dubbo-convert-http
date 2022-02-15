package jclipper.dubbo.convert.maven.plugins.generate;

import com.google.common.reflect.ClassPath;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/23 23:54.
 */
public class InterfaceScanner {


    private String[] scanPackages;
    private String[] includeInterfaces;
    private String[] excludeInterfaces;
    public MavenProject project;
    private Log log;

    public InterfaceScanner(String[] scanPackages, String[] includeInterfaces, String[] excludeInterfaces, MavenProject project, Log log) {
        this.scanPackages = scanPackages;
        this.includeInterfaces = includeInterfaces;
        this.excludeInterfaces = excludeInterfaces;
        this.project = project;
        this.log = log;
    }

    public Set<String> getAllInterfaces() throws IOException {
        Set<String> pkgs = new HashSet<>();
        if (includeInterfaces != null && includeInterfaces.length > 0) {
            pkgs.addAll(Arrays.asList(includeInterfaces));
        }
        if (scanPackages != null && scanPackages.length > 0) {
            for (String pkg : scanPackages) {
                Set<Class<?>> classes = findAllClassesUsingGoogleGuice(pkg);
                log.info(String.format("scan package[%s] get classes:%s", pkg, classes.stream().map(Class::getName).collect(Collectors.toSet())));
                for (Class<?> clazz : classes) {
                    if (clazz.isInterface()) {
                        pkgs.add(clazz.getName());
                    }
                }
            }
        }
        if (excludeInterfaces != null && excludeInterfaces.length > 0) {
            pkgs.removeAll(Arrays.asList(excludeInterfaces));
        }
        return pkgs;
    }


    private Set<Class<?>> findAllClassesUsingGoogleGuice(String... packages) throws IOException {
        return ClassPath.from(getClassLoader(this.project))
                .getAllClasses()
                .stream()
                .filter(clazz -> Arrays.stream(packages).anyMatch(pkg -> clazz.getPackageName().startsWith(pkg)))
                .map(ClassPath.ClassInfo::load)
                .collect(Collectors.toSet());
    }

    /**
     * @param project
     * @return
     * @see <a href="https://blog.csdn.net/qq_35425070/article/details/107651270">https://blog.csdn.net/qq_35425070/article/details/107651270</a>
     */
    private ClassLoader getClassLoader(MavenProject project) {
        try {
            // 所有的类路径环境，也可以直接用 compilePath
            List<String> classpathElements = project.getCompileClasspathElements();

            classpathElements.add(project.getBuild().getOutputDirectory());
            classpathElements.add(project.getBuild().getTestOutputDirectory());
            // 转为 URL 数组
            URL[] urls = new URL[classpathElements.size()];
            for (int i = 0; i < classpathElements.size(); ++i) {
                urls[i] = new File(classpathElements.get(i)).toURL();
            }
            // 自定义类加载器
            return new URLClassLoader(urls, this.getClass().getClassLoader());
        } catch (Exception e) {
            log.error("Couldn't get the classloader.");
            return this.getClass().getClassLoader();
        }
    }
}
