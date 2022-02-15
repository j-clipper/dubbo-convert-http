package jclipper.dubbo.proxy.core.model;


import lombok.AllArgsConstructor;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2020/4/27 11:12.
 */
@AllArgsConstructor
public enum CommonErrorCode implements BaseErrorCode {
    /**
     *
     */
    REQUEST_PARAM_ERROR(400, "请求参数不符合"),
    UNAUTHORIZED(40101, "身份验证无效，请登录"),
    LOGIN_TOKEN_IS_UPDATE(40102, "您的账号在其他地方被登录，请重新登录"),
    LOGIN_STATUS_EXPIRE(40103, "身份验证过期，请重新登录"),
    FORBIDDEN(403, "权限不够"),
    NOT_FOUND(404, "资源信息不存在"),
    METHOD_NOT_SUPPORTED(405, "请求方法错误"),
    FILE_OVER_SIZE(413, "上传文件大小超出限制"),
    RESOURCE_LOCKED(423, "资源被锁定，请稍后再试"),
    SERVER_LIMIT_CODE(429, "服务限流"),
    SYSTEM_ERROR(500, "服务器异常"),
    SERVER_DOWNGRADE_CODE(700, "服务降级"),
    ;


    private int code;

    private String name;


    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }
}
