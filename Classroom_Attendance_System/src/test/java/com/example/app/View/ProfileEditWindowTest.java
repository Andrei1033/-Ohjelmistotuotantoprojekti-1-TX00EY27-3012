package com.example.app.View;

import com.example.app.DaoElements.LoginDao.UserDao;
import com.example.app.Localisation.i18n;
import com.example.app.Model.LoginComponents.Role;
import com.example.app.Model.LoginComponents.User;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileEditWindowTest extends ViewTestSupport {
    private User user;
    private UserDao dao;
    private AtomicInteger updates;
    @BeforeEach void setUp() {
        user = new User(1, "Matti", "Meikäläinen", "matti@example.com", Role.STUDENT);
        dao = mock(UserDao.class);
        updates = new AtomicInteger();
        when(dao.updateProfile(anyInt(), anyString(), anyString(), anyString())).thenReturn(true);
        when(dao.verifyPassword(1, "oldPassword")).thenReturn(true);
        when(dao.updatePassword(anyInt(), anyString())).thenReturn(true);
    }
    private void withWindow(Consumer<Stage> action) {
        interact(() -> inModal(() -> ProfileEditWindow.show(null, user, updates::incrementAndGet, dao), action));
    }
    private TextField field(Parent root, String id) { return (TextField) root.lookup("#" + id); }
    private void save(Parent root) { ((Button) root.lookup("#saveButton")).fire(); }
    private void error(Parent root, String key) {
        assertEquals(i18n.t(key), ((Label) root.lookup("#errorLabel")).getText());
        assertEquals(0, updates.get());
    }
    private void passwords(Parent root, String current, String password, String confirmation) {
        field(root, "currentPasswordField").setText(current == null ? "" : current);
        field(root, "newPasswordField").setText(password);
        field(root, "confirmPasswordField").setText(confirmation);
    }
    @Test void showsProfileAndCancelDoesNotSave() {
        withWindow(stage -> {
            Parent root = stage.getScene().getRoot();
            assertEquals("Matti", field(root, "firstNameField").getText());
            assertEquals("Meikäläinen", field(root, "lastNameField").getText());
            assertEquals("matti@example.com", field(root, "emailField").getText());
            assertEquals(i18n.t("profile.title"), stage.getTitle());
            ((Button) root.lookup("#backButton")).fire();
            assertFalse(stage.isShowing());
        });
        verifyNoInteractions(dao);
        assertEquals(0, updates.get());
    }
    @ParameterizedTest
    @CsvSource({"firstNameField, '   ', profile.requiredFields", "lastNameField, '   ', profile.requiredFields",
            "emailField, '   ', profile.requiredFields", "emailField, invalid-email, profile.invalidEmail"})
    void invalidProfileDoesNotReachDatabase(String id, String value, String key) {
        withWindow(stage -> {
            Parent root = stage.getScene().getRoot();
            field(root, id).setText(value);
            save(root);
            error(root, key);
            assertTrue(stage.isShowing());
        });
        verifyNoInteractions(dao);
    }
    @Test void rejectsEmailAlreadyInUse() {
        when(dao.isEmailTaken("other@example.com", 1)).thenReturn(true);
        withWindow(stage -> {
            Parent root = stage.getScene().getRoot();
            field(root, "emailField").setText("other@example.com");
            save(root);
            error(root, "profile.emailTaken");
        });
        verify(dao, never()).updateProfile(anyInt(), anyString(), anyString(), anyString());
    }
    @Test void savesTrimmedProfileUpdatesUserAndClosesWindow() {
        withWindow(stage -> {
            Parent root = stage.getScene().getRoot();
            field(root, "firstNameField").setText(" Liisa ");
            field(root, "lastNameField").setText(" Virtanen ");
            field(root, "emailField").setText(" liisa@example.com ");
            save(root);
            assertFalse(stage.isShowing());
        });
        var order = inOrder(dao);
        order.verify(dao).isEmailTaken("liisa@example.com", 1);
        order.verify(dao).updateProfile(1, "Liisa", "Virtanen", "liisa@example.com");
        verify(dao, never()).updatePassword(anyInt(), anyString());
        assertEquals("Liisa Virtanen", user.getFullName());
        assertEquals("liisa@example.com", user.getEmail());
        assertEquals(1, updates.get());
    }
    @ParameterizedTest
    @CsvSource(value = {"|newPassword|newPassword|profile.enterCurrentPassword",
            "wrong|newPassword|newPassword|profile.currentPasswordWrong",
            "oldPassword|short|short|profile.passwordTooShort",
            "oldPassword|newPassword|different|profile.passwordMismatch"}, delimiter = '|')
    void rejectsInvalidPasswordChange(String current, String password, String confirmation, String key) {
        withWindow(stage -> {
            Parent root = stage.getScene().getRoot();
            passwords(root, current, password, confirmation);
            save(root);
            error(root, key);
        });
        verify(dao, never()).updateProfile(anyInt(), anyString(), anyString(), anyString());
        verify(dao, never()).updatePassword(anyInt(), anyString());
    }
    @Test void changesPasswordAndNotifiesOnce() {
        withWindow(stage -> {
            Parent root = stage.getScene().getRoot();
            passwords(root, "oldPassword", "newPassword", "newPassword");
            save(root);
            assertFalse(stage.isShowing());
        });
        var order = inOrder(dao);
        order.verify(dao).verifyPassword(1, "oldPassword");
        order.verify(dao).updateProfile(1, "Matti", "Meikäläinen", "matti@example.com");
        order.verify(dao).updatePassword(1, "newPassword");
        verify(dao, never()).isEmailTaken(anyString(), anyInt());
        assertEquals(1, updates.get());
    }
    @Test void failedProfileSaveKeepsWindowOpenAndUserUnchanged() {
        when(dao.updateProfile(anyInt(), anyString(), anyString(), anyString())).thenReturn(false);
        withWindow(stage -> {
            Parent root = stage.getScene().getRoot();
            field(root, "firstNameField").setText("Liisa");
            save(root);
            error(root, "profile.saveFailed");
            assertTrue(stage.isShowing());
            assertEquals("Matti", user.getFirstName());
        });
    }
    @Test void failedPasswordSaveDoesNotNotifySuccess() {
        when(dao.updatePassword(1, "newPassword")).thenReturn(false);
        withWindow(stage -> {
            Parent root = stage.getScene().getRoot();
            passwords(root, "oldPassword", "newPassword", "newPassword");
            save(root);
            error(root, "profile.passwordChangeFailed");
            assertTrue(stage.isShowing());
        });
    }
    @Test void arabicProfileUsesRightToLeftLayout() {
        interact(() -> i18n.setLanguage(i18n.AR));
        withWindow(stage -> assertEquals(NodeOrientation.RIGHT_TO_LEFT, stage.getScene().getRoot().getNodeOrientation()));
    }
}
