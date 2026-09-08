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


public class TeacherCoursePage extends BorderPane {

    private static final String NAVY = "#202F49";

    public TeacherCoursePage(Runnable onBack, Runnable onStartLesson, Runnable onAddStudents) {

        setStyle("-fx-background-color: white;");

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(160);
        sidebar.setPadding(new Insets(15,12,12,12));
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");

        HBox logoBox = new HBox(8);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        Circle logoCircle = new Circle(9, Color.web("#4A6594"));
        Label logoText = text("LO", 8, FontWeight.BOLD, "#FFFFFF");
        StackPane logoStack = new StackPane(logoCircle, logoText);
        Label brand = text("Läsnäolo", 11, FontWeight.BOLD, "#FFFFFF");
        logoBox.getChildren().addAll(logoStack, brand);

        Button coursesButton = new Button("•   Omat kurssit");
        coursesButton.setPrefHeight(26);
        coursesButton.setMaxWidth(Double.MAX_VALUE);
        coursesButton.setAlignment(Pos.CENTER_LEFT);
        coursesButton.setStyle("-fx-background-color: #31425F; -fx-text-fill: white; "
                + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 4; -fx-cursor: hand;");
        coursesButton.setOnAction(e -> onBack.run());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox userBox = new HBox(8);
        userBox.setAlignment(Pos.CENTER_LEFT);
        Circle avatarCircle = new Circle(9, Color.web("#4A6594"));
        Label initials = text("MA", 8, FontWeight.BOLD, "#FFFFFF");
        StackPane avatarStack = new StackPane(avatarCircle, initials);
        VBox userInfo = new VBox(0,
                text("Etunimi Sukunimi", 8, FontWeight.BOLD, "#FFFFFF"),
                text("Opettaja", 6, FontWeight.NORMAL, "#A9B0BD")
        );
        userBox.getChildren().addAll(avatarStack, userInfo);

        sidebar.getChildren().addAll(logoBox, coursesButton, spacer, userBox);

        VBox contentBox = new VBox(16);
        contentBox.setPadding(new Insets(25,30, 25,30));

        Label courseTitle = text("Kurssi: Ohjelmoinnin perusteet", 16, FontWeight.BOLD, "#202F49");
        Label courseCode = text("Kurssikoodi: CS101", 14, FontWeight.NORMAL, "#202F49");
        VBox titleBox = new VBox(2, courseTitle, courseCode);

        Button addStudent = new Button("Lisää opiskelijoita");
        addStudent.setStyle("-fx-background-color: transparent; -fx-border-color: #D1D5DB; "
                + "-fx-border-radius: 4; -fx-background-radius: 4; -fx-font-size: 8px; -fx-font-weight: bold; -fx-text-fill: #374151; -fx-cursor: hand;");
        addStudent.setOnAction(e -> onAddStudents.run());

        Button startLesson = new Button("Aloita oppitunti");
        startLesson.setStyle("-fx-background-color: " + NAVY + "; -fx-text-fill: white; "
                + "-fx-font-size: 8px; -fx-font-weight: bold; -fx-background-radius: 4; -fx-cursor: hand;");
        startLesson.setOnAction(e -> onStartLesson.run());

        HBox actionButtons = new HBox(10, addStudent, startLesson);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        Region headerSpacer = new Region();
        VBox.setVgrow(headerSpacer, Priority.ALWAYS);

        HBox headerBar = new HBox(10, titleBox, headerSpacer, actionButtons);
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        VBox LessonList = new VBox(10);
        LessonList.getChildren().addAll(
                createLessonCard("2.9.2026", "Ma", "Muuttujat ja tietotyypit", "Merkitty", true),
                createLessonCard("2.9.2026", "Ke", "Muuttujat ja tietotyypit", "Merkitty", true),
                createLessonCard("2.9.2026", "Pe", "Muuttujat ja tietotyypit", "Käynnissä nyt", false)
        );
        contentBox.getChildren().addAll(headerBar, LessonList);

        setLeft(sidebar);
        setCenter(contentBox);



    }
    private HBox createLessonCard(String date, String day, String topic, String statusText, boolean isDone) {
        HBox lessonCard = new HBox();
        lessonCard.setAlignment(Pos.CENTER_LEFT);
        lessonCard.setPadding(new Insets(12, 16, 12, 16));
        lessonCard.setStyle("-fx-background-color: white; -fx-border-color: #E5E7EB; "
                + "-fx-border-radius: 8; -fx-background-radius: 8;");

        VBox dateBox = new VBox(1,
                text(date, 9, FontWeight.BOLD, "#111827"),
                text(day, 7, FontWeight.NORMAL, "#6B7280")
        );
        dateBox.setPrefWidth(90);

        Label topicLabel = text(topic, 10, FontWeight.BOLD, "#111827");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox status = new HBox(5);
        status.setAlignment(Pos.CENTER);
        status.setPadding(new Insets(4, 12, 4, 12));

        if (isDone) {

        Circle dot = new Circle(2.5, Color.web("#16A34A"));
        Label label = text(statusText, 8, FontWeight.BOLD, "#166534");
        status.getChildren().addAll(dot, label);
        status.setStyle("-fx-background-color: #DCFCE7; -fx-background-radius: 12; -fx-border-color: #BBF7D0; -fx-border-radius: 12;");
    } else {
            Label label = text(statusText, 8, FontWeight.BOLD, "#374151");
            status.getChildren().add(label);
            status.setStyle("-fx-background-color: #F3F4F6; -fx-background-radius: 12; -fx-border-color: #E5E7EB; -fx-border-radius: 12;");
        }
        lessonCard.getChildren().addAll(dateBox, topicLabel, spacer, status);
        return lessonCard;

    }
    private static Label text(String content, int size, FontWeight weight, String color) {
        Label label = new Label(content);
        label.setFont(Font.font("System", weight, size));
        label.setTextFill(Color.web(color));
        return label;
    }



}
