package com.example.app.DaoElements;

import com.example.app.Database.DatabaseConnection;
import com.example.app.Model.Admin;
import com.example.app.Model.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminDao {
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
}
