package jclipper.dubbo.proxy;

import jclipper.dubbo.proxy.convert.core.DubboConvertControllerRegister;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/16 15:40.
 */
@SpringBootApplication
public class ProxyWebApplication {

    @Resource
    private DubboConvertControllerRegister register;

    public static void main(String[] args) {
        SpringApplication.run(ProxyWebApplication.class, args);
    }

//    @PostConstruct
//    public void init() {
//        register.register(TestController.class);
//    }
}
