package com.pcc.phoenixsql.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

/**
 * 避免和SringUtils名字重复的一个String处理工具类
 * 
 * @author peichenchen
 * @version 17/10/12 上午10:58
 */
public class MyStringUtil {

    public static String getStringList(List<?> list, String prefix, boolean useComma) {
        StringBuilder ans = new StringBuilder();
        String comma = ",";
        if (!useComma) {
            comma = "";
        }
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                ans.append(prefix).append("_").append(list.get(i)).append((i < list.size() - 1) ? comma : "");
            }
        }

        return ans.toString();
    }

    public static String addPrefixIfNotBlank(String originStr, String columnPrefix) {
        String resultStr = originStr;
        if (StringUtils.isNotBlank(columnPrefix)) {
            resultStr = columnPrefix + "_" + originStr;
        }
        return resultStr;
    }

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
