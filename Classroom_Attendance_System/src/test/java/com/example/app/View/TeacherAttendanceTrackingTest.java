package com.example.app.View;

import com.example.app.Model.Teacher;
import com.example.app.Model.TeacherCourse;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class TeacherAttendanceTrackingTest extends ApplicationTest {

    private TeacherAttendanceTracking teacherAttendanceTracking;
    private Teacher teacher;
    private TeacherCourse course;

    @Override
    public void start(Stage stage) {

        teacher = new Teacher(1, "Testi", "Opettaja", "test@example.com");

        // KORJATTU: 4 parametria (courseId, name, code, teacherId)
        course = new TeacherCourse(1, "Oppitunti", "ABC123", 1);

        teacherAttendanceTracking = new TeacherAttendanceTracking(
                teacher,
                course,
                1,
                () -> {}
        );
    }

    @Test
    void teacherAttendanceTrackingShouldInitialize() {
        assertNotNull(teacherAttendanceTracking);
    }

    @Test
    void teacherShouldBeCorrect() {
        assertNotNull(teacher);
        assertEquals(1, teacher.getId());
        assertEquals("Testi Opettaja", teacher.getFullName());
    }

    @Test
    void courseShouldBeCorrect() {
        assertNotNull(course, "course-kenttä on null – konstruktori epäonnistui start():ssa");
        assertEquals(1, course.getCourseid());
        assertEquals("Oppitunti", course.getCoursename());
        assertEquals(1, course.getTeacherid());
    }
}