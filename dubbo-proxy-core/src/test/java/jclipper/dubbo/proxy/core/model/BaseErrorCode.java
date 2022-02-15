package jclipper.dubbo.proxy.core.model;

import java.io.Serializable;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2020/4/27 11:10.
 */
public interface BaseErrorCode extends Serializable {

    /**
     * 错误码
     *
     * @return
     */
    int getCode();

    /**
     * 错误信息
     *
     * @return
     */
    String getName();

}
