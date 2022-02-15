# dubbo-convert-http

## 功能描述
在通过dubbo协议暴露dubbo接口的基础上，无需再声明rest协议，只需要通过简单的配置就可以实现额外通过http方式进行dubbo接口的调用

## 使用场景
- dubbo接口列表展示与查询；
- 开发或测试阶段快速调用dubbo接口进行调试；
- 便于对dubbo接口进行测试；

## 使用教程
[使用教程](./Guide.md)

## 实现原理
1. dubbo接口类通过maven插件生成空实现类;
2. 使用Cglib动态代理生成代理类，代理空实现类的方法，代理方法的执行逻辑是查找dubbo接口的注册信息并执行远程dubbo方法调用；
3. 将步骤2代理类注册到SpringMVC中，即可实现通过http方式进行调用；
4. 当通过http方式进行调用时，会调用到步骤2中的代理方法，从注册中心查找对应的Dubbo接口调用地址，进行Dubbo方法调用；

## 项目模块说明
```bash
.
├── dubbo-convert-http-spring-boot-starter  # 将dubbo接口生成代理类，并注册到SpringMVC中，实现以Http的方式进行访问
├── dubbo-metadata-parser    # dubbo服务提供者的元数据解析  
├── dubbo-proxy-core    # 使用动态代理为dubbo接口生成代理类，并进行dubbo调用  
├── generate-default-springmvc-impl-maven-plugin    # maven插件：为dubbo接口生成默认的实现类，便于进行注册到SpringMVC中

```





## 说明
### 为什么要采用maven插件生成空的实现类，而不采用动态代理直接生成代理类的方式？
最开始，笔者采用的就是动态代理直接为dubbo接口生成代理类的方式，将生成的代理类(见InvocationHandlerDubboProxy类)通过`requestMappingHandlerMapping.registerMapping`方法注册到SpringMVC中，但是此种方法会导致在注册到SpringMVC中时，方法参数名称会丢失。后面又测试过利用Javasist库在运行时，动态拼接生成实现类源码从而生成实现类class，但经过测试，此种方法也会导致泛型的原始类丢失。所以最终采用了通过maven插件预先生成空的实现类的方式。
### dubbo代理方法注册到SpringMVC中时，是如何指定http请求方式和访问路径的？
执行maven插件生成空返回值实现类时，会为自动添加SpringMVC和Swagger相关的注解，并会根据方法名称和参数类型长度自动匹配HttpMethod类型

### http接口的访问路径
#### 前缀
http接口的方法路径的统一前缀是`/dubbo/`+`<dubbo接口>.class.getName()`

#### 子路径
生成的实现类中，会加上`@RequestMapping`,通过指定path属性指定子路径，默认为当前方法名。如果当前类中存在同名方法，则子路径中还会携带上方法参数类型签名，例如：

```java
package com.a.b.c.remote.api
public interface LoginService {
    User loginByMobileCode(Long mobile, String code);
    User loginByPassword(String userName, String password);
    User loginByPassword(String userName, String password,String code);
}
```
以上三个方法的访问路径分别是:
- `/dubbo/com.a.b.c.remote.api.LoginService/loginByMobileCode`
- `/dubbo/com.a.b.c.remote.api.LoginService/loginByPassword_String_String`
- `/dubbo/com.a.b.c.remote.api.LoginService/loginByPassword_String_String_String`

### HttpMethod类型自动匹配规则说明
如果在接口的方法上没有声明`@RequestMapping`(或`@GetMapping`、`@PostMapping`等)注解，则会在生成实现类时自动设置`@RequestMapping`注解
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
2. 方法参数长度为1时：
   a. 参数类型是基本类型，使用GET；
   b. 参数类型是Map，使用GET；
   c. 参数类型是泛型，且泛型的实际类型是基本类型，使用GET，例如List<Long>；
   d. 参数类型是数组，且数组的实际类型是基本类型，使用GET，例如String[]；
   e. 其他情况使用POST；
3. 方法参数长度大于1时：
   a. 所有参数都是基本类型，则用GET；
   b. 带有Map参数则用POST；
   c. 有任何自定义类型，并且自定义类型的字段里面带有复杂类型则用POST；
   d. 有任何泛型类型，并且泛型的实际类型的的字段里面带有复杂类型则用POST；
   e. 有任何数组，并且数组类型的字段里面带有复杂类型则用POST；
   f. 其他情况使用GET；

### 方法参数补全@RequestParam和@RequestBody注解
如果在接口声明的方法参数上没有设置`@RequestParam`或`@RequestBody`注解，在生成实现类时也会自动按需对参数补全上`@RequestParam`或`@RequestBody`注解;

#### 方法参数长度为1时
1. 如果请求方式为GET，并且参数类型是集合或数组，则为参数设置RequestParam注解；
2. 如果请求方式为POST，且参数类型不是基础类型，则为参数设置RequestBody注解

#### 方法参数长度大于1时
1. 请求方式为GET时,为所有不是基础类型的参数设置RequestParam注解；
2. 请求方式为POST时
   a. 参数类型是Map类型,如果所有参数中还没有设置过RequestBody,则为该参数设置RequestBody注解；
   b. 参数类型是自定义类型，且自定义类型的参数的所有属性中存在不是基本类型的属性，如果所有参数中还没有设置过RequestBody,则为该参数设置RequestBody注解；
   c. 参数类型是泛型，且泛型类型的实际参数类型不是复杂类型     
   ① 如果所有参数中还没有设置过RequestBody,则为该参数设置RequestBody注解；
   ② 如果参数类型是集合，则为该参数设置RequestParam注解 ；
   d. 参数类型是数组，且泛型类型的实际参数类型不是复杂类型  
   ① 如果所有参数中还没有设置过RequestBody,则为该参数设置RequestBody注解；
   ② 否则为该参数设置RequestParam注解；


