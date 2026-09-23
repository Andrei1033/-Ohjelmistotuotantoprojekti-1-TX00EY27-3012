package com.example.app.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;




public class TeacherStartPage extends BorderPane {

    private static final String NAVY = "#202F49";


    public TeacherStartPage(Runnable onLogout) {
        setStyle("-fx-background-color: white;");

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(158);
        sidebar.setPadding(new Insets(12, 14, 10, 10));
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");
        HBox hBox = new HBox(9);
        hBox.setAlignment(Pos.CENTER_LEFT);

        Circle logo = new Circle(10, Color.web("#536FA4"));
        Label lo = new Label("LO");
        lo.setTextFill(Color.WHITE);
        lo.setFont(Font.font("System", FontWeight.BOLD, 9));
        StackPane logoBox = new StackPane(logo, lo);
        logoBox.setPrefSize(20, 20);

        Label brandText = text("Läsnäolo", 11, FontWeight.BOLD, "#FFFFFF");
        hBox.getChildren().addAll(logoBox, brandText);

        Button courses = new Button("•   Omat kurssit");
        courses.setPrefHeight(22);
        courses.setMaxWidth(Double.MAX_VALUE);
        courses.setAlignment(Pos.CENTER_LEFT);
        courses.setStyle("-fx-background-color: #344A70; -fx-text-fill: white; "
                + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 4;");
        courses.setFocusTraversable(false);

        Region sideSpacer = new Region();
        VBox.setVgrow(sideSpacer, Priority.ALWAYS);

        VBox userInfo = new VBox(0,
                text("Etunimi Sukunimi", 8, FontWeight.BOLD, "#FFFFFF"),
                text("Opettaja", 6, FontWeight.NORMAL, "#A9B0BD")
        );

        HBox user = new HBox(7);
        user.setAlignment(Pos.CENTER_LEFT);
        Circle avatar = new Circle(10, Color.web("#536FA4"));
        Label initials = text("MA", 8, FontWeight.BOLD, "#FFFFFF");
        StackPane avatarBox = new StackPane(avatar, initials);
        avatarBox.setPrefSize(20, 20);

        user.getChildren().addAll(avatarBox, userInfo);

        sidebar.getChildren().addAll(hBox, new Region(), courses, sideSpacer, user);

        VBox content = new VBox(0);
        content.setPadding(new Insets(41, 30, 20, 31));

        Label heading = text("Omat kurssit", 15, FontWeight.BOLD, "#171717");
        Label intro = text("Sinulla on 3 kurssia tällä lukukaudella.", 8, FontWeight.BOLD, "#171717");

        VBox headingBox = new VBox(2, heading, intro);
        headingBox.setPadding(new Insets(0, 0, 18, 0));

        HBox cards = new HBox(22);
        cards.getChildren().addAll(
                courseCard("TX00CV45", "Ohjelmoinnin perusteet", "5 oppituntia"),
                courseCard("TX00CV45", "Ohjelmoinnin perusteet", "5 oppituntia"),
                courseCard("TX00CV45", "Ohjelmoinnin perusteet", "5 oppituntia")
        );

        content.getChildren().addAll(headingBox, cards);

        Button logout = new Button("Kirjaudu ulos");
        logout.setVisible(false);
        logout.setOnAction(e -> onLogout.run());

        setLeft(sidebar);
        setCenter(content);


    }



    private VBox courseCard(String code, String name, String hours) {
        VBox card = new VBox(6);
        card.setPrefSize(190, 75);
        card.setPadding(new Insets(11, 13, 8, 13));
        card.setStyle("-fx-background-color: white; -fx-border-color: #D7D7D7; "
                + "-fx-border-radius: 5; -fx-background-radius: 5;");

        Label codeLabel = text(code, 7, FontWeight.BOLD, "#4B83A0");
        codeLabel.setStyle("-fx-background-color: #D9F0FA; -fx-background-radius: 3; -fx-padding: 3 6;");

        Label nameLabel = text(name, 10, FontWeight.BOLD, "#171717");
        Label hoursLabel = text(hours, 8, FontWeight.NORMAL, "#858585");

        card.getChildren().addAll(codeLabel, nameLabel, hoursLabel);
        return card;
    }

    private static Label text(String value, double size, FontWeight weight, String color) {
        Label l = new Label(value);
        l.setFont(Font.font("System", weight, size));
        l.setTextFill(Color.web(color));
        return l;
    }


}
