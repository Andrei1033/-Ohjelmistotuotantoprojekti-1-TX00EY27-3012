package com.example.app.Database;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import org.junit.jupiter.api.Test;
class DatabaseConnectionTest {
    @Test
    void connectionShouldWork() throws Exception {
        Connection connection = DatabaseConnection.getConnection();

        assertNotNull(connection);
        assertFalse(connection.isClosed());

        connection.close();
    }
}
