package jclipper.dubbo.proxy.core.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/25 14:43.
 */
public interface TestService {

    Map<Long, Object> select(Collection<Long> courseIds);

}
