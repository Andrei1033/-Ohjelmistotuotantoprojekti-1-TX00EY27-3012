package com.example.app.DaoElements.StudentDao;

import com.example.app.Database.DatabaseConnection;
import com.example.app.Model.LoginComponents.Role;
import com.example.app.Model.LoginComponents.User;
import com.example.app.Model.StudentComponents.AttendanceRecord;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDao {

    /**
     * Opiskelija:
     * Hakee kaikki kurssin oppitunnit ja opiskelijan läsnäolot.
     */
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
            e.printStackTrace();
        }

        return records;
    }

    /**
     * Opettaja:
     * Hakee kaikki tietyn kurssin opiskelijat.
     */
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
            System.err.println(
                    "Virhe haettaessa kurssin opiskelijoita: "
                            + e.getMessage()
            );
        }

        return students;
    }


    /**
     * Opettaja:
     * Tallentaa opiskelijan läsnäolon.
     *
     * Jos opiskelijalle on jo olemassa merkintä kyseiselle oppitunnille,
     * päivitetään vanha merkintä.
     *
     * Jos merkintää ei ole, luodaan uusi.
     */
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

            // Tarkistetaan löytyykö merkintä jo.
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

            System.err.println(
                    "Virhe tallennettaessa läsnäoloa: "
                            + e.getMessage()
            );

            return false;
        }
    }


    /**
     * Lopettaa oppitunnin.
     * Asettaa lessons.end_time-arvon nykyiseen aikaan.
     */
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

            System.err.println(
                    "Virhe lopetettaessa oppituntia: "
                            + e.getMessage()
            );

            return false;
        }
    }
}

