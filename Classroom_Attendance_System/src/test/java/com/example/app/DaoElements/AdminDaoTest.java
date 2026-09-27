package com.example.app.DaoElements;

import com.example.app.Database.DatabaseConnection;
import com.example.app.Model.Admin;
import com.example.app.Model.LoginComponents.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminDaoTest {

    private final AdminDao dao = new AdminDao();

    @BeforeEach
    void createCleanSchema() throws Exception {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS course_students");
            statement.execute("DROP TABLE IF EXISTS courses");
            statement.execute("DROP TABLE IF EXISTS users");
            statement.execute("""
                    CREATE TABLE users (
                        user_id INT AUTO_INCREMENT PRIMARY KEY,
                        first_name VARCHAR(100) NOT NULL,
                        last_name VARCHAR(100) NOT NULL,
                        email VARCHAR(255) NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        role VARCHAR(20) NOT NULL
                    )
                    """);
            statement.execute("""
                    CREATE TABLE courses (
                        course_id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        teacher_id INT
                    )
                    """);
            statement.execute("""
                    CREATE TABLE course_students (
                        course_id INT NOT NULL,
                        student_id INT NOT NULL,
                        PRIMARY KEY (course_id, student_id)
                    )
                    """);
        }
    }

    @Test
    void findAllReturnsUsersAndTheirCourses() throws Exception {
        execute("INSERT INTO users (first_name, last_name, email, password, role) " +
                "VALUES ('Ada', 'Lovelace', 'ada@example.com', 'secret', 'student')");
        execute("INSERT INTO courses (name) VALUES ('Mathematics')");
        execute("INSERT INTO course_students (course_id, student_id) VALUES (1, 1)");

        List<Admin> users = dao.findAll();

        assertEquals(1, users.size());
        assertEquals("Ada Lovelace", users.get(0).getName());
        assertEquals("Mathematics", users.get(0).getCourses());
        assertEquals(Role.STUDENT, users.get(0).getRole());
    }

    @Test
    void insertStoresUserPasswordAndStudentCourse() throws Exception {
        execute("INSERT INTO courses (name) VALUES ('Physics')");

        Admin user = dao.insert(
                "Grace Hopper", "grace@example.com", Role.STUDENT, "Physics", "temporary"
        );

        assertEquals("Grace Hopper", user.getName());
        assertEquals(1, count("SELECT COUNT(*) FROM users WHERE email = 'grace@example.com' " +
                "AND password = 'temporary'"));
        assertEquals(1, count("SELECT COUNT(*) FROM course_students " +
                "WHERE course_id = 1 AND student_id = " + user.getId()));
    }

    @Test
    void updateChangesUserFieldsPasswordAndCourses() throws Exception {
        execute("INSERT INTO users (first_name, last_name, email, password, role) " +
                "VALUES ('Alan', 'Turing', 'alan@example.com', 'old', 'student')");
        execute("INSERT INTO courses (name) VALUES ('Programming')");
        Admin user = dao.findAll().get(0);

        dao.update(user, "Alan Mathison Turing", "alan-new@example.com",
                Role.STUDENT, "Programming", "new");

        assertEquals(1, count("SELECT COUNT(*) FROM users WHERE email = 'alan-new@example.com' " +
                "AND password = 'new' AND first_name = 'Alan' AND last_name = 'Mathison Turing'"));
        assertEquals(1, count("SELECT COUNT(*) FROM course_students " +
                "WHERE course_id = 1 AND student_id = " + user.getId()));
    }

    @Test
    void deleteRemovesUser() throws Exception {
        execute("INSERT INTO users (first_name, last_name, email, password, role) " +
                "VALUES ('Katherine', 'Johnson', 'katherine@example.com', 'secret', 'admin')");
        Admin user = dao.findAll().get(0);

        dao.delete(user);

        assertEquals(0, count("SELECT COUNT(*) FROM users"));
    }

    private static void execute(String sql) throws Exception {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    private static int count(String sql) throws Exception {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            assertTrue(resultSet.next());
            return resultSet.getInt(1);
        }
    }
}
