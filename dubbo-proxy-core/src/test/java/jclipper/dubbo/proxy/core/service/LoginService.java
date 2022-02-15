package jclipper.dubbo.proxy.core.service;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/25 20:04.
 */
public interface LoginService {
    Object loginByMobileCode(Long mobile, String code);

    Object loginByPassword(String userName, String password);

    Object loginByPassword(String userName, String password, String code);
}
