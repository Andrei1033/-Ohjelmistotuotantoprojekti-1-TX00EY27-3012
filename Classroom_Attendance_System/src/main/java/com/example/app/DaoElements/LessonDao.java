package com.example.app.DaoElements;

import com.example.app.Database.DatabaseConnection;
import com.example.app.Model.Lesson;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LessonDao {

    private static final Logger logger = Logger.getLogger(LessonDao.class.getName());

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
                            "Lesson",
                            "upcoming"
                    );
                    lessons.add(lesson);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving lessons for course", e);
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

            LocalDateTime startTime =
                    LocalDateTime.now().withNano(0);

            // Temporary end_time.
            // The actual end time is set in the endLesson() method.
            LocalDateTime endTime =
                    startTime.plusMinutes(1);

            stmt.setInt(1, courseId);
            stmt.setObject(2, Timestamp.valueOf(startTime));
            stmt.setObject(3, Timestamp.valueOf(endTime));

            int affected = stmt.executeUpdate();

            if (affected == 0) {
                throw new SQLException(
                        "Failed to create lesson."
                );
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {

                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

        } catch (SQLException e) {

            logger.log(Level.SEVERE, "Error starting lesson", e);
        }
        return -1;
    }
}