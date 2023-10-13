package dev.imlukas.hoarderplugin.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MapUtil {

    private MapUtil() {}

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Entry<K, V>> entryList = new ArrayList<>(map.entrySet());
        entryList.sort(Entry.comparingByValue());

        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : entryList) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}