package com.example.app.Database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final Properties properties = new Properties();

    static {
        String configFile = System.getenv("DATABASE_CONFIG_FILE");
        try (InputStream input = configFile == null || configFile.isBlank()
                ? DatabaseConnection.class.getClassLoader().getResourceAsStream("database.properties")
                : new FileInputStream(configFile)) {

            if (input == null) {
                throw new RuntimeException("Tiedostoa 'database.properties' ei löytynyt resources-kansiosta!");
            }
            properties.load(input);

        }
        catch (IOException e) {
            throw new RuntimeException("Database configuration could not be loaded.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(properties.getProperty("db.url"), properties.getProperty("db.user"),
                properties.getProperty("db.password"));
    }
}
