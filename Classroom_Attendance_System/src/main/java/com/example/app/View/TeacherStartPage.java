package com.example.app.View;


import com.example.app.Controller.TeacherController;


import com.example.app.Model.TeacherCourse;
import com.example.app.Model.Teacher;
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




    public TeacherStartPage(Teacher teacher, TeacherController teacherController, Runnable onLogout) {


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


        Button logout = new Button("Kirjaudu ulos");
        logout.setMaxWidth(Double.MAX_VALUE);
        logout.setStyle("-fx-background-color: transparent; -fx-text-fill: #A9B0BD; "
                + "-fx-font-size: 9px; -fx-alignment: CENTER-LEFT; -fx-cursor: hand;");
        logout.setOnAction(e -> {
            if(onLogout != null) {
                onLogout.run();
            }
        });






        String teacherName = (teacher != null) ? teacher.getFirstName() + " " + teacher.getLastName() : "Etunimi Sukunimi";




        VBox userInfo = new VBox(0,
                text(teacherName, 8, FontWeight.BOLD, "#FFFFFF"),
                text("Opettaja", 6, FontWeight.NORMAL, "#A9B0BD")
        );






        HBox user = new HBox(7);
        user.setAlignment(Pos.CENTER_LEFT);
        Circle avatar = new Circle(10, Color.web("#536FA4"));


        StackPane avatarBox = new StackPane(avatar);
        avatarBox.setPrefSize(20, 20);


        user.getChildren().addAll(avatarBox, userInfo);


        sidebar.getChildren().addAll(hBox, courses, sideSpacer, logout, user);


        VBox content = new VBox(0);
        content.setPadding(new Insets(41, 30, 20, 31));


        int teacherId = (teacher != null) ? teacher.getId(): 1;
        List<TeacherCourse> teacherCours = teacherController.getTeacherCourses(teacherId);


        Label heading = text("Omat kurssit", 15, FontWeight.BOLD, "#171717");
        Label intro = text("Sinulla on " + teacherCours.size() + " Kurssia  tällä lukukaudella", 8, FontWeight.BOLD, "#171717");


        VBox headingBox = new VBox(2, heading, intro);
        headingBox.setPadding(new Insets(0, 0, 18, 0));


        HBox cards = new HBox(22);
        if (teacherCours.isEmpty()) {
            cards.getChildren().add(text("Ei aktiivisia kursseja tällä lukukaudella",8,FontWeight.NORMAL, "#858585"));
        } else {


            for (TeacherCourse teacherCourse : teacherCours) {
                int lessonCount = teacherController.getLessonsForCourse(teacherCourse.getCourseid()).size();
                String courseCode = String.format("%02d", teacherCourse.getCourseid());
                String courseName = teacherCourse.getCoursename();
                String lessonsText = lessonCount + " Oppituntia";


                VBox card = courseCard(
                        courseCode,
                        courseName,
                        lessonsText,
                        () -> teacherController.startLesson(teacherCourse.getCourseid()),
                        () -> teacherController.openCoursePage(teacherCourse.getCourseid())
                );


                cards.getChildren().add(card);
            }
        }




        content.getChildren().addAll(headingBox, cards);




        setLeft(sidebar);
        setCenter(content);




    }






    private VBox courseCard(String code, String name, String hours, Runnable onStartLesson, Runnable onOpenCourse) {
        VBox card = new VBox(6);
        card.setPrefSize(190, 75);
        card.setPadding(new Insets(11, 13, 8, 13));
        card.setStyle("-fx-background-color: white; -fx-border-color: #D7D7D7; "
                + "-fx-border-radius: 5; -fx-background-radius: 5;");


        card.setOnMouseClicked(e -> {
            if (onOpenCourse!=null) {
                onOpenCourse.run();
            }
        });


        Label codeLabel = text(code, 7, FontWeight.BOLD, "#4B83A0");
        codeLabel.setStyle("-fx-background-color: #D9F0FA; -fx-background-radius: 3; -fx-padding: 3 6;");


        Label nameLabel = text(name, 10, FontWeight.BOLD, "#171717");
        Label hoursLabel = text(hours, 8, FontWeight.NORMAL, "#858585");


        Button startbtn = new Button("Aloita oppitunti");
        startbtn.setMaxWidth(Double.MAX_VALUE);
        startbtn.setStyle("-fx-background-color: " + NAVY + "; -fx-text-fill: white; "
                + "-fx-font-size: 8px; -fx-font-weight: bold; -fx-background-radius: 3; -fx-cursor: hand;");
        startbtn.setOnAction(e -> {
            if(onStartLesson != null) {
                onStartLesson.run();
            }
        });






        card.getChildren().addAll(codeLabel, nameLabel, hoursLabel, startbtn);
        return card;
    }


    private static Label text(String value, double size, FontWeight weight, String color) {
        Label l = new Label(value);
        l.setFont(Font.font("System", weight, size));
        l.setTextFill(Color.web(color));
        return l;
    }




}
