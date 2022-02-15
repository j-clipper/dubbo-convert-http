package jclipper.dubbo.proxy.convert.configuration;

import com.google.common.collect.Sets;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2021/11/17 17:02.
 */
@ConfigurationProperties(DubboConvertProperties.PREFIX)
@Configuration
@Data
public class DubboConvertProperties {
    public static final String PREFIX = "jclipper.dubbo.convert.http";

    private static final Map<RequestMethod, Set<String>> DEFAULT_HTTP_METHOD_KEYWORDS = new TreeMap<>();

    private String application;

    private boolean enabled = false;

    private String[] scanPackages;

    private String swaggerTagPrefix = "dubbo-";

    private String httpPathPrefix = "/dubbo/";

    private Map<RequestMethod, Set<String>> requestMethodKeywords = DEFAULT_HTTP_METHOD_KEYWORDS;

    private Map<RequestMethod, Set<String>> extendRequestMethodKeywords = Collections.emptyMap();

    static {
        DEFAULT_HTTP_METHOD_KEYWORDS.put(RequestMethod.GET, Sets.newHashSet("get", "query", "find", "select", "count", "check", "show", "page", "batchGet", "batchQuery", "batchFind", "batchSelect"));
        DEFAULT_HTTP_METHOD_KEYWORDS.put(RequestMethod.POST, Sets.newHashSet("new", "add", "create", "save","verify", "batchAdd", "batchCreate", "batchSave"));
        DEFAULT_HTTP_METHOD_KEYWORDS.put(RequestMethod.PUT, Sets.newHashSet("update", "mod", "batchUpdate", "batchMod", "change", "batchChange"));
        DEFAULT_HTTP_METHOD_KEYWORDS.put(RequestMethod.DELETE, Sets.newHashSet("del", "remove", "batchDel", "batchRemove"));
        DEFAULT_HTTP_METHOD_KEYWORDS.put(RequestMethod.HEAD, Sets.newHashSet());
        DEFAULT_HTTP_METHOD_KEYWORDS.put(RequestMethod.PATCH, Sets.newHashSet());
        DEFAULT_HTTP_METHOD_KEYWORDS.put(RequestMethod.OPTIONS, Sets.newHashSet());
    }


}
