package dev.imlukas.hoarderplugin.utils.collection;

import dev.imlukas.hoarderplugin.event.data.hoarder.HoarderPlayerEventData;

import java.util.*;
import java.util.stream.Collectors;

public class MapUtils {

    public static <T> Map<Integer, T> getLeaderboardMap(Map<T, Integer> mapToSort) {
        System.out.println("Unsorted:");
        for (Map.Entry<T, Integer> tIntegerEntry : mapToSort.entrySet()) {
            System.out.println(tIntegerEntry.getKey() + " " + tIntegerEntry.getValue());
        }

        List<Map.Entry<T, Integer>> values = new ArrayList<>(mapToSort.entrySet());
        values.sort(Map.Entry.comparingByValue());
        Collections.reverse(values);

        Map<Integer, T> leaderboardMap = new HashMap<>();

        System.out.println("Sorted:");
        for (Map.Entry<T, Integer> tIntegerEntry : values) {
            System.out.println(tIntegerEntry.getKey() + " " + tIntegerEntry.getValue());
        }

        int i = 1;
        for (Map.Entry<T, Integer> leaderboardEntry : values) {
            leaderboardMap.put(i, leaderboardEntry.getKey());
            i++;
        }

        System.out.println("Leaderboard:");
        for (Map.Entry<Integer, T> integerTEntry : leaderboardMap.entrySet()) {
            System.out.println(integerTEntry.getKey() + " " + integerTEntry.getValue());
        }

        return leaderboardMap;
    }
}
