package jclipper.dubbo.proxy.core.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2020/5/20 09:09.
 */
@Data
@NoArgsConstructor
@ApiModel("统一响应类")
public class Result<T> implements Serializable {
    /**
     * 业务响应是否成功
     */
    @ApiModelProperty(value = "业务响应是否成功")
    private Boolean success;
    /**
     * 业务响应状态码
     */
    @ApiModelProperty(value = "业务响应状态码")
    private Integer code = 200;
    /**
     * 业务响应信息
     */
    @ApiModelProperty(value = "业务响应信息")
    private String message;
    /**
     * 业务响应数据
     */
    @ApiModelProperty(value = "业务响应数据")
    private T data;

    public static <T> Result<T> ok() {
        return of(200, true, null, null);
    }

    public static <T> Result<T> ok(T data) {
        return of(200, true, null, data);
    }

    public static <T> Result<T> ok(String message) {
        return of(200, true, message, null);
    }

    public static <T> Result<T> error() {
        return error(CommonErrorCode.REQUEST_PARAM_ERROR.getCode(), null);
    }

    public static <T> Result<T> paramError() {
        return error(CommonErrorCode.REQUEST_PARAM_ERROR.getCode(), CommonErrorCode.REQUEST_PARAM_ERROR.getName());
    }

    public static <T> Result<T> error(String message) {
        return of(CommonErrorCode.REQUEST_PARAM_ERROR.getCode(), false, message, null);
    }

    public static <T> Result<T> error(int code, String message) {
        return of(code, false, message, null);
    }

    public static <T> Result<T> error(BaseErrorCode error) {
        return of(error.getCode(), false, error.getName(), null);
    }

    public static <T> Result<T> error(int code) {
        return of(code, false, null, null);
    }

    public static <T> Result<T> of(Integer code, Boolean success, String message, T data) {

        Result<T> result = new Result<>();
        result.setCode(code);
        result.setData(data);
        result.setMessage(message);
        result.setSuccess(success);
        return result;
    }

    public Result success(boolean success) {
        this.success = success;
        return this;
    }

    public Result code(int code) {
        this.code = code;
        return this;
    }

    public Result message(String message) {
        this.message = message;
        return this;
    }

    public Result data(T data) {
        this.data = data;
        return this;
    }
}
