package com.example.app.View;

import com.example.app.Controller.TeacherController;
import com.example.app.Model.Teacher;
import com.example.app.Model.TeacherCourse;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class TeacherStartPageTest {

    private TeacherStartPage teacherStartPage;
    private Teacher teacher;
    private TeacherController controller;

    @BeforeEach
    void setUp() {
        teacher = new Teacher(1, "Matti", "Meikäläinen", "matti@testi.fi");

        controller = mock(TeacherController.class);

        // Palautetaan YKSI kurssi, jotta kortit syntyvät
        TeacherCourse course = new TeacherCourse(1, "Ohjelmoinnin perusteet", "CS101", 1);
        when(controller.getTeacherCourses(anyInt()))
                .thenReturn(List.of(course));
        when(controller.getLessonsForCourse(anyInt()))
                .thenReturn(Collections.emptyList());

        teacherStartPage = new TeacherStartPage(teacher, controller, () -> {});
        new Scene(teacherStartPage, 1024, 399);
    }

    @Test
    void testRootIsBorderPane() {
        assertInstanceOf(BorderPane.class, teacherStartPage);
    }

    @Test
    void testLeftSidebarExists() {
        Node left = teacherStartPage.getLeft();
        assertNotNull(left);
        assertInstanceOf(VBox.class, left);
    }

    @Test
    void testSidebarWidth() {
        VBox sidebar = (VBox) teacherStartPage.getLeft();
        assertEquals(158.0, sidebar.getPrefWidth(), 0.1);
    }

    @Test
    void testCenterExists() {
        assertNotNull(teacherStartPage.getCenter());
    }

    @Test
    void testCenterIsVBox() {
        assertInstanceOf(VBox.class, teacherStartPage.getCenter());
    }

    @Test
    void testCourseHeadingExists() {
        assertNotNull(findLabel(teacherStartPage, "Omat kurssit"));
    }

    @Test
    void testCourseIntroductionExists() {
        Label introduction = findLabelContaining(teacherStartPage, "Sinulla on ");
        assertNotNull(introduction);
        assertTrue(introduction.getText().matches(
                "Sinulla on \\d+ kurssia tällä lukukaudella\\."));
    }

    @Test
    void testCourseCardsExist() {
        FlowPane cards = getCourseCards();
        assertNotNull(cards);
        assertFalse(cards.getChildren().isEmpty());
    }

    @Test
    void testCourseCardsAreVBoxes() {
        FlowPane cards = getCourseCards();
        assertNotNull(cards);
        assertFalse(cards.getChildren().isEmpty());
        for (Node child : cards.getChildren()) {
            assertInstanceOf(VBox.class, child);
        }
    }

    @Test
    void testCourseCardsHaveCorrectWidth() {
        FlowPane cards = getCourseCards();
        for (Node child : cards.getChildren()) {
            VBox card = (VBox) child;
            assertEquals(190.0, card.getPrefWidth(), 0.1);
        }
    }

    @Test
    void testCourseCardsHaveCorrectHeight() {
        FlowPane cards = getCourseCards();
        for (Node child : cards.getChildren()) {
            VBox card = (VBox) child;
            // HUOM: koodissa on setPrefSize(190, 112), ei 75
            assertEquals(112.0, card.getPrefHeight(), 0.1);
        }
    }

    @Test
    void testCourseCardsContainLabels() {
        FlowPane cards = getCourseCards();
        for (Node child : cards.getChildren()) {
            assertNotNull(findLabelInNode(child));
        }
    }

    @Test
    void testCourseCodeExistsWhenCourseExists() {
        FlowPane cards = getCourseCards();
        boolean found = false;
        for (Node child : cards.getChildren()) {
            for (Node cardChild : ((VBox) child).getChildren()) {
                if (cardChild instanceof Label label) {
                    String text = label.getText();
                    if (text != null && text.matches("\\d{2}|[A-Z]+\\d+")) {
                        found = true;
                    }
                }
            }
        }
        assertTrue(found);
    }

    @Test
    void testTeacherNameExists() {
        assertNotNull(findLabel(teacherStartPage, "Matti Meikäläinen"));
    }

    @Test
    void testTeacherRoleExists() {
        assertNotNull(findLabel(teacherStartPage, "Opettaja"));
    }

    @Test
    void testUserInitials() {
        assertNotNull(findLabel(teacherStartPage, "MM"));
    }

    @Test
    void testLogoExists() {
        assertNotNull(findLabel(teacherStartPage, "LO"));
    }

    // --- Apumetodit ---

    /**
     * Hakee FlowPane-kurssikorttikontin ScrollPanen sisältä.
     * TeacherStartPage laittaa kortit ScrollPane -> FlowPane -rakenteeseen.
     */
    private FlowPane getCourseCards() {
        VBox center = (VBox) teacherStartPage.getCenter();
        for (Node child : center.getChildren()) {
            if (child instanceof ScrollPane scroll
                    && scroll.getContent() instanceof FlowPane flow) {
                return flow;
            }
        }
        return null;
    }

    private Label findLabel(Node root, String expectedText) {
        if (root instanceof Label label && expectedText.equals(label.getText())) {
            return label;
        }
        if (root instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                Label result = findLabel(child, expectedText);
                if (result != null) return result;
            }
        }
        return null;
    }

    private Label findLabelContaining(Node root, String expectedText) {
        if (root instanceof Label label) {
            String text = label.getText();
            if (text != null && text.contains(expectedText)) return label;
        }
        if (root instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                Label result = findLabelContaining(child, expectedText);
                if (result != null) return result;
            }
        }
        return null;
    }

    private Label findLabelInNode(Node root) {
        if (root instanceof Label label) return label;
        if (root instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                Label result = findLabelInNode(child);
                if (result != null) return result;
            }
        }
        return null;
    }
}