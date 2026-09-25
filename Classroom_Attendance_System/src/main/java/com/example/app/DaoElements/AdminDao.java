package com.example.app.DaoElements;

import com.example.app.Database.DatabaseConnection;
import com.example.app.Model.Admin;
import com.example.app.Model.LoginComponents.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminDao {
    private static final String DEFAULT_PASSWORD_HASH =
            "$2a$10$GKAKW1uUj7MTPQAIGeeqJelwEoG1bf77W.oA36IYLw1ZWeu/gGV.S";

    private static final String FIND_ALL_USERS = """
            SELECT u.user_id, u.first_name, u.last_name, u.email, u.role,
                   GROUP_CONCAT(DISTINCT c.name ORDER BY c.name SEPARATOR ', ') AS courses
            FROM users u
            LEFT JOIN courses c
              ON c.teacher_id = u.user_id
              OR EXISTS (
                  SELECT 1
                  FROM course_students cs
                  WHERE cs.course_id = c.course_id
                    AND cs.student_id = u.user_id
              )
            GROUP BY u.user_id, u.first_name, u.last_name, u.email, u.role
            ORDER BY u.user_id
            """;

    public List<Admin> findAll() {
        List<Admin> users = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_USERS);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Role role = Role.fromString(resultSet.getString("role"));
                if (role == null) {
                    throw new IllegalStateException(
                            "Unknown role for user " + resultSet.getInt("user_id"));
                }

                String courses = resultSet.getString("courses");
                users.add(new Admin(
                        resultSet.getInt("user_id"),
                        resultSet.getString("first_name") + " " + resultSet.getString("last_name"),
                        resultSet.getString("email"),
                        role,
                        courses == null ? "—" : courses
                ));
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Käyttäjien haku tietokannasta epäonnistui", exception);
        }

        return users;
    }

    public Admin insert(String name, String email, Role role, String courses) {
        String[] nameParts = splitName(name);
        String insertUser = """
                INSERT INTO users (first_name, last_name, email, password, role)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(
                    insertUser, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, nameParts[0]);
                statement.setString(2, nameParts[1]);
                statement.setString(3, email);
                statement.setString(4, DEFAULT_PASSWORD_HASH);
                statement.setString(5, role.name().toLowerCase());
                statement.executeUpdate();

                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Uuden käyttäjän ID:tä ei saatu tietokannasta");
                    }
                    int userId = keys.getInt(1);
                    syncCourses(connection, userId, role, courses);
                    connection.commit();
                    return new Admin(userId, name, email, role, courses);
                }
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Käyttäjän tallennus tietokantaan epäonnistui", exception);
        }
    }

    public void update(Admin user, String name, String email, Role role, String courses) {
        String[] nameParts = splitName(name);
        String updateUser = """
                UPDATE users
                SET first_name = ?, last_name = ?, email = ?, role = ?
                WHERE user_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(updateUser)) {
                statement.setString(1, nameParts[0]);
                statement.setString(2, nameParts[1]);
                statement.setString(3, email);
                statement.setString(4, role.name().toLowerCase());
                statement.setInt(5, user.getId());
                if (statement.executeUpdate() != 1) {
                    throw new SQLException("Käyttäjää ei löytynyt tietokannasta");
                }
                syncCourses(connection, user.getId(), role, courses);
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Käyttäjän päivitys tietokantaan epäonnistui", exception);
        }
    }

    public void delete(Admin user) {
        String deleteUser = "DELETE FROM users WHERE user_id = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(deleteUser)) {
                statement.setInt(1, user.getId());
                if (statement.executeUpdate() != 1) {
                    throw new SQLException("Käyttäjää ei löytynyt tietokannasta");
                }
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Käyttäjän poisto tietokannasta epäonnistui", exception);
        }
    }

    private void syncCourses(Connection connection, int userId, Role role, String courses)
            throws SQLException {
        if (role == Role.STUDENT) {
            try (PreparedStatement delete = connection.prepareStatement(
                    "DELETE FROM course_students WHERE student_id = ?")) {
                delete.setInt(1, userId);
                delete.executeUpdate();
            }
        }

        if (courses == null || courses.isBlank() || courses.equals("—")) {
            return;
        }

        String lookup = "SELECT course_id FROM courses WHERE name = ?";
        String relation = role == Role.STUDENT
                ? "INSERT IGNORE INTO course_students (course_id, student_id) VALUES (?, ?)"
                : "UPDATE courses SET teacher_id = ? WHERE course_id = ?";

        for (String course : courses.split(",\\s*")) {
            try (PreparedStatement find = connection.prepareStatement(lookup)) {
                find.setString(1, course.trim());
                try (ResultSet result = find.executeQuery()) {
                    if (!result.next()) {
                        continue;
                    }
                    int courseId = result.getInt("course_id");
                    try (PreparedStatement statement = connection.prepareStatement(relation)) {
                        if (role == Role.STUDENT) {
                            statement.setInt(1, courseId);
                            statement.setInt(2, userId);
                        } else {
                            statement.setInt(1, userId);
                            statement.setInt(2, courseId);
                        }
                        statement.executeUpdate();
                    }
                }
            }
        }
    }

    private static String[] splitName(String name) {
        String trimmed = name.trim();
        int separator = trimmed.indexOf(' ');
        return separator < 0
                ? new String[]{trimmed, ""}
                : new String[]{trimmed.substring(0, separator),
                trimmed.substring(separator + 1).trim()};
    }
}
