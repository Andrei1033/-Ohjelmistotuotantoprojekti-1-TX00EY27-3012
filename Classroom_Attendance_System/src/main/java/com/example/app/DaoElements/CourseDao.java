package com.example.app.DaoElements;

import com.example.app.Database.DatabaseConnection;
import com.example.app.Model.Course;
import com.example.app.Model.Lesson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDao {
    public List<Course> getCoursesByTeacherId(int teacherId) {
        List<Course> courses = new ArrayList<>();

        String sql = "SELECT course_id, name, teacher_id FROM courses WHERE teacher_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teacherId);

            try (ResultSet rs = stmt.executeQuery()) {
               while (rs.next()) {
                    Course course = new Course(
                            rs.getInt("course_id"),
                            rs.getString("name"),
                            teacherId
                    );
                    courses.add(course);
                }
            }
        } catch (SQLException e) {
            System.err.println("Virhe haettaessa kurssia: " + e.getMessage());
        }
        return courses;
    }

    public boolean addCourse(String courseName, int teacherId) {
        String sql = "INSERT INTO courses (name, teacher_id) VALUES(?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {


            stmt.setString(1, courseName);
            stmt.setInt(2, teacherId);

            int rowInsert = stmt.executeUpdate();
            return rowInsert > 0;

        } catch (SQLException e) {
            System.err.println("Virhe lisättäessä kurssia: " + e.getMessage());
            return false;
        }

    }

    public boolean addCourse(Course course) {
        return addCourse(course.getCoursename(), course.getTeacherid());
    }
}
