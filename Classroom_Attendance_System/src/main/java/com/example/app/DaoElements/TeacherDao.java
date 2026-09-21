package com.example.app.DaoElements;

import com.example.app.Database.DatabaseConnection;
import com.example.app.Model.Role;
import com.example.app.Model.Teacher;
import com.example.app.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class TeacherDao {
    public Optional <Teacher> getTeacherbyId(int userId) {
        String sql = "SELECT user_id, email, first_name, last_name " +
                "FROM users WHERE user_id = ? AND `role` = 'teacher'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

          stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Teacher teacher = new Teacher(
                            rs.getInt("user_id"),
                            rs.getString("email"),
                            rs.getString("first_name"),
                            rs.getString("last_name")

                    );
                    return Optional.of(teacher);
                }
            }
        }
        catch (SQLException e) {
            System.err.println("Opettajan Tiedonhaussa: " + e.getMessage());
        }
        return Optional.empty();
    }
    }


