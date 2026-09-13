package com.example.app.View;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class LoginTest extends ApplicationTest {

    private Login loginView;

    @Override
    public void start(javafx.stage.Stage stage) {
        // Required by ApplicationTest but we build the view manually in tests
    }

    @BeforeEach
    void setUp() {
        // Must be on JavaFX thread
        interact(() -> loginView = new Login(() -> {}));
    }

    // ---------- Helpers ----------

    private <T extends Node> T findByType(javafx.scene.Parent root, Class<T> type) {
        for (Node n : root.lookupAll("*")) {
            if (type.isInstance(n)) return type.cast(n);
        }
        return null;
    }

    private List<Node> allNodes(javafx.scene.Parent root) {
        List<Node> nodes = new ArrayList<>();
        for (Node n : root.lookupAll("*")) nodes.add(n);
        return nodes;
    }

    private <T extends Node> List<T> findAllByType(javafx.scene.Parent root, Class<T> type) {
        List<T> result = new ArrayList<>();
        for (Node n : allNodes(root)) {
            if (type.isInstance(n)) result.add(type.cast(n));
        }
        return result;
    }

    private Button findLoginButton() {
        return findAllByType(loginView, Button.class).stream()
                .filter(b -> "Kirjaudu".equals(b.getText()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Login button not found"));
    }

    private TextField findEmailField() {
        return findAllByType(loginView, TextField.class).stream()
                .filter(f -> !(f instanceof PasswordField))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Email field not found"));
    }

    private PasswordField findPasswordField() {
        return findAllByType(loginView, PasswordField.class).stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("Password field not found"));
    }

    private Label findLabelByText(String text) {
        return findAllByType(loginView, Label.class).stream()
                .filter(l -> text.equals(l.getText()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Label not found: " + text));
    }

    // ---------- Tests ----------

    @Test
    @DisplayName("Login view is a BorderPane with a centered card")
    void testLayoutStructure() {
        assertInstanceOf(BorderPane.class, loginView, "Login should extend BorderPane");
        assertNotNull(loginView.getCenter(), "Center node should be set");
        assertInstanceOf(StackPane.class, loginView.getCenter(), "Center should be a StackPane");
    }

    @Test
    @DisplayName("Card contains a VBox with all expected children")
    void testCardContent() {
        StackPane center = (StackPane) loginView.getCenter();
        assertFalse(center.getChildren().isEmpty(), "Card should have children");

        Node card = center.getChildren().get(0);
        assertInstanceOf(VBox.class, card, "Card should be a VBox");

        VBox cardBox = (VBox) card;
        // logoBox, header, form, spacer, bottom
        assertEquals(5, cardBox.getChildren().size(),
                "Card should have 5 top-level sections");
    }

    @Test
    @DisplayName("Title and subtitle labels use correct text")
    void testTitleAndSubtitle() {
        assertEquals("Kirjaudu sisään", findLabelByText("Kirjaudu sisään").getText());
        assertNotNull(findLabelByText("Läsnäolojärjestelmä"));
    }

    @Test
    @DisplayName("Email field is pre-filled with default value")
    void testEmailFieldDefault() {
        TextField email = findEmailField();
        assertEquals("example@example.edu", email.getPromptText());
        assertNotNull(email.getText(), "Email text should not be null");
    }

    @Test
    @DisplayName("Password field has placeholder and hides text")
    void testPasswordField() {
        PasswordField pw = findPasswordField();
        assertEquals("--------", pw.getPromptText());
        assertNotNull(pw.getText(), "Password text should not be null");
    }

    @Test
    @DisplayName("Login button has correct text and triggers callback")
    void testLoginButtonTriggersCallback() {
        AtomicBoolean clicked = new AtomicBoolean(false);

        // Rebuild view with a callback we can observe
        interact(() -> loginView = new Login(() -> clicked.set(true)));

        Button loginBtn = findLoginButton();
        assertEquals("Kirjaudu", loginBtn.getText());

        interact(loginBtn::fire);
        assertTrue(clicked.get(), "onLogin callback should have been invoked");
    }

    @Test
    @DisplayName("Login button calls callback exactly once per click")
    void testLoginButtonClickCount() {
        final int[] count = {0};
        interact(() -> loginView = new Login(() -> count[0]++));

        Button loginBtn = findLoginButton();
        interact(loginBtn::fire);
        interact(loginBtn::fire);
        interact(loginBtn::fire);

        assertEquals(3, count[0], "Callback should fire once per click");
    }

    @Test
    @DisplayName("Callback is not invoked without interaction")
    void testNoPrematureCallback() {
        AtomicBoolean clicked = new AtomicBoolean(false);
        interact(() -> loginView = new Login(() -> clicked.set(true)));
        assertFalse(clicked.get(), "Callback must not run just by constructing the view");
    }

    @Test
    @DisplayName("Forgot-password label is present with correct text")
    void testForgotPasswordLabel() {
        Label forgot = findLabelByText("Unohtuiko salasana? Ota yhteyttä opettajaan");
        assertNotNull(forgot);
    }

    @Test
    @DisplayName("There is exactly one login button")
    void testSingleLoginButton() {
        long buttonCount = findAllByType(loginView, Button.class).stream()
                .filter(b -> "Kirjaudu".equals(b.getText()))
                .count();
        assertEquals(1, buttonCount);
    }

    @Test
    @DisplayName("There is exactly one email field and one password field")
    void testSingleFields() {
        List<TextField> plainTextFields = findAllByType(loginView, TextField.class).stream()
                .filter(f -> !(f instanceof PasswordField))
                .toList();
        List<PasswordField> passwordFields = findAllByType(loginView, PasswordField.class);

        assertEquals(1, plainTextFields.size(), "Exactly one email TextField expected");
        assertEquals(1, passwordFields.size(), "Exactly one PasswordField expected");
    }

    @Test
    @DisplayName("Email and password fields can be edited")
    void testFieldEditing() {
        TextField email = findEmailField();
        PasswordField pw = findPasswordField();

        interact(() -> {
            email.setText("test@example.com");
            pw.setText("secret123");
        });

        assertEquals("test@example.com", email.getText());
        assertEquals("secret123", pw.getText());
    }

    @Test
    @DisplayName("Card has fixed preferred size 292x332")
    void testCardSize() {
        StackPane center = (StackPane) loginView.getCenter();
        VBox card = (VBox) center.getChildren().get(0);
        assertEquals(292, card.getPrefWidth(), 0.01);
        assertEquals(332, card.getPrefHeight(), 0.01);
    }
}