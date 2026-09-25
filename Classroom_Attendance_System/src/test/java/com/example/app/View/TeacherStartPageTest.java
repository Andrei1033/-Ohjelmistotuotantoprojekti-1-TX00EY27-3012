package com.example.app.View;

import com.example.app.Controller.TeacherController;
import com.example.app.Model.Teacher;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class TeacherStartPageTest extends ApplicationTest {

    private TeacherStartPage teacherStartPage;

    @Override
    public void start(Stage stage) {
        // 1. Luodaan valheellinen opettaja testille
        Teacher mockTeacher = new Teacher(1, "Matti", "Meikäläinen", "matti@testi.fi");

        // 2. Luodaan controlleri (tai käytetään mockia)
        TeacherController mockController = new TeacherController(mockTeacher, () -> {});

        // 3. Syötetään kaikki kolme argumenttia
        teacherStartPage = new TeacherStartPage(
                mockTeacher,
                mockController,
                () -> {} // Vasta tämä 3. argumentti on lambda (Runnable onLogout)
        );

        Scene scene = new Scene(teacherStartPage, 1024, 399);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    void teacherStartPageShouldInitialize() {
        assertNotNull(teacherStartPage);
    }

    @Test
    void testLayoutStructure() {
        assertInstanceOf(BorderPane.class, teacherStartPage);
        assertInstanceOf(VBox.class, teacherStartPage.getLeft());
        assertInstanceOf(VBox.class, teacherStartPage.getCenter());
    }

    @Test
    void testSidebarWidth() {
        VBox sidebar = (VBox) teacherStartPage.getLeft();

        assertEquals(158, sidebar.getPrefWidth(), 0.01);
    }

    @Test
    void testSidebarExists() {
        assertNotNull(teacherStartPage.getLeft());
    }

    @Test
    void testContentExists() {
        assertNotNull(teacherStartPage.getCenter());
    }

    @Test
    void testCourseTitle() {
        assertTrue(findLabel(
                teacherStartPage,
                "Omat kurssit"
        ));
    }

    @Test
    void testCourseIntroduction() {
        assertTrue(findLabel(
                teacherStartPage,
                "Sinulla on 3 kurssia tällä lukukaudella."
        ));
    }

    @Test
    void testCourseCode() {
        assertTrue(findLabel(
                teacherStartPage,
                "TX00CV45"
        ));
    }

    @Test
    void testCourseName() {
        assertTrue(findLabel(
                teacherStartPage,
                "Ohjelmoinnin perusteet"
        ));
    }

    @Test
    void testLessonCount() {
        assertTrue(findLabel(
                teacherStartPage,
                "5 oppituntia"
        ));
    }

    @Test
    void testCoursesButton() {
        assertTrue(findButton(
                teacherStartPage,
                "•   Omat kurssit"
        ));
    }

    @Test
    void testUserName() {
        assertTrue(findLabel(
                teacherStartPage,
                "Etunimi Sukunimi"
        ));
    }

    @Test
    void testUserRole() {
        assertTrue(findLabel(
                teacherStartPage,
                "Opettaja"
        ));
    }

    @Test
    void testLogoText() {
        assertTrue(findLabel(
                teacherStartPage,
                "LO"
        ));
    }

    @Test
    void testUserInitials() {
        assertTrue(findLabel(
                teacherStartPage,
                "MA"
        ));
    }


    @Test
    void testNumberOfCourseCards() {
        VBox content = (VBox) teacherStartPage.getCenter();

        assertNotNull(content);
        assertEquals(2, content.getChildren().size());
    }

    @Test
    void testCourseCardsAreVBoxes() {
        VBox content = (VBox) teacherStartPage.getCenter();

        Node cardsNode = content.getChildren().get(1);

        assertInstanceOf(
                javafx.scene.layout.HBox.class,
                cardsNode
        );
    }

    @Test
    void testThreeCourseCards() {
        VBox content = (VBox) teacherStartPage.getCenter();

        javafx.scene.layout.HBox cards =
                (javafx.scene.layout.HBox) content.getChildren().get(1);

        assertEquals(3, cards.getChildren().size());
    }

    @Test
    void testCourseCardsHaveCorrectWidth() {
        VBox content = (VBox) teacherStartPage.getCenter();

        javafx.scene.layout.HBox cards =
                (javafx.scene.layout.HBox) content.getChildren().get(1);

        for (Node node : cards.getChildren()) {
            VBox card = (VBox) node;

            assertEquals(190, card.getPrefWidth(), 0.01);
        }
    }

    @Test
    void testCourseCardsHaveCorrectHeight() {
        VBox content = (VBox) teacherStartPage.getCenter();

        javafx.scene.layout.HBox cards =
                (javafx.scene.layout.HBox) content.getChildren().get(1);

        for (Node node : cards.getChildren()) {
            VBox card = (VBox) node;

            assertEquals(75, card.getPrefHeight(), 0.01);
        }
    }

    private boolean findLabel(Node node, String text) {
        return findLabelNode(node, text) != null;
    }

    private Label findLabelNode(Node node, String text) {
        if (node instanceof Label label &&
                text.equals(label.getText())) {
            return label;
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                Label result = findLabelNode(child, text);

                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    private boolean findButton(Node node, String text) {
        return findButtonObject(node, text) != null;
    }

    private Button findButtonObject(Node node, String text) {
        if (node instanceof Button button &&
                text.equals(button.getText())) {
            return button;
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                Button result = findButtonObject(child, text);

                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }
}