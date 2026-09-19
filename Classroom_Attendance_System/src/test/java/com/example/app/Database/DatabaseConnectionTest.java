package com.example.app.Database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
class DatabaseConnectionTest {
    @Test
    void connectionShouldWork() throws Exception {
        Connection connection = DatabaseConnection.getConnection();

        assertNotNull(connection);
        assertFalse(connection.isClosed());

        connection.close();
    }
}