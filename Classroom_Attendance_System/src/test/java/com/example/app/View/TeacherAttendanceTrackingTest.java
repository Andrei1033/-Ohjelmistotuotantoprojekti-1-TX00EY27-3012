
package com.example.app.View;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class TeacherAttendanceTrackingTest extends ApplicationTest {

    private TeacherAttendanceTracking teacherAttendanceTracking;

    @Override
    public void start(Stage stage) {
        teacherAttendanceTracking =
                new TeacherAttendanceTracking(() -> {});
    }

    @Test
    void teacherAttendanceTrackingShouldInitialize() {
        assertNotNull(teacherAttendanceTracking);
    }

    @Test
    void testLayoutStructure() {
        assertInstanceOf(
                BorderPane.class,
                teacherAttendanceTracking
        );

        assertInstanceOf(
                VBox.class,
                teacherAttendanceTracking.getLeft()
        );

        assertInstanceOf(
                VBox.class,
                teacherAttendanceTracking.getCenter()
        );
    }

    @Test
    void testSidebarExists() {
        assertNotNull(
                teacherAttendanceTracking.getLeft()
        );
    }

    @Test
    void testContentExists() {
        assertNotNull(
                teacherAttendanceTracking.getCenter()
        );
    }

    @Test
    void testTitleExists() {
        assertTrue(findLabel(
                teacherAttendanceTracking,
                "Oppitunti11111111111111 — 16.9.2026"
        ));
    }

    private boolean findLabel(
            javafx.scene.Node node,
            String text
    ) {
        if (node instanceof Label label &&
                text.equals(label.getText())) {
            return true;
        }

        if (node instanceof javafx.scene.Parent parent) {
            for (javafx.scene.Node child :
                    parent.getChildrenUnmodifiable()) {

                if (findLabel(child, text)) {
                    return true;
                }
            }
        }

        return false;
    }
}
