package com.example.app.View;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.stage.Stage;

import static org.junit.jupiter.api.Assertions.*;

public class TeacherCoursePageTest extends ApplicationTest {

    private TeacherCoursePage teacherCoursePage;

    @Override
    public void start(Stage stage) {
        teacherCoursePage = new TeacherCoursePage();
    }


    @Test
    void teacherCoursePageShouldInitialize() {
        assertNotNull(teacherCoursePage);
    }


    @Test
    void testLayoutStructure() {
        assertInstanceOf(BorderPane.class, teacherCoursePage);
        assertInstanceOf(VBox.class, teacherCoursePage.getLeft());
        assertInstanceOf(VBox.class, teacherCoursePage.getCenter());
    }


    @Test
    void testSidebarWidth() {
        VBox sidebar = (VBox) teacherCoursePage.getLeft();

        assertEquals(160, sidebar.getPrefWidth(), 0.01);
    }


    @Test
    void testCourseTitle() {
        assertTrue(findLabel(
                teacherCoursePage,
                "Kurssi: Ohjelmoinnin perusteet"
        ));
    }


    @Test
    void testCourseCode() {
        assertTrue(findLabel(
                teacherCoursePage,
                "Kurssikoodi: CS101"
        ));
    }


    @Test
    void testAddStudentsButton() {
        assertTrue(findButton(
                teacherCoursePage,
                "Lisää opiskelijoita"
        ));
    }


    @Test
    void testStartLessonButton() {
        assertTrue(findButton(
                teacherCoursePage,
                "Aloita oppitunti"
        ));
    }


    @Test
    void testMyCoursesButton() {
        assertTrue(findButton(
                teacherCoursePage,
                "•   Omat kurssit"
        ));
    }


    @Test
    void testLessonCardsExist() {
        VBox content = (VBox) teacherCoursePage.getCenter();

        assertNotNull(content);
        assertTrue(content.getChildren().size() >= 1);
    }




    private boolean findLabel(Node node, String text) {

        if (node instanceof Label label &&
                text.equals(label.getText())) {
            return true;
        }

        if (node instanceof javafx.scene.Parent parent) {

            for (Node child : parent.getChildrenUnmodifiable()) {

                if (findLabel(child, text)) {
                    return true;
                }
            }
        }

        return false;
    }



    private boolean findButton(Node node, String text) {

        if (node instanceof Button button &&
                text.equals(button.getText())) {
            return true;
        }

        if (node instanceof javafx.scene.Parent parent) {

            for (Node child : parent.getChildrenUnmodifiable()) {

                if (findButton(child, text)) {
                    return true;
                }
            }
        }

        return false;
    }
}