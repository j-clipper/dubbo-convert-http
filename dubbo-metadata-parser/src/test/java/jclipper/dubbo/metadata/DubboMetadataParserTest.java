package jclipper.dubbo.metadata;

import com.alibaba.fastjson.JSON;
import jclipper.dubbo.metadata.model.DubboUrlInfo;
import jclipper.dubbo.metadata.model.SelectRequest;
import jclipper.dubbo.metadata.finder.DubboMetadataFinder;
import jclipper.dubbo.metadata.finder.NacosDubboMetadataFinder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2022/2/10 15:24.
 */
@Slf4j
public class DubboMetadataParserTest {
    private static DubboMetadataFinder finder;

    @BeforeAll
    public static void init() {
        finder = new NacosDubboMetadataFinder();
    }

    @Test
    public void test1() {
        SelectRequest request = SelectRequest.builder()
                .serverAddress("http://192.168.20.156:8848")
                .namespace("8a87e535-6c65-45b0-8cf2-2e57c9563651")
                .serviceName("strong-account")
                .ip("192.168.20.2")
                .ip("192.168.150.78")
                .build();

        Map<String, List<DubboUrlInfo>> metadata = finder.getDubboMetadata(request);

        System.out.println(JSON.toJSONString(metadata));
    }

}
