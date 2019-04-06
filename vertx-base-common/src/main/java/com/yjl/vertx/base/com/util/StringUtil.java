package com.yjl.vertx.base.com.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @Auther: yinjiaolong
 * @Date: 2019/1/14 21:10
 * @Description:
 */
public class StringUtil {

    private static final Pattern PARAM_PATTERN = Pattern.compile("\\$\\{(?<name>\\w+)\\}");

    public static boolean isBlank(String str) {
        return str == null || "".equals(str.trim());
    }

    public static String concatPath(String... paths) {
        return concatPath(true, paths);
    }

    public static String nvl(String string) {
        return nvl(string, "");
    }

    public static String nvl(boolean nvlSpace, String string, String defaultValue) {
        return (string == null || nvlSpace && "".equals(string.trim())) ? defaultValue : string;
    }

    public static boolean equals(String str1, String str2) {
        return nvl(str1).equals(nvl(str2));
    }

    public static String nvl(String string, String defaultValue) {
        return nvl(false, string, defaultValue);
    }

    public static String concatPath(boolean startSlash, String... paths) {
//        String start = startSlash ? "/" : "";
//        return Stream.of(paths).map(path -> {
//            if (path.startsWith("/")) {
//                path = path.substring(1);
//            }
//            if (path.endsWith("/")) {
//                path = path.substring(0, path.length() - 1);
//            }
//            return path;
//        }).reduce((p1, p2) -> p1 + "/" + p2).map(path -> !path.startsWith("/") ? start + path : path).orElse(start);
        return concat(startSlash ? "/" : "", "/", paths);
    }

    public static String concat(String start, String delimiter, String... concatStrings) {
        return Stream.of(concatStrings).map(str -> {
            if (str.startsWith(delimiter)) {
                str = str.substring(delimiter.length());
            }
            if (str.endsWith(delimiter)) {
                str = str.substring(0, str.length() - delimiter.length());
            }
            return str;
        }).reduce((p1, p2) -> p1 + delimiter + p2).map(result -> !result.startsWith(start) ? start + result : result).orElse(start);
    }

    public static String replaceParam(String str, Map<String, Object> paramMap) {
        Matcher matcher = PARAM_PATTERN.matcher(str);
        String ret = str;
        while (matcher.find()) {
            ret = ret.replace(matcher.group(), String.valueOf(paramMap.get(matcher.group("name"))));
        }
        return ret;
    }
}
