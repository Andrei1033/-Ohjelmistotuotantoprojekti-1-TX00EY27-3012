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
                    // KORJATTU: parametrijärjestys vastaa nyt User-konstruktoria
                    // (userId, firstName, lastName, email, role). Aiemmin email ja
                    // first_name/last_name olivat väärässä järjestyksessä, jolloin
                    // esim. currentUser.getFirstName() palautti sähköpostin.
                    User user = new User(
                            rs.getInt("user_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email"),
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

    //@return true jos annettu sähköposti on jo käytössä jollakin MUULLA käyttäjällä.
    public boolean isEmailTaken(String email, int excludingUserId) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND user_id <> ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setInt(2, excludingUserId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe sähköpostin tarkistuksessa: " + e.getMessage());
        }
        return false;
    }

    //Päivittää käyttäjän nimen ja sähköpostin. Ei koske salasanaan.
    public boolean updateProfile(int userId, String firstName, String lastName, String email) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setInt(4, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe tietojen päivityksessä: " + e.getMessage());
            return false;
        }
    }


     //Tarkistaa täsmääkö annettu salasana käyttäjän nykyiseen salasanaan.
     //HUOM: salasana verrataan tällä hetkellä plain-tekstinä, samalla tavalla
     //kuin findByEmailAndPassword tekee. Jos otatte hashauksen (esim. BCrypt)
     //käyttöön, tämä metodi täytyy päivittää vastaavasti.

    public boolean verifyPassword(int userId, String password) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ? AND `password` = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe salasanan tarkistuksessa: " + e.getMessage());
        }
        return false;
    }

    // Päivittää käyttäjän salasanan.
    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET `password` = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Tietokantavirhe salasanan päivityksessä: " + e.getMessage());
            return false;
        }
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