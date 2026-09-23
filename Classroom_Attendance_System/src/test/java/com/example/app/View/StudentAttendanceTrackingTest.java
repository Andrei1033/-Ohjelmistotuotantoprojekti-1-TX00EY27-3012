package com.example.app.View;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class StudentAttendanceTrackingTest extends ApplicationTest {

    private StudentAttendanceTracking view;

    @Override
    public void start(javafx.stage.Stage stage) {
        // Not used — view built manually
    }

    @BeforeEach
    void setUp() {
        interact(() -> view = new StudentAttendanceTracking(() -> {}));
    }

    // ---------- Helpers ----------

    private List<Node> allNodes(Parent root) {
        return new ArrayList<>(root.lookupAll("*"));
    }

    private <T extends Node> List<T> findAllByType(Parent root, Class<T> type) {
        List<T> result = new ArrayList<>();
        for (Node n : allNodes(root)) {
            if (type.isInstance(n)) result.add(type.cast(n));
        }
        return result;
    }

    private Label labelWithText(String text) {
        return findAllByType(view, Label.class).stream()
                .filter(l -> text.equals(l.getText()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Label not found: " + text));
    }

    private Button buttonWithText(String text) {
        return findAllByType(view, Button.class).stream()
                .filter(b -> text.equals(b.getText()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Button not found: " + text));
    }

    private VBox content() {
        assertInstanceOf(VBox.class, view.getCenter());
        return (VBox) view.getCenter();
    }

    private VBox sidebar() {
        assertInstanceOf(VBox.class, view.getLeft());
        return (VBox) view.getLeft();
    }

    /**
     * Finds attendance rows: HBox with 4 children where the last child is
     * an HBox (status box) containing a Circle + Label.
     */
    private List<HBox> findAttendanceRows() {
        List<HBox> rows = new ArrayList<>();
        for (HBox h : findAllByType(view, HBox.class)) {
            if (h.getChildren().size() != 4) continue;
            Node last = h.getChildren().get(3);
            if (!(last instanceof HBox statusBox)) continue;
            if (statusBox.getChildren().size() != 2) continue;
            if (!(statusBox.getChildren().get(0) instanceof Circle)) continue;
            if (!(statusBox.getChildren().get(1) instanceof Label)) continue;
            rows.add(h);
        }
        return rows;
    }

    private HBox statusBoxOf(HBox row) {
        return (HBox) row.getChildren().get(3);
    }

    private Circle statusCircleOf(HBox row) {
        return (Circle) statusBoxOf(row).getChildren().get(0);
    }

    private Label statusLabelOf(HBox row) {
        return (Label) statusBoxOf(row).getChildren().get(1);
    }

    private VBox dateBoxOf(HBox row) {
        return (VBox) row.getChildren().get(0);
    }

    // ---------- Structure ----------

    @Test
    @DisplayName("View is a BorderPane with sidebar on left and content in center")
    void testLayoutStructure() {
        assertInstanceOf(BorderPane.class, view);
        assertNotNull(view.getLeft());
        assertNotNull(view.getCenter());
        assertInstanceOf(VBox.class, view.getLeft());
        assertInstanceOf(VBox.class, view.getCenter());
    }

    @Test
    @DisplayName("Sidebar has fixed preferred width of 158")
    void testSidebarWidth() {
        assertEquals(158, sidebar().getPrefWidth(), 0.01);
    }

    // ---------- Sidebar ----------

    @Test
    @DisplayName("Sidebar contains brand 'Läsnäolo' and logo 'LO'")
    void testSidebarBrand() {
        assertNotNull(labelWithText("Läsnäolo"));
        assertNotNull(labelWithText("LO"));
    }

    @Test
    @DisplayName("Sidebar has user info (name + role + initials)")
    void testSidebarUser() {
        assertNotNull(labelWithText("MA"));
        assertNotNull(labelWithText("Etunimi Sukunimi"));
        assertNotNull(labelWithText("Opiskelija"));
    }

    @Test
    @DisplayName("Sidebar has navigation button and exactly two circles")
    void testSidebarNavAndCircles() {
        assertNotNull(buttonWithText("•   Omat kurssit"));
        // Only sidebar has circles in this class
        List<Circle> circles = findAllByType(view, Circle.class);
        // 2 sidebar circles + 3 status circles
        assertEquals(5, circles.size(),
                "Expected 2 sidebar + 3 status circles");
    }

    // ---------- Header ----------

    @Test
    @DisplayName("Header shows course title and course code subtitle")
    void testHeader() {
        assertNotNull(labelWithText("Ohjelmoinnin perusteet"));
        assertNotNull(labelWithText("TX00CV70 - omat läsnäolomerkinnät"));
    }

    @Test
    @DisplayName("Summary shows counts: 1 paikalla, 1 myöhässä, 1 Poissa")
    void testSummaryCounts() {
        assertNotNull(labelWithText("1 paikalla"));
        assertNotNull(labelWithText("1 myöhässä"));
        assertNotNull(labelWithText("1 Poissa"));
    }

    // ---------- Attendance rows ----------

    @Test
    @DisplayName("Exactly three attendance rows are rendered")
    void testThreeAttendanceRows() {
        assertEquals(3, findAttendanceRows().size());
    }

    @Test
    @DisplayName("Each row has a date box with date and weekday")
    void testRowDateBoxes() {
        for (HBox row : findAttendanceRows()) {
            VBox dateBox = dateBoxOf(row);
            assertEquals(2, dateBox.getChildren().size(),
                    "Date box should have exactly two labels");

            Label date = (Label) dateBox.getChildren().get(0);
            Label day = (Label) dateBox.getChildren().get(1);

            assertEquals("2.9.2026", date.getText());
            assertEquals("Ke", day.getText());
        }
    }

    @Test
    @DisplayName("Each row shows the same lesson title")
    void testRowLesson() {
        for (HBox row : findAttendanceRows()) {
            Label lesson = (Label) row.getChildren().get(1);
            assertEquals("Muuttujat ja tietotyypit", lesson.getText());
        }
    }

    @Test
    @DisplayName("Row statuses are paikalla, myöhässä, poissa in that order")
    void testRowStatuses() {
        List<HBox> rows = findAttendanceRows();
        assertEquals("paikalla", statusLabelOf(rows.get(0)).getText());
        assertEquals("myöhässä", statusLabelOf(rows.get(1)).getText());
        assertEquals("poissa",  statusLabelOf(rows.get(2)).getText());
    }

    @Test
    @DisplayName("Row pref height is 58 and uses rounded border style")
    void testRowStyle() {
        for (HBox row : findAttendanceRows()) {
            assertEquals(58, row.getPrefHeight(), 0.01);
            String style = row.getStyle();
            assertTrue(style.contains("-fx-background-radius: 12"), "Row rounding missing");
            assertTrue(style.contains("#D7D7D7"), "Row border color missing");
        }
    }

    // ---------- Status badge styling ----------

    @Test
    @DisplayName("Status 'paikalla' uses green color and green-tinted background")
    void testPaikallaStyle() {
        HBox row = findAttendanceRows().get(0);
        Circle circle = statusCircleOf(row);
        Label label = statusLabelOf(row);

        assertEquals(Color.web("#2E9560"), circle.getFill());
        assertEquals(Color.web("#2E9560"), label.getTextFill());

        String style = statusBoxOf(row).getStyle();
        assertTrue(style.contains("#E5F4EA"), "Green background missing");
        assertTrue(style.contains("#2E9560"), "Green border missing");
    }

    @Test
    @DisplayName("Status 'myöhässä' uses orange color and orange-tinted background")
    void testMyohassaStyle() {
        HBox row = findAttendanceRows().get(1);
        Circle circle = statusCircleOf(row);
        Label label = statusLabelOf(row);

        assertEquals(Color.web("#C77A00"), circle.getFill());
        assertEquals(Color.web("#C77A00"), label.getTextFill());

        String style = statusBoxOf(row).getStyle();
        assertTrue(style.contains("#FFF2DF"), "Orange background missing");
        assertTrue(style.contains("#C77A00"), "Orange border missing");
    }

    @Test
    @DisplayName("Status 'poissa' uses red color and red-tinted background")
    void testPoissaStyle() {
        HBox row = findAttendanceRows().get(2);
        Circle circle = statusCircleOf(row);
        Label label = statusLabelOf(row);

        assertEquals(Color.web("#C44D3A"), circle.getFill());
        assertEquals(Color.web("#C44D3A"), label.getTextFill());

        String style = statusBoxOf(row).getStyle();
        assertTrue(style.contains("#FBE6E2"), "Red background missing");
        assertTrue(style.contains("#C44D3A"), "Red border missing");
    }

    @Test
    @DisplayName("Status badge has fixed size and rounded corners")
    void testStatusBadgeShape() {
        for (HBox row : findAttendanceRows()) {
            HBox box = statusBoxOf(row);
            assertEquals(120, box.getPrefWidth(), 0.01);
            assertEquals(28,  box.getPrefHeight(), 0.01);

            String style = box.getStyle();
            assertTrue(style.contains("-fx-background-radius: 20"), "Badge rounding missing");
            assertTrue(style.contains("-fx-border-radius: 20"), "Badge border rounding missing");
        }
    }

    @Test
    @DisplayName("Every status circle has radius 4")
    void testStatusCircleRadius() {
        for (HBox row : findAttendanceRows()) {
            assertEquals(4, statusCircleOf(row).getRadius(), 0.01);
        }
    }

    // ---------- Content area ----------

    @Test
    @DisplayName("Content area contains a horizontal divider and the attendance list")
    void testContentSections() {
        VBox content = content();
        // header + divider + attendanceList
        assertEquals(3, content.getChildren().size(),
                "Content should have header, divider, attendance list");
        assertInstanceOf(HBox.class, content.getChildren().get(0));
        assertInstanceOf(Region.class, content.getChildren().get(1));
        assertInstanceOf(VBox.class,  content.getChildren().get(2));
    }

    @Test
    @DisplayName("Divider is a 1px light-gray line")
    void testDivider() {
        Region line = (Region) content().getChildren().get(1);
        assertEquals(1, line.getPrefHeight(), 0.01);
        assertTrue(line.getStyle().contains("#EEEEEE"), "Divider color should be #EEEEEE");
    }

    @Test
    @DisplayName("Attendance list uses 14px vertical spacing")
    void testAttendanceListSpacing() {
        VBox list = (VBox) content().getChildren().get(2);
        assertEquals(14, list.getSpacing(), 0.01);
        assertEquals(3, list.getChildren().size());
    }

    // ---------- Callback ----------

    @Test
    @DisplayName("onBack callback is stored but not invoked on construction")
    void testNoPrematureBack() {
        AtomicBoolean called = new AtomicBoolean(false);
        interact(() -> view = new StudentAttendanceTracking(() -> called.set(true)));
        assertFalse(called.get(), "onBack must not fire by itself");
    }
}