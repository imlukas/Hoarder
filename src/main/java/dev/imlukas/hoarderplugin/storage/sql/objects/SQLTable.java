package dev.imlukas.hoarderplugin.storage.sql.objects;

import dev.imlukas.hoarderplugin.storage.sql.SQLDatabase;
import dev.imlukas.hoarderplugin.storage.sql.data.ColumnData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SQLTable {

    private final String name;
    private final SQLDatabase provider;
    private final Map<String, SQLColumn> columns = new HashMap<>();

    public SQLTable(String name, SQLDatabase provider) {
        this.name = name;
        this.provider = provider;
        createTable();
    }

    public CompletableFuture<Void> createTable() {
        return provider.getConnection().thenAccept(connection -> {
            try {
                connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS " + name + " (id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY (id))");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Void> addColumn(ColumnData... columnList) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (ColumnData data : columnList) {
            columns.put(name, new SQLColumn(this, data));

            futures.add(provider.getConnection().thenAccept(connection -> {
                try {
                    // if the column already exists, don't add it
                    if (connection.getMetaData().getColumns(null, null, name, data.getName()).next())
                        return;

                    Object value = data.getData();

                    // value can be the length of a varchar, or the precision of a decimal, or just null
                    String valueString = value == null ? "" : "(" + value + ")";

                    connection.createStatement().executeUpdate("ALTER TABLE " + this.name + " ADD COLUMN " + data.getName() + " " + data.getType().name() + valueString);
                    // mariadb is giving me a headache
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    public CompletableFuture<Void> removeColumn(String columnName) {
        columns.remove(columnName);

        return provider.getConnection().thenAccept(connection -> {
            try {
                connection.createStatement().executeUpdate("ALTER TABLE " + this.name + " DROP COLUMN " + columnName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Void> insert(Map<String, Object> data) {
        return provider.getConnection().thenAccept(connection -> {
            try {
                StringBuilder builder = new StringBuilder();

                for (String key : data.keySet()) {
                    builder.append(key).append(", ");
                }

                String columns = builder.substring(0, builder.length() - 2);

                builder = new StringBuilder();

                for (Object value : data.values()) {

                    if (value instanceof String) {
                        value = "'" + value + "'";
                    }
                    builder.append(value).append(", ");
                }

                String values = builder.substring(0, builder.length() - 2);

                connection.createStatement().executeUpdate("INSERT INTO " + name + " (" + columns + ") VALUES (" + values + ")");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<ResultSet> executeQuery(String query, Object... args) {
        return provider.getConnection().thenApply(connection -> {
            try {
                PreparedStatement statement = connection.prepareStatement(query);

                for (int index = 0; index < args.length; index++) {
                    Object ar = args[index];

                    if (ar instanceof String) {
                        statement.setString(index + 1, (String) ar);
                        continue;
                    }

                    statement.setObject(index + 1, args[index]);
                }

                if (query.contains("SELECT") || query.contains("select") || query.contains("Select")) {
                    return statement.executeQuery();
                }

                statement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }


    public SQLColumn getColumn(String columnName) {
        return columns.get(columnName);
    }

    public String getName() {
        return name;
    }
}
