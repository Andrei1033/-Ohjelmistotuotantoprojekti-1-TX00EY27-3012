package com.example.app.View;

import com.example.app.Model.LoginComponents.Role;
import com.example.app.Model.LoginComponents.User;
import com.example.app.Model.StudentComponents.AttendanceRecord;
import com.example.app.Model.StudentComponents.Course;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class StudentAttendanceTrackingTest extends ApplicationTest {

    private static final LocalDateTime START = LocalDateTime.of(2026, 9, 2, 10, 0);
    private static final LocalDateTime END   = LocalDateTime.of(2026, 9, 2, 11, 30);

    private Stage stage;
    private Course course;
    private User student;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
    }

    @BeforeEach
    void setUp() {
        course  = new Course(1, "TX00CV70", "Ohjelmoinnin perusteet", 99);
        student = new User(42, "Matti", "Meikäläinen", "matti@example.com", Role.STUDENT);
    }

    @AfterEach
    void tearDown() {
        interact(() -> stage.hide());
    }

    // ---------------- helpers ----------------

    /**
     * Builds the view, attaches it to a shown Scene so lookupAll works,
     * and returns the root.
     */
    private StudentAttendanceTracking show(List<AttendanceRecord> records, Runnable onBack) {
        StudentAttendanceTracking[] ref = new StudentAttendanceTracking[1];
        interact(() -> {
            StudentAttendanceTracking view =
                    new StudentAttendanceTracking(course, records, onBack, student);
            Scene scene = new Scene(view, 900, 600);
            stage.setScene(scene);
            stage.show();
            ref[0] = view;
        });
        return ref[0];
    }

    private AttendanceRecord rec(String status, String topic) {
        return new AttendanceRecord(1, START, END, topic, status);
    }

    private <T extends Node> List<T> findAllByType(Parent root, Class<T> type) {
        List<T> out = new ArrayList<>();
        for (Node n : root.lookupAll("*")) {
            if (type.isInstance(n)) out.add(type.cast(n));
        }
        return out;
    }

    private boolean hasLabel(Parent root, String text) {
        return findAllByType(root, Label.class).stream()
                .anyMatch(l -> text.equals(l.getText()));
    }

    private List<HBox> attendanceRows(Parent root) {
        List<HBox> rows = new ArrayList<>();
        for (HBox h : findAllByType(root, HBox.class)) {
            if (h.getChildren().size() != 4) continue;
            if (!(h.getChildren().get(3) instanceof HBox status)) continue;
            if (status.getChildren().size() != 2) continue;
            if (!(status.getChildren().get(1) instanceof Label)) continue;
            rows.add(h);
        }
        return rows;
    }

    private Label statusLabelOf(HBox row) {
        HBox status = (HBox) row.getChildren().get(3);
        return (Label) status.getChildren().get(1);
    }

    // ---------------- tests ----------------

    @Test
    @DisplayName("View is a BorderPane with sidebar left and content center")
    void layoutStructure() {
        StudentAttendanceTracking view = show(List.of(), () -> {});
        assertInstanceOf(BorderPane.class, view);
        assertInstanceOf(VBox.class, view.getLeft());
        assertInstanceOf(VBox.class, view.getCenter());
    }

    @Test
    @DisplayName("Sidebar shows logged-in user's full name, role and initials")
    void sidebarShowsUser() {
        StudentAttendanceTracking view = show(List.of(), () -> {});
        assertTrue(hasLabel(view, "Matti Meikäläinen"), "full name");
        assertTrue(hasLabel(view, "Opiskelija"),       "role");
        assertTrue(hasLabel(view, "MM"),               "initials");
    }

    @Test
    @DisplayName("Header shows course name and code from the Course model")
    void headerShowsCourse() {
        StudentAttendanceTracking view = show(List.of(), () -> {});
        assertTrue(hasLabel(view, "Ohjelmoinnin perusteet"));
        assertTrue(hasLabel(view, "TX00CV70 - omat läsnäolomerkinnät"));
    }

    @Test
    @DisplayName("Summary counts present/late/absent from records (excused -> poissa)")
    void summaryCounts() {
        StudentAttendanceTracking view = show(List.of(
                rec("present", "A"),
                rec("present", "B"),
                rec("late",    "C"),
                rec("absent",  "D"),
                rec("excused", "E")
        ), () -> {});

        assertTrue(hasLabel(view, "2 paikalla"), "present");
        assertTrue(hasLabel(view, "1 myöhässä"), "late");
        assertTrue(hasLabel(view, "2 poissa"),   "absent+excused");
    }

    @Test
    @DisplayName("One row per record; empty list shows empty-state message")
    void rowCountAndEmptyState() {
        StudentAttendanceTracking empty = show(List.of(), () -> {});
        assertEquals(0, attendanceRows(empty).size());
        assertTrue(hasLabel(empty, "Tällä kurssilla ei ole vielä oppitunteja."));

        StudentAttendanceTracking three = show(List.of(
                rec("present", "X"),
                rec("late",    "X"),
                rec("absent",  "X")
        ), () -> {});
        assertEquals(3, attendanceRows(three).size());
    }

    @Test
    @DisplayName("Status labels are localized in the given order")
    void statusLabels() {
        StudentAttendanceTracking view = show(List.of(
                rec("present", "X"),
                rec("late",    "X"),
                rec("absent",  "X"),
                rec("excused", "X")
        ), () -> {});

        List<HBox> rows = attendanceRows(view);
        assertEquals(4, rows.size());
        assertEquals("paikalla",            statusLabelOf(rows.get(0)).getText());
        assertEquals("myöhässä",            statusLabelOf(rows.get(1)).getText());
        assertEquals("poissa",              statusLabelOf(rows.get(2)).getText());
        assertEquals("poissa (hyväksytty)", statusLabelOf(rows.get(3)).getText());
    }

    @Test
    @DisplayName("null status falls back to 'poissa'")
    void nullStatusFallback() {
        StudentAttendanceTracking view = show(List.of(rec(null, "X")), () -> {});
        List<HBox> rows = attendanceRows(view);
        assertEquals(1, rows.size());
        assertEquals("poissa", statusLabelOf(rows.get(0)).getText());
    }

    @Test
    @DisplayName("Row shows the lesson topic from AttendanceRecord")
    void rowShowsTopic() {
        StudentAttendanceTracking view = show(
                List.of(rec("present", "Muuttujat ja tietotyypit")), () -> {});

        HBox row = attendanceRows(view).get(0);
        Label topic = (Label) row.getChildren().get(1);
        assertEquals("Muuttujat ja tietotyypit", topic.getText());
    }

    @Test
    @DisplayName("Row date box shows formatted date and Finnish weekday")
    void rowShowsFormattedDate() {
        StudentAttendanceTracking view = show(List.of(rec("present", "X")), () -> {});

        HBox row = attendanceRows(view).get(0);
        VBox dateBox = (VBox) row.getChildren().get(0);
        Label date = (Label) dateBox.getChildren().get(0);
        Label day  = (Label) dateBox.getChildren().get(1);

        assertEquals("2.9.2026", date.getText());
        assertEquals("Ke",       day.getText());
    }

    @Test
    @DisplayName("Back button fires the onBack callback")
    void backButtonFiresCallback() {
        AtomicBoolean called = new AtomicBoolean(false);
        StudentAttendanceTracking view = show(List.of(), () -> called.set(true));

        Button back = findAllByType(view, Button.class).stream()
                .filter(b -> b.getText() != null && b.getText().contains("Takaisin"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Back button not found"));

        interact(back::fire);
        assertTrue(called.get(), "onBack must be invoked");
    }

    @Test
    @DisplayName("onBack is not invoked on construction")
    void noPrematureBack() {
        AtomicBoolean called = new AtomicBoolean(false);
        show(List.of(), () -> called.set(true));
        assertFalse(called.get());
    }
}