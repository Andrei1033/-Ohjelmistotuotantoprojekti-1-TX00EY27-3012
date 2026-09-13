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
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");

        HBox brand = new HBox(9);
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPadding(new Insets(0, 0, 20, 0));

        Circle logo = new Circle(10, Color.web("#536FA4"));
        Label lo = new Label("LO");
        lo.setTextFill(Color.WHITE);
        lo.setFont(Font.font("System", FontWeight.BOLD, 9));
        StackPane logoBox = new StackPane(logo, lo);
        logoBox.setPrefSize(20, 20);

        Label brandText = text("Läsnäolo", 11, FontWeight.BOLD, "#FFFFFF");
        brand.getChildren().addAll(logoBox, brandText);

        Button courses = new Button("•   Omat kurssit");
        courses.setPrefHeight(22);
        courses.setMaxWidth(Double.MAX_VALUE);
        courses.setAlignment(Pos.CENTER_LEFT);
        courses.setStyle("-fx-background-color: #344A70; -fx-text-fill: white; "
                + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 4;");
        courses.setFocusTraversable(false);

        Region sideSpacer = new Region();
        VBox.setVgrow(sideSpacer, Priority.ALWAYS);

        HBox user = new HBox(7);
        user.setAlignment(Pos.CENTER_LEFT);
        Circle avatar = new Circle(10, Color.web("#536FA4"));
        Label initials = text("MA", 8, FontWeight.BOLD, "#FFFFFF");
        StackPane avatarBox = new StackPane(avatar, initials);
        avatarBox.setPrefSize(20, 20);

        VBox userInfo = new VBox(0,
                text("Etunimi Sukunimi", 8, FontWeight.BOLD, "#FFFFFF"),
                text("Opiskelija", 6, FontWeight.NORMAL, "#A9B0BD")
        );
        user.getChildren().addAll(avatarBox, userInfo);

        sidebar.getChildren().addAll(brand, new Region(), courses, sideSpacer, user);

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
        summary.setAlignment(Pos.CENTER_RIGHT);

        Label presentCount = text("1 paikalla", 8, FontWeight.BOLD, "#171717");
        Label lateCount = text("1 myöhässä", 8, FontWeight.BOLD, "#171717");
        Label absentCount = text("1 Poissa", 8, FontWeight.BOLD, "#171717");

        summary.getChildren().addAll(presentCount, lateCount, absentCount);

        HBox header = new HBox();
        header.setAlignment(Pos.BOTTOM_LEFT);

        HBox.setHgrow(titleBox, Priority.ALWAYS);

// Tämä työntää summaryn oikealle
        Region pushRight = new Region();
        HBox.setHgrow(pushRight, Priority.ALWAYS);

        header.getChildren().addAll(titleBox, pushRight, summary);

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

        Region pushRight = new Region();
        HBox.setHgrow(pushRight, Priority.ALWAYS);

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
                pushRight,
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
