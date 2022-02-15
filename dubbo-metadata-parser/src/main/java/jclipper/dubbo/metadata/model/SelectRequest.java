package jclipper.dubbo.metadata.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2022/2/10 16:11.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SelectRequest implements Serializable {

    /**
     * Discovery类型
     */
    private DiscoveryType type;

    private String serviceName;
    private String serverAddress;
    private String namespace;
    private String ip;
    private Integer port;
}
