package dev.imlukas.hoarderplugin.storage.sql;

import dev.imlukas.hoarderplugin.storage.sql.objects.SQLTable;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SQLDatabase {

    private Connection connection;
    private final String host, database, username, password;
    private final int port;
    private final Map<String, SQLTable> tables = new HashMap<>();

    public SQLDatabase(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public SQLDatabase(ConfigurationSection section) {
        this.host = section.getString("host");
        this.port = section.getInt("port");
        this.database = section.getString("database");
        this.username = section.getString("username");
        this.password = section.getString("password");
        this.connection = createConnection().join();
    }

    public List<SQLTable> createTables(String... names) {
        List<SQLTable> tables = new ArrayList<>();
        for (String name : names) {
            tables.add(getOrCreateTable(name));
        }
        return tables;
    }

    public SQLTable createTable(String name, String creationQuery) {
        SQLTable table = new SQLTable(name, this);
        table.create(creationQuery);
        tables.put(name, table);
        return table;
    }

    public SQLTable getOrCreateTable(String name) {
        if (tables.containsKey(name)) {
            return tables.get(name);
        }

        SQLTable table = new SQLTable(name, this);
        table.create();
        tables.put(name, table);
        return table;
    }

    public CompletableFuture<Connection> getConnection() {
        if (connection == null) {
            createConnection().thenAccept(connection -> this.connection = connection);
        }

        // if the current is not valid, create a new one
        return validateConnection(connection).thenCompose(valid -> {
            if (valid) {
                return CompletableFuture.completedFuture(connection);
            }
            return createConnection();
        });
    }

    private CompletableFuture<Boolean> validateConnection(Connection connection) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return connection.isValid(1);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        });
    }

    private CompletableFuture<Connection> createConnection() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
                System.out.println("Connected to MySQL server | Using database: " + database);

                return connection;
            } catch (Exception e) {
                System.out.println("Failed to connect to MySQL server.");
                e.printStackTrace();
            }
            return null;
        });
    }
}
