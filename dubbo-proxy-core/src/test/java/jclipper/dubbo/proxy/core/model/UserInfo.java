package jclipper.dubbo.proxy.core.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息
 *
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/6/28 16:13.
 */
@Data
public class UserInfo extends CommonUserInfo implements Serializable {


    @ApiModelProperty("角色ID")
    private Integer roleId;

    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("是否是开发者")
    private Boolean isDeveloper;


}
