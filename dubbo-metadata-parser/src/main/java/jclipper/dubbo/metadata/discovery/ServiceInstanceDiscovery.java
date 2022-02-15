package jclipper.dubbo.metadata.discovery;

import org.springframework.cloud.client.ServiceInstance;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2022/2/10 15:53.
 */
public interface ServiceInstanceDiscovery {
    /**
     * 查询服务实例
     *
     * @param name 服务名
     * @param ip   服务ip
     * @param port 服务端口
     * @return
     */
    ServiceInstance discovery(String name, String ip, Integer port);
}
