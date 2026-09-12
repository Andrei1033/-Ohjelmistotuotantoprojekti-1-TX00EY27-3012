package com.example.app.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class StudentAttendanceTracking extends BorderPane {

    private static final String NAVY = "#202F49";
    private static final String BLUE = "#344A70";

    public StudentAttendanceTracking(Runnable onBack) {

        setStyle("-fx-background-color: white;");

        // =========================
        // SIDEBAR
        // =========================

        VBox sidebar = new VBox();

        sidebar.setPrefWidth(158);
        sidebar.setPadding(new Insets(12, 14, 10, 10));

        sidebar.setStyle(
                "-fx-background-color: " + NAVY + ";"
        );

        // Logo + Läsnäolo
        HBox brand = new HBox(9);
        brand.setAlignment(Pos.CENTER_LEFT);

        Circle logoCircle = new Circle(
                10,
                Color.web("#536FA4")
        );

        Label logoText = new Label("LO");
        logoText.setTextFill(Color.WHITE);
        logoText.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        9
                )
        );

        StackPane logo = new StackPane(
                logoCircle,
                logoText
        );

        logo.setPrefSize(20, 20);

        Label brandName = text(
                "Läsnäolo",
                11,
                FontWeight.BOLD,
                "#FFFFFF"
        );

        brand.getChildren().addAll(
                logo,
                brandName
        );

        // Kurssini-painike
        ButtonStyle courseButton = new ButtonStyle();

        sidebar.getChildren().add(brand);

        Region topSpace = new Region();
        topSpace.setPrefHeight(15);

        sidebar.getChildren().add(topSpace);

        javafx.scene.control.Button courseButtonNode =
                new javafx.scene.control.Button(
                        "•   Kurssini"
                );

        courseButtonNode.setPrefHeight(22);
        courseButtonNode.setMaxWidth(
                Double.MAX_VALUE
        );

        courseButtonNode.setAlignment(
                Pos.CENTER_LEFT
        );

        courseButtonNode.setStyle(
                "-fx-background-color: #344A70;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 4;"
        );

        courseButtonNode.setFocusTraversable(false);

        // Tällä voidaan myöhemmin palata kurssilistaan
        courseButtonNode.setOnAction(
                e -> onBack.run()
        );

        sidebar.getChildren().add(courseButtonNode);

        // Tyhjä tila ennen käyttäjää
        Region spacer = new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        sidebar.getChildren().add(spacer);

        // =========================
        // USER
        // =========================

        HBox user = new HBox(7);

        user.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle avatarCircle = new Circle(
                10,
                Color.web("#536FA4")
        );

        Label avatarText = new Label("MA");

        avatarText.setTextFill(Color.WHITE);

        avatarText.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        8
                )
        );

        StackPane avatar = new StackPane(
                avatarCircle,
                avatarText
        );

        avatar.setPrefSize(20, 20);

        VBox userInfo = new VBox(
                0,
                text(
                        "Etunimi Sukunimi",
                        8,
                        FontWeight.BOLD,
                        "#FFFFFF"
                ),
                text(
                        "Opiskelija",
                        6,
                        FontWeight.NORMAL,
                        "#A9B0BD"
                )
        );

        user.getChildren().addAll(
                avatar,
                userInfo
        );

        sidebar.getChildren().add(user);

        // =========================
        // MAIN CONTENT
        // =========================

        VBox content = new VBox();

        content.setPadding(
                new Insets(
                        41,
                        24,
                        20,
                        25
                )
        );

        // Otsikko
        Label title = text(
                "Ohjelmoinnin perusteet",
                15,
                FontWeight.BOLD,
                "#171717"
        );

        Label subtitle = text(
                "TX00CV70 - omat läsnäolomerkinnät",
                8,
                FontWeight.BOLD,
                "#171717"
        );

        VBox titleBox = new VBox(
                2,
                title,
                subtitle
        );

        // Yhteenveto oikealle
        HBox summary = new HBox(20);

        summary.setAlignment(
                Pos.CENTER_RIGHT
        );

        Label presentCount = text(
                "1 paikalla",
                8,
                FontWeight.BOLD,
                "#171717"
        );

        Label lateCount = text(
                "1 myöhässä",
                8,
                FontWeight.BOLD,
                "#171717"
        );

        Label absentCount = text(
                "1 Poissa",
                8,
                FontWeight.BOLD,
                "#171717"
        );

        summary.getChildren().addAll(
                presentCount,
                lateCount,
                absentCount
        );

        HBox header = new HBox();

        header.setAlignment(
                Pos.BOTTOM_LEFT
        );

        HBox.setHgrow(
                titleBox,
                Priority.ALWAYS
        );

        header.getChildren().addAll(
                titleBox,
                summary
        );

        content.getChildren().add(header);

        // =========================
        // ATTENDANCE ROWS
        // =========================

        Region line = new Region();

        line.setPrefHeight(1);

        line.setStyle(
                "-fx-background-color: #EEEEEE;"
        );

        VBox attendanceList = new VBox(
                14
        );

        attendanceList.setPadding(
                new Insets(
                        16,
                        0,
                        0,
                        0
                )
        );

        // Paikalla
        attendanceList.getChildren().add(
                attendanceRow(
                        "2.9.2026",
                        "Ke",
                        "Muuttujat ja tietotyypit",
                        "paikalla",
                        "#2E9560",
                        "#E5F4EA"
                )
        );

        // Myöhässä
        attendanceList.getChildren().add(
                attendanceRow(
                        "2.9.2026",
                        "Ke",
                        "Muuttujat ja tietotyypit",
                        "myöhässä",
                        "#C77A00",
                        "#FFF2DF"
                )
        );

        // Poissa
        attendanceList.getChildren().add(
                attendanceRow(
                        "2.9.2026",
                        "Ke",
                        "Muuttujat ja tietotyypit",
                        "poissa",
                        "#C44D3A",
                        "#FBE6E2"
                )
        );

        content.getChildren().addAll(
                line,
                attendanceList
        );

        setLeft(sidebar);
        setCenter(content);
    }

    // =====================================================
    // ATTENDANCE ROW
    // =====================================================

    private HBox attendanceRow(
            String date,
            String day,
            String lesson,
            String status,
            String statusColor,
            String statusBackground
    ) {

        HBox row = new HBox();

        row.setPrefHeight(58);

        row.setMaxWidth(
                Double.MAX_VALUE
        );

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(
                        8,
                        9,
                        8,
                        8
                )
        );

        row.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #D7D7D7;" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;"
        );

        // -------------------------
        // DATE
        // -------------------------

        VBox dateBox = new VBox(
                0,
                text(
                        date,
                        10,
                        FontWeight.BOLD,
                        "#171717"
                ),
                text(
                        day,
                        7,
                        FontWeight.BOLD,
                        "#555555"
                )
        );

        dateBox.setPrefWidth(95);

        // -------------------------
        // LESSON
        // -------------------------

        Label lessonLabel = text(
                lesson,
                10,
                FontWeight.BOLD,
                "#666666"
        );

        HBox.setHgrow(
                lessonLabel,
                Priority.ALWAYS
        );

        // -------------------------
        // STATUS
        // -------------------------

        HBox statusBox = new HBox(10);

        statusBox.setPrefWidth(120);

        statusBox.setPrefHeight(28);

        statusBox.setAlignment(
                Pos.CENTER_LEFT
        );

        statusBox.setPadding(
                new Insets(
                        0,
                        12,
                        0,
                        12
                )
        );

        statusBox.setStyle(
                "-fx-background-color: " +
                        statusBackground +
                        ";" +
                        "-fx-border-color: " +
                        statusColor +
                        ";" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;"
        );

        Circle statusCircle = new Circle(
                4,
                Color.web(statusColor)
        );

        Label statusLabel = text(
                status,
                10,
                FontWeight.BOLD,
                statusColor
        );

        statusBox.getChildren().addAll(
                statusCircle,
                statusLabel
        );

        row.getChildren().addAll(
                dateBox,
                lessonLabel,
                statusBox
        );

        return row;
    }

    // =====================================================
    // TEXT HELPER
    // =====================================================

    private static Label text(
            String value,
            double size,
            FontWeight weight,
            String color
    ) {

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

    // Tyhjä apuluokka, jotta sidebar-rakenne pysyy selkeänä.
    private static class ButtonStyle {
    }
}
