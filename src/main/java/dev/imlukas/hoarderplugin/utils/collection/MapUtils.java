package dev.imlukas.hoarderplugin.utils.collection;

import java.util.*;

public class MapUtils {

    private MapUtils() {}
    public static <T> Map<Integer, T> getLeaderboardMap(Map<T, Integer> mapToSort) {
        return getLeaderboardMap(mapToSort, 1);
    }
    public static <T> Map<Integer, T> getLeaderboardMap(Map<T, Integer> mapToSort, int amount) {
        List<Map.Entry<T, Integer>> values = new ArrayList<>(mapToSort.entrySet());
        values.sort(Map.Entry.comparingByValue());
        Collections.reverse(values);

        Map<Integer, T> leaderboardMap = new HashMap<>();

        int i = amount == 1 ? values.size() : amount;

        for (int j = 1; j <= i; j++) {
            Map.Entry<T, Integer> leaderboardEntry = values.get(j - 1);
            leaderboardMap.put(i, leaderboardEntry.getKey());
        }

        return leaderboardMap;
    }
}
