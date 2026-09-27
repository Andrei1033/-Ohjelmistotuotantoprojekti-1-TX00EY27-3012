package com.example.app.View;

import com.example.app.Controller.TeacherController;
import com.example.app.Model.Lesson;
import com.example.app.Model.Teacher;
import com.example.app.Model.TeacherCourse;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TeacherCoursePage extends BorderPane {

    private static final String NAVY = "#202F49";
    private final TeacherController teacherController;
    private final int courseId;
    private final VBox lessonList;

    public TeacherCoursePage(Teacher teacher,
                             int courseId,
                             TeacherController teacherController,
                             Runnable onBack,
                             Runnable onAddLesson) {
        this.teacherController = teacherController;
        this.courseId = courseId;
        this.lessonList = new VBox(10);

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


        String teacherName = (teacher != null) ? teacher.getFirstName() + " " + teacher.getLastName() : "Etunimi Sukunimi";


        HBox userBox = new HBox(8);
        userBox.setAlignment(Pos.CENTER_LEFT);
        Circle avatarCircle = new Circle(9, Color.web("#4A6594"));

        StackPane avatarStack = new StackPane(avatarCircle);
        VBox userInfo = new VBox(0,
                text(teacherName, 8, FontWeight.BOLD, "#FFFFFF"),
                text("Opettaja", 6, FontWeight.NORMAL, "#A9B0BD")
        );
        userBox.getChildren().addAll(avatarStack, userInfo);

        sidebar.getChildren().addAll(logoBox, coursesButton, spacer, userBox);


        VBox contentBox = new VBox(16);
        contentBox.setPadding(new Insets(25, 30, 25, 30));


        String courseNameText = "Kurssi " + courseId;
        String courseCodeText = "Koodi: " + String.format("%02d", courseId);

        List<TeacherCourse> courses = teacherController.getTeacherCourses((teacher != null) ? teacher.getId() : 1);
        for (TeacherCourse tc : courses) {
            if (tc.getCourseid() == courseId) {
                courseNameText = tc.getCoursename();
                break;
            }
        }

        Label courseTitle = text("Kurssi: " + courseNameText, 16, FontWeight.BOLD, "#202F49");
        Label courseCode = text("Kurssikoodi: " + courseCodeText, 14, FontWeight.NORMAL, "#202F49");
        VBox titleBox = new VBox(2, courseTitle, courseCode);

        Button addLesson = new Button("Lisää oppituntu");
        addLesson.setStyle("-fx-background-color: transparent; -fx-border-color: #D1D5DB; "
                + "-fx-border-radius: 4; -fx-background-radius: 4; -fx-font-size: 8px; -fx-font-weight: bold; -fx-text-fill: #374151; -fx-cursor: hand;");
        addLesson.setOnAction(e -> showCreateLessonDialog());




        Button startLesson = new Button("Aloita oppitunti");
        startLesson.setStyle("-fx-background-color: " + NAVY + "; -fx-text-fill: white; "
                + "-fx-font-size: 8px; -fx-font-weight: bold; -fx-background-radius: 4; -fx-cursor: hand;");
        startLesson.setOnAction(e -> teacherController.startLesson(courseId));

        HBox actionButtons = new HBox(10, addLesson, startLesson);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        HBox headerBar = new HBox(10, titleBox, headerSpacer, actionButtons);


        VBox lessonList = new VBox(10);
        List<Lesson> lessons = teacherController.getLessonsForCourse(courseId);

        if (lessons.isEmpty()) {
            lessonList.getChildren().add(text("Tällä kurssilla ei ole vielä oppitunteja.", 10, FontWeight.NORMAL, "#6B7280"));
        } else {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d.M.yyyy");
            for (Lesson lesson : lessons) {
                String dateStr = lesson.getFormattedData();


                boolean isDone = lesson.isDone();
                String statusText = lesson.getLessonStatus();

                HBox card = createLessonCard(dateStr, "Oppitunti #" + lesson.getId(), statusText, isDone);


                card.setOnMouseClicked(e -> teacherController.openExistingLesson(courseId, lesson.getId()));
                card.setStyle(card.getStyle() + "; -fx-cursor: hand;");

                lessonList.getChildren().add(card);
            }
        }

        contentBox.getChildren().addAll(headerBar, lessonList);

        setLeft(sidebar);
        setCenter(contentBox);
    }

    private HBox createLessonCard(String date, String topic, String statusText, boolean isDone) {
        HBox lessonCard = new HBox();
        lessonCard.setAlignment(Pos.CENTER_LEFT);
        lessonCard.setPadding(new Insets(12, 16, 12, 16));
        lessonCard.setStyle("-fx-background-color: white; -fx-border-color: #E5E7EB; "
                + "-fx-border-radius: 8; -fx-background-radius: 8;");

        VBox dateBox = new VBox(1,
                text(date, 9, FontWeight.BOLD, "#111827")
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

    private void refreshLessons() {
        lessonList.getChildren().clear();
        List<Lesson> lessons = teacherController.getLessonsForCourse(courseId);
        if (lessons.isEmpty()) {
            lessonList.getChildren().add(text("Tällä kurssilla ei ole oppituntia",10, FontWeight.NORMAL, "#6B7280"));

        }else {
            
        }
    }

    private static Label text(String content, int size, FontWeight weight, String color) {
        Label label = new Label(content);
        label.setFont(Font.font("System", weight, size));
        label.setTextFill(Color.web(color));
        return label;
    }

    private void showCreateLessonDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Luo uusi oppitunti");
        dialog.setHeaderText("Aika");

        DatePicker startDate = new DatePicker(LocalDate.now());
        TextField startTime = new TextField("00:00");


        DatePicker endDate = new DatePicker(LocalDate.now());
        TextField endTime = new TextField("00:00");



        VBox dialogForm = new VBox(10,
                new Label("Alkamisaika:"), new HBox(10, startDate, startTime),
                new Label("Päättymisaika:"), new HBox(10, endDate, endTime)
        ); dialogForm.setPadding(new Insets(15));

        dialog.getDialogPane().setContent(dialogForm);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(e -> {
            if(e == ButtonType.OK) {
                try {
                    LocalDateTime start = LocalDateTime.of(startDate.getValue(), LocalTime.parse(startTime.getText()));
                    LocalDateTime end = LocalDateTime.of(endDate.getValue(), LocalTime.parse(endTime.getText()));

                    if (teacherController.createLesson(start, end, courseId)) {
                        refreshLessons();
                    }

                } catch (Exception ex) {
                    System.err.println("Tarkista kello");
                }
            }
        });



    }
}