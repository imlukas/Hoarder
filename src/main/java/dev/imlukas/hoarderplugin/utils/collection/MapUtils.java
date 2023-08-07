package dev.imlukas.hoarderplugin.utils.collection;

import dev.imlukas.hoarderplugin.event.data.hoarder.HoarderPlayerEventData;

import java.util.*;
import java.util.stream.Collectors;

public class MapUtils {

    public static <T> Map<Integer, T> getLeaderboardMap(Map<T, Integer> mapToSort) {
        List<Map.Entry<T, Integer>> values = new ArrayList<>(mapToSort.entrySet());
        values.sort(Map.Entry.comparingByValue());
        Collections.reverse(values);

        Map<Integer, T> leaderboardMap = new HashMap<>();

        int i = 1;
        for (Map.Entry<T, Integer> leaderboardEntry : values) {
            leaderboardMap.put(i, leaderboardEntry.getKey());
            i++;
        }

        return leaderboardMap;
    }
}
