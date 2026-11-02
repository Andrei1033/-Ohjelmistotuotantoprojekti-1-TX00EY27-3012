package com.example.app.View;

import com.example.app.Controller.TeacherController;
import com.example.app.Localisation.i18n;
import com.example.app.Model.Teacher;
import com.example.app.Model.TeacherCourse;
import com.example.app.Model.LoginComponents.*;
import com.example.app.Model.StudentComponents.AttendanceRecord;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeacherCoursePageTest extends ViewTestSupport {
    private TeacherController controller;
    private TeacherCoursePage view;
    private AtomicInteger backs, logouts;
    @BeforeEach void setUp() {
        controller = mock(TeacherController.class);
        backs = new AtomicInteger();
        logouts = new AtomicInteger();
        when(controller.getTeacherCourses(1)).thenReturn(List.of(new TeacherCourse(12, "Java", "JAVA12", 1)));
    }
    private void show() {
        var teacher = new Teacher(1, "Matti", "Opettaja", "matti@example.com");
        view = new TeacherCoursePage(teacher, 12, controller, backs::incrementAndGet, () -> {},
                teacher, logouts::incrementAndGet, () -> {});
        Stage stage = new Stage();
        stage.setScene(new Scene(view, 1000, 650));
        stage.show();
    }
    @Test void showsCourseAndEmptyStudentList() {
        interact(() -> {
            show();
            hasLabel(view, "Java");
            hasLabel(view, "JAVA12");
            hasLabel(view, i18n.t("teacher.noStudents"));
            verify(controller).getStudentsForCourse(12);
        });
    }
    @Test void missingCourseUsesFallbackTitle() {
        when(controller.getTeacherCourses(1)).thenReturn(List.of());
        interact(() -> { show(); hasLabel(view, i18n.t("course.name") + " 12"); });
    }
    @Test void blankCourseCodeUsesCourseId() {
        when(controller.getTeacherCourses(1)).thenReturn(List.of(new TeacherCourse(12, "Java", " ", 1)));
        interact(() -> { show(); hasLabel(view, "12"); });
    }
    @Test void displaysStudentAttendanceIncludingLateAsAttended() {
        when(controller.getStudentsForCourse(12)).thenReturn(List.of(new User(7, "Liisa", "Virtanen", "l@example.com", Role.STUDENT)));
        when(controller.getAttendanceForStudentAndCourse(7, 12)).thenReturn(List.of(
                new AttendanceRecord(1, null, null, "", "present"),
                new AttendanceRecord(2, null, null, "", "late"),
                new AttendanceRecord(3, null, null, "", "absent")));
        interact(() -> {
            show();
            hasLabel(view, "Liisa Virtanen");
            hasLabel(view, "ID: 7");
            hasLabel(view, i18n.t("teacher.attendance", 67));
        });
    }
    @Test void navigationAndLessonButtonsInvokeTheirActions() {
        interact(() -> {
            show();
            button(view, "•   " + i18n.t("teacher.myCourses")).fire();
            button(view, i18n.t("teacher.startLesson")).fire();
            button(view, i18n.t("app.logout")).fire();
            assertEquals(1, backs.get());
            assertEquals(1, logouts.get());
            verify(controller).startLesson(12);
        });
    }
    @Test void changingLanguageReloadsThisCourse() {
        interact(() -> {
            show();
            languageBox(view).setValue(i18n.EN);
            assertEquals(i18n.EN, i18n.getCurrentLang());
            verify(controller).openCoursePage(12);
        });
    }
    @Test void arabicUsesRightToLeftLayout() {
        interact(() -> { i18n.setLanguage(i18n.AR); show(); assertEquals(NodeOrientation.RIGHT_TO_LEFT, view.getNodeOrientation()); });
    }
    @Test void editingCourseSavesTrimmedValuesAndRefreshesHeader() {
        when(controller.updateCourse(12, "New Java", "NEW12")).thenReturn(true);
        interact(() -> {
            show();
            inModal(() -> button(view, i18n.t("teacher.editCourse")).fire(), stage -> {
                var root = stage.getScene().getRoot();
                var fields = nodes(root, TextField.class);
                assertEquals("Java", fields.get(0).getText());
                fields.get(0).setText("   ");
                assertTrue(button(root, i18n.t("course.save")).isDisabled());
                fields.get(0).setText(" New Java ");
                fields.get(1).setText(" NEW12 ");
                button(root, i18n.t("course.save")).fire();
            });
            verify(controller).updateCourse(12, "New Java", "NEW12");
            hasLabel(view, "New Java");
            hasLabel(view, "NEW12");
        });
    }
    @Test void cancellingEditDoesNotUpdateCourse() {
        interact(() -> {
            show();
            inModal(() -> button(view, i18n.t("teacher.editCourse")).fire(), stage ->
                    button(stage.getScene().getRoot(), i18n.t("app.cancel")).fire());
            verify(controller, never()).updateCourse(anyInt(), anyString(), anyString());
            hasLabel(view, "Java");
        });
    }
    @Test void addStudentDialogFiltersStudentsAndCancelDoesNotSave() {
        when(controller.getAllStudents(12)).thenReturn(List.of(
                new User(7, "Liisa", "Virtanen", "l@example.com", Role.STUDENT),
                new User(8, "Pekka", "Testi", "p@example.com", Role.STUDENT)));
        interact(() -> {
            show();
            inModal(() -> button(view, i18n.t("teacher.addStudent")).fire(), stage -> {
                var root = stage.getScene().getRoot();
                nodes(root, TextField.class).getFirst().setText(" liisa ");
                ListView<?> list = nodes(root, ListView.class).getFirst();
                assertEquals(1, list.getItems().size());
                assertEquals(7, ((User) list.getItems().getFirst()).getId());
                button(root, i18n.t("app.cancel")).fire();
            });
            verify(controller, never()).addStudents(anyList(), anyInt());
        });
    }
}
