package jclipper.dubbo.metadata.model;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author <a href="mailto:wf2311@163.com">wf2311</a>
 * @since 2022/2/10 17:57.
 */
@Data
public class DubboUrlInfo implements Serializable {
    private String url;
    private Map<String, Object> params = new HashMap<>();

    public static Map<String, List<DubboUrlInfo>> parse(Map<String, String> map) {
        Map<String, List<DubboUrlInfo>> result = new HashMap<>(map.size());
        map.forEach((key, value) -> {
            List<String> list = JSON.parseArray(value, String.class);
            List<DubboUrlInfo> urls = list.stream().map(DubboUrlInfo::parse).collect(Collectors.toList());
            result.put(key, urls);
        });
        return result;
    }

    public static DubboUrlInfo parse(String url) {
        String[] temp = url.split("\\?", 2);
        DubboUrlInfo info = new DubboUrlInfo();
        info.setUrl(temp[0]);
        String[] params = temp[1].split("&");
        Map<String, Object> map = new HashMap<>(params.length);
        for (String param : params) {
            String[] kv = param.split("=", 2);
            if (kv.length == 2) {
                String key = kv[0];
                String value = kv[1];
                String[] vs = value.split(",");
                if (vs.length == 1) {
                    map.put(key, value);
                } else {
                    map.put(key, vs);
                }
            }
        }
        info.setParams(map);
        return info;
    }


}
