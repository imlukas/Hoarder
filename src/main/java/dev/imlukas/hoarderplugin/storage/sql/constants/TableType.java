package dev.imlukas.hoarderplugin.storage.sql.constants;

/**
 * Enum for available tables in this plugin
 */
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

    public String getName() {
        return name;
    }
}
