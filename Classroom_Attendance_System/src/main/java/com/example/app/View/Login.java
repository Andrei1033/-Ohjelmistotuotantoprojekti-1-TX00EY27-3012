package com.example.app.View;

import com.example.app.Localisation.FontSupport;
import com.example.app.Localisation.i18n;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;

import java.util.function.BiConsumer;

public class Login extends BorderPane {

    private static final String NAVY = "#202F49";
    private static final String BLUE = "#344A70";
    private static final String MUTED = "#858585";

    private final Label title, subtitle, emailLabel, passLabel, forgot;
    private final TextField email;
    private final PasswordField password;
    private final Button login;
    private final ComboBox<i18n.Lang> langBox;

    public Login(Runnable onLogin) {
        this((e, p) -> onLogin.run());
    }

    public Login(BiConsumer<String, String> onLoginAttempt) {
        setStyle("-fx-background-color: " + NAVY + ";");

        // --- language dropdown (top-right) ---
        langBox = new ComboBox<>();
        langBox.getItems().setAll(i18n.SUPPORTED);
        langBox.setValue(i18n.getCurrentLang());
        langBox.setConverter(new StringConverter<>() {
            @Override public String toString(i18n.Lang l) { return l == null ? "" : l.label(); }
            @Override public i18n.Lang fromString(String s) { return null; }
        });
        langBox.setOnAction(e -> i18n.setLanguage(langBox.getValue()));
        langBox.setStyle("-fx-font-size: 11px; -fx-background-radius: 6;");

        HBox topBar = new HBox(langBox);
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setPadding(new Insets(10, 12, 0, 0));
        setTop(topBar);

        // --- card ---
        VBox card = new VBox(0);
        card.setPrefSize(292, 332);
        card.setMaxSize(292, 332);
        card.setPadding(new Insets(14, 10, 8, 10));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 14;");

        Rectangle logo = new Rectangle(20, 20);
        logo.setArcWidth(6);
        logo.setArcHeight(6);
        logo.setFill(Color.web("#536FA4"));

        Label logoText = new Label("LO");
        logoText.setTextFill(Color.WHITE);
        logoText.setFont(Font.font("System", FontWeight.BOLD, 9));

        StackPane logoBox = new StackPane(logo, logoText);
        logoBox.setPrefSize(20, 20);
        logoBox.setMaxSize(20, 20);
        logoBox.setAlignment(Pos.CENTER);

        title = label("", 15, FontWeight.BOLD, "#171717");
        subtitle = label("", 9, FontWeight.BOLD, MUTED);

        VBox header = new VBox(2, title, subtitle);
        header.setPadding(new Insets(0, 8, 18, 8));

        email = new TextField();
        email.setPrefHeight(38);
        email.setStyle(fieldStyle());

        password = new PasswordField();
        password.setPrefHeight(38);
        password.setStyle(fieldStyle());

        emailLabel = label("", 9, FontWeight.BOLD, MUTED);
        passLabel = label("", 9, FontWeight.BOLD, MUTED);

        VBox form = new VBox(5,
                emailLabel, email,
                passLabel, password
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        login = new Button();
        login.setPrefSize(196, 29);
        login.setStyle("-fx-background-color: " + BLUE + "; -fx-text-fill: white; "
                + "-fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 4;");
        login.setOnAction(e -> onLoginAttempt.accept(email.getText(), password.getText()));

        forgot = label("", 9, FontWeight.BOLD, MUTED);
        forgot.setWrapText(true);
        forgot.setMaxWidth(196);
        forgot.setAlignment(Pos.CENTER);
        forgot.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        VBox bottom = new VBox(12, login, forgot);
        bottom.setAlignment(Pos.CENTER);

        card.getChildren().addAll(logoBox, header, form, spacer, bottom);

        StackPane center = new StackPane(card);
        center.setAlignment(Pos.CENTER);
        setCenter(center);

        // --- react to language changes ---
        i18n.currentLangProperty().addListener((obs, o, n) -> applyTexts());
        applyTexts();
    }

    private void applyTexts() {
        title.setText(i18n.t("login.title"));
        subtitle.setText(i18n.t("login.subtitle"));
        emailLabel.setText(i18n.t("login.email"));
        passLabel.setText(i18n.t("login.password"));
        email.setPromptText(i18n.t("login.email.placeholder"));
        password.setPromptText(i18n.t("login.password.placeholder"));
        login.setText(i18n.t("login.button"));
        forgot.setText(i18n.t("login.forgot"));

        // script-aware font
        applyFont(title, 15, FontWeight.BOLD);
        applyFont(subtitle, 9, FontWeight.BOLD);
        applyFont(emailLabel, 9, FontWeight.BOLD);
        applyFont(passLabel, 9, FontWeight.BOLD);
        applyFont(forgot, 9, FontWeight.BOLD);

        // RTL support (optional, but nice)
        Node root = this;
        root.setNodeOrientation(FontSupport.isRtl()
                ? javafx.geometry.NodeOrientation.RIGHT_TO_LEFT
                : javafx.geometry.NodeOrientation.LEFT_TO_RIGHT);
    }

    private static void applyFont(Label l, double size, FontWeight weight) {
        l.setFont(FontSupport.font(size, weight));
    }

    private static String fieldStyle() {
        return "-fx-background-color: white; -fx-border-color: #D7D7D7; "
                + "-fx-border-radius: 12; -fx-background-radius: 12; "
                + "-fx-font-size: 10px; -fx-font-weight: bold; "
                + "-fx-padding: 0 13px;";
    }

    private static Label label(String text, double size, FontWeight weight, String color) {
        Label l = new Label(text);
        l.setFont(FontSupport.font(size, weight));
        l.setTextFill(Color.web(color));
        return l;
    }
}