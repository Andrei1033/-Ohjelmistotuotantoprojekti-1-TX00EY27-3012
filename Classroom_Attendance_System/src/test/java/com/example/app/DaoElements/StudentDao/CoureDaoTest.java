package com.example.app.DaoElements.StudentDao;

import com.example.app.Database.DatabaseConnection;
import com.example.app.Model.StudentComponents.Course;
import com.example.app.Model.TeacherCourse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

class CourseDaoTest {

    private CourseDao dao;

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    @BeforeEach
    void setUp() throws Exception {
        dao = new CourseDao();

        conn = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        rs = mock(ResultSet.class);
    }

    // ---------------------------------------------------------------
    // getCoursesForStudent
    // ---------------------------------------------------------------

    @Test
    void getCoursesForStudent_returnsCoursesWithLessonCount()
            throws Exception {

        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        // Row 1, Row 2, then end
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getInt("course_id")).thenReturn(1, 2);
        when(rs.getString("code")).thenReturn("CS101", "MA201");
        when(rs.getString("name")).thenReturn("Java Basics", "Math");
        when(rs.getInt("teacher_id")).thenReturn(50, 51);
        when(rs.getInt("lesson_count")).thenReturn(3, 0);

        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(conn);

            List<Course> courses = dao.getCoursesForStudent(42);

            assertEquals(2, courses.size());

            Course first = courses.get(0);
            assertEquals(1, first.getId());
            assertEquals("CS101", first.getCode());
            assertEquals("Java Basics", first.getName());
            assertEquals(50, first.getTeacherId());
            assertEquals(3, first.getLessonCount());

            Course second = courses.get(1);
            assertEquals(2, second.getId());
            assertEquals("MA201", second.getCode());
            assertEquals("Math", second.getName());
            assertEquals(51, second.getTeacherId());
            assertEquals(0, second.getLessonCount());
        }

        verify(ps).setInt(1, 42);
    }

    @Test
    void getCoursesForStudent_returnsEmptyWhenNoRows() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(conn);

            assertTrue(dao.getCoursesForStudent(42).isEmpty());
        }
    }

    @Test
    void getCoursesForStudent_returnsEmptyOnSqlException() throws Exception {
        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection)
                    .thenThrow(new SQLException("DB down"));

            assertTrue(dao.getCoursesForStudent(42).isEmpty());
        }
    }

    // ---------------------------------------------------------------
    // addCourse
    // ---------------------------------------------------------------

    @Test
    void addCourse_insertsAndReturnsTrue() throws Exception {
        when(conn.prepareStatement(contains("INSERT INTO courses")))
                .thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(conn);

            boolean result = dao.addCourse("Java Basics", "CS101", 50);

            assertTrue(result);

            verify(ps).setString(1, "Java Basics"); // trimmed
            verify(ps).setString(2, "CS101");       // code
            verify(ps).setInt(3, 50);
            verify(ps).executeUpdate();
        }
    }

    @Test
    void addCourse_trimsWhitespaceInName() throws Exception {
        when(conn.prepareStatement(contains("INSERT INTO courses")))
                .thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(conn);

            assertTrue(dao.addCourse("   Web Dev   ", "WD101", 7));

            verify(ps).setString(1, "Web Dev");
        }
    }

    @Test
    void addCourse_returnsFalseForNullName() {
        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            assertFalse(dao.addCourse(null, "CS101", 50));

            mocked.verifyNoInteractions();
        }
    }

    @Test
    void addCourse_returnsFalseForBlankName() {
        assertFalse(dao.addCourse("   ", "CS101", 50));
    }

    @Test
    void addCourse_returnsFalseForEmptyName() {
        assertFalse(dao.addCourse("", "CS101", 50));
    }

    @Test
    void addCourse_returnsFalseForInvalidTeacherId() {
        assertFalse(dao.addCourse("Java", "CS101", 0));
        assertFalse(dao.addCourse("Java", "CS101", -1));
    }

    @Test
    void addCourse_returnsFalseWhenNoRowInserted() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(0);

        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(conn);

            assertFalse(dao.addCourse("Java", "CS101", 50));
        }
    }

    @Test
    void addCourse_returnsFalseOnSqlException() throws Exception {
        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection)
                    .thenThrow(new SQLException("fail"));

            assertFalse(dao.addCourse("Java", "CS101", 50));
        }
    }

    // ---------------------------------------------------------------
    // getCoursesByTeacherId
    // ---------------------------------------------------------------

    @Test
    void getCoursesByTeacherId_returnsCourses() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, true, false);
        when(rs.getInt("course_id")).thenReturn(10, 11);
        when(rs.getString("name")).thenReturn("Java", "Databases");
        when(rs.getInt("teacher_id")).thenReturn(50, 50);

        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(conn);

            List<TeacherCourse> courses = dao.getCoursesByTeacherId(50);

            assertEquals(2, courses.size());

            TeacherCourse first = courses.get(0);
            assertEquals(10, first.getCourseid());
            assertEquals("Java", first.getCoursename());  // adjust if getter differs
            assertEquals(50, first.getTeacherid());

            TeacherCourse second = courses.get(1);
            assertEquals(11, second.getCourseid());
            assertEquals("Databases", second.getCoursename());
            assertEquals(50, second.getTeacherid());
        }

        verify(ps).setInt(1, 50);
    }

    @Test
    void getCoursesByTeacherId_returnsEmptyForInvalidTeacherId() {
        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            assertTrue(dao.getCoursesByTeacherId(0).isEmpty());
            assertTrue(dao.getCoursesByTeacherId(-5).isEmpty());
            mocked.verifyNoInteractions();
        }
    }

    @Test
    void getCoursesByTeacherId_returnsEmptyWhenNoRows() throws Exception {
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection).thenReturn(conn);

            assertTrue(dao.getCoursesByTeacherId(50).isEmpty());
        }
    }

    @Test
    void getCoursesByTeacherId_returnsEmptyOnSqlException() throws Exception {
        try (MockedStatic<DatabaseConnection> mocked =
                     mockStatic(DatabaseConnection.class)) {

            mocked.when(DatabaseConnection::getConnection)
                    .thenThrow(new SQLException("boom"));

            assertTrue(dao.getCoursesByTeacherId(50).isEmpty());
        }
    }
}