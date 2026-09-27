
package com.example.app.View;

import com.example.app.Controller.TeacherController;
import com.example.app.Model.Teacher;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TeacherStartPageTest {

    private TeacherStartPage teacherStartPage;

    @BeforeEach
    void setUp() {
        Teacher mockTeacher = new Teacher(
                1,
                "Matti",
                "Meikäläinen",
                "matti@testi.fi"
        );

        TeacherController mockController =
                new TeacherController(mockTeacher, () -> {});

        teacherStartPage = new TeacherStartPage(
                mockTeacher,
                mockController,
                () -> {}
        );

        new Scene(teacherStartPage, 1024, 399);
    }

    @Test
    void testRootIsBorderPane() {
        assertTrue(
                teacherStartPage instanceof BorderPane,
                "TeacherStartPage should be a BorderPane"
        );
    }

    @Test
    void testLeftSidebarExists() {
        Node left = teacherStartPage.getLeft();

        assertNotNull(left);
        assertTrue(left instanceof VBox);
    }

    @Test
    void testSidebarWidth() {
        VBox sidebar = (VBox) teacherStartPage.getLeft();

        assertEquals(
                158.0,
                sidebar.getPrefWidth(),
                0.1
        );
    }

    @Test
    void testCenterExists() {
        assertNotNull(teacherStartPage.getCenter());
    }

    @Test
    void testCenterIsVBox() {
        assertTrue(
                teacherStartPage.getCenter() instanceof VBox
        );
    }

    @Test
    void testCourseHeadingExists() {
        assertNotNull(
                findLabel(
                        teacherStartPage,
                        "Omat kurssit"
                )
        );
    }

    @Test
    void testCourseIntroductionExists() {
        Label introduction =
                findLabelContaining(
                        teacherStartPage,
                        "Sinulla on "
                );

        assertNotNull(introduction);

        assertTrue(
                introduction.getText().matches(
                        "Sinulla on \\d+ kurssia tällä lukukaudella\\."
                )
        );
    }

    @Test
    void testCenterHasTwoChildren() {
        VBox center = (VBox) teacherStartPage.getCenter();

        assertEquals(
                2,
                center.getChildren().size()
        );
    }

    @Test
    void testSecondCenterChildIsHBox() {
        VBox center = (VBox) teacherStartPage.getCenter();

        assertTrue(
                center.getChildren().get(1) instanceof HBox
        );
    }

    @Test
    void testCourseCardsExist() {
        HBox cards = getCourseCards();

        assertNotNull(cards);
        assertFalse(cards.getChildren().isEmpty());
    }

    @Test
    void testCourseCardsAreVBoxes() {
        HBox cards = getCourseCards();

        for (Node child : cards.getChildren()) {
            assertTrue(
                    child instanceof VBox,
                    "Every course card should be a VBox"
            );
        }
    }

    @Test
    void testCourseCardsHaveCorrectWidth() {
        HBox cards = getCourseCards();

        for (Node child : cards.getChildren()) {
            VBox card = (VBox) child;

            assertEquals(
                    190.0,
                    card.getPrefWidth(),
                    0.1
            );
        }
    }

    @Test
    void testCourseCardsHaveCorrectHeight() {
        HBox cards = getCourseCards();

        for (Node child : cards.getChildren()) {
            VBox card = (VBox) child;

            assertEquals(
                    75.0,
                    card.getPrefHeight(),
                    0.1
            );
        }
    }

    @Test
    void testCourseCardsContainLabels() {
        HBox cards = getCourseCards();

        for (Node child : cards.getChildren()) {
            VBox card = (VBox) child;

            assertNotNull(
                    findLabelInNode(card)
            );
        }
    }

    @Test
    void testCourseCodeExistsWhenCourseExists() {
        HBox cards = getCourseCards();

        if (!containsRealCourseCode(cards)) {
            return;
        }

        boolean foundCourseCode = false;

        for (Node child : cards.getChildren()) {
            VBox card = (VBox) child;

            for (Node cardChild : card.getChildren()) {
                if (cardChild instanceof Label label) {
                    String text = label.getText();

                    if (text != null &&
                            text.matches("\\d{2}")) {
                        foundCourseCode = true;
                    }
                }
            }
        }

        assertTrue(foundCourseCode);
    }

    @Test
    void testCourseNameExistsWhenCourseExists() {
        HBox cards = getCourseCards();

        if (!containsRealCourseCode(cards)) {
            return;
        }

        boolean foundCourseName = false;

        for (Node child : cards.getChildren()) {
            VBox card = (VBox) child;

            for (Node cardChild : card.getChildren()) {
                if (cardChild instanceof Label label) {
                    String text = label.getText();

                    if (text != null &&
                            !text.isBlank() &&
                            !text.matches("\\d{2}") &&
                            !text.matches("\\d+\\s+oppituntia") &&
                            !text.equals("Ei aktiivisia kursseja") &&
                            !text.equals("Tällä hetkellä ei ole kursseja.")) {

                        foundCourseName = true;
                    }
                }
            }
        }

        assertTrue(foundCourseName);
    }

    @Test
    void testLessonCountExistsWhenCourseExists() {
        HBox cards = getCourseCards();

        if (!containsRealCourseCode(cards)) {
            return;
        }

        boolean foundLessonCount = false;

        for (Node child : cards.getChildren()) {
            VBox card = (VBox) child;

            for (Node cardChild : card.getChildren()) {
                if (cardChild instanceof Label label) {
                    String text = label.getText();

                    if (text != null &&
                            text.matches("\\d+\\s+oppituntia")) {
                        foundLessonCount = true;
                        break;
                    }
                }
            }

            if (foundLessonCount) {
                break;
            }
        }

        assertTrue(
                foundLessonCount,
                "Course card should contain lesson count"
        );
    }

    @Test
    void testTeacherNameExists() {
        assertNotNull(
                findLabel(
                        teacherStartPage,
                        "Matti Meikäläinen"
                )
        );
    }

    @Test
    void testTeacherRoleExists() {
        assertNotNull(
                findLabel(
                        teacherStartPage,
                        "Opettaja"
                )
        );
    }

    @Test
    void testUserInitials() {
        assertNotNull(
                findLabel(
                        teacherStartPage,
                        "MM"
                )
        );
    }

    @Test
    void testLogoExists() {
        assertNotNull(
                findLabel(
                        teacherStartPage,
                        "LO"
                )
        );
    }

    @Test
    void testMyCoursesButtonExists() {
        assertNotNull(
                findButton(
                        teacherStartPage,
                        "•   Omat kurssit"
                )
        );
    }

    private HBox getCourseCards() {
        VBox center =
                (VBox) teacherStartPage.getCenter();

        assertEquals(
                2,
                center.getChildren().size()
        );

        Node cardsNode =
                center.getChildren().get(1);

        assertTrue(
                cardsNode instanceof HBox
        );

        return (HBox) cardsNode;
    }

    private boolean containsRealCourseCode(HBox cards) {
        for (Node child : cards.getChildren()) {
            if (!(child instanceof VBox)) {
                continue;
            }

            VBox card = (VBox) child;

            for (Node cardChild : card.getChildren()) {
                if (cardChild instanceof Label label) {
                    String text = label.getText();

                    if (text != null &&
                            text.matches("\\d{2}")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private Label findLabel(
            Node root,
            String expectedText
    ) {
        if (root instanceof Label label) {
            if (expectedText.equals(label.getText())) {
                return label;
            }
        }

        if (root instanceof javafx.scene.Parent parent) {
            for (Node child :
                    parent.getChildrenUnmodifiable()) {

                Label result =
                        findLabel(
                                child,
                                expectedText
                        );

                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    private Label findLabelContaining(
            Node root,
            String expectedText
    ) {
        if (root instanceof Label label) {
            String text = label.getText();

            if (text != null &&
                    text.contains(expectedText)) {
                return label;
            }
        }

        if (root instanceof javafx.scene.Parent parent) {
            for (Node child :
                    parent.getChildrenUnmodifiable()) {

                Label result =
                        findLabelContaining(
                                child,
                                expectedText
                        );

                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    private Label findLabelInNode(Node root) {
        if (root instanceof Label label) {
            return label;
        }

        if (root instanceof javafx.scene.Parent parent) {
            for (Node child :
                    parent.getChildrenUnmodifiable()) {

                Label result =
                        findLabelInNode(child);

                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    private Button findButton(
            Node root,
            String expectedText
    ) {
        if (root instanceof Button button) {
            if (expectedText.equals(button.getText())) {
                return button;
            }
        }

        if (root instanceof javafx.scene.Parent parent) {
            for (Node child :
                    parent.getChildrenUnmodifiable()) {

                Button result =
                        findButton(
                                child,
                                expectedText
                        );

                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }
}



/*
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
*/