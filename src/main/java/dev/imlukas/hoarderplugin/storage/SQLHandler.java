package dev.imlukas.hoarderplugin.storage;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.storage.sql.SQLDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SQLHandler {

    private final static String TABLE_NAME = "hoarder";
    private final SQLDatabase sqlDatabase;

    public SQLHandler(HoarderPlugin plugin) {
        sqlDatabase = plugin.getSqlDatabase();
    }

    public CompletableFuture<Void> insertValue(String columnName, Object value) {
        return sqlDatabase.getOrCreateTable(TABLE_NAME).insert(Map.of(columnName, value));
    }

    public CompletableFuture<Void> insertValue(Map<String, Object> values) {
        return sqlDatabase.getOrCreateTable(TABLE_NAME).insert(values);
    }

    public <T> CompletableFuture<List<T>> getValue(String columnName, UUID playerId, Class<T> T) {
        return sqlDatabase.getOrCreateTable(TABLE_NAME).executeQuery("SELECT " + columnName + " FROM" +
                TABLE_NAME + "WHERE winnerid = '" + playerId.toString() + "'").thenApply(result -> {
            try {
                List<T> values = new ArrayList<>();
                while (result.next()) {
                    values.add(result.getObject(columnName, T));
                }

                return values;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
