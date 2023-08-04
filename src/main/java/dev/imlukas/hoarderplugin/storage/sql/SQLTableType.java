package dev.imlukas.hoarderplugin.storage.sql;

import lombok.Getter;

@Getter
public enum SQLTableType {

    HOARDER_WINNER("hoarder_winners"),
    HOARDER_STATS("hoarder_stats");

    private final String name;

    SQLTableType(String name) {
        this.name = name;
    }

}
