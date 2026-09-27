package com.example.app.View;
import com.example.app.Model.TeacherTest;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class TeacherAttendanceTrackingTest extends ApplicationTest {

    private TeacherAttendanceTracking teacherAttendanceTracking;

    private TeacherTest teacher;
    private TeacherCourse course;

    @Override
    public void start(Stage stage) {

        teacher = new TeacherTest(
                1,
                "Testi",
                "Opettaja",
                "test@example.com"
        );

        course = new TeacherCourse(
                1,
                "Oppitunti",
                1
        );

        teacherAttendanceTracking =
                new TeacherAttendanceTracking(
                        teacher,
                        course,
                        1,
                        () -> {
                        }
                );
    }

    @Test
    void teacherAttendanceTrackingShouldInitialize() {

        assertNotNull(
                teacherAttendanceTracking
        );
    }

    @Test
    void teacherShouldBeCorrect() {

        assertEquals(
                1,
                teacher.getId()
        );

        assertEquals(
                "Testi Opettaja",
                teacher.getFullName()
        );
    }

    @Test
    void courseShouldBeCorrect() {

        assertEquals(
                1,
                course.getCourseid()
        );

        assertEquals(
                "Oppitunti",
                course.getCoursename()
        );

        assertEquals(
                1,
                course.getTeacherid()
        );
    }
}



