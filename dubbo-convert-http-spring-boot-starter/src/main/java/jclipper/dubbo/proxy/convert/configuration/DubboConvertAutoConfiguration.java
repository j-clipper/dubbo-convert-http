package jclipper.dubbo.proxy.convert.configuration;

import jclipper.dubbo.proxy.finder.DirectIpInstanceChooser;
import jclipper.dubbo.proxy.finder.InstanceChooser;
import jclipper.dubbo.proxy.finder.ServiceFinder;
import jclipper.dubbo.proxy.finder.SpringCloudServiceFinder;
import jclipper.dubbo.proxy.http.DefaultRequestMappingGuesser;
import jclipper.dubbo.proxy.http.RequestMappingGuesser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/17 15:32.
 */
@Configuration
@ComponentScan(basePackages = {"jclipper.dubbo.proxy.convert"})
public class DubboConvertAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(InstanceChooser.class)
    public InstanceChooser instanceChooser() {
        return new DirectIpInstanceChooser();
    }

    @Bean
    @ConditionalOnMissingBean(ServiceFinder.class)
    @ConditionalOnBean(DiscoveryClient.class)
    public ServiceFinder serviceFinder(InstanceChooser chooser, DiscoveryClient discoveryClient) {
        return new SpringCloudServiceFinder(chooser, discoveryClient);
    }

    @Bean
    @ConditionalOnMissingBean(RequestMappingGuesser.class)
    public RequestMappingGuesser requestMappingGuesser(DubboConvertProperties properties) {
        return new DefaultRequestMappingGuesser(properties.getRequestMethodKeywords(), properties.getExtendRequestMethodKeywords());
    }
}
