# generate-default-springmvc-impl-maven-plugin

## 功能描述

Maven插件：为Java接口生成默认的空返回值实现Controller类

## 功能说明

生成空返回值实现类时，会为自动添加SpringMVC和Swagger相关的注解，并会根据方法名称和参数类型长度自动匹配HttpMethod类型

### HttpMethod类型自动匹配规则说明

如果在接口的方法上没有声明`@RequestMapping`(或`@GetMapping`、`PostMapping`等)注解，则会在生成实现类时自动设置`@RequestMapping`注解

#### 优先按方法名称前缀进行匹配

| 方法名的前缀                                                                                                                   | 自动匹配的HttpMethod类型 |
|--------------------------------------------------------------------------------------------------------------------------|:------------------|
| "get", "query", "find", "select", "count", "check", "show", "page", "batchGet", "batchQuery", "batchFind", "batchSelect" | GET               |
| "new","insert", "add", "create", "save", "batchAdd","batchInsert", "batchCreate", "batchSave","login","logout","post"    | POST              |
| "update", "mod", "batchUpdate", "batchMod", "change", "batchChange","put"                                                | PUT               |
| "del", "remove", "batchDel", "batchRemove"                                                                               | DELETE            |
| 暂无                                                                                                                       | HEAD              |
| 暂无                                                                                                                       | PATCH             |
| 暂无                                                                                                                       | OPTIONS           |

#### 按照方法参数类型和长度进行匹配

如果按照方法名称前缀进行匹配无法进行匹配，则继续按照方法参数类型和长度进行匹配：

1. 无参方法，则使用GET；
2. 方法参数长度为1时： a. 参数类型是基本类型，使用GET； b. 参数类型是Map，使用GET； c. 参数类型是泛型，且泛型的实际类型是基本类型，使用GET，例如List<Long>； d.
   参数类型是数组，且数组的实际类型是基本类型，使用GET，例如String[]； e. 其他情况使用POST；
3. 方法参数长度大于1时： a. 所有参数都是基本类型，则用GET； b. 带有Map参数则用POST； c. 有任何自定义类型，并且自定义类型的字段里面带有复杂类型则用POST； d.
   有任何泛型类型，并且泛型的实际类型的的字段里面带有复杂类型则用POST； e. 有任何数组，并且数组类型的字段里面带有复杂类型则用POST； f. 其他情况使用GET；

### 方法参数补全@RequestParam和@RequestBody注解

如果在接口声明的方法参数上没有设置`@RequestParam`或`@RequestBody`注解，在生成实现类时也会自动按需对参数补全上`@RequestParam`或`@RequestBody`注解;

#### 方法参数长度为1时

1. 如果请求方式为GET，并且参数类型是集合或数组，则为参数设置RequestParam注解；
2. 如果请求方式为POST，且参数类型不是基础类型，则为参数设置RequestBody注解

#### 方法参数长度大于1时

1. 请求方式为GET时,为所有不是基础类型的参数设置RequestParam注解；
2. 请求方式为POST时 a. 参数类型是Map类型,如果所有参数中还没有设置过RequestBody,则为该参数设置RequestBody注解； b.
   参数类型是自定义类型，且自定义类型的参数的所有属性中存在不是基本类型的属性，如果所有参数中还没有设置过RequestBody,则为该参数设置RequestBody注解； c. 参数类型是泛型，且泛型类型的实际参数类型不是复杂类型  
   ① 如果所有参数中还没有设置过RequestBody,则为该参数设置RequestBody注解； ② 如果参数类型是集合，则为该参数设置RequestParam注解 ； d. 参数类型是数组，且泛型类型的实际参数类型不是复杂类型  
   ① 如果所有参数中还没有设置过RequestBody,则为该参数设置RequestBody注解； ② 否则为该参数设置RequestParam注解；

## 使用说明

在目标项目模块的pom.xml中添加如下配置：

```xml

<build>
    <plugins>
        <plugin>
            <groupId>com.wf2311.jclipper</groupId>
            <artifactId>generate-default-springmvc-impl-plugin</artifactId>
            <version>2021.11-SNAPSHOT</version>
            <configuration>
                <scanPackages>
                    <scanPackage>${要扫描的dubbo接口所在包路径，支持多个}</scanPackage>
                </scanPackages>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>generate-default-springmvc-impl</goal>
                    </goals>
                </execution>
            </executions>
            <dependencies>
                <dependency>
                    <groupId>${接口项目.groupId}</groupId>
                    <artifactId>${接口项目.artifactId}</artifactId>
                    <version>${接口项目.version}</version>
                </dependency>
            </dependencies>
        </plugin>
    </plugins>
</build>
```

完整配置：

```xml

<configuration>
    <scanPackages>
        <!-- 要扫描的包路径 -->
        <scanPackage>要扫描的包路径1</scanPackage>
        <scanPackage>要扫描的包路径2</scanPackage>
    </scanPackages>
    <includeInterfaces>
        <!-- 包含的接口路径 -->
        <includeInterface>包含的接口路径1</includeInterface>
        <includeInterface>包含的接口路径2</includeInterface>
    </includeInterfaces>
    <excludeInterfaces>
        <!-- 要排除的接口路径，会从scanPackages和includeInterfaces中进行排除 -->
        <excludeInterface>要排除的接口路径1</excludeInterface>
        <excludeInterface>要排除的接口路径2</excludeInterface>
    </excludeInterfaces>
    <enable>是否启用插件，默认为true</enable>
    <classNamePrefix>生成实现类的前缀，默认为Empty</classNamePrefix>
    <classNamePostfix>生成实现类的前缀，默认为Controller</classNamePostfix>
    <targetPackage>生成实现类的包路径，指定targetSubPackage时此配置失效</targetPackage>
    <targetSubPackage>生成实现类相对于接口所在包的子路径</targetSubPackage>
    <outputDirectory>生成实现类存放的目录，默认为${project.build.directory}/generated-sources/java,不建议修改</outputDirectory>
</configuration>
```

### 注意
为了保证在编译后保留方法的参数名称，需要在目标项目中或其父项目中添加如下插件
```xml
<plugin>
   <groupId>org.apache.maven.plugins</groupId>
   <artifactId>maven-compiler-plugin</artifactId>
   <version>3.5</version>
   <configuration>
       <compilerArgs>
           <!--配置此参数是为了在javac编译信息中保留方法的参数名称 -->
           <compilerArg>-parameters</compilerArg>
       </compilerArgs>
   </configuration>
</plugin>
```