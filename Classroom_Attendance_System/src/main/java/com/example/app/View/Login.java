package com.example.app.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Login extends BorderPane {

    private static final String NAVY = "#202F49";
    private static final String BLUE = "#344A70";
    private static final String MUTED = "#858585";

    public Login(Runnable onLogin) {
        setStyle("-fx-background-color: " + NAVY + ";");

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

        Label title = label("Kirjaudu sisään", 15, FontWeight.BOLD, "#171717");
        Label subtitle = label("Läsnäolojärjestelmä - Metropolia AMK", 9, FontWeight.BOLD, MUTED);

        VBox header = new VBox(2, title, subtitle);
        header.setPadding(new Insets(0, 8, 18, 8));

        TextField email = new TextField("Andreits@metropolia.fi");
        email.setPrefHeight(38);
        email.setStyle(fieldStyle());

        PasswordField password = new PasswordField();
        password.setPromptText("--------");
        password.setPrefHeight(38);
        password.setStyle(fieldStyle());

        VBox form = new VBox(5,
                label("Sähköposti", 9, FontWeight.BOLD, MUTED), email,
                label("Salasana", 9, FontWeight.BOLD, MUTED), password
        );
        form.setPadding(new Insets(0, 0, 0, 0));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button login = new Button("Kirjaudu");
        login.setPrefSize(196, 29);
        login.setStyle("-fx-background-color: " + BLUE + "; -fx-text-fill: white; "
                + "-fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 4;");
        login.setOnAction(e -> onLogin.run());

        Label forgot = new Label("Unohtuiko salasana? Ota yhteyttä opettajaan");
        forgot.setFont(Font.font("System", FontWeight.BOLD, 9));
        forgot.setTextFill(Color.web(MUTED));

        VBox bottom = new VBox(12, login, forgot);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(0, 0, 0, 0));

        card.getChildren().addAll(logoBox, header, form, spacer, bottom);

        StackPane center = new StackPane(card);
        center.setAlignment(Pos.CENTER);
        setCenter(center);
    }

    private static String fieldStyle() {
        return "-fx-background-color: white; -fx-border-color: #D7D7D7; "
                + "-fx-border-radius: 12; -fx-background-radius: 12; "
                + "-fx-font-size: 10px; -fx-font-weight: bold; "
                + "-fx-padding: 0 13px;";
    }

    private static Label label(String text, double size, FontWeight weight, String color) {
        Label l = new Label(text);
        l.setFont(Font.font("System", weight, size));
        l.setTextFill(Color.web(color));
        return l;
    }
}
