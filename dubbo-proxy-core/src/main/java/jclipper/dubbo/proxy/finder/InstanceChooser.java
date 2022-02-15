package jclipper.dubbo.proxy.finder;

import jclipper.dubbo.proxy.model.ServiceInstance;

import java.util.List;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/17 15:22.
 */
public interface InstanceChooser {

    ServiceInstance choose(List<ServiceInstance> instances);

}
