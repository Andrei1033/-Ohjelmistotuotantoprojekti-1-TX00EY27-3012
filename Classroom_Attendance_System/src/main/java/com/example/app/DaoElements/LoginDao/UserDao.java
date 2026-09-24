package com.example.app.DaoElements.LoginDao;

import com.example.app.Database.DatabaseConnection;
import com.example.app.Model.LoginComponents.Role;
import com.example.app.Model.LoginComponents.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDao {
    public Optional<User> findByEmailAndPassword(String email, String password) {
        String sql = "SELECT user_id, email, first_name, last_name, role " +
                "FROM users WHERE email = ? AND `password` = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Role role = Role.fromString(rs.getString("role"));
                    if (role == null) {
                        return Optional.empty();
                    }
                    User user = new User(
                            rs.getInt("user_id"),
                            rs.getString("email"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            role
                    );
                    return Optional.of(user);
                }
            }
        }
        catch (SQLException e) {
            System.err.println("Tietokantavirhe kirjautumisessa: " + e.getMessage());
        }
        return Optional.empty();
    }

    /* test main method for testing the UserDao class
    public static void main(String[] args) {
        UserDao dao = new UserDao();
        dao.findByEmailAndPassword("admin1@school.edu", "$2b$10$abcdefghijklmnopqrstuv")
                .ifPresentOrElse(
                        u -> System.out.println("Löytyi: " + u),
                        () -> System.out.println("Ei löytynyt")
                );
    }
    */

}
