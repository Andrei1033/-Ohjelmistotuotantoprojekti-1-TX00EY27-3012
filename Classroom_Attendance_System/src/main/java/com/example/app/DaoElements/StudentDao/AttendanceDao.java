package com.example.app.DaoElements.StudentDao;

import com.example.app.Database.DatabaseConnection;
import com.example.app.Model.StudentComponents.AttendanceRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDao {
    /**
     * Retrieves all lessons for a given course and the student's attendance record
     * for each of them. A LEFT JOIN ensures that the lesson appears in the list
     * even if no record has been made yet—in which case the status defaults
     * to "absent".
     */
    public List<AttendanceRecord> getAttendanceForStudentAndCourse(int studentId, int courseId) {
        List<AttendanceRecord> records = new ArrayList<>();

        String sql =
                "SELECT l.lesson_id, l.start_time, l.end_time, l.topic, a.status " +
                        "FROM lessons l " +
                        "LEFT JOIN attendance a " +
                        "ON a.lesson_id = l.lesson_id AND a.student_id = ? " +
                        "WHERE l.course_id = ? " +
                        "ORDER BY l.start_time";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, courseId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp start = rs.getTimestamp("start_time");
                    Timestamp end = rs.getTimestamp("end_time");
                    String status = rs.getString("status");

                    records.add(new AttendanceRecord(
                            rs.getInt("lesson_id"),
                            start != null ? start.toLocalDateTime() : null,
                            end != null ? end.toLocalDateTime() : null,
                            rs.getString("topic"),
                            status != null ? status : "absent"
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }
}
