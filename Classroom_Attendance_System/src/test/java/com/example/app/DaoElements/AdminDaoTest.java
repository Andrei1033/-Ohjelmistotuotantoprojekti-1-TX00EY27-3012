package com.example.app.DaoElements;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.app.Database.DatabaseConnection;
import com.example.app.Model.Admin;
import com.example.app.Model.LoginComponents.Role;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        execute("INSERT INTO users (first_name, last_name, email, password, role) "
                + "VALUES ('Ada', 'Lovelace', 'ada@example.com', 'secret', 'student')");
        execute("INSERT INTO courses (name) VALUES ('Mathematics')");
        execute("INSERT INTO course_students (course_id, student_id) VALUES (1, 1)");

        List<Admin> users = dao.findAll();

        assertEquals(1, users.size());
        assertEquals("Ada Lovelace", users.get(0).getName());
        assertEquals("Mathematics", users.get(0).getCourses());
        assertEquals(Role.STUDENT, users.get(0).getRole());
    }

    @Test
    void findAllUsesDashForUsersWithoutCoursesAndKeepsUserIdOrder() throws Exception {
        execute("INSERT INTO users (first_name, last_name, email, password, role) VALUES "
                + "('Zoe', 'Zimmerman', 'zoe@example.com', 'secret', 'admin'), "
                + "('Bob', 'Builder', 'bob@example.com', 'secret', 'teacher')");

        List<Admin> users = dao.findAll();

        assertEquals(2, users.size());
        assertEquals("Zoe Zimmerman", users.get(0).getName());
        assertEquals("—", users.get(0).getCourses());
        assertEquals(Role.ADMIN, users.get(0).getRole());
        assertEquals("Bob Builder", users.get(1).getName());
        assertEquals(Role.TEACHER, users.get(1).getRole());
    }

    @Test
    void findAllReturnsAllDistinctCoursesForAUser() throws Exception {
        execute("INSERT INTO users (first_name, last_name, email, password, role) "
                + "VALUES ('Ada', 'Lovelace', 'ada@example.com', 'secret', 'student')");
        execute("INSERT INTO courses (name) VALUES ('Zoology'), ('Algebra'), ('Zoology')");
        execute("INSERT INTO course_students (course_id, student_id) VALUES (1, 1), (2, 1), (3, 1)");

        Admin user = dao.findAll().get(0);

        assertEquals("Algebra, Zoology", user.getCourses());
    }

    @Test
    void findAllRejectsUnknownRole() throws Exception {
        execute("INSERT INTO users (first_name, last_name, email, password, role) "
                + "VALUES ('Unknown', 'Role', 'unknown@example.com', 'secret', 'guest')");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> dao.findAll());

        assertTrue(exception.getMessage().contains("Unknown role"));
    }

    @Test
    void findAllFailsExplicitlyWhenUsersTableIsUnavailable() throws Exception {
        execute("DROP TABLE users");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> dao.findAll());

        assertTrue(exception.getMessage().contains("Käyttäjien haku"));
    }

    @Test
    void insertStoresUserPasswordAndStudentCourse() throws Exception {
        execute("INSERT INTO courses (name) VALUES ('Physics')");

        Admin user = dao.insert("Grace Hopper", "grace@example.com", Role.STUDENT, "Physics", "temporary");

        assertEquals("Grace Hopper", user.getName());
        assertEquals(1,
                count("SELECT COUNT(*) FROM users WHERE email = 'grace@example.com' " + "AND password = 'temporary'"));
        assertEquals(1, count(
                "SELECT COUNT(*) FROM course_students " + "WHERE course_id = 1 AND student_id = " + user.getId()));
    }

    @Test
    void insertAssignsTeacherToNamedCourse() throws Exception {
        execute("INSERT INTO courses (name) VALUES ('Physics')");

        Admin user = dao.insert("Isaac Newton", "isaac@example.com", Role.TEACHER, "Physics", "temporary");

        assertEquals(Role.TEACHER, user.getRole());
        assertEquals(1, count("SELECT COUNT(*) FROM courses WHERE name = 'Physics' AND teacher_id = "
                + user.getId()));
        assertEquals(0, count("SELECT COUNT(*) FROM course_students WHERE student_id = " + user.getId()));
    }

    @Test
    void insertSupportsSingleWordName() throws Exception {
        Admin user = dao.insert("Plato", "plato@example.com", Role.ADMIN, "—", "temporary");

        assertEquals("Plato", user.getName());
        assertEquals(1, count("SELECT COUNT(*) FROM users WHERE first_name = 'Plato' AND last_name = ''"));
    }

    @Test
    void insertFailsAndRollsBackWhenUsersTableIsUnavailable() throws Exception {
        execute("DROP TABLE users");

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> dao.insert("Failed User", "failed@example.com", Role.ADMIN, "—", "temporary"));

        assertTrue(exception.getMessage().contains("Käyttäjän tallennus"));
    }

    @Test
    void updateChangesUserFieldsPasswordAndCourses() throws Exception {
        execute("INSERT INTO users (first_name, last_name, email, password, role) "
                + "VALUES ('Alan', 'Turing', 'alan@example.com', 'old', 'student')");
        execute("INSERT INTO courses (name) VALUES ('Programming')");
        Admin user = dao.findAll().get(0);

        dao.update(user, "Alan Mathison Turing", "alan-new@example.com", Role.STUDENT, "Programming", "new");

        assertEquals(1, count("SELECT COUNT(*) FROM users WHERE email = 'alan-new@example.com' "
                + "AND password = 'new' AND first_name = 'Alan' AND last_name = 'Mathison Turing'"));
        assertEquals(1, count(
                "SELECT COUNT(*) FROM course_students " + "WHERE course_id = 1 AND student_id = " + user.getId()));
    }

    @Test
    void updateWithoutPasswordKeepsExistingPassword() throws Exception {
        execute("INSERT INTO users (first_name, last_name, email, password, role) "
                + "VALUES ('Alan', 'Turing', 'alan@example.com', 'old', 'admin')");
        Admin user = dao.findAll().get(0);

        dao.update(user, "Alan Mathison Turing", "alan-new@example.com", Role.ADMIN, "—");

        assertEquals(1, count("SELECT COUNT(*) FROM users WHERE email = 'alan-new@example.com' "
                + "AND password = 'old' AND first_name = 'Alan' AND last_name = 'Mathison Turing'"));
    }

    @Test
    void updateWithBlankPasswordKeepsExistingPassword() throws Exception {
        execute("INSERT INTO users (first_name, last_name, email, password, role) "
                + "VALUES ('Alan', 'Turing', 'alan@example.com', 'old', 'admin')");
        Admin user = dao.findAll().get(0);

        dao.update(user, "Alan Turing", "alan@example.com", Role.ADMIN, "—", " ");

        assertEquals(1, count("SELECT COUNT(*) FROM users WHERE user_id = " + user.getId()
                + " AND password = 'old'"));
    }

    @Test
    void updateReplacesStudentCourseLinks() throws Exception {
        execute("INSERT INTO users (first_name, last_name, email, password, role) "
                + "VALUES ('Ada', 'Lovelace', 'ada@example.com', 'old', 'student')");
        execute("INSERT INTO courses (name) VALUES ('Mathematics'), ('Physics')");
        execute("INSERT INTO course_students (course_id, student_id) VALUES (1, 1)");
        Admin user = dao.findAll().get(0);

        dao.update(user, "Ada Lovelace", "ada@example.com", Role.STUDENT, "Physics");

        assertEquals(0, count("SELECT COUNT(*) FROM course_students WHERE course_id = 1 AND student_id = 1"));
        assertEquals(1, count("SELECT COUNT(*) FROM course_students WHERE course_id = 2 AND student_id = 1"));
    }

    @Test
    void updateMissingUserFailsExplicitly() throws Exception {
        Admin missingUser = new Admin(999, "Missing User", "missing@example.com", Role.ADMIN, "—");

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> dao.update(missingUser, "Updated User", "updated@example.com", Role.ADMIN, "—"));

        assertTrue(exception.getMessage().contains("Käyttäjän päivitys"));
    }

    @Test
    void updateFailsExplicitlyWhenUsersTableIsUnavailable() throws Exception {
        Admin user = new Admin(1, "Existing User", "existing@example.com", Role.ADMIN, "—");
        execute("DROP TABLE users");

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> dao.update(user, "Updated User", "updated@example.com", Role.ADMIN, "—"));

        assertTrue(exception.getMessage().contains("Käyttäjän päivitys"));
    }

    @Test
    void deleteRemovesUser() throws Exception {
        execute("INSERT INTO users (first_name, last_name, email, password, role) "
                + "VALUES ('Katherine', 'Johnson', 'katherine@example.com', 'secret', 'admin')");
        Admin user = dao.findAll().get(0);

        dao.delete(user);

        assertEquals(0, count("SELECT COUNT(*) FROM users"));
    }

    @Test
    void deleteMissingUserFailsExplicitly() throws Exception {
        Admin missingUser = new Admin(999, "Missing User", "missing@example.com", Role.ADMIN, "—");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> dao.delete(missingUser));

        assertTrue(exception.getMessage().contains("Käyttäjän poisto"));
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
