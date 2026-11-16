package com.example.app.View;

import com.example.app.Localisation.FontSupport;
import com.example.app.Localisation.i18n;
import com.example.app.Model.LoginComponents.User;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;

public final class ViewSupport {
    public static final String NAVY = "#202F49";
    public static final String BLUE = "#344A70";

    private ViewSupport() {
    }

    public static HBox createBrandBox() {
        HBox brand = new HBox(9);
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPadding(new Insets(0, 0, 20, 0));

        Circle logo = new Circle(13, Color.web("#536FA4"));
        Label lo = text(i18n.t("app.logo"), 12, FontWeight.BOLD, "#FFFFFF");
        StackPane logoBox = new StackPane(logo, lo);
        logoBox.setPrefSize(20, 20);

        Label brandText = text(i18n.t("app.attendance"), 13, FontWeight.BOLD, "#FFFFFF");
        brand.getChildren().addAll(logoBox, brandText);
        return brand;
    }

    public static ComboBox<i18n.Lang> createLanguageSelector(Runnable onLanguageChanged) {
        ComboBox<i18n.Lang> langCombo = new ComboBox<>();
        langCombo.getItems().addAll(i18n.SUPPORTED);
        langCombo.setValue(i18n.getCurrentLang());
        langCombo.setMaxWidth(Double.MAX_VALUE);
        langCombo.setFocusTraversable(false);
        langCombo.setCursor(Cursor.HAND);
        langCombo.setStyle("-fx-background-color: " + BLUE + "; " + "-fx-background-radius: 4; " + "-fx-font-family: '"
                + FontSupport.pickFamily() + "'; " + "-fx-font-size: 11px; " + "-fx-text-fill: white;");

        langCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(i18n.Lang lang) {
                return lang == null ? null : lang.label();
            }

            @Override
            public i18n.Lang fromString(String string) {
                return null;
            }
        });

        FontSupport.configureLanguageSelector(langCombo);
        langCombo.setOnAction(e -> {
            i18n.Lang selected = langCombo.getValue();
            if (selected == null || selected.equals(i18n.getCurrentLang()))
                return;
            i18n.setLanguage(selected);
            if (onLanguageChanged != null) {
                onLanguageChanged.run();
            }
        });
        return langCombo;
    }

    public static HBox createUserRow(User currentUser, Consumer<MouseEvent> onUserClick) {
        return createUserRow(currentUser, null, onUserClick);
    }

    public static HBox createUserRow(User currentUser, String roleOverride, Consumer<MouseEvent> onUserClick) {
        HBox user = new HBox(7);
        user.setAlignment(Pos.CENTER_LEFT);
        user.setCursor(Cursor.HAND);

        Circle avatar = new Circle(15, Color.web("#536FA4"));
        Label initials = text(initialsOf(currentUser), 12, FontWeight.BOLD, "#FFFFFF");
        StackPane avatarBox = new StackPane(avatar, initials);
        avatarBox.setPrefSize(20, 20);
        avatarBox.setCursor(Cursor.HAND);

        VBox userInfo = new VBox(0,
                text(currentUser != null ? currentUser.getFullName() : "", 12, FontWeight.BOLD, "#FFFFFF"),
                text(roleOverride != null ? roleOverride : roleLabel(currentUser), 11, FontWeight.NORMAL, "#A9B0BD"));
        user.getChildren().addAll(avatarBox, userInfo);

        user.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().name().equals("PRIMARY") && onUserClick != null) {
                onUserClick.accept(event);
                event.consume();
            }
        });
        return user;
    }

    public static Button createLogoutButton(Runnable onLogout) {
        Button logout = new Button(i18n.t("app.logout"));
        logout.setStyle("-fx-background-color: " + BLUE + "; " + "-fx-text-fill: white; " + "-fx-font-family: '"
                + FontSupport.pickFamily() + "'; " + "-fx-font-size: 11px; " + "-fx-font-weight: bold; "
                + "-fx-background-radius: 4; " + "-fx-padding: 6 14;");
        logout.setCursor(Cursor.HAND);
        logout.setOnAction(e -> {
            if (onLogout != null) {
                onLogout.run();
            }
        });
        return logout;
    }

    public static String initialsOf(User user) {
        if (user == null) {
            return "";
        }
        String first = (user.getFirstName() == null || user.getFirstName().isEmpty())
                ? ""
                : user.getFirstName().substring(0, 1);
        String last = (user.getLastName() == null || user.getLastName().isEmpty())
                ? ""
                : user.getLastName().substring(0, 1);
        return (first + last).toUpperCase();
    }

    public static String roleLabel(User user) {
        if (user == null || user.getRole() == null)
            return "";
        switch (user.getRole().name()) {
            case "TEACHER" :
                return i18n.t("app.teacher");
            case "ADMIN" :
                return i18n.t("app.admin");
            default :
                return i18n.t("app.student");
        }
    }

    public static Label text(String value, double size, FontWeight weight, String color) {
        Label label = new Label(value);
        label.setStyle("-fx-font-family: '" + FontSupport.pickFamily() + "'; " + "-fx-font-size: " + size + "px; "
                + "-fx-font-weight: " + weightToCss(weight) + "; " + "-fx-text-fill: " + color + ";");
        return label;
    }

    public static String weightToCss(FontWeight weight) {
        return weight == FontWeight.BOLD ? "bold" : "normal";
    }
}
