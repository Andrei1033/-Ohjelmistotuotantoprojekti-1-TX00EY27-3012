package com.example.app.DaoElements;

import com.example.app.Database.DatabaseConnection;
import com.example.app.Model.Lesson;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class LessonDao {
    public List<Lesson> getLessonsByCourseById(int courseId) {
        List<Lesson> lessons = new ArrayList<>();
        String sql = "SELECT lesson_id, course_id, start_time, end_time " +
                "FROM lessons WHERE course_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Lesson lesson = new Lesson(
                            rs.getInt("lesson_id"),
                            rs.getInt("course_id"),
                            rs.getString("start_time"),
                            rs.getString("end_time"),
                            "Oppitunti",
                            "upcoming"
                    );
                    lessons.add(lesson);
                }
            }
        } catch (SQLException e) {
            System.err.println("Virhe haettaessa kurssia: " + e.getMessage());
        }
        return lessons;
    }


    public int startLesson(int courseId) {

        String sql =
                "INSERT INTO lessons " +
                        "(course_id, start_time, end_time, topic) " +
                        "VALUES (?, ?, ?, ?)";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt =
                        conn.prepareStatement(
                                sql,
                                PreparedStatement.RETURN_GENERATED_KEYS
                        )
        ) {

            java.time.LocalDateTime startTime =
                    java.time.LocalDateTime.now();

            // Väliaikainen end_time.
            // Todellinen lopetusaika asetetaan endLesson()-metodissa.
            java.time.LocalDateTime endTime =
                    startTime.plusMinutes(1);

            stmt.setInt(1, courseId);
            stmt.setObject(2, startTime);
            stmt.setObject(3, endTime);
            stmt.setString(4, "");

            int affected = stmt.executeUpdate();

            if (affected == 0) {
                throw new SQLException(
                        "Oppitunnin luonti epäonnistui."
                );
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {

                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Virhe aloitettaessa oppituntia:"
            );

            e.printStackTrace();
        }
        return -1;
    }
    public static boolean addLesson(LocalDateTime startTime, LocalDateTime endTime, int courseId) {
        String sql = "INSERT INTO lesson (start_time, end_time, course_id, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(startTime));
            pstmt.setTimestamp(2, Timestamp.valueOf(endTime));
            pstmt.setInt(3, courseId);
            pstmt.setString(4, "UPCOMING"); // Oletustila uudelle oppitunnille

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Virhe oppitunnin lisäyksessä: " + e.getMessage());
            return false;
        }
    }


}






