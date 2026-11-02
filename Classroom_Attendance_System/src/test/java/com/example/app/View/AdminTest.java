package com.example.app.View;

import com.example.app.Controller.AdminController;
import com.example.app.Localisation.i18n;
import com.example.app.Model.LoginComponents.Role;
import com.example.app.Model.LoginComponents.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminTest extends ViewTestSupport {
    private AdminController controller;
    private ObservableList<com.example.app.Model.Admin> users;
    private BorderPane view;
    private Stage stage;
    private AtomicInteger logouts;
    private User currentUser;

    @BeforeEach void setUp() {
        controller = mock(AdminController.class);
        users = FXCollections.observableArrayList(
                new com.example.app.Model.Admin(7, "Liisa Virtanen", "liisa@example.com", Role.STUDENT, "Java"),
                new com.example.app.Model.Admin(8, "Pekka Opettaja", "pekka@example.com", Role.TEACHER, "Java, SQL"),
                new com.example.app.Model.Admin(9, "Anna Admin", "anna@example.com", Role.ADMIN, "—"));
        when(controller.getUsers()).thenReturn(users);
        when(controller.getCourses()).thenReturn(new LinkedHashSet<>(List.of("Java", "SQL")));
        when(controller.filterUsers(any(), anyString(), anyString())).thenReturn(users);
        currentUser = new User(9, "Anna", "Admin", "anna@example.com", Role.ADMIN);
        logouts = new AtomicInteger();
    }
    private void show() {
        view = new Admin(controller, currentUser, logouts::incrementAndGet).getView();
        stage = new Stage();
        stage.setScene(new Scene(view, 1200, 700));
        stage.show();
        view.applyCss();
        view.layout();
    }
    @SuppressWarnings("unchecked")
    private TableView<com.example.app.Model.Admin> table() {
        return (TableView<com.example.app.Model.Admin>) nodes(view, TableView.class).getFirst();
    }
    private void addDialog(Consumer<Parent> action) {
        inModal(() -> button(view, i18n.t("admin.addUser")).fire(), dialog -> action.accept(dialog.getScene().getRoot()));
    }
    private void editDialog(Consumer<Parent> action) {
        inModal(() -> button(view, "✎").fire(), dialog -> action.accept(dialog.getScene().getRoot()));
    }
    private List<TextField> profileFields(Parent root) {
        return nodes(root, TextField.class).stream().filter(f -> !(f instanceof PasswordField)).toList();
    }
    @SuppressWarnings("unchecked")
    private ComboBox<String> option(Parent root, int index) {
        return (ComboBox<String>) nodes(root, ComboBox.class).get(index);
    }
    private void fillNewUser(Parent root) {
        profileFields(root).get(0).setText("Uusi User");
        profileFields(root).get(1).setText("uusi@example.com");
        nodes(root, PasswordField.class).forEach(p -> p.setText("password123"));
    }
    private void expectErrorOnSave(Parent root, String expected) {
        inModal(() -> button(root, i18n.t("app.save")).fire(), alert -> {
            DialogPane pane = nodes(alert.getScene().getRoot(), DialogPane.class).getFirst();
            assertEquals(expected, pane.getContentText());
            assertEquals(i18n.t("admin.invalidUserData"), pane.getHeaderText());
            button(pane, i18n.t("app.confirm")).fire();
        });
    }

    @Test void showsUsersAndLocalizedColumnValues() {
        interact(() -> {
            show();
            assertInstanceOf(VBox.class, view.getLeft());
            assertInstanceOf(VBox.class, view.getCenter());
            assertSame(users, table().getItems());
            assertEquals(List.of(i18n.t("admin.userName"), i18n.t("admin.userEmail"),
                    i18n.t("admin.userRole"), i18n.t("admin.userCourses"), ""),
                    table().getColumns().stream().map(TableColumn::getText).toList());
            assertEquals("Liisa Virtanen", table().getColumns().get(0).getCellData(0));
            assertEquals("liisa@example.com", table().getColumns().get(1).getCellData(0));
            assertEquals(i18n.t("admin.userRoleStudent"), table().getColumns().get(2).getCellData(0));
            assertEquals("Java", table().getColumns().get(3).getCellData(0));
            hasLabel(view.getLeft(), "Anna Admin");
            hasLabel(view.getLeft(), "AA");
        });
    }
    @Test void emptyListShowsPlaceholder() {
        users.clear();
        interact(() -> {
            show();
            assertTrue(table().getItems().isEmpty());
            assertEquals(i18n.t("admin.emptyUserList"), ((Label) table().getPlaceholder()).getText());
        });
    }
    @Test void databaseFailureIsVisible() {
        when(controller.getDatabaseError()).thenReturn("Database unavailable");
        interact(() -> { show(); hasLabel(view, i18n.t("admin.loadFailed", "Database unavailable")); });
    }
    @ParameterizedTest
    @EnumSource(Role.class)
    void roleFiltersPassCorrectRoleAndDisplayReturnedRows(Role role) {
        var filtered = FXCollections.observableArrayList(users.stream().filter(u -> u.getRole() == role).toList());
        when(controller.filterUsers(eq(role), anyString(), anyString())).thenReturn(filtered);
        interact(() -> {
            show();
            String key = switch (role) {
                case ADMIN -> "admin.userRoleAdmin";
                case TEACHER -> "admin.userRoleTeacher";
                case STUDENT -> "admin.userRoleStudent";
            };
            Button selected = button(view, i18n.t(key));
            selected.fire();
            verify(controller).filterUsers(role, "", i18n.t("app.all"));
            assertSame(filtered, table().getItems());
            assertTrue(selected.getStyleClass().contains("filter-button-active"));
            assertFalse(button(view, i18n.t("admin.filterAll")).getStyleClass().contains("filter-button-active"));
        });
    }
    @Test void searchAndCourseFilterKeepSelectedRole() {
        interact(() -> {
            show();
            button(view, i18n.t("admin.userRoleStudent")).fire();
            nodes(view, TextField.class).getFirst().setText("Liisa");
            option(view, 1).setValue("Java");
            verify(controller).filterUsers(Role.STUDENT, "Liisa", "Java");
            button(view, i18n.t("admin.filterAll")).fire();
            verify(controller).filterUsers(null, "Liisa", "Java");
        });
    }
    @Test void logoutCallsCallbackOnce() {
        interact(() -> {
            show();
            assertEquals(0, logouts.get());
            button(view, i18n.t("app.logout")).fire();
            assertEquals(1, logouts.get());
        });
    }
    @Test void changingLanguageRebuildsViewWithArabicLayout() {
        interact(() -> {
            show();
            languageBox(view).setValue(i18n.AR);
            Parent rebuilt = stage.getScene().getRoot();
            assertNotSame(view, rebuilt);
            assertEquals(i18n.AR, i18n.getCurrentLang());
            assertEquals(NodeOrientation.RIGHT_TO_LEFT, rebuilt.getNodeOrientation());
            assertNotNull(button(rebuilt, i18n.t("admin.addUser")));
        });
    }
    @Test void missingCurrentUserUsesAdminFromList() {
        currentUser = null;
        interact(() -> { show(); hasLabel(view.getLeft(), "Anna Admin"); });
    }
    @Test void missingAdminUsesDefaultName() {
        currentUser = null;
        users.removeIf(user -> user.getRole() == Role.ADMIN);
        interact(() -> { show(); hasLabel(view.getLeft(), i18n.t("admin.defaultName")); });
    }
    @Test void cancellingAddDoesNotSave() {
        interact(() -> {
            show();
            addDialog(root -> button(root, i18n.t("app.cancel")).fire());
            verify(controller, never()).addUser(anyString(), anyString(), any(), anyString(), anyString());
        });
    }
    @Test void addsUserWithSelectedRoleAndCourses() {
        interact(() -> {
            show();
            addDialog(root -> {
                fillNewUser(root);
                option(root, 0).setValue(i18n.t("admin.userRoleTeacher"));
                option(root, 1).setValue("Java");
                button(root, "+").fire();
                assertFalse(option(root, 2).getItems().contains("Java"));
                option(root, 2).setValue("SQL");
                button(root, i18n.t("app.save")).fire();
            });
            verify(controller).addUser("Uusi User", "uusi@example.com", Role.TEACHER, "Java, SQL", "password123");
        });
    }
    @Test void addsUserWithoutCoursesUsingEmptyMarker() {
        interact(() -> {
            show();
            addDialog(root -> { fillNewUser(root); button(root, i18n.t("app.save")).fire(); });
            verify(controller).addUser("Uusi User", "uusi@example.com", Role.STUDENT, "—", "password123");
        });
    }
    @Test void missingPasswordPreventsUserCreation() {
        interact(() -> {
            show();
            addDialog(root -> expectErrorOnSave(root, i18n.t("admin.passwordRequired")));
            verify(controller, never()).addUser(anyString(), anyString(), any(), anyString(), anyString());
        });
    }
    @Test void mismatchedPasswordsPreventUserCreation() {
        interact(() -> {
            show();
            addDialog(root -> {
                fillNewUser(root);
                nodes(root, PasswordField.class).get(1).setText("different");
                expectErrorOnSave(root, i18n.t("admin.confirmPasswordMismatch"));
            });
            verify(controller, never()).addUser(anyString(), anyString(), any(), anyString(), anyString());
        });
    }
    @Test void controllerSaveFailureIsDisplayed() {
        when(controller.addUser(anyString(), anyString(), any(), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Email already exists"));
        interact(() -> {
            show();
            addDialog(root -> { fillNewUser(root); expectErrorOnSave(root, "Email already exists"); });
            verify(controller).addUser("Uusi User", "uusi@example.com", Role.STUDENT, "—", "password123");
        });
    }
    @Test void editingUserKeepsPasswordWhenLeftBlank() {
        interact(() -> {
            show();
            editDialog(root -> {
                assertEquals("Liisa Virtanen", profileFields(root).get(0).getText());
                profileFields(root).get(0).setText("Liisa Uusi");
                button(root, i18n.t("app.save")).fire();
            });
            verify(controller).updateUser(users.getFirst(), "Liisa Uusi", "liisa@example.com", Role.STUDENT, "Java", null);
        });
    }
    @Test void deletingUserRequiresConfirmation() {
        interact(() -> {
            show();
            editDialog(root -> inModal(() -> button(root, i18n.t("admin.delete")).fire(), confirmation ->
                    button(confirmation.getScene().getRoot(), i18n.t("app.cancel")).fire()));
            verify(controller, never()).deleteUser(any());
        });
    }
    @Test void confirmedDeletionPassesSelectedUserToController() {
        interact(() -> {
            show();
            editDialog(root -> inModal(() -> button(root, i18n.t("admin.delete")).fire(), confirmation ->
                    button(confirmation.getScene().getRoot(), i18n.t("app.confirm")).fire()));
            verify(controller).deleteUser(users.getFirst());
        });
    }
}
