package jclipper.dubbo.proxy.file;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/23 20:09.
 */
public class ScannerClassException extends IllegalArgumentException{
    public ScannerClassException(String message, Exception e) {
        super(message,e);
    }
}
