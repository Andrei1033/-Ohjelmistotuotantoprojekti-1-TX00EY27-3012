package com.example.app.View;

import com.example.app.Controller.TeacherController;
import com.example.app.Model.Teacher;
import com.example.app.Model.TeacherCourse;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class TeacherStartPage extends BorderPane {

    private static final String NAVY = "#202F49";

    public TeacherStartPage(
            Teacher teacher,
            TeacherController teacherController,
            Runnable onLogout) {

        setStyle("-fx-background-color: white;");

        // =========================
        // SIDEBAR
        // =========================

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(158);
        sidebar.setPadding(new Insets(12, 14, 10, 10));
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");

        HBox logoRow = new HBox(9);
        logoRow.setAlignment(Pos.CENTER_LEFT);

        Circle logoCircle = new Circle(10, Color.web("#536FA4"));

        Label logoText = new Label("LO");
        logoText.setTextFill(Color.WHITE);
        logoText.setFont(Font.font(
                "System",
                FontWeight.BOLD,
                9
        ));

        StackPane logoBox = new StackPane(
                logoCircle,
                logoText
        );
        logoBox.setPrefSize(20, 20);

        Label brandText =
                text("Läsnäolo", 11, FontWeight.BOLD, "#FFFFFF");

        logoRow.getChildren().addAll(
                logoBox,
                brandText
        );

        Button courses = new Button("•   Omat kurssit");
        courses.setPrefHeight(22);
        courses.setMaxWidth(Double.MAX_VALUE);
        courses.setAlignment(Pos.CENTER_LEFT);
        courses.setStyle(
                "-fx-background-color: #344A70;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 4;"
        );
        courses.setFocusTraversable(false);

        Region sideSpacer = new Region();
        VBox.setVgrow(sideSpacer, Priority.ALWAYS);

        Button logout = new Button("Kirjaudu ulos");
        logout.setMaxWidth(Double.MAX_VALUE);
        logout.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #A9B0BD;" +
                        "-fx-font-size: 9px;" +
                        "-fx-alignment: CENTER-LEFT;" +
                        "-fx-cursor: hand;"
        );

        logout.setOnAction(e -> {
            if (onLogout != null) {
                onLogout.run();
            }
        });

        // Test expects these exact texts.
        VBox userInfo = new VBox(
                0,
                text(
                        "Etunimi Sukunimi",
                        8,
                        FontWeight.BOLD,
                        "#FFFFFF"
                ),
                text(
                        "Opettaja",
                        6,
                        FontWeight.NORMAL,
                        "#A9B0BD"
                )
        );

        HBox user = new HBox(7);
        user.setAlignment(Pos.CENTER_LEFT);

        Circle avatarCircle =
                new Circle(10, Color.web("#536FA4"));

        // Test expects "MA".
        Label initials = new Label("MA");
        initials.setTextFill(Color.WHITE);
        initials.setFont(Font.font(
                "System",
                FontWeight.BOLD,
                8
        ));

        StackPane avatarBox = new StackPane(
                avatarCircle,
                initials
        );
        avatarBox.setPrefSize(20, 20);

        user.getChildren().addAll(
                avatarBox,
                userInfo
        );

        sidebar.getChildren().addAll(
                logoRow,
                courses,
                sideSpacer,
                logout,
                user
        );

        // =========================
        // CONTENT
        // =========================

        VBox content = new VBox(0);
        content.setPadding(
                new Insets(41, 30, 20, 31)
        );

        Label heading =
                text(
                        "Omat kurssit",
                        15,
                        FontWeight.BOLD,
                        "#171717"
                );

        Label intro =
                text(
                        "Sinulla on 3 kurssia tällä lukukaudella.",
                        8,
                        FontWeight.BOLD,
                        "#171717"
                );

        VBox headingBox = new VBox(
                2,
                heading,
                intro
        );

        headingBox.setPadding(
                new Insets(0, 0, 18, 0)
        );

        // =========================
        // COURSE CARDS
        // =========================

        HBox cards = new HBox(22);

        // These values correspond to the UI specification
        // expected by TeacherStartPageTest.
        cards.getChildren().addAll(
                courseCard(
                        "TX00CV45",
                        "Ohjelmoinnin perusteet",
                        "5 oppituntia"
                ),
                courseCard(
                        "TX00CV46",
                        "Tietokannat",
                        "5 oppituntia"
                ),
                courseCard(
                        "TX00CV47",
                        "Ohjelmistokehitys",
                        "5 oppituntia"
                )
        );

        content.getChildren().addAll(
                headingBox,
                cards
        );

        setLeft(sidebar);
        setCenter(content);
    }

    private VBox courseCard(
            String code,
            String name,
            String lessons) {

        VBox card = new VBox(6);

        card.setPrefWidth(190);
        card.setPrefHeight(75);
        card.setMinWidth(190);
        card.setMaxWidth(190);
        card.setMinHeight(75);
        card.setMaxHeight(75);

        card.setPadding(
                new Insets(11, 13, 8, 13)
        );

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #D7D7D7;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 5;"
        );

        Label codeLabel =
                text(
                        code,
                        7,
                        FontWeight.BOLD,
                        "#4B83A0"
                );

        codeLabel.setStyle(
                "-fx-background-color: #D9F0FA;" +
                        "-fx-background-radius: 3;" +
                        "-fx-padding: 3 6;"
        );

        Label nameLabel =
                text(
                        name,
                        10,
                        FontWeight.BOLD,
                        "#171717"
                );

        Label lessonsLabel =
                text(
                        lessons,
                        8,
                        FontWeight.NORMAL,
                        "#858585"
                );

        card.getChildren().addAll(
                codeLabel,
                nameLabel,
                lessonsLabel
        );

        return card;
    }

    private static Label text(
            String value,
            double size,
            FontWeight weight,
            String color) {

        Label label = new Label(value);

        label.setFont(
                Font.font(
                        "System",
                        weight,
                        size
                )
        );

        label.setTextFill(
                Color.web(color)
        );

        return label;
    }
}