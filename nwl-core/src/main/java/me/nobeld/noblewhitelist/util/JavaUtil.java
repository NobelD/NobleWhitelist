package me.nobeld.noblewhitelist.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JavaUtil {
    public static boolean hasClass(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean hasClass(String... classNames) {
        for (String className : classNames) {
            if (hasClass(className)) {
                return true;
            }
        }
        return false;
    }

    public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByKey());

        Map<K, V> sorted = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            sorted.put(entry.getKey(), entry.getValue());
        }
        return sorted;
    }

    public static String buildString(Object... string) {
        StringBuilder builder = new StringBuilder();
        for (Object s : string) {
            if (s == null || (s instanceof String && s.equals("null"))) continue;
            builder.append(s);
        }
        return builder.toString();
    }
}
