/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jclipper.dubbo.convert.maven.plugins.generate;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

@Mojo(
        name = "generate-default-springmvc-impl",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES
)
public class GenerateDefaultSpringMvcImplMojo extends AbstractMojo {


    /**
     * 是否启用
     */
    @Parameter(
            defaultValue = "true",
            property = "enable"
    )
    String enable;

    /**
     * 生成实现类的前缀，默认为Empty
     */
    @Parameter(
            defaultValue = "Empty",
            property = "classNamePrefix"
    )
    String classNamePrefix;

    /**
     * 生成实现类的前缀，默认为Controller
     */
    @Parameter(
            defaultValue = "Controller",
            property = "classNamePostfix"
    )
    String classNamePostfix;

    /**
     * 要扫描的包路径（数组）
     */
    @Parameter(
            required = false,
            property = "scanPackages"
    )
    String[] scanPackages;

    /**
     * 要排除的接口路径（数组），会从scanPackages和includeInterfaces中进行排除
     */
    @Parameter(
            required = false,
            property = "excludeInterfaces"
    )
    String[] excludeInterfaces;

    /**
     * 包含的接口路径（数组）
     */
    @Parameter(
            required = false,
            property = "includeInterfaces"
    )
    String[] includeInterfaces;

    /**
     * 生成实现类存放的目录，默认为${project.build.directory}/generated-sources/java,不建议修改
     */
    @Parameter(
            defaultValue = "${project.build.directory}/generated-sources/java",
            property = "outputDirectory"
    )
    File outputDirectory;

    /**
     * 生成实现类相对于接口所在包的子路径,此配置优先级大于targetSubPackage
     */
    @Parameter(
            defaultValue = "",
            property = "targetPackage"
    )
    String targetPackage;


    /**
     * 生成实现类的包路径，指定targetSubPackage时此配置失效
     */
    @Parameter(
            defaultValue = "empty",
            property = "targetSubPackage"
    )
    String targetSubPackage;

    @Parameter(defaultValue = "${project}", readonly = true)
    public MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {
        if (!"true".equalsIgnoreCase(enable)) {
            if (getLog().isInfoEnabled()) {
                getLog().info("GenerateDefaultSpringMvcImplMojo is disable");
            }
            return;
        }
        InterfaceScanner interfaceScanner = new InterfaceScanner(scanPackages, includeInterfaces, excludeInterfaces, project, getLog());
        SpringMvcImplGenerator springMvcImplGenerator = new SpringMvcImplGenerator();
        if (getLog().isInfoEnabled()) {
            getLog().info("Generating default implementations into " + outputDirectory.getAbsolutePath());
        }

        Set<String> allInterfaces = null;
        try {
            allInterfaces = interfaceScanner.getAllInterfaces();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (allInterfaces == null || allInterfaces.isEmpty()) {
            if (getLog().isWarnEnabled()) {
                getLog().warn("no any interface scan");
            }
            return;
        }
        if (getLog().isInfoEnabled()) {
            getLog().info("Generating default implementations for interfaces: " + allInterfaces);
        }
        for (String interfaceName : allInterfaces) {
            generateAndWriteFile(interfaceName, springMvcImplGenerator);
        }

        this.project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
        if (getLog().isInfoEnabled()) {
            getLog().info("Source directory: " + outputDirectory + " added.");
        }
    }

    private void generateAndWriteFile(String interfaceName, SpringMvcImplGenerator generator) throws MojoExecutionException {
        FileWriter fileWriter = null;

        try {
            Class<?> interfaceClass = Class.forName(interfaceName);
            String realTargetPackage = getRealTargetPackage(interfaceClass);
            File javaScrPackage = getJavaScrPackage(realTargetPackage);
            String implClassName = classNamePrefix + interfaceClass.getSimpleName() + classNamePostfix;
            File file = new File(javaScrPackage, implClassName + ".java");

            fileWriter = new FileWriter(file);

            PrintWriter printWriter = new PrintWriter(fileWriter);
            generator.generateToPrintWriter(interfaceClass, implClassName, realTargetPackage, printWriter);
            fileWriter.close();
            getLog().debug("Generated default impl: " + file.getAbsolutePath());
        } catch (IOException e) {
            throw new MojoExecutionException("Cannot generate file for interface " + interfaceName, e);
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException("Cannot load interface " + interfaceName, e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    // nothing to do
                }
            }
        }
    }

    private File getJavaScrPackage(String realTargetPackage) {
        File javaSrcPackage = new File(outputDirectory, realTargetPackage.replace('.', File.separatorChar));
        if (!javaSrcPackage.exists()) {
            javaSrcPackage.mkdirs();
        }
        return javaSrcPackage;
    }

    private String getRealTargetPackage(Class<?> clazz) {
        String realTargetPackage;
        if (targetSubPackage != null && targetSubPackage.length() > 0) {
            realTargetPackage = clazz.getPackage().getName() + "." + targetSubPackage;
        } else {
            if (targetPackage != null && targetPackage.length() > 0) {
                realTargetPackage = targetPackage;
            } else {
                realTargetPackage = clazz.getPackage().getName();
            }
        }
        return realTargetPackage;
    }


}
