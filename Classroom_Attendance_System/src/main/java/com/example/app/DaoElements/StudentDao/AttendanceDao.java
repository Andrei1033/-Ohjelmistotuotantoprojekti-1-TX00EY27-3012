package com.example.app.DaoElements.StudentDao;

import com.example.app.Database.DatabaseConnection;
import com.example.app.Model.LoginComponents.Role;
import com.example.app.Model.LoginComponents.User;
import com.example.app.Model.StudentComponents.AttendanceRecord;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AttendanceDao {

    private static final Logger logger = Logger.getLogger(AttendanceDao.class.getName());

    // Student:
    // Retrieves all lessons of a course and the student's attendance records.
    public List<AttendanceRecord> getAttendanceForStudentAndCourse(
            int studentId,
            int courseId
    ) {
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
            logger.log(Level.SEVERE, "Error retrieving attendance records for student and course", e);
        }

        return records;
    }

    // Teacher:
    // Retrieves all students of a given course.
    public List<User> getStudentsForCourse(int courseId) {

        List<User> students = new ArrayList<>();

        String sql =
                "SELECT u.user_id, u.first_name, u.last_name, u.email " +
                        "FROM users u " +
                        "JOIN course_students cs ON u.user_id = cs.student_id " +
                        "WHERE cs.course_id = ? " +
                        "ORDER BY u.last_name, u.first_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    students.add(new User(
                            rs.getInt("user_id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email"),
                            Role.STUDENT
                    ));
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving students for course", e);
        }

        return students;
    }


    // Teacher:
    // Saves a student's attendance.
    //
    // If a record already exists for the student for the given lesson,
    // the existing record is updated.
    //
    // If no record exists, a new one is created.
    public boolean saveAttendance(
            int lessonId,
            int studentId,
            String status
    ) {

        String checkSql =
                "SELECT attendance_id " +
                        "FROM attendance " +
                        "WHERE lesson_id = ? AND student_id = ?";

        String updateSql =
                "UPDATE attendance " +
                        "SET status = ? " +
                        "WHERE attendance_id = ?";

        String insertSql =
                "INSERT INTO attendance (lesson_id, student_id, status) " +
                        "VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {

            // Check whether a record already exists.
            try (PreparedStatement check = conn.prepareStatement(checkSql)) {

                check.setInt(1, lessonId);
                check.setInt(2, studentId);

                try (ResultSet rs = check.executeQuery()) {

                    if (rs.next()) {

                        int attendanceId =
                                rs.getInt("attendance_id");

                        try (PreparedStatement update =
                                     conn.prepareStatement(updateSql)) {

                            update.setString(1, status);
                            update.setInt(2, attendanceId);

                            update.executeUpdate();
                        }

                    } else {

                        try (PreparedStatement insert =
                                     conn.prepareStatement(insertSql)) {

                            insert.setInt(1, lessonId);
                            insert.setInt(2, studentId);
                            insert.setString(3, status);

                            insert.executeUpdate();
                        }
                    }
                }
            }

            return true;

        } catch (SQLException e) {

            logger.log(Level.SEVERE, "Error saving attendance", e);

            return false;
        }
    }


    // Ends a lesson.
    // Sets lessons.end_time to the current time.
    public boolean endLesson(int lessonId) {

        String sql =
                "UPDATE lessons " +
                        "SET end_time = ? " +
                        "WHERE lesson_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(
                    1,
                    Timestamp.valueOf(java.time.LocalDateTime.now())
            );

            ps.setInt(2, lessonId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {

            logger.log(Level.SEVERE, "Error ending lesson", e);

            return false;
        }
    }
}