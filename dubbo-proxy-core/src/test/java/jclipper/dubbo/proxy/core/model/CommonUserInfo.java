package jclipper.dubbo.proxy.core.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2020/8/26 13:38.
 */
@Data
public class CommonUserInfo extends BaseUserInfo implements Serializable {

    @ApiModelProperty("性别")
    private Integer gender;

    @ApiModelProperty("生日")
    private LocalDateTime birthday;

    @ApiModelProperty("手机号码")
    protected String mobile;

    @ApiModelProperty("邮箱")
    protected String email;

    @ApiModelProperty("头像")
    protected String photo;

    @ApiModelProperty("注册时间")
    private LocalDateTime registerTime;
}
