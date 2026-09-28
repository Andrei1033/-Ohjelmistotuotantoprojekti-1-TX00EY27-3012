package com.example.app.DaoElements.LoginDao;

import com.example.app.Database.DatabaseConnection;
import com.example.app.Model.LoginComponents.Role;
import com.example.app.Model.LoginComponents.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDaoTest {

    @Mock private Connection conn;
    @Mock private PreparedStatement stmt;
    @Mock private ResultSet rs;

    private UserDao userDao;

    @BeforeEach
    void setUp() {
        userDao = new UserDao();
    }

    // ------------------------------------------------------------------
    // findByEmailAndPassword
    // ------------------------------------------------------------------

    @Test
    void findByEmailAndPassword_returnsUser_whenCredentialsMatch() throws SQLException {
        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);

            when(rs.next()).thenReturn(true);
            when(rs.getInt("user_id")).thenReturn(1);
            when(rs.getString("first_name")).thenReturn("Matti");
            when(rs.getString("last_name")).thenReturn("Meikäläinen");
            when(rs.getString("email")).thenReturn("matti@example.com");
            when(rs.getString("role")).thenReturn("ADMIN");

            Optional<User> result = userDao.findByEmailAndPassword(
                    "matti@example.com", "salasana");

            assertTrue(result.isPresent());
            User u = result.get();

            // User.java exposes getId(), not getUserId()
            assertEquals(1, u.getId());
            assertEquals("Matti", u.getFirstName());
            assertEquals("Meikäläinen", u.getLastName());
            assertEquals("matti@example.com", u.getEmail());
            assertEquals(Role.ADMIN, u.getRole());

            verify(stmt).setString(1, "matti@example.com");
            verify(stmt).setString(2, "salasana");
        }
    }

    @Test
    void findByEmailAndPassword_returnsEmpty_whenNoMatch() throws SQLException {
        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(false);

            assertTrue(userDao.findByEmailAndPassword("x@y.z", "nope").isEmpty());
        }
    }

    @Test
    void findByEmailAndPassword_returnsEmpty_whenRoleIsInvalid() throws SQLException {
        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);

            when(rs.next()).thenReturn(true);
            when(rs.getString("role")).thenReturn("NOT_A_REAL_ROLE");

            assertTrue(userDao.findByEmailAndPassword("a@b.c", "p").isEmpty());
        }
    }

    @Test
    void findByEmailAndPassword_returnsEmpty_onSqlException() throws SQLException {
        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenThrow(new SQLException("boom"));

            assertTrue(userDao.findByEmailAndPassword("a@b.c", "p").isEmpty());
        }
    }

    // ------------------------------------------------------------------
    // isEmailTaken
    // ------------------------------------------------------------------

    @Test
    void isEmailTaken_returnsTrue_whenCountGreaterThanZero() throws SQLException {
        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(true);
            when(rs.getInt(1)).thenReturn(1);

            assertTrue(userDao.isEmailTaken("taken@example.com", 42));
            verify(stmt).setString(1, "taken@example.com");
            verify(stmt).setInt(2, 42);
        }
    }

    @Test
    void isEmailTaken_returnsFalse_whenCountIsZero() throws SQLException {
        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(true);
            when(rs.getInt(1)).thenReturn(0);

            assertFalse(userDao.isEmailTaken("free@example.com", 1));
        }
    }

    @Test
    void isEmailTaken_returnsFalse_onSqlException() throws SQLException {
        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenThrow(new SQLException("boom"));

            assertFalse(userDao.isEmailTaken("a@b.c", 1));
        }
    }

    // ------------------------------------------------------------------
    // updateProfile
    // ------------------------------------------------------------------

    @Test
    void updateProfile_returnsTrue_whenRowUpdated() throws SQLException {
        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeUpdate()).thenReturn(1);

            boolean ok = userDao.updateProfile(5, "Etunimi", "Sukunimi", "new@example.com");

            assertTrue(ok);
            verify(stmt).setString(1, "Etunimi");
            verify(stmt).setString(2, "Sukunimi");
            verify(stmt).setString(3, "new@example.com");
            verify(stmt).setInt(4, 5);
        }
    }

    @Test
    void updateProfile_returnsFalse_whenNoRowsUpdated() throws SQLException {
        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeUpdate()).thenReturn(0);

            assertFalse(userDao.updateProfile(5, "A", "B", "c@d.e"));
        }
    }

    @Test
    void updateProfile_returnsFalse_onSqlException() throws SQLException {
        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenThrow(new SQLException("boom"));

            assertFalse(userDao.updateProfile(5, "A", "B", "c@d.e"));
        }
    }

    // ------------------------------------------------------------------
    // verifyPassword
    // ------------------------------------------------------------------

    @Test
    void verifyPassword_returnsTrue_whenPasswordMatches() throws SQLException {
        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(true);
            when(rs.getInt(1)).thenReturn(1);

            assertTrue(userDao.verifyPassword(7, "correct"));
            verify(stmt).setInt(1, 7);
            verify(stmt).setString(2, "correct");
        }
    }

    @Test
    void verifyPassword_returnsFalse_whenPasswordDoesNotMatch() throws SQLException {
        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(true);
            when(rs.getInt(1)).thenReturn(0);

            assertFalse(userDao.verifyPassword(7, "wrong"));
        }
    }

    @Test
    void verifyPassword_returnsFalse_onSqlException() throws SQLException {
        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenThrow(new SQLException("boom"));

            assertFalse(userDao.verifyPassword(7, "any"));
        }
    }

    // ------------------------------------------------------------------
    // updatePassword
    // ------------------------------------------------------------------

    @Test
    void updatePassword_returnsTrue_whenRowUpdated() throws SQLException {
        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeUpdate()).thenReturn(1);

            assertTrue(userDao.updatePassword(9, "newSecret"));
            verify(stmt).setString(1, "newSecret");
            verify(stmt).setInt(2, 9);
        }
    }

    @Test
    void updatePassword_returnsFalse_whenNoRowsUpdated() throws SQLException {
        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenReturn(conn);
            when(conn.prepareStatement(anyString())).thenReturn(stmt);
            when(stmt.executeUpdate()).thenReturn(0);

            assertFalse(userDao.updatePassword(9, "newSecret"));
        }
    }

    @Test
    void updatePassword_returnsFalse_onSqlException() throws SQLException {
        try (MockedStatic<DatabaseConnection> db = mockStatic(DatabaseConnection.class)) {
            db.when(DatabaseConnection::getConnection).thenThrow(new SQLException("boom"));

            assertFalse(userDao.updatePassword(9, "newSecret"));
        }
    }
}