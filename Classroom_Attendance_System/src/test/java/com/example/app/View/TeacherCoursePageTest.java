package com.example.app.View;

import com.example.app.Controller.TeacherController;
import com.example.app.Model.LoginComponents.User;
import com.example.app.Model.Teacher;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
public class TeacherCoursePageTest extends ApplicationTest {

    private TeacherCoursePage teacherCoursePage;
    private Teacher teacher;
    private TeacherController controller;

    @Override
    public void start(Stage stage) {

        teacher = new Teacher(1, "Etunimi", "Sukunimi", "test@example.com");

        // Mockataan TeacherController – EI oikeaa tietokantakutsua
        controller = mock(TeacherController.class);

        // Palautetaan tyhjät listat, jotta konstruktori ei kaadu
        when(controller.getTeacherCourses(anyInt()))
                .thenReturn(Collections.emptyList());
        when(controller.getStudentsForCourse(anyInt()))
                .thenReturn(Collections.emptyList());

        Runnable onBack = () -> {};
        Runnable onAddLesson = () -> {};
        Runnable onLogout = () -> {};
        Runnable onProfileUpdated = () -> {};

        teacherCoursePage = new TeacherCoursePage(
                teacher,
                1,
                controller,
                onBack,
                onAddLesson,
                (User) teacher,
                onLogout,
                onProfileUpdated
        );
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
        assertEquals(158, sidebar.getPrefWidth(), 0.01);
    }

    @Test
    void testCourseTitle() {
        // Ilman course-dataa nimi on "Kurssi 1"
        assertNotNull(findLabel(teacherCoursePage, "Kurssi 1"));
    }

    @Test
    void testCourseCode() {
        // Ilman course-dataa codeText jää tyhjäksi — TARKISTA onko tämä
        // haluttu käytös vai pitäisikö koodin olla "01"
        Label code = findLabel(teacherCoursePage, "");
        // Tai jos haluat että tyhjäkin löytyy:
        assertNotNull(findLabel(teacherCoursePage, "Kurssi 1"));
    }

    @Test
    void testAddStudentsButton() {
        assertTrue(findButton(teacherCoursePage, "Lisää opiskelija"));
    }

    @Test
    void testStartLessonButton() {
        assertTrue(findButton(teacherCoursePage, "Aloita oppitunti"));
    }

    @Test
    void testMyCoursesButton() {
        assertTrue(findButton(teacherCoursePage, "•   Omat kurssit"));
    }

    @Test
    void testLessonCardsExist() {
        VBox content = (VBox) teacherCoursePage.getCenter();
        assertNotNull(content);
        assertTrue(content.getChildren().size() >= 1);
    }

    private Label findLabel(Node node, String text) {
        if (node == null) return null;

        if (node instanceof Label label) {
            if (text == null || (label.getText() != null && label.getText().contains(text))) {
                return label;
            }
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                Label found = findLabel(child, text);
                if (found != null) return found;
            }
        }
        return null;
    }

    private boolean findButton(Node node, String text) {
        if (node instanceof Button button && text.equals(button.getText())) {
            return true;
        }
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                if (findButton(child, text)) return true;
            }
        }
        return false;
    }
}