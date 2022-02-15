package jclipper.dubbo.proxy.finder;

import jclipper.dubbo.proxy.model.ServiceInstance;

import java.util.List;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/17 15:24.
 */
public class FirstInstanceChooser implements InstanceChooser {
    @Override
    public ServiceInstance choose(List<ServiceInstance> instances) {
        if (instances == null || instances.isEmpty()) {
            return null;
        }
        return instances.get(0);
    }
}
