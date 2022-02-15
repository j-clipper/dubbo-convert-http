package jclipper.dubbo.metadata.finder;

import jclipper.dubbo.metadata.model.DubboUrlInfo;
import jclipper.dubbo.metadata.model.SelectRequest;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2022/2/10 16:32.
 */
public interface DubboMetadataFinder {

    /**
     * 获取DubboMetadata
     *
     * @param request SelectRequest
     * @return
     */
    Map<String, List<DubboUrlInfo>> getDubboMetadata(SelectRequest request);


}
