import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Maps;

/**
 * Created by peichenchen on 17/10/12.
 */
public class Test {
    public static void main(String[] args) throws IOException {
        String queryParam = FileUtils.readFileToString(new File("/Users/peichenchen/Downloads/orderListData"), "utf-8");
        List<String> matchItems = StringUtil.extractMatchItems(queryParam, "[a-zA-Z]+=");
        Map<String, AtomicInteger> param2Count = Maps.newHashMap();
        for (String matchItem : matchItems) {
            AtomicInteger count = param2Count.get(matchItem);
            if (count == null) {
                count = new AtomicInteger();
                param2Count.put(matchItem, count);
            }
            count.incrementAndGet();
        }

        Set<Map.Entry<String, AtomicInteger>> entries = param2Count.entrySet();
        for (Map.Entry<String, AtomicInteger> entry : entries) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }

    }
}
