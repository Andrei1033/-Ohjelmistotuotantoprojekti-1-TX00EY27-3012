package com.example.app.View;

import com.example.app.Localisation.FontSupport;
import com.example.app.Localisation.i18n;
import com.example.app.Model.LoginComponents.Role;
import com.example.app.Model.LoginComponents.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ViewSupportTest {

    private static boolean fxStarted = false;

    @BeforeAll
    void initJavaFX() throws Exception {
        if (fxStarted) return;
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException alreadyStarted) {
            latch.countDown();
        }
        assertTrue(latch.await(10, TimeUnit.SECONDS), "JavaFX did not start");
        fxStarted = true;
    }

    // ---- helper to run code on the FX thread and capture result/exceptions ----

    private <T> T onFx(java.util.function.Supplier<T> supplier) {
        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                result.set(supplier.get());
            } catch (Throwable t) {
                error.set(t);
            } finally {
                latch.countDown();
            }
        });
        try {
            assertTrue(latch.await(10, TimeUnit.SECONDS), "FX task timeout");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Interrupted", e);
        }
        if (error.get() != null) {
            throw new RuntimeException(error.get());
        }
        return result.get();
    }

    private void onFx(Runnable r) {
        onFx(() -> { r.run(); return null; });
    }

    // ------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------

    @Test
    void constantsAreDefined() {
        assertEquals("#202F49", ViewSupport.NAVY);
        assertEquals("#344A70", ViewSupport.BLUE);
    }

    // ------------------------------------------------------------------
    // text(...) and weightToCss(...)
    // ------------------------------------------------------------------

    @Test
    void textCreatesLabelWithGivenValueAndStyle() {
        Label label = onFx(() -> ViewSupport.text("Hello", 14, FontWeight.BOLD, "#FFFFFF"));
        assertNotNull(label);
        assertEquals("Hello", label.getText());
        String style = label.getStyle();
        assertTrue(style.contains("14.0px"), "expected font-size 14px in: " + style);
        assertTrue(style.contains("-fx-font-weight: bold"), "expected bold in: " + style);
        assertTrue(style.contains("#FFFFFF"), "expected color in: " + style);
    }

    @Test
    void textNormalWeightMapsToNormalCss() {
        Label label = onFx(() -> ViewSupport.text("x", 10, FontWeight.NORMAL, "#000000"));
        assertTrue(label.getStyle().contains("-fx-font-weight: normal"));
    }

    @Test
    void textNullValueProducesEmptyLabel() {
        Label label = onFx(() -> ViewSupport.text(null, 12, FontWeight.BOLD, "#000000"));
        assertNotNull(label);
        assertNull(label.getText());
    }

    @Test
    void weightToCssBold() {
        assertEquals("bold", ViewSupport.weightToCss(FontWeight.BOLD));
    }

    @Test
    void weightToCssNormal() {
        assertEquals("normal", ViewSupport.weightToCss(FontWeight.NORMAL));
    }

    // ------------------------------------------------------------------
    // initialsOf(...)
    // ------------------------------------------------------------------

    @Test
    void initialsOfNull() {
        assertEquals("", ViewSupport.initialsOf(null));
    }

    @Test
    void initialsOfFirstAndLast() {
        User u = makeUser("John", "Doe");
        assertEquals("JD", ViewSupport.initialsOf(u));
    }

    @Test
    void initialsOfLowercaseAreUppercased() {
        User u = makeUser("john", "doe");
        assertEquals("JD", ViewSupport.initialsOf(u));
    }

    @Test
    void initialsOfEmptyFirst() {
        User u = makeUser("", "Doe");
        assertEquals("D", ViewSupport.initialsOf(u));
    }

    @Test
    void initialsOfEmptyLast() {
        User u = makeUser("John", "");
        assertEquals("J", ViewSupport.initialsOf(u));
    }

    @Test
    void initialsOfNullNames() {
        User u = makeUser(null, null);
        assertEquals("", ViewSupport.initialsOf(u));
    }

    // ------------------------------------------------------------------
    // createLogoutButton(...)
    // ------------------------------------------------------------------

    @Test
    void createLogoutButtonRunsCallback() {
        AtomicBoolean clicked = new AtomicBoolean(false);
        Button btn = onFx(() -> ViewSupport.createLogoutButton(() -> clicked.set(true)));
        assertNotNull(btn);
        assertEquals(Cursor.HAND, btn.getCursor());
        onFx(btn::fire);
        assertTrue(clicked.get());
    }

    @Test
    void createLogoutButtonNullCallbackDoesNotThrow() {
        Button btn = onFx(() -> ViewSupport.createLogoutButton(null));
        assertNotNull(btn);
        assertDoesNotThrow(() -> onFx(btn::fire));
    }

    // ------------------------------------------------------------------
    // createBrandBox(...)
    // ------------------------------------------------------------------

    @Test
    void createBrandBoxHasTwoChildren() {
        HBox box = onFx(ViewSupport::createBrandBox);
        assertNotNull(box);
        assertEquals(2, box.getChildren().size());
    }

    // ------------------------------------------------------------------
    // createLanguageSelector(...)
    // ------------------------------------------------------------------

    @Test
    void createLanguageSelectorPopulated() {
        ComboBox<i18n.Lang> combo = onFx(() -> ViewSupport.createLanguageSelector(null));
        assertNotNull(combo);
        assertEquals(i18n.SUPPORTED.size(), combo.getItems().size());
        assertEquals(i18n.getCurrentLang(), combo.getValue());
        assertEquals(Cursor.HAND, combo.getCursor());
        assertFalse(combo.isFocusTraversable());
    }

    @Test
    void createLanguageSelectorConverterRoundTrips() {
        ComboBox<i18n.Lang> combo = onFx(() -> ViewSupport.createLanguageSelector(null));
        i18n.Lang value = i18n.SUPPORTED.get(0);
        assertEquals(value.label(), combo.getConverter().toString(value));
        assertNull(combo.getConverter().toString(null));
        assertNull(combo.getConverter().fromString("anything"));
    }

    // ------------------------------------------------------------------
    // createUserRow(...)
    // ------------------------------------------------------------------

    @Test
    void createUserRowShowsFullNameAndRole() {
        User u = makeUser("John", "Doe");
        HBox row = onFx(() -> ViewSupport.createUserRow(u, null, null));
        assertNotNull(row);
        assertEquals(2, row.getChildren().size());
        // userInfo VBox is second child
        javafx.scene.layout.VBox info = (javafx.scene.layout.VBox) row.getChildren().get(1);
        Label name = (Label) info.getChildren().get(0);
        Label role = (Label) info.getChildren().get(1);
        assertEquals(u.getFullName(), name.getText());
        assertFalse(role.getText().isEmpty());
    }

    @Test
    void createUserRowUsesRoleOverride() {
        User u = makeUser("John", "Doe");
        HBox row = onFx(() -> ViewSupport.createUserRow(u, "Custom Role", null));
        javafx.scene.layout.VBox info = (javafx.scene.layout.VBox) row.getChildren().get(1);
        Label role = (Label) info.getChildren().get(1);
        assertEquals("Custom Role", role.getText());
    }

    @Test
    void createUserRowNullUserDoesNotThrow() {
        HBox row = onFx(() -> ViewSupport.createUserRow(null, null, null));
        assertNotNull(row);
    }

    @Test
    void createUserRowCursorIsHand() {
        HBox row = onFx(() -> ViewSupport.createUserRow(makeUser("A", "B"), null, null));
        assertEquals(Cursor.HAND, row.getCursor());
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private static User makeUser(String first, String last) {
        return new User(1, first, last, "test@example.com", Role.STUDENT);
    }

    private static User makeUser(String first, String last, Role role) {
        User u = makeUser(first, last);
        return new User(1, first, last, "test@example.com", role);
    }
}