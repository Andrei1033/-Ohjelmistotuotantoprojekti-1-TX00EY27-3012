package com.example.app.DaoElements.StudentDao;

import com.example.app.Database.DatabaseConnection;
import com.example.app.Model.LoginComponents.Role;
import com.example.app.Model.LoginComponents.User;
import com.example.app.Model.StudentComponents.Course;
import com.example.app.Model.TeacherCourse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CourseDao {

    /**
     * Retrieves all courses to which the given student is linked
     * via the course_students join table, as well as the number of
     * lessons for each course.
     */
    public List<Course> getCoursesForStudent(int studentId) {
        List<Course> courses = new ArrayList<>();

        String sql =
                "SELECT c.course_id, c.code, c.name, c.teacher_id, " +
                        "COUNT(l.lesson_id) AS lesson_count " +
                        "FROM courses c " +
                        "JOIN course_students cs ON cs.course_id = c.course_id " +
                        "LEFT JOIN lessons l ON l.course_id = c.course_id " +
                        "WHERE cs.student_id = ? " +
                        "GROUP BY c.course_id, c.code, c.name, c.teacher_id " +
                        "ORDER BY c.name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Course course = new Course(
                            rs.getInt("course_id"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getInt("teacher_id")
                    );
                    course.setLessonCount(rs.getInt("lesson_count"));
                    courses.add(course);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;
    }

    public boolean addCourse(String courseName, String courseCode, int teacherId) {
        if (courseName == null || courseName.trim().isEmpty() || teacherId <= 0) {
            return false;
        }

        String sql = "INSERT INTO courses (name, code, teacher_id) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courseName.trim());
            ps.setString(2, courseCode == null ? "" : courseCode.trim());
            ps.setInt(3, teacherId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCourse(int courseId, String newName, String newCode) {
        if (courseId <= 0
                || newName == null
                || newName.trim().isEmpty()) {
            return false;
        }

        String sql = "UPDATE courses SET name = ?, code = ? WHERE course_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newName.trim());
            ps.setString(2, newCode == null ? "" : newCode.trim());
            ps.setInt(3, courseId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Virhe kurssin päivittämisessä: " + e.getMessage());
            return false;
        }
    }

    public List<TeacherCourse> getCoursesByTeacherId(int teacherId) {
        if (teacherId <= 0) {
            return Collections.emptyList();
        }

        List<TeacherCourse> courses = new ArrayList<>();
        String sql = "SELECT course_id, name, code, teacher_id FROM courses WHERE teacher_id = ? ORDER BY course_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, teacherId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    courses.add(new TeacherCourse(
                            rs.getInt("course_id"),
                            rs.getString("name"),
                            rs.getString("code"),
                            rs.getInt("teacher_id")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;
    }

    public List<User> getAllStudents() {
        List<User> students = new ArrayList<>();
        String sql = "SELECT user_id, first_name, last_name, email FROM users WHERE role = 'STUDENT' ORDER BY last_name, first_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User student = new User(
                        rs.getInt("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        Role.STUDENT
                );
                students.add(student);
            }

        } catch (SQLException e) {
            System.err.println("Virhe opiskelijoiden hakemisessa: " + e.getMessage());
        }

        return students;
    }


    public boolean addStudents(int studentId, int courseId) {
        String sql = "INSERT INTO course_students (course_id, student_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ps.setInt(2, studentId);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Virhe opiskelijan lisäämisessä kurssille: " + e.getMessage());
            return false;
        }
    }

    public List<User>getStudentsForCourse(int courseId) {
        List<User> students = new ArrayList<>();
        String sql = "SELECT u.user_id, u.first_name, u.last_name, u.email, u.role " +
                "FROM users u " +
                "JOIN course_students cs ON u.user_id = cs.student_id " +
                "WHERE cs.course_id = ? AND u.role = 'STUDENT'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String email = rs.getString("email");
                    String roleStr = rs.getString("role");
                    Role role = Role.fromString(roleStr);

                    students.add(new User(userId, firstName, lastName, email, role));
                }
            }

        } catch (SQLException e) {
            System.err.println("Virhe haettaessa kurssin opiskelijoita: " + e.getMessage());
            e.printStackTrace();
        }

        return students;
    }


}





