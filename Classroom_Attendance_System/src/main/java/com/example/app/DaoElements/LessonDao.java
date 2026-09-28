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
                        "(course_id, start_time, end_time) " +
                        "VALUES (?, ?, ?)";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt =
                        conn.prepareStatement(
                                sql,
                                PreparedStatement.RETURN_GENERATED_KEYS
                        )
        ) {

            java.time.LocalDateTime startTime =
                    LocalDateTime.now().withNano(0);

            // Väliaikainen end_time.
            // Todellinen lopetusaika asetetaan endLesson()-metodissa.
            java.time.LocalDateTime endTime =
                    startTime.plusMinutes(1);

            stmt.setInt(1, courseId);
            stmt.setObject(2, Timestamp.valueOf(startTime));
            stmt.setObject(3, Timestamp.valueOf(endTime));

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
}










