package jclipper.dubbo.proxy.finder;

import jclipper.dubbo.proxy.model.ServiceInstance;

import java.util.List;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/16 15:17.
 */
public abstract class ServiceFinder {

    protected InstanceChooser chooser;

    public ServiceFinder(InstanceChooser chooser) {
        this.chooser = chooser;
    }

    public ServiceInstance chooseInstance(String service) {
        List<ServiceInstance> instances = getInstances(service);
        return chooser.choose(instances);
    }

    public abstract List<ServiceInstance> getInstances(String service);

}
