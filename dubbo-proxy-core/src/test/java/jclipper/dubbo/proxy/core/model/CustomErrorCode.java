package jclipper.dubbo.proxy.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2020/5/11 21:14.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomErrorCode implements BaseErrorCode {
    private int code;

    private String name;

    public static CustomErrorCode wrapper(BaseErrorCode errorCode) {
        return new CustomErrorCode(errorCode.getCode(), errorCode.getName());
    }

    public static CustomErrorCode wrapper(BaseErrorCode errorCode, Object... params) {
        return new CustomErrorCode(errorCode.getCode(), String.format(errorCode.getName(), params));
    }

}
