/**
 * Modaali-ikkuna, jossa käyttäjä voi muokata omaa nimeään, sähköpostiaan ja
 * vaihtaa salasanansa. Avataan sivupalkin avatar-ympyrän klikkauksesta.
 *
 * Käyttö:
 *   ProfileEditWindow.show(someNode.getScene().getWindow(), currentUser, () -> refresh());
 *
 * onProfileUpdated-callback kutsutaan vain onnistuneen tallennuksen jälkeen,
 * jotta kutsuja voi päivittää näytöllä olevan sivun (esim. sivupalkin nimen).
 */

package com.example.app.View;

import com.example.app.DaoElements.LoginDao.UserDao;
import com.example.app.Model.LoginComponents.User;

import javafx.geometry.Insets;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;


public final class ProfileEditWindow {

    private static final String BLUE = "#344A70";

    private ProfileEditWindow() {
    }

    public static void show(Window owner, User currentUser, Runnable onProfileUpdated) {

        UserDao userDao = new UserDao();

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        if (owner != null) {
            stage.initOwner(owner);
        }
        stage.setTitle("Omat tiedot");
        stage.setResizable(false);

        VBox root = new VBox(10);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: white;");

        Label heading = label("Omat tiedot", 15, true, "#171717");

        TextField firstNameField = textField(currentUser.getFirstName());
        TextField lastNameField = textField(currentUser.getLastName());
        TextField emailField = textField(currentUser.getEmail());

        PasswordField currentPasswordField = passwordField();
        PasswordField newPasswordField = passwordField();
        PasswordField confirmPasswordField = passwordField();

        Label errorLabel = label("", 10, false, "#C44D3A");
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(290);

        Button back = new Button("Takaisin");
        styleButton(back, "#EFEFEF", "#171717");
        back.setOnAction(e -> stage.close());

        Button save = new Button("Tallenna");
        styleButton(save, BLUE, "white");

        save.setOnAction(e -> {

            errorLabel.setText("");

            String firstName = safeTrim(firstNameField.getText());
            String lastName = safeTrim(lastNameField.getText());
            String email = safeTrim(emailField.getText());

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                errorLabel.setText("Etunimi, sukunimi ja sähköposti ovat pakollisia.");
                return;
            }

            if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                errorLabel.setText("Sähköpostiosoite ei ole kelvollinen.");
                return;
            }

            if (!email.equalsIgnoreCase(currentUser.getEmail())
                    && userDao.isEmailTaken(email, currentUser.getId())) {
                errorLabel.setText("Sähköpostiosoite on jo käytössä.");
                return;
            }

            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            boolean wantsPasswordChange =
                    !isBlank(currentPassword) || !isBlank(newPassword) || !isBlank(confirmPassword);

            if (wantsPasswordChange) {

                if (isBlank(currentPassword)) {
                    errorLabel.setText("Anna nykyinen salasana vaihtaaksesi salasanan.");
                    return;
                }

                if (!userDao.verifyPassword(currentUser.getId(), currentPassword)) {
                    errorLabel.setText("Nykyinen salasana on väärin.");
                    return;
                }

                if (newPassword == null || newPassword.length() < 6) {
                    errorLabel.setText("Uuden salasanan on oltava vähintään 6 merkkiä.");
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    errorLabel.setText("Uudet salasanat eivät täsmää.");
                    return;
                }
            }

            boolean profileOk = userDao.updateProfile(currentUser.getId(), firstName, lastName, email);

            if (!profileOk) {
                errorLabel.setText("Tietojen tallennus epäonnistui. Yritä uudelleen.");
                return;
            }

            if (wantsPasswordChange) {
                boolean passwordOk = userDao.updatePassword(currentUser.getId(), newPassword);
                if (!passwordOk) {
                    errorLabel.setText("Salasanan vaihto epäonnistui. Tiedot tallennettiin, mutta salasana ei vaihtunut.");
                    return;
                }
            }

            // Päivitä olemassa oleva User-olio, jotta esim. sivupalkin nimi
            // näkyy heti oikein ilman uutta kirjautumista.
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
                label("Etunimi", 10, true, "#555555"), firstNameField,
                label("Sukunimi", 10, true, "#555555"), lastNameField,
                label("Sähköposti", 10, true, "#555555"), emailField,
                new Separator(),
                label("Vaihda salasana (valinnainen)", 10, true, "#555555"),
                label("Nykyinen salasana", 9, false, "#858585"), currentPasswordField,
                label("Uusi salasana", 9, false, "#858585"), newPasswordField,
                label("Vahvista uusi salasana", 9, false, "#858585"), confirmPasswordField,
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
        field.setStyle("-fx-font-size: 11px;");
        return field;
    }

    private static PasswordField passwordField() {
        PasswordField field = new PasswordField();
        field.setStyle("-fx-font-size: 11px;");
        return field;
    }

    private static Label label(String value, double size, boolean bold, String color) {
        Label label = new Label(value);
        label.setStyle(
                "-fx-font-family: 'System'; " +
                        "-fx-font-size: " + size + "px; " +
                        "-fx-font-weight: " + (bold ? "bold" : "normal") + "; " +
                        "-fx-text-fill: " + color + ";"
        );
        return label;
    }

    private static void styleButton(Button button, String backgroundColor, String textColor) {
        button.setStyle(
                "-fx-background-color: " + backgroundColor + "; " +
                        "-fx-text-fill: " + textColor + "; " +
                        "-fx-font-size: 11px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 4; " +
                        "-fx-padding: 6 14;"
        );
        button.setCursor(Cursor.HAND);
    }
}