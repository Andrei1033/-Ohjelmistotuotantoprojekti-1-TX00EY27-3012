package com.example.app.DaoElements.StudentDao;

import com.example.app.Database.DatabaseConnection;
import com.example.app.Model.LoginComponents.Role;
import com.example.app.Model.LoginComponents.User;
import com.example.app.Model.StudentComponents.AttendanceRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AttendanceDaoTest {

    private AttendanceDao dao;

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    @BeforeEach
    void setUp() throws Exception {
        dao = new AttendanceDao();

        conn = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);
    }

    // ---------------------------------------------------------------
    // getAttendanceForStudentAndCourse
    // ---------------------------------------------------------------

    @Test
    void getAttendanceForStudentAndCourse_returnsRecords() throws Exception {
        Timestamp start = Timestamp.valueOf("2025-01-10 09:00:00");
        Timestamp end = Timestamp.valueOf("2025-01-10 11:00:00");

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        // Row 1: with attendance
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getInt("lesson_id")).thenReturn(1, 2);
        when(rs.getTimestamp("start_time")).thenReturn(start, start);
        when(rs.getTimestamp("end_time")).thenReturn(end, end);
        when(rs.getString("topic")).thenReturn("Java Basics", "OOP");
        when(rs.getString("status")).thenReturn("present", null);

        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(conn);

            List<AttendanceRecord> records =
                    dao.getAttendanceForStudentAndCourse(10, 5);

            assertEquals(2, records.size());

            assertEquals(1, records.get(0).getLessonId());
            assertEquals(LocalDateTime.of(2025, 1, 10, 9, 0),
                    records.get(0).getStartTime());
            assertEquals(LocalDateTime.of(2025, 1, 10, 11, 0),
                    records.get(0).getEndTime());
            assertEquals("Java Basics", records.get(0).getTopic());
            assertEquals("present", records.get(0).getStatus());

            // NULL status defaults to "absent"
            assertEquals(2, records.get(1).getLessonId());
            assertEquals("absent", records.get(1).getStatus());
        }

        verify(ps).setInt(1, 10);
        verify(ps).setInt(2, 5);
    }

    @Test
    void getAttendanceForStudentAndCourse_nullTimestampsHandled()
            throws Exception {

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("lesson_id")).thenReturn(3);
        when(rs.getTimestamp("start_time")).thenReturn(null);
        when(rs.getTimestamp("end_time")).thenReturn(null);
        when(rs.getString("topic")).thenReturn("Topic");
        when(rs.getString("status")).thenReturn("present");

        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(conn);

            List<AttendanceRecord> records =
                    dao.getAttendanceForStudentAndCourse(1, 1);

            assertEquals(1, records.size());
            assertNull(records.get(0).getStartTime());
            assertNull(records.get(0).getEndTime());
        }
    }

    @Test
    void getAttendanceForStudentAndCourse_returnsEmptyOnSqlException()
            throws Exception {

        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection)
                    .thenThrow(new SQLException("DB down"));

            List<AttendanceRecord> records =
                    dao.getAttendanceForStudentAndCourse(1, 1);

            assertTrue(records.isEmpty());
        }
    }

    // ---------------------------------------------------------------
    // getStudentsForCourse
    // ---------------------------------------------------------------

    @Test
    void getStudentsForCourse_returnsStudents() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, true, false);
        when(rs.getInt("user_id")).thenReturn(100, 101);
        when(rs.getString("first_name")).thenReturn("Matti", "Liisa");
        when(rs.getString("last_name")).thenReturn("Meikäläinen", "Virtanen");
        when(rs.getString("email"))
                .thenReturn("matti@x.fi", "liisa@x.fi");

        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(conn);

            List<User> students = dao.getStudentsForCourse(7);

            assertEquals(2, students.size());

            User first = students.get(0);
            assertEquals(100, first.getId());
            assertEquals("Matti", first.getFirstName());
            assertEquals("Meikäläinen", first.getLastName());
            assertEquals("matti@x.fi", first.getEmail());
            assertEquals(Role.STUDENT, first.getRole());
        }

        verify(ps).setInt(1, 7);
    }

    @Test
    void getStudentsForCourse_returnsEmptyOnSqlException() throws Exception {
        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection)
                    .thenThrow(new SQLException("boom"));

            assertTrue(dao.getStudentsForCourse(1).isEmpty());
        }
    }

    // ---------------------------------------------------------------
    // saveAttendance
    // ---------------------------------------------------------------

    @Test
    void saveAttendance_insertsWhenNoExistingRecord() throws Exception {
        PreparedStatement checkPs = mock(PreparedStatement.class);
        PreparedStatement insertPs = mock(PreparedStatement.class);
        ResultSet checkRs = mock(ResultSet.class);

        when(conn.prepareStatement(contains("SELECT attendance_id")))
                .thenReturn(checkPs);
        when(conn.prepareStatement(contains("INSERT INTO attendance")))
                .thenReturn(insertPs);

        when(checkPs.executeQuery()).thenReturn(checkRs);
        when(checkRs.next()).thenReturn(false); // no existing record

        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(conn);

            boolean result = dao.saveAttendance(1, 10, "present");

            assertTrue(result);

            verify(checkPs).setInt(1, 1);
            verify(checkPs).setInt(2, 10);

            verify(insertPs).setInt(1, 1);
            verify(insertPs).setInt(2, 10);
            verify(insertPs).setString(3, "present");
            verify(insertPs).executeUpdate();
        }
    }

    @Test
    void saveAttendance_updatesWhenRecordExists() throws Exception {
        PreparedStatement checkPs = mock(PreparedStatement.class);
        PreparedStatement updatePs = mock(PreparedStatement.class);
        ResultSet checkRs = mock(ResultSet.class);

        when(conn.prepareStatement(contains("SELECT attendance_id")))
                .thenReturn(checkPs);
        when(conn.prepareStatement(contains("UPDATE attendance")))
                .thenReturn(updatePs);

        when(checkPs.executeQuery()).thenReturn(checkRs);
        when(checkRs.next()).thenReturn(true);
        when(checkRs.getInt("attendance_id")).thenReturn(42);

        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(conn);

            boolean result = dao.saveAttendance(1, 10, "absent");

            assertTrue(result);

            verify(updatePs).setString(1, "absent");
            verify(updatePs).setInt(2, 42);
            verify(updatePs).executeUpdate();
        }
    }

    @Test
    void saveAttendance_returnsFalseOnSqlException() throws Exception {
        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection)
                    .thenThrow(new SQLException("fail"));

            assertFalse(dao.saveAttendance(1, 10, "present"));
        }
    }

    // ---------------------------------------------------------------
    // endLesson
    // ---------------------------------------------------------------

    @Test
    void endLesson_returnsTrueWhenRowUpdated() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(conn);

            assertTrue(dao.endLesson(5));

            verify(ps).setTimestamp(eq(1), any(Timestamp.class));
            verify(ps).setInt(2, 5);
        }
    }

    @Test
    void endLesson_returnsFalseWhenNoRowUpdated() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(0);

        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(conn);

            assertFalse(dao.endLesson(5));
        }
    }

    @Test
    void endLesson_returnsFalseOnSqlException() throws Exception {
        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection)
                    .thenThrow(new SQLException("fail"));

            assertFalse(dao.endLesson(5));
        }
    }
}