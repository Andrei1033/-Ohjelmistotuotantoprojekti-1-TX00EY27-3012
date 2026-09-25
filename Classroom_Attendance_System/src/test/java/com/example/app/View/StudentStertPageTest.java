package com.example.app.View;

import com.example.app.Model.LoginComponents.User;
import com.example.app.Model.LoginComponents.Role;
import com.example.app.Model.StudentComponents.Course;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class StudentStartPageTest extends ApplicationTest {


    private StudentStartPage view;
    private User testUser;
    private List<Course> testCourses;
    private AtomicReference<Course> selectedCourse;
    private AtomicBoolean loggedOut;
    private Stage stage;

    @Override
    public void start(javafx.stage.Stage stage) {
        // Stage luodaan jokaisessa testissä setUp()-metodissa.
    }

    @BeforeEach
    void setUp() {
        testUser = new User(
                1,
                "Etunimi",
                "Sukunimi",
                "etunimi.sukunimi@example.com",
                Role.STUDENT
        );

        Course c1 = new Course(
                1,
                "TX00CV45",
                "Ohjelmoinnin perusteet",
                10
        );
        c1.setLessonCount(5);

        Course c2 = new Course(
                2,
                "TX00CV46",
                "Tietokannat",
                10
        );
        c2.setLessonCount(4);

        Course c3 = new Course(
                3,
                "TX00CV47",
                "Web-sovellukset",
                10
        );
        c3.setLessonCount(3);

        testCourses = List.of(c1, c2, c3);

        selectedCourse = new AtomicReference<>();
        loggedOut = new AtomicBoolean(false);

        interact(() -> {
            view = new StudentStartPage(
                    testUser,
                    testCourses,
                    selectedCourse::set,
                    () -> loggedOut.set(true)
            );

            stage = new Stage();
            stage.setScene(new Scene(view, 800, 600));
            stage.show();
        });
    }

    @AfterEach
    void tearDown() {
        interact(() -> {
            if (stage != null) {
                stage.close();
                stage = null;
            }
        });
    }

// =========================================================
// Helpers
// =========================================================

    private List<Node> allNodes(Parent root) {
        return new ArrayList<>(root.lookupAll("*"));
    }

    private <T extends Node> List<T> findAllByType(
            Parent root,
            Class<T> type
    ) {
        List<T> result = new ArrayList<>();

        for (Node node : allNodes(root)) {
            if (type.isInstance(node)) {
                result.add(type.cast(node));
            }
        }

        return result;
    }

    private <T extends Node> T findByType(
            Parent root,
            Class<T> type
    ) {
        return findAllByType(root, type)
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError(
                                "Not found: " + type.getSimpleName()
                        )
                );
    }

    private Label labelWithText(String text) {
        return findAllByType(view, Label.class)
                .stream()
                .filter(label -> text.equals(label.getText()))
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError(
                                "Label not found: " + text
                        )
                );
    }

    private Button buttonWithText(String text) {
        return findAllByType(view, Button.class)
                .stream()
                .filter(button -> text.equals(button.getText()))
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError(
                                "Button not found: " + text
                        )
                );
    }

    private VBox sidebar() {
        Node left = view.getLeft();

        assertNotNull(
                left,
                "Left (sidebar) must be set"
        );

        assertInstanceOf(
                VBox.class,
                left
        );

        return (VBox) left;
    }

    private VBox content() {
        Node center = view.getCenter();

        assertNotNull(
                center,
                "Center (content) must be set"
        );

        assertInstanceOf(
                VBox.class,
                center
        );

        return (VBox) center;
    }

    /**
     * Extracts all course cards.
     *
     * A course card is a VBox with exactly
     * three Label children.
     */
    private List<VBox> findCourseCards() {
        List<VBox> cards = new ArrayList<>();

        for (VBox v : findAllByType(view, VBox.class)) {

            long labelCount = v.getChildren()
                    .stream()
                    .filter(c -> c instanceof Label)
                    .count();

            if (labelCount == 3 && v.getChildren().size() == 3) {
                cards.add(v);
            }
        }

        return cards;
    }

    /**
     * Fires the MOUSE_CLICKED event directly on the course card.
     *
     * This avoids depending on TestFX mouse coordinates,
     * ScrollPane positioning and JavaFX rendering in Jenkins.
     */
    private void clickCard(VBox card) {
        interact(() -> {
            MouseEvent event = new MouseEvent(
                    MouseEvent.MOUSE_CLICKED,
                    0,
                    0,
                    0,
                    0,
                    MouseButton.PRIMARY,
                    1,
                    false,
                    false,
                    false,
                    false,
                    true,
                    false,
                    false,
                    false,
                    false,
                    false,
                    null
            );

            card.fireEvent(event);
        });
    }

// =========================================================
// Structure
// =========================================================

    @Test
    @DisplayName(
            "View is a BorderPane with sidebar on left and content in center"
    )
    void testLayoutStructure() {
        assertInstanceOf(BorderPane.class, view);

        assertInstanceOf(
                VBox.class,
                view.getLeft(),
                "Sidebar (left) should be a VBox"
        );

        assertInstanceOf(
                VBox.class,
                view.getCenter(),
                "Content (center) should be a VBox"
        );
    }

    @Test
    @DisplayName("Sidebar has fixed preferred width of 158")
    void testSidebarWidth() {
        assertEquals(
                158,
                sidebar().getPrefWidth(),
                0.01
        );
    }

// =========================================================
// Sidebar
// =========================================================

    @Test
    @DisplayName("Sidebar contains brand label 'Läsnäolo'")
    void testBrandLabel() {
        assertNotNull(
                labelWithText("Läsnäolo")
        );
    }

    @Test
    @DisplayName(
            "Sidebar contains two circular avatar/logo elements"
    )
    void testLogoAndAvatarCircles() {

        List<Circle> circles =
                findAllByType(view, Circle.class);

        // Logo circle + avatar circle
        assertEquals(
                2,
                circles.size(),
                "Expected exactly two circles (logo + avatar)"
        );

        circles.forEach(circle ->
                assertEquals(
                        10,
                        circle.getRadius(),
                        0.01
                )
        );
    }

    @Test
    @DisplayName("Logo box contains 'LO' text")
    void testLogoText() {
        assertNotNull(
                labelWithText("LO")
        );
    }

    @Test
    @DisplayName("Avatar shows user initials 'ES'")
    void testAvatarInitials() {
        assertNotNull(
                labelWithText("ES")
        );
    }

    @Test
    @DisplayName("Sidebar shows user name and role")
    void testUserInfo() {

        assertNotNull(
                labelWithText("Etunimi Sukunimi")
        );

        assertNotNull(
                labelWithText("Opiskelija")
        );
    }

    @Test
    @DisplayName(
            "Back button exists but is hidden and not managed"
    )
    void testBackButtonHidden() {

        Button back =
                buttonWithText("<   Takaisin");

        assertFalse(
                back.isVisible(),
                "Back button should be invisible on start page"
        );

        assertFalse(
                back.isManaged(),
                "Back button should not be managed on start page"
        );

        assertFalse(
                back.isFocusTraversable(),
                "Back button should not be focus-traversable"
        );
    }

// =========================================================
// Content
// =========================================================

    @Test
    @DisplayName(
            "Content heading 'Omat kurssit' is present"
    )
    void testHeading() {
        assertNotNull(
                labelWithText("Omat kurssit")
        );
    }

    @Test
    @DisplayName(
            "Intro text mentions 3 courses"
    )
    void testIntro() {
        assertNotNull(
                labelWithText(
                        "Sinulla on 3 kurssia tällä lukukaudella."
                )
        );
    }

    @Test
    @DisplayName(
            "Empty course list shows placeholder text and no FlowPane"
    )
    void testEmptyCourseList() {

        interact(() ->
                view = new StudentStartPage(
                        testUser,
                        List.of(),
                        selectedCourse::set,
                        () -> {
                        }
                )
        );

        assertNotNull(
                labelWithText(
                        "Sinulla ei ole vielä kursseja."
                )
        );

        assertTrue(
                findAllByType(view, FlowPane.class).isEmpty(),
                "No FlowPane should exist for empty course list"
        );

        assertTrue(
                findAllByType(view, ScrollPane.class).isEmpty(),
                "No ScrollPane should exist for empty course list"
        );
    }

    @Test
    @DisplayName(
            "Content contains exactly 3 course cards in a FlowPane with 22px gaps"
    )
    void testCourseCardsContainer() {

        List<VBox> cards =
                findCourseCards();

        assertEquals(
                3,
                cards.size(),
                "Exactly 3 course cards expected"
        );

        FlowPane cardsPane =
                (FlowPane) cards.get(0).getParent();

        assertNotNull(cardsPane);

        assertEquals(
                3,
                cardsPane.getChildren().size()
        );

        assertEquals(
                22,
                cardsPane.getHgap(),
                0.01
        );

        assertEquals(
                22,
                cardsPane.getVgap(),
                0.01
        );
    }

    @Test
    @DisplayName(
            "Course cards are wrapped in a ScrollPane with fitToWidth=true"
    )
    void testScrollPaneWrapsCards() {

        ScrollPane scroll =
                findByType(view, ScrollPane.class);

        assertTrue(
                scroll.isFitToWidth(),
                "ScrollPane should fit to width"
        );

        Node inner = scroll.getContent();

        assertInstanceOf(
                FlowPane.class,
                inner
        );
    }

    @Test
    @DisplayName(
            "Each course card has a code badge, name and hours label (data-driven)"
    )
    void testCourseCardContent() {

        List<VBox> cards =
                findCourseCards();

        assertEquals(
                testCourses.size(),
                cards.size()
        );

        for (int i = 0; i < cards.size(); i++) {

            Course expected =
                    testCourses.get(i);

            List<Label> labels =
                    cards.get(i)
                            .getChildren()
                            .stream()
                            .filter(n -> n instanceof Label)
                            .map(n -> (Label) n)
                            .toList();

            assertEquals(
                    3,
                    labels.size()
            );

            assertEquals(
                    expected.getCode(),
                    labels.get(0).getText(),
                    "Course code"
            );

            assertEquals(
                    expected.getName(),
                    labels.get(1).getText(),
                    "Course name"
            );

            assertEquals(
                    expected.getLessonCount() + " oppituntia",
                    labels.get(2).getText(),
                    "Hours"
            );
        }
    }

    @Test
    @DisplayName(
            "Course card has fixed preferred size 190x75"
    )
    void testCourseCardSize() {

        for (VBox card : findCourseCards()) {

            assertEquals(
                    190,
                    card.getPrefWidth(),
                    0.01
            );

            assertEquals(
                    75,
                    card.getPrefHeight(),
                    0.01
            );
        }
    }

    @Test
    @DisplayName(
            "Course code badge has custom background style"
    )
    void testCourseCodeBadgeStyle() {

        VBox card =
                findCourseCards().get(0);

        Label code =
                (Label) card.getChildren().get(0);

        String style =
                code.getStyle();

        assertTrue(
                style.contains("#D9F0FA"),
                "Badge background color missing"
        );

        assertTrue(
                style.contains("-fx-background-radius"),
                "Badge rounding missing"
        );
    }

// =========================================================
// Interaction
// =========================================================

    @Test
    @DisplayName(
            "Clicking a course card triggers onCourseSelected with that course"
    )
    void testCourseCardClickFiresCallback() {

        List<VBox> cards =
                findCourseCards();

        VBox firstCard =
                cards.get(0);

        // Ensure no course is selected before the click
        assertNull(
                selectedCourse.get(),
                "No course should be selected before clicking"
        );

        // Fire the click directly on the card.
        // This is more reliable in Jenkins than clickOn().
        clickCard(firstCard);

        assertEquals(
                testCourses.get(0),
                selectedCourse.get(),
                "Clicking first card should select the first course"
        );
    }

    @Test
    @DisplayName(
            "Clicking the second card selects the second course"
    )
    void testSecondCourseCardClick() {

        List<VBox> cards =
                findCourseCards();

        clickCard(cards.get(1));

        assertEquals(
                testCourses.get(1),
                selectedCourse.get()
        );
    }

// =========================================================
// Logout
// =========================================================

    @Test
    @DisplayName(
            "Logout button exists, is invisible, and triggers callback when fired"
    )
    void testLogoutButtonTriggersCallback() {

        Button logout =
                buttonWithText("Kirjaudu ulos");

        assertFalse(
                logout.isVisible(),
                "Logout button should start invisible"
        );

        assertFalse(
                logout.isManaged(),
                "Logout button should not be managed"
        );

        interact(logout::fire);

        assertTrue(
                loggedOut.get(),
                "onLogout callback should have been invoked"
        );
    }

    @Test
    @DisplayName(
            "Logout callback does not fire on construction"
    )
    void testNoPrematureLogout() {

        assertFalse(
                loggedOut.get(),
                "onLogout should not be invoked during construction"
        );
    }

// =========================================================
// Invariants
// =========================================================

    @Test
    @DisplayName(
            "Only one 'Kirjaudu ulos' button exists"
    )
    void testSingleLogoutButton() {

        long count =
                findAllByType(view, Button.class)
                        .stream()
                        .filter(button ->
                                "Kirjaudu ulos".equals(button.getText()))
                        .count();

        assertEquals(
                1,
                count
        );
    }

    @Test
    @DisplayName(
            "Only one '<   Takaisin' button exists"
    )
    void testSingleBackButton() {

        long count =
                findAllByType(view, Button.class)
                        .stream()
                        .filter(button ->
                                "<   Takaisin".equals(button.getText()))
                        .count();

        assertEquals(
                1,
                count
        );
    }

    @Test
    @DisplayName(
            "All course cards share the same size (uniform layout invariant)"
    )
    void testCardsAreUniformInSize() {

        List<VBox> cards =
                findCourseCards();

        for (VBox card : cards) {

            assertEquals(
                    190,
                    card.getPrefWidth(),
                    0.01
            );

            assertEquals(
                    75,
                    card.getPrefHeight(),
                    0.01
            );

            assertEquals(
                    3,
                    card.getChildren().size()
            );
        }
    }


}

/*
package com.example.app.View;

import com.example.app.Model.LoginComponents.User;
import com.example.app.Model.LoginComponents.Role;
import com.example.app.Model.StudentComponents.Course;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class StudentStartPageTest extends ApplicationTest {

    private StudentStartPage view;
    private User testUser;
    private List<Course> testCourses;
    private AtomicReference<Course> selectedCourse;
    private AtomicBoolean loggedOut;
    private Stage stage;

    @Override
    public void start(javafx.stage.Stage stage) {
    }

    @BeforeEach
    void setUp() {
        testUser = new User(1, "Etunimi", "Sukunimi", "etunimi.sukunimi@example.com", Role.STUDENT);

        Course c1 = new Course(1, "TX00CV45", "Ohjelmoinnin perusteet", 10); c1.setLessonCount(5);
        Course c2 = new Course(2, "TX00CV46", "Tietokannat",            10); c2.setLessonCount(4);
        Course c3 = new Course(3, "TX00CV47", "Web-sovellukset",        10); c3.setLessonCount(3);
        testCourses = List.of(c1, c2, c3);

        selectedCourse = new AtomicReference<>();
        loggedOut = new AtomicBoolean(false);

        interact(() -> {
            view = new StudentStartPage(testUser, testCourses,
                    selectedCourse::set, () -> loggedOut.set(true));

            stage = new Stage();
            stage.setScene(new Scene(view, 800, 600));
            stage.show();
        });
    }

    @AfterEach
    void tearDown() {
        interact(() -> {
            if (stage != null) {
                stage.close();
                stage = null;
            }
        });
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

    /** Extracts all course cards: VBox with exactly 3 Label children and matching pref size.
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
    @DisplayName("Avatar shows user initials 'ES'")
    void testAvatarInitials() {
        assertNotNull(labelWithText("ES"));
    }

    @Test
    @DisplayName("Sidebar shows user name and role")
    void testUserInfo() {
        assertNotNull(labelWithText("Etunimi Sukunimi"));
        assertNotNull(labelWithText("Opiskelija"));
    }

    @Test
    @DisplayName("Back button exists but is hidden and not managed")
    void testBackButtonHidden() {
        Button back = buttonWithText("<   Takaisin");
        assertFalse(back.isVisible(), "Back button should be invisible on start page");
        assertFalse(back.isManaged(), "Back button should not be managed on start page");
        assertFalse(back.isFocusTraversable(), "Back button should not be focus-traversable");
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
    @DisplayName("Empty course list shows placeholder text and no FlowPane")
    void testEmptyCourseList() {
        interact(() -> view = new StudentStartPage(
                testUser, List.of(), selectedCourse::set, () -> {}));

        assertNotNull(labelWithText("Sinulla ei ole vielä kursseja."));
        assertTrue(findAllByType(view, FlowPane.class).isEmpty(),
                "No FlowPane should exist for empty course list");
        assertTrue(findAllByType(view, ScrollPane.class).isEmpty(),
                "No ScrollPane should exist for empty course list");
    }

    @Test
    @DisplayName("Content contains exactly 3 course cards in a FlowPane with 22px gaps")
    void testCourseCardsContainer() {
        List<VBox> cards = findCourseCards();
        assertEquals(3, cards.size(), "Exactly 3 course cards expected");

        FlowPane cardsPane = (FlowPane) cards.get(0).getParent();
        assertNotNull(cardsPane);
        assertEquals(3, cardsPane.getChildren().size());
        assertEquals(22, cardsPane.getHgap(), 0.01);
        assertEquals(22, cardsPane.getVgap(), 0.01);
    }

    @Test
    @DisplayName("Course cards are wrapped in a ScrollPane with fitToWidth=true")
    void testScrollPaneWrapsCards() {
        ScrollPane scroll = findByType(view, ScrollPane.class);
        assertTrue(scroll.isFitToWidth(), "ScrollPane should fit to width");

        Node inner = scroll.getContent();
        assertInstanceOf(FlowPane.class, inner);
    }

    @Test
    @DisplayName("Each course card has a code badge, name and hours label (data-driven)")
    void testCourseCardContent() {
        List<VBox> cards = findCourseCards();
        assertEquals(testCourses.size(), cards.size());

        for (int i = 0; i < cards.size(); i++) {
            Course expected = testCourses.get(i);
            List<Label> labels = cards.get(i).getChildren().stream()
                    .filter(n -> n instanceof Label)
                    .map(n -> (Label) n)
                    .toList();

            assertEquals(3, labels.size());
            assertEquals(expected.getCode(), labels.get(0).getText(), "Course code");
            assertEquals(expected.getName(), labels.get(1).getText(), "Course name");
            assertEquals(expected.getLessonCount() + " oppituntia",
                    labels.get(2).getText(), "Hours");
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

    // ---------- Interaction ----------

    @Test
    @DisplayName("Clicking a course card triggers onCourseSelected with that course")
    void testCourseCardClickFiresCallback() {
        List<VBox> cards = findCourseCards();
        VBox firstCard = cards.get(0);

        // Ensure no course is selected before the click
        assertNull(selectedCourse.get(), "No course should be selected before clicking");

        // Real mouse click — TestFX moves the pointer and fires MOUSE_CLICKED
        clickOn(firstCard);

        assertEquals(testCourses.get(0), selectedCourse.get(),
                "Clicking first card should select the first course");
    }

    @Test
    @DisplayName("Clicking the second card selects the second course")
    void testSecondCourseCardClick() {
        List<VBox> cards = findCourseCards();
        clickOn(cards.get(1));
        assertEquals(testCourses.get(1), selectedCourse.get());
    }

    // ---------- Logout (hidden button) ----------

    @Test
    @DisplayName("Logout button exists, is invisible, and triggers callback when fired")
    void testLogoutButtonTriggersCallback() {
        Button logout = buttonWithText("Kirjaudu ulos");
        assertFalse(logout.isVisible(), "Logout button should start invisible");
        assertFalse(logout.isManaged(), "Logout button should not be managed");

        interact(logout::fire);
        assertTrue(loggedOut.get(), "onLogout callback should have been invoked");
    }

    @Test
    @DisplayName("Logout callback does not fire on construction")
    void testNoPrematureLogout() {
        assertFalse(loggedOut.get(),
                "onLogout should not be invoked during construction");
    }

    // ---------- Invariants ----------

    @Test
    @DisplayName("Only one 'Kirjaudu ulos' button exists")
    void testSingleLogoutButton() {
        long count = findAllByType(view, Button.class).stream()
                .filter(b -> "Kirjaudu ulos".equals(b.getText()))
                .count();
        assertEquals(1, count);
    }

    @Test
    @DisplayName("Only one '<   Takaisin' button exists")
    void testSingleBackButton() {
        long count = findAllByType(view, Button.class).stream()
                .filter(b -> "<   Takaisin".equals(b.getText()))
                .count();
        assertEquals(1, count);
    }

    @Test
    @DisplayName("All course cards share the same size (uniform layout invariant)")
    void testCardsAreUniformInSize() {
        List<VBox> cards = findCourseCards();
        for (VBox card : cards) {
            assertEquals(190, card.getPrefWidth(), 0.01);
            assertEquals(75, card.getPrefHeight(), 0.01);
            assertEquals(3, card.getChildren().size());
        }
    }
}*/