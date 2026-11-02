package com.example.app.View;

import com.example.app.DaoElements.StudentDao.AttendanceDao;
import com.example.app.Localisation.i18n;
import com.example.app.Model.LoginComponents.*;
import com.example.app.Model.Teacher;
import com.example.app.Model.TeacherCourse;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeacherAttendanceTrackingTest extends ViewTestSupport {
    private AttendanceDao dao;
    private Stage stage;
    private Parent root;
    private AtomicInteger finished;
    private final User student = new User(7, "Liisa", "Virtanen", "liisa@example.com", Role.STUDENT);

    @BeforeEach void setUp() { finished = new AtomicInteger(); }
    private void show(List<User> students) {
        List<Window> existing = new ArrayList<>(Window.getWindows());
        try (var construction = mockConstruction(AttendanceDao.class, (mock, context) -> {
            when(mock.getStudentsForCourse(12)).thenReturn(students);
            when(mock.saveAttendance(anyInt(), anyInt(), anyString())).thenReturn(true);
            when(mock.endLesson(34)).thenReturn(true);
        })) {
            var view = new TeacherAttendanceTracking(new Teacher(1, "Matti", "Opettaja", "matti@example.com"),
                    new TeacherCourse(12, "Java", "JAVA12", 1), 34, finished::incrementAndGet);
            view.show();
            dao = construction.constructed().getFirst();
        }
        stage = (Stage) Window.getWindows().stream().filter(w -> !existing.contains(w)).findFirst().orElseThrow();
        root = stage.getScene().getRoot();
    }
    private void save() { button(root, i18n.t("attendance.saveAndFinish")).fire(); }
    private void count(String key, int count) { hasLabel(root, count + " " + i18n.t(key)); }

    @Test void showsStudentsAndDefaultsToAbsent() {
        interact(() -> {
            show(List.of(student));
            verify(dao).getStudentsForCourse(12);
            hasLabel(root, "Liisa Virtanen");
            hasLabel(root, "Matti Opettaja");
            count("attendance.present", 0);
            count("attendance.late", 0);
            count("attendance.absent", 1);
            assertEquals(0, finished.get());
        });
    }
    @Test void statusButtonsUpdateCountersWithoutDoubleCounting() {
        interact(() -> {
            show(List.of(student));
            button(root, i18n.t("attendance.present")).fire();
            button(root, i18n.t("attendance.present")).fire();
            count("attendance.present", 1);
            count("attendance.absent", 0);
            button(root, i18n.t("attendance.late")).fire();
            count("attendance.present", 0);
            count("attendance.late", 1);
            button(root, i18n.t("attendance.absent")).fire();
            count("attendance.late", 0);
            count("attendance.absent", 1);
        });
    }
    @Test void savesEveryStudentBeforeEndingLesson() {
        interact(() -> {
            show(List.of(student, new User(8, "Pekka", "Testi", "pekka@example.com", Role.STUDENT)));
            save();
            verify(dao).saveAttendance(34, 7, "absent");
            verify(dao).saveAttendance(34, 8, "absent");
            var order = inOrder(dao);
            order.verify(dao, times(2)).saveAttendance(eq(34), anyInt(), eq("absent"));
            order.verify(dao).endLesson(34);
            assertFalse(stage.isShowing());
            assertEquals(1, finished.get());
        });
    }
    @Test void localizedButtonSavesUntranslatedDatabaseStatus() {
        interact(() -> {
            i18n.setLanguage(i18n.AR);
            show(List.of(student));
            assertEquals(NodeOrientation.RIGHT_TO_LEFT, root.getNodeOrientation());
            button(root, i18n.t("attendance.late")).fire();
            save();
            verify(dao).saveAttendance(34, 7, "late");
            assertEquals(1, finished.get());
        });
    }
    @Test void emptyCourseShowsPlaceholderAndCanEndLesson() {
        interact(() -> {
            show(List.of());
            hasLabel(root, i18n.t("teacher.noStudents"));
            count("attendance.absent", 0);
            save();
            verify(dao, never()).saveAttendance(anyInt(), anyInt(), anyString());
            verify(dao).endLesson(34);
            assertFalse(stage.isShowing());
        });
    }
    @Test void failedAttendanceSaveKeepsLessonOpenAndShowsError() {
        interact(() -> {
            show(List.of(student));
            when(dao.saveAttendance(34, 7, "absent")).thenReturn(false);
            try (var alerts = mockConstruction(Alert.class)) {
                save();
                assertEquals(1, alerts.constructed().size());
                verify(alerts.constructed().getFirst()).setContentText(i18n.t("attendance.errorSave"));
                verify(alerts.constructed().getFirst()).showAndWait();
            }
            verify(dao, never()).endLesson(anyInt());
            assertTrue(stage.isShowing());
            assertEquals(0, finished.get());
        });
    }
    @Test void failedEndLessonKeepsWindowOpenAndShowsError() {
        interact(() -> {
            show(List.of(student));
            when(dao.endLesson(34)).thenReturn(false);
            try (var alerts = mockConstruction(Alert.class)) {
                save();
                assertEquals(1, alerts.constructed().size());
                verify(alerts.constructed().getFirst()).setContentText(i18n.t("attendance.errorEndLesson"));
            }
            assertTrue(stage.isShowing());
            assertEquals(0, finished.get());
        });
    }
}
