package jclipper.dubbo.proxy.finder;

import jclipper.dubbo.proxy.model.ServiceInstance;

import java.util.List;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/17 15:07.
 */
public class NacosServiceFinder extends ServiceFinder{


    public NacosServiceFinder(InstanceChooser chooser) {
        super(chooser);
    }

    @Override
    public List<ServiceInstance> getInstances(String service) {
        return null;
    }
}
