package jclipper.dubbo.proxy.core.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/6/16 10:19.
 */
@Data
public class BaseUserInfo implements Serializable {
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long userId;
    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户名称")
    private String username;
    /**
     * 当前学校ID
     */
    @ApiModelProperty(value = "当前学校ID")
    private Long schoolId;
    /**
     * 登录名
     */
    @ApiModelProperty(value = "登录名")
    private String loginName;
    /**
     * 设备类型
     */
    @ApiModelProperty(value = "设备类型")
    private Integer deviceType;
    /**
     * 设备Id
     */
    @ApiModelProperty(value = "设备Id")
    private String deviceId;
    /**
     * 当前时间戳(毫秒)
     */
    @ApiModelProperty(value = "当前时间戳(毫秒)")
    private long timestamp;

    public static BaseUserInfo of(Long userId) {
        BaseUserInfo u = new BaseUserInfo();
        u.setUserId(userId);
        return u;
    }

}
