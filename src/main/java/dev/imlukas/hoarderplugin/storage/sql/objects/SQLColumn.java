package dev.imlukas.hoarderplugin.storage.sql.objects;

import dev.imlukas.hoarderplugin.storage.sql.data.ColumnData;
import lombok.Getter;

@Getter
public class SQLColumn {

    private final SQLTable table;
    private final ColumnData data;

    public SQLColumn(SQLTable table, ColumnData data) {
        this.table = table;
        this.data = data;
    }
}
