package com.example.app.View;

import com.example.app.Controller.TeacherController;
import com.example.app.Model.LoginComponents.User;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherCoursePage extends BorderPane {

    private static final String NAVY = "#202F49";
    private final TeacherController teacherController;
    private final int courseId;
    private final VBox studentList;

    public TeacherCoursePage(Teacher teacher,
                             int courseId,
                             TeacherController teacherController,
                             Runnable onBack,
                             Runnable onAddLesson) {
        this.teacherController = teacherController;
        this.courseId = courseId;
        this.studentList = new VBox(10);

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

        Button addStudent = new Button("Lisää opiskelija");
        addStudent.setStyle("-fx-background-color: transparent; -fx-border-color: #D1D5DB; "
                + "-fx-border-radius: 4; -fx-background-radius: 4; -fx-font-size: 8px; -fx-font-weight: bold; -fx-text-fill: #374151; -fx-cursor: hand;");
        addStudent.setOnAction(e -> ShowaddStudent());




        Button startLesson = new Button("Aloita oppitunti");
        startLesson.setStyle("-fx-background-color: " + NAVY + "; -fx-text-fill: white; "
                + "-fx-font-size: 8px; -fx-font-weight: bold; -fx-background-radius: 4; -fx-cursor: hand;");
        startLesson.setOnAction(e -> teacherController.startLesson(courseId));

        HBox actionButtons = new HBox(10, addStudent, startLesson);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        HBox headerBar = new HBox(10, titleBox, headerSpacer, actionButtons);


        Label studentsHeader = text("Opiskelijat", 12, FontWeight.BOLD, "#111827");
        refreshStudents();


        contentBox.getChildren().addAll(headerBar, studentList,studentsHeader);

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

    private static Label text(String content, int size, FontWeight weight, String color) {
        Label label = new Label(content);
        label.setFont(Font.font("System", weight, size));
        label.setTextFill(Color.web(color));
        return label;
    }


    private void refreshStudents() {

        studentList.getChildren().clear();
        List<User> students = teacherController.getStudentsForCourse(courseId);

        if (students.isEmpty()) {
            studentList.getChildren().add(text("Tälle kurssille ei ole vielä lisätty opiskelijoita.", 10, FontWeight.NORMAL, "#6B7280"));
            return;
        }
        students.forEach(student -> {
            HBox card = new HBox(
                    new VBox(2, text(student.getFullName(), 10, FontWeight.BOLD, "#111827"),
                            text("ID: " + student.getId(), 8, FontWeight.NORMAL, "#6B7280"))
            );
            card.setPadding(new Insets(10, 14, 10, 14));
            card.setStyle("-fx-background-color: white; -fx-border-color: #E5E7EB; -fx-border-radius: 6; -fx-background-radius: 6;");
            studentList.getChildren().add(card);
        });
    }



    private void ShowaddStudent() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Lisää opiskelijoita");

        List<User> allStudents = teacherController.getAllStudents(courseId);
        Map<User, CheckBox> selectedMap = new HashMap<>();

        TextField searchField = new TextField();
        searchField.setPromptText("Hae opiskelijaa");

        ListView<User> listView = new ListView<>();
        listView.getItems().addAll(allStudents);
        listView.setPrefHeight(300);

        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setGraphic(null);
                } else {
                    CheckBox cb = selectedMap.computeIfAbsent(user, u -> new CheckBox());
                    HBox row = new HBox(10, cb, new Label(user.getFullName()));
                    row.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(row);
                }
            }
        });


        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal.toLowerCase().trim();
            listView.getItems().setAll(allStudents.stream()
                    .filter(u -> u.getFullName().toLowerCase().contains(filter))
                    .toList());
        });

        VBox layout = new VBox(10, text("Lisää opiskelijoita", 14, FontWeight.BOLD, "#111827"), searchField, listView);
        layout.setPadding(new Insets(15));
        layout.setPrefWidth(350);

        ButtonType saveBtn = new ButtonType("Tallenna", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().setContent(layout);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, saveBtn);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == saveBtn) {
                List<Integer> ids = selectedMap.entrySet().stream()
                        .filter(e -> e.getValue().isSelected())
                        .map(e -> e.getKey().getId())
                        .toList();

                if (!ids.isEmpty() && teacherController.addStudents(ids, courseId)) {
                    refreshStudents();
                }
            }
        });
    }





    }





