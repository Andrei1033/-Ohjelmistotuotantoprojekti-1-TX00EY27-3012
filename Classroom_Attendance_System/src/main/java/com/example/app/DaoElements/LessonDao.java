package com.example.app.DaoElements;

import com.example.app.Database.DatabaseConnection;
import com.example.app.Model.Lesson;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class LessonDao {
    public List<Lesson> getLessonsByCourseById(int courseId) {
        List<Lesson> lessons = new ArrayList<>();
        String sql = "SELECT lesson_id, course_id, start_time " +
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
}






