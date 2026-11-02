package com.example.app.View;

import com.example.app.DaoElements.LoginDao.UserDao;
import com.example.app.Localisation.FontSupport;
import com.example.app.Localisation.i18n;
import com.example.app.Model.LoginComponents.User;

import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;


public final class ProfileEditWindow {

    private static final String BLUE = "#344A70";

    private ProfileEditWindow() {
    }

    public static void show(Window owner, User currentUser, Runnable onProfileUpdated) {
        show(owner, currentUser, onProfileUpdated, new UserDao());
    }

    public static void show(Window owner,
                            User currentUser,
                            Runnable onProfileUpdated,
                            UserDao userDao) {

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) {
            stage.initOwner(owner);
        }
        stage.setTitle(i18n.t("profile.title"));
        stage.setResizable(false);

        VBox root = new VBox(10);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: white;");

        // RTL support (Arabic)
        if (FontSupport.isRtl()) {
            root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        }

        Label heading = label(i18n.t("profile.title"), 15, true, "#171717");

        TextField firstNameField = textField(currentUser.getFirstName());
        TextField lastNameField  = textField(currentUser.getLastName());
        TextField emailField     = textField(currentUser.getEmail());

        PasswordField currentPasswordField = passwordField();
        PasswordField newPasswordField     = passwordField();
        PasswordField confirmPasswordField = passwordField();

        Label errorLabel = label("", 10, false, "#C44D3A");
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(290);

        Button back = new Button(i18n.t("app.back"));
        styleButton(back, "#EFEFEF", "#171717");
        back.setOnAction(e -> stage.close());

        Button save = new Button(i18n.t("app.save"));
        styleButton(save, BLUE, "white");

        // ----- TEST HOOKS: stable IDs -----
        firstNameField.setId("firstNameField");
        lastNameField.setId("lastNameField");
        emailField.setId("emailField");
        currentPasswordField.setId("currentPasswordField");
        newPasswordField.setId("newPasswordField");
        confirmPasswordField.setId("confirmPasswordField");
        errorLabel.setId("errorLabel");
        save.setId("saveButton");
        back.setId("backButton");
        // ----------------------------------

        save.setOnAction(e -> {

            errorLabel.setText("");

            String firstName = safeTrim(firstNameField.getText());
            String lastName  = safeTrim(lastNameField.getText());
            String email     = safeTrim(emailField.getText());

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                errorLabel.setText(i18n.t("profile.requiredFields"));
                return;
            }

            if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                errorLabel.setText(i18n.t("profile.invalidEmail"));
                return;
            }

            if (!email.equalsIgnoreCase(currentUser.getEmail())
                    && userDao.isEmailTaken(email, currentUser.getId())) {
                errorLabel.setText(i18n.t("profile.emailTaken"));
                return;
            }

            String currentPassword = currentPasswordField.getText();
            String newPassword     = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            boolean wantsPasswordChange =
                    !isBlank(currentPassword) || !isBlank(newPassword) || !isBlank(confirmPassword);

            if (wantsPasswordChange) {
                if (isBlank(currentPassword)) {
                    errorLabel.setText(i18n.t("profile.enterCurrentPassword"));
                    return;
                }
                if (!userDao.verifyPassword(currentUser.getId(), currentPassword)) {
                    errorLabel.setText(i18n.t("profile.currentPasswordWrong"));
                    return;
                }
                if (newPassword == null || newPassword.length() < 6) {
                    errorLabel.setText(i18n.t("profile.passwordTooShort"));
                    return;
                }
                if (!newPassword.equals(confirmPassword)) {
                    errorLabel.setText(i18n.t("profile.passwordMismatch"));
                    return;
                }
            }

            boolean profileOk = userDao.updateProfile(
                    currentUser.getId(), firstName, lastName, email);

            if (!profileOk) {
                errorLabel.setText(i18n.t("profile.saveFailed"));
                return;
            }

            if (wantsPasswordChange) {
                boolean passwordOk = userDao.updatePassword(currentUser.getId(), newPassword);
                if (!passwordOk) {
                    errorLabel.setText(i18n.t("profile.passwordChangeFailed"));
                    return;
                }
            }

            currentUser.setFirstName(firstName);
            currentUser.setLastName(lastName);
            currentUser.setEmail(email);

            if (onProfileUpdated != null) {
                onProfileUpdated.run();
            }

            stage.close();
        });

        HBox buttons = new HBox(10, back, save);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        root.getChildren().addAll(
                heading,
                label(i18n.t("profile.firstName"), 10, true, "#555555"), firstNameField,
                label(i18n.t("profile.lastName"), 10, true, "#555555"), lastNameField,
                label(i18n.t("profile.email"), 10, true, "#555555"), emailField,
                new Separator(),
                label(i18n.t("profile.changePassword"), 10, true, "#555555"),
                label(i18n.t("profile.currentPassword"), 9, false, "#858585"), currentPasswordField,
                label(i18n.t("profile.newPassword"), 9, false, "#858585"), newPasswordField,
                label(i18n.t("profile.confirmNewPassword"), 9, false, "#858585"), confirmPasswordField,
                errorLabel,
                buttons
        );

        Scene scene = new Scene(root, 340, 580);
        stage.setScene(scene);
        stage.showAndWait();
    }

    // =====================================================
    // HELPERS
    // =====================================================

    private static boolean isBlank(String s) {
        return s == null || s.isEmpty();
    }

    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    private static TextField textField(String value) {
        TextField field = new TextField(value == null ? "" : value);
        field.setFont(FontSupport.font(11, FontWeight.NORMAL));
        return field;
    }

    private static PasswordField passwordField() {
        PasswordField field = new PasswordField();
        field.setFont(FontSupport.font(11, FontWeight.NORMAL));
        return field;
    }

    private static Label label(String value, double size, boolean bold, String color) {
        Label label = new Label(value);
        label.setFont(FontSupport.font(size, bold ? FontWeight.BOLD : FontWeight.NORMAL));
        label.setStyle("-fx-text-fill: " + color + ";");
        return label;
    }

    private static void styleButton(Button button, String backgroundColor, String textColor) {
        button.setFont(FontSupport.font(11, FontWeight.BOLD));
        button.setStyle(
                "-fx-background-color: " + backgroundColor + "; " +
                        "-fx-text-fill: " + textColor + "; " +
                        "-fx-background-radius: 4; " +
                        "-fx-padding: 6 14;"
        );
        button.setCursor(Cursor.HAND);
    }
}