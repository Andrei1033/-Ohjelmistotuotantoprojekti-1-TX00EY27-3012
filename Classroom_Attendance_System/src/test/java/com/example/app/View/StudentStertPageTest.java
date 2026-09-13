package com.example.app.View;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class StudentStartPageTest extends ApplicationTest {

    private StudentStartPage view;

    @Override
    public void start(javafx.stage.Stage stage) {
        // Not used — we build the view manually
    }

    @BeforeEach
    void setUp() {
        interact(() -> view = new StudentStartPage(() -> {}));
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

    private <T extends Node> T findByType(Parent root, Class<T> type) {
        return findAllByType(root, type).stream().findFirst()
                .orElseThrow(() -> new AssertionError("Not found: " + type.getSimpleName()));
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

    private VBox sidebar() {
        Node left = view.getLeft();
        assertNotNull(left, "Left (sidebar) must be set");
        assertInstanceOf(VBox.class, left);
        return (VBox) left;
    }

    private VBox content() {
        Node center = view.getCenter();
        assertNotNull(center, "Center (content) must be set");
        assertInstanceOf(VBox.class, center);
        return (VBox) center;
    }

    /** Extracts all course cards: VBox with exactly 3 Label children. */
    private List<VBox> findCourseCards() {
        List<VBox> cards = new ArrayList<>();
        for (VBox v : findAllByType(view, VBox.class)) {
            long labelCount = v.getChildren().stream()
                    .filter(c -> c instanceof Label).count();
            if (labelCount == 3 && v.getChildren().size() == 3) {
                cards.add(v);
            }
        }
        return cards;
    }

    // ---------- Structure ----------

    @Test
    @DisplayName("View is a BorderPane with sidebar on left and content in center")
    void testLayoutStructure() {
        assertInstanceOf(BorderPane.class, view);
        assertInstanceOf(VBox.class, view.getLeft(), "Sidebar (left) should be a VBox");
        assertInstanceOf(VBox.class, view.getCenter(), "Content (center) should be a VBox");
    }

    @Test
    @DisplayName("Sidebar has fixed preferred width of 158")
    void testSidebarWidth() {
        assertEquals(158, sidebar().getPrefWidth(), 0.01);
    }

    // ---------- Sidebar ----------

    @Test
    @DisplayName("Sidebar contains brand label 'Läsnäolo'")
    void testBrandLabel() {
        assertNotNull(labelWithText("Läsnäolo"));
    }

    @Test
    @DisplayName("Sidebar contains two circular avatar/logo elements")
    void testLogoAndAvatarCircles() {
        List<Circle> circles = findAllByType(view, Circle.class);
        // logo circle + avatar circle
        assertEquals(2, circles.size(), "Expected exactly two circles (logo + avatar)");
        circles.forEach(c -> assertEquals(10, c.getRadius(), 0.01));
    }

    @Test
    @DisplayName("Logo box contains 'LO' text")
    void testLogoText() {
        assertNotNull(labelWithText("LO"));
    }

    @Test
    @DisplayName("Avatar shows user initials 'MA'")
    void testAvatarInitials() {
        assertNotNull(labelWithText("MA"));
    }

    @Test
    @DisplayName("Sidebar shows user name and role")
    void testUserInfo() {
        assertNotNull(labelWithText("Etunimi Sukunimi"));
        assertNotNull(labelWithText("Opiskelija"));
    }

    @Test
    @DisplayName("Sidebar has 'Omat kurssit' navigation button")
    void testCoursesNavButton() {
        Button nav = buttonWithText("•   Omat kurssit");
        assertEquals(22, nav.getPrefHeight(), 0.01);
        assertEquals(Double.MAX_VALUE, nav.getMaxWidth(), 0.01);
        assertFalse(nav.isFocusTraversable(),
                "Nav button should not be focus-traversable");
    }

    // ---------- Content ----------

    @Test
    @DisplayName("Content heading 'Omat kurssit' is present")
    void testHeading() {
        assertNotNull(labelWithText("Omat kurssit"));
    }

    @Test
    @DisplayName("Intro text mentions 3 courses")
    void testIntro() {
        assertNotNull(labelWithText("Sinulla on 3 kurssia tällä lukukaudella."));
    }

    @Test
    @DisplayName("Content contains exactly 3 course cards in an HBox with 22px spacing")
    void testCourseCardsContainer() {
        List<VBox> cards = findCourseCards();
        assertEquals(3, cards.size(), "Exactly 3 course cards expected");

        HBox cardsBox = (HBox) cards.get(0).getParent();
        assertNotNull(cardsBox);
        assertEquals(3, cardsBox.getChildren().size());
        assertEquals(22, cardsBox.getSpacing(), 0.01);
    }

    @Test
    @DisplayName("Each course card has a code badge, name and hours label")
    void testCourseCardContent() {
        for (VBox card : findCourseCards()) {
            List<Label> labels = card.getChildren().stream()
                    .filter(n -> n instanceof Label)
                    .map(n -> (Label) n)
                    .toList();

            assertEquals(3, labels.size());
            assertEquals("TX00CV45", labels.get(0).getText(), "Course code");
            assertEquals("Ohjelmoinnin perusteet", labels.get(1).getText(), "Course name");
            assertEquals("5 oppituntia", labels.get(2).getText(), "Hours");
        }
    }

    @Test
    @DisplayName("Course card has fixed preferred size 190x75")
    void testCourseCardSize() {
        for (VBox card : findCourseCards()) {
            assertEquals(190, card.getPrefWidth(), 0.01);
            assertEquals(75, card.getPrefHeight(), 0.01);
        }
    }

    @Test
    @DisplayName("Course code badge has custom background style")
    void testCourseCodeBadgeStyle() {
        VBox card = findCourseCards().get(0);
        Label code = (Label) card.getChildren().get(0);
        String style = code.getStyle();
        assertTrue(style.contains("#D9F0FA"), "Badge background color missing");
        assertTrue(style.contains("-fx-background-radius"), "Badge rounding missing");
    }

    // ---------- Logout (hidden button) ----------

    /*
    @Test
    @DisplayName("Logout button exists, is invisible, and triggers callback")
    void testLogoutButtonTriggersCallback() {
        AtomicBoolean loggedOut = new AtomicBoolean(false);
        interact(() -> view = new StudentStartPage(() -> loggedOut.set(true)));

        Button logout = buttonWithText("Kirjaudu ulos");
        assertFalse(logout.isVisible(), "Logout button should start invisible");

        interact(logout::fire);
        assertTrue(loggedOut.get(), "onLogout callback should have been invoked");
    }*/

    @Test
    @DisplayName("Logout callback does not fire on construction")
    void testNoPrematureLogout() {
        AtomicBoolean loggedOut = new AtomicBoolean(false);
        interact(() -> view = new StudentStartPage(() -> loggedOut.set(true)));
        assertFalse(loggedOut.get());
    }

    // ---------- Invariants ----------

    /*
    @Test
    @DisplayName("Only one 'Kirjaudu ulos' button exists")
    void testSingleLogoutButton() {
        long count = findAllByType(view, Button.class).stream()
                .filter(b -> "Kirjaudu ulos".equals(b.getText()))
                .count();
        assertEquals(1, count);
    }

    @Test
    @DisplayName("Only one 'Omat kurssit' navigation button exists")
    void testSingleNavButton() {
        long count = findAllByType(view, Button.class).stream()
                .filter(b -> "•   Omat kurssit".equals(b.getText()))
                .count();
        assertEquals(1, count);
    }*/

    @Test
    @DisplayName("All course cards share the same size and content (data-driven invariant)")
    void testCardsAreUniform() {
        List<VBox> cards = findCourseCards();
        String firstText = cards.get(0).getChildren().stream()
                .filter(n -> n instanceof Label)
                .map(n -> ((Label) n).getText())
                .reduce("", (a, b) -> a + "|" + b);

        for (VBox card : cards) {
            String text = card.getChildren().stream()
                    .filter(n -> n instanceof Label)
                    .map(n -> ((Label) n).getText())
                    .reduce("", (a, b) -> a + "|" + b);
            assertEquals(firstText, text, "All cards should show the same demo data");
        }
    }
}
