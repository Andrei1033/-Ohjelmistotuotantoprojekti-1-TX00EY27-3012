package com.example.app.DaoElements;

import com.example.app.Database.DatabaseConnection;
import com.example.app.Model.Lesson;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonDaoTest {

    private LessonDao lessonDao;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private MockedStatic<DatabaseConnection> mockedDbConnection;

    @BeforeEach
    void setUp() {
        lessonDao = new LessonDao();

        mockedDbConnection = Mockito.mockStatic(DatabaseConnection.class);
    }

    @AfterEach
    void tearDown() {

        mockedDbConnection.close();
    }



    @Test
    @DisplayName("getLessonsByCourseById palauttaa pyydetyt oppitunnit oikealla alustuksella")
    void testGetLessonsByCourseById_Success() throws SQLException {

        int courseId = 10;
        mockedDbConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);


        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("lesson_id")).thenReturn(101, 102);
        when(mockResultSet.getInt("course_id")).thenReturn(courseId, courseId);
        when(mockResultSet.getString("start_time")).thenReturn("2026-10-04 10:00:00", "2026-10-04 12:00:00");
        when(mockResultSet.getString("end_time")).thenReturn("2026-10-04 11:30:00", "2026-10-04 13:30:00");


        List<Lesson> result = lessonDao.getLessonsByCourseById(courseId);


        assertNotNull(result);
        assertEquals(2, result.size());


        verify(mockPreparedStatement, times(1)).setInt(1, courseId);
        verify(mockPreparedStatement, times(1)).executeQuery();
    }

    @Test
    @DisplayName("getLessonsByCourseById palauttaa tyhjän listan, kun oppitunteja ei löydy")
    void testGetLessonsByCourseById_NoResults() throws SQLException {

        int courseId = 99;
        mockedDbConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(false);


        List<Lesson> result = lessonDao.getLessonsByCourseById(courseId);


        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getLessonsByCourseById käsittelee tietokantavirheen (SQLException) palauttamalla tyhjän listan")
    void testGetLessonsByCourseById_SQLException() throws SQLException {
        // Arrange
        mockedDbConnection.when(DatabaseConnection::getConnection)
                .thenThrow(new SQLException("Yhteysvirhe tietokantaan"));


        List<Lesson> result = lessonDao.getLessonsByCourseById(1);


        assertNotNull(result);
        assertTrue(result.isEmpty());
    }



    @Test
    @DisplayName("startLesson lisää oppitunnin ja palauttaa luodun tunnisteen (ID)")
    void testStartLesson_Success() throws SQLException {

        int courseId = 5;
        int generatedLessonId = 42;

        mockedDbConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(generatedLessonId);


        int resultId = lessonDao.startLesson(courseId);


        assertEquals(generatedLessonId, resultId);


        verify(mockPreparedStatement, times(1)).setInt(1, courseId);
        verify(mockPreparedStatement, times(1)).setObject(eq(2), any(Timestamp.class));
        verify(mockPreparedStatement, times(1)).setObject(eq(3), any(Timestamp.class));
    }

    @Test
    @DisplayName("startLesson palauttaa -1, jos tietokantaan ei lisätty yhtään riviä (executeUpdate == 0)")
    void testStartLesson_ZeroRowsAffected() throws SQLException {

        int courseId = 5;

        mockedDbConnection.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);


        int resultId = lessonDao.startLesson(courseId);


        assertEquals(-1, resultId);
    }

    @Test
    @DisplayName("startLesson palauttaa -1, jos tapahtuu tietokantavirhe (SQLException)")
    void testStartLesson_SQLException() throws SQLException {
        // Arrange
        mockedDbConnection.when(DatabaseConnection::getConnection)
                .thenThrow(new SQLException("Virhe tietokantaan kirjoitettaessa"));


        int resultId = lessonDao.startLesson(1);


        assertEquals(-1, resultId);
    }
}