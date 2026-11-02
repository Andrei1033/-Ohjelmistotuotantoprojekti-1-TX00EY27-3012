package com.example.app.View;

import com.example.app.Controller.TeacherController;
import com.example.app.Localisation.i18n;
import com.example.app.Model.Teacher;
import com.example.app.Model.TeacherCourse;
import com.example.app.Model.Lesson;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeacherStartPageTest extends ViewTestSupport {
    private TeacherController controller;
    private TeacherStartPage view;
    private AtomicInteger logouts;
    @BeforeEach void setUp() {
        controller = mock(TeacherController.class);
        logouts = new AtomicInteger();
        when(controller.getTeacherCourses(1)).thenReturn(List.of(new TeacherCourse(12, "Java", "JAVA12", 1)));
        when(controller.getLessonsForCourse(12)).thenReturn(List.of(mock(Lesson.class), mock(Lesson.class)));
    }
    private void show() {
        view = new TeacherStartPage(new Teacher(1, "Matti", "Opettaja", "matti@example.com"), controller, logouts::incrementAndGet);
        Stage stage = new Stage();
        stage.setScene(new Scene(view, 1000, 650));
        stage.show();
    }
    @Test void displaysTeacherCourseDetailsAndLessonCount() {
        interact(() -> {
            show();
            hasLabel(view, "Matti Opettaja");
            hasLabel(view, "MO");
            hasLabel(view, i18n.t("app.teacher"));
            hasLabel(view, i18n.t("teacher.myCourses"));
            hasLabel(view, i18n.t("teacher.courseCount", 1));
            hasLabel(view, "Java");
            hasLabel(view, "JAVA12");
            hasLabel(view, i18n.t("course.lessonCount", 2));
            assertEquals(1, nodes(view, FlowPane.class).getFirst().getChildren().size());
        });
    }
    @Test void emptyCourseListShowsPlaceholderWithoutCards() {
        when(controller.getTeacherCourses(1)).thenReturn(List.of());
        interact(() -> {
            show();
            hasLabel(view, i18n.t("teacher.noCourses"));
            hasLabel(view, i18n.t("teacher.courseCount", 0));
            assertTrue(nodes(view, FlowPane.class).isEmpty());
        });
    }
    @Test void failedCourseLoadShowsEmptyState() {
        when(controller.getTeacherCourses(1)).thenThrow(new IllegalStateException("Unavailable"));
        interact(() -> { show(); hasLabel(view, i18n.t("teacher.noCourses")); });
    }
    @Test void failedLessonLoadDisplaysZeroLessons() {
        when(controller.getLessonsForCourse(12)).thenThrow(new IllegalStateException("Unavailable"));
        interact(() -> { show(); hasLabel(view, i18n.t("course.lessonCount", 0)); });
    }
    @Test void startLessonUsesCorrectCourseWithoutOpeningCoursePage() {
        interact(() -> {
            show();
            button(view, i18n.t("teacher.startLesson")).fire();
            verify(controller).startLesson(12);
            verify(controller, never()).openCoursePage(anyInt());
        });
    }
    private void clickCard(MouseButton mouseButton) {
        var card = nodes(view, FlowPane.class).getFirst().getChildren().getFirst();
        card.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, mouseButton, 1,
                false, false, false, false, mouseButton == MouseButton.PRIMARY,
                false, mouseButton == MouseButton.SECONDARY, false, false, false, null));
    }
    @Test void primaryClickOpensCorrectCourse() {
        interact(() -> { show(); clickCard(MouseButton.PRIMARY); verify(controller).openCoursePage(12); });
    }
    @Test void secondaryClickDoesNotOpenCourse() {
        interact(() -> { show(); clickCard(MouseButton.SECONDARY); verify(controller, never()).openCoursePage(anyInt()); });
    }
    @Test void logoutInvokesCallbackOnce() {
        interact(() -> {
            show();
            assertEquals(0, logouts.get());
            button(view, i18n.t("app.logout")).fire();
            assertEquals(1, logouts.get());
        });
    }
    @Test void changingLanguageReloadsStartPage() {
        interact(() -> {
            show();
            assertEquals(i18n.SUPPORTED, languageBox(view).getItems());
            languageBox(view).setValue(i18n.EN);
            assertEquals(i18n.EN, i18n.getCurrentLang());
            verify(controller).showStartPage();
        });
    }
    @Test void arabicUsesRightToLeftLayoutAndTranslatedHeading() {
        interact(() -> {
            i18n.setLanguage(i18n.AR);
            show();
            assertEquals(NodeOrientation.RIGHT_TO_LEFT, view.getNodeOrientation());
            hasLabel(view, i18n.t("teacher.myCourses"));
        });
    }
    @Test void createCourseRequiresNameAndSavesTrimmedValues() {
        when(controller.createCourses("New Java", "NEW12", 1)).thenReturn(true);
        interact(() -> {
            show();
            inModal(() -> button(view, i18n.t("teacher.newCourse")).fire(), stage -> {
                var root = stage.getScene().getRoot();
                Button save = button(root, i18n.t("app.save"));
                assertTrue(save.isDisabled());
                var fields = nodes(root, TextField.class);
                fields.get(0).setText("   ");
                assertTrue(save.isDisabled());
                fields.get(0).setText(" New Java ");
                fields.get(1).setText(" NEW12 ");
                assertFalse(save.isDisabled());
                save.fire();
            });
            verify(controller).createCourses("New Java", "NEW12", 1);
            verify(controller).showStartPage();
        });
    }
    @Test void cancellingCreationDoesNotSaveOrReload() {
        interact(() -> {
            show();
            inModal(() -> button(view, i18n.t("teacher.newCourse")).fire(), stage -> stage.close());
            verify(controller, never()).createCourses(anyString(), anyString(), anyInt());
            verify(controller, never()).showStartPage();
        });
    }
}
