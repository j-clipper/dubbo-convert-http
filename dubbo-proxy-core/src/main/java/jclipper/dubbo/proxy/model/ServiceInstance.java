package jclipper.dubbo.proxy.model;

import lombok.Data;

import java.util.Map;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/17 15:03.
 */
@Data
public class ServiceInstance {
    private String id;
    private String ip;
    private Integer port;
    private Map<String, String> metadata;
}
