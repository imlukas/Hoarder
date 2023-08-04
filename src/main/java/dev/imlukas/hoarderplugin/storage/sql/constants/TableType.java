package dev.imlukas.hoarderplugin.storage.sql.constants;

import lombok.Getter;

/**
 * Enum for available tables in this plugin
 */
@Getter
public enum TableType {

    BANS("bans"),
    KICKS("kicks"),
    UNBANS("unbans"),
    WARNINGS("warnings"),
    REPORT("report");

    private final String name;

    TableType(String name) {
        this.name = name;
    }

}
