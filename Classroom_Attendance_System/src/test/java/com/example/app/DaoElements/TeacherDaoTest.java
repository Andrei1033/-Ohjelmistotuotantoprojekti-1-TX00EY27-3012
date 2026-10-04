package com.example.app.DaoElements;

import com.example.app.Database.DatabaseConnection;
import com.example.app.Model.Teacher;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
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
class TeacherDaoTest {

    private TeacherDao teacherDao;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private MockedStatic<DatabaseConnection> mockedDbConnection;

    @BeforeEach
    void setUp() {
        teacherDao = new TeacherDao();

        mockedDbConnection = Mockito.mockStatic(DatabaseConnection.class);
    }

    @AfterEach
    void tearDown() {

        mockedDbConnection.close();
    }

    @Test
    @DisplayName("getTeacherbyId palauttaa Optional<Teacher> kun opettaja löytyy")
    void testGetTeacherById_Success() throws SQLException {
        // Arrange
        int userId = 1;
        mockedDbConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);


        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("user_id")).thenReturn(userId);
        when(mockResultSet.getString("first_name")).thenReturn("Matti");
        when(mockResultSet.getString("last_name")).thenReturn("Meikäläinen");
        when(mockResultSet.getString("email")).thenReturn("matti.meikalainen@example.com");


        Optional<Teacher> result = teacherDao.getTeacherbyId(userId);


        assertTrue(result.isPresent(), "Opettajan pitäisi löytyä");


        verify(mockPreparedStatement, times(1)).setInt(1, userId);
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    @DisplayName("getTeacherbyId palauttaa Optional.empty() kun käyttäjää ei löydy tai rooli ei ole 'teacher'")
    void testGetTeacherById_NotFound() throws SQLException {
        // Arrange
        int userId = 99;
        mockedDbConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(false);


        Optional<Teacher> result = teacherDao.getTeacherbyId(userId);


        assertTrue(result.isEmpty(), "Pitäisi palauttaa tyhjä Optional kun käyttäjää ei löydy");
        verify(mockPreparedStatement, times(1)).setInt(1, userId);
    }

    @Test
    @DisplayName("getTeacherbyId palauttaa Optional.empty() kun tapahtuu SQLException")
    void testGetTeacherById_HandlesSQLException() throws SQLException {
        // Arrange
        int userId = 1;
        mockedDbConnection.when(DatabaseConnection::getConnection)
                .thenThrow(new SQLException("Yhteys katkesi tietokantaan"));


        Optional<Teacher> result = teacherDao.getTeacherbyId(userId);


        assertTrue(result.isEmpty(), "Pitäisi palauttaa tyhjä Optional virhetilanteessa");
    }
}