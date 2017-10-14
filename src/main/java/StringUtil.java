import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

/**
 * Created by peichenchen on 17/10/12.
 */
public class StringUtil {

    /**
     * 提取匹配的字符串，返回匹配组内的group(1)集合
     *
     * @param str
     * @param regex
     * @return
     */
    public static List<String> extractMatchItems(String str, String regex) {
        List<String> matchItems = Lists.newArrayList();
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        while (m.find()) {
            matchItems.add(m.group(1));
        }

        return matchItems;
    }

    /**
     * 提取匹配的字符串，只有第一个匹配项会返回
     * @param str
     * @param regex
     * @return
     */
    public static String extractMatchItem(String str, String regex) {
        List<String> matchItems = extractMatchItems(str, regex);
        if (matchItems.isEmpty()) {
            return null;
        }
        return matchItems.get(0);
    }
}
