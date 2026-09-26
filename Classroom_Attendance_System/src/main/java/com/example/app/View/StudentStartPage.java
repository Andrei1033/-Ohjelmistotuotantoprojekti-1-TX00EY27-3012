package com.example.app.View;

import com.example.app.Model.LoginComponents.User;
import com.example.app.Model.StudentComponents.Course;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.function.Consumer;

public class StudentStartPage extends BorderPane {

    private static final String NAVY = "#202F49";
    private static final String BLUE = "#344A70";

    /**
     * @param currentUser      kirjautunut opiskelija (näytetään sivupalkissa)
     * @param courses          opiskelijan kurssit
     * @param onCourseSelected kutsutaan kun kurssikorttia klikataan
     * @param onLogout         uloskirjautumisen callback
     */
    public StudentStartPage(User currentUser,
                            List<Course> courses,
                            Consumer<Course> onCourseSelected,
                            Runnable onLogout) {

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
        lo.setFont(Font.font("System", FontWeight.BOLD, 11));

        StackPane logoBox = new StackPane(logo, lo);
        logoBox.setPrefSize(20, 20);

        Label brandText = text(
                "Läsnäolo",
                13,
                FontWeight.BOLD,
                "#FFFFFF"
        );

        brand.getChildren().addAll(logoBox, brandText);

        // Etusivulla ei ole paluupainikkeelle käyttöä.
        Button back = new Button("<   Takaisin");
        back.setPrefHeight(22);
        back.setMaxWidth(Double.MAX_VALUE);
        back.setAlignment(Pos.CENTER_LEFT);
        back.setStyle(
                "-fx-background-color: #344A70; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 10px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 4;"
        );
        back.setFocusTraversable(false);
        back.setVisible(false);
        back.setManaged(false);

        Region sideSpacer = new Region();
        VBox.setVgrow(sideSpacer, Priority.ALWAYS);

        // =========================
        // USER INFO
        // =========================

        HBox user = new HBox(7);
        user.setAlignment(Pos.CENTER_LEFT);

        Circle avatar = new Circle(
                10,
                Color.web("#536FA4")
        );

        Label initials = text(
                initialsOf(currentUser),
                10,
                FontWeight.BOLD,
                "#FFFFFF"
        );

        StackPane avatarBox = new StackPane(
                avatar,
                initials
        );

        avatarBox.setPrefSize(20, 20);

        VBox userInfo = new VBox(
                0,
                text(
                        currentUser.getFullName(),
                        10,
                        FontWeight.BOLD,
                        "#FFFFFF"
                ),
                text(
                        roleLabel(currentUser),
                        9,
                        FontWeight.NORMAL,
                        "#C9D0DB"
                )
        );

        user.getChildren().addAll(
                avatarBox,
                userInfo
        );

        sidebar.getChildren().addAll(
                brand,
                new Region(),
                back,
                sideSpacer,
                user
        );

        // =========================
        // MAIN CONTENT
        // =========================

        VBox content = new VBox(0);
        content.setPadding(
                new Insets(41, 30, 20, 31)
        );

        Label heading = text(
                "Omat kurssit",
                15,
                FontWeight.BOLD,
                "#171717"
        );

        Label intro = text(
                introText(courses.size()),
                10,
                FontWeight.NORMAL,
                "#555555"
        );

        VBox headingBox = new VBox(
                2,
                heading,
                intro
        );

        headingBox.setPadding(
                new Insets(0, 0, 18, 0)
        );

        content.getChildren().add(headingBox);

        // =========================
        // COURSES
        // =========================

        if (courses.isEmpty()) {

            content.getChildren().add(
                    text(
                            "Sinulla ei ole vielä kursseja.",
                            11,
                            FontWeight.NORMAL,
                            "#858585"
                    )
            );

        } else {

            FlowPane cards = new FlowPane(
                    22,
                    22
            );

            for (Course course : courses) {
                cards.getChildren().add(
                        courseCard(
                                course,
                                onCourseSelected
                        )
                );
            }

            ScrollPane scroll = new ScrollPane(cards);

            scroll.setFitToWidth(true);

            scroll.setStyle(
                    "-fx-background-color: transparent; " +
                            "-fx-background: transparent;"
            );

            VBox.setVgrow(
                    scroll,
                    Priority.ALWAYS
            );

            content.getChildren().add(scroll);
        }

        // =========================
        // LOGOUT
        // =========================

        Button logout = new Button(
                "Kirjaudu ulos"
        );

        logout.setStyle(
                "-fx-background-color: " + BLUE + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 11px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 4; " +
                        "-fx-padding: 6 14;"
        );

        logout.setCursor(Cursor.HAND);

        logout.setOnAction(
                e -> onLogout.run()
        );

        VBox logoutBox = new VBox(logout);
        logoutBox.setPadding(new Insets(14, 0, 0, 0));
        logoutBox.setAlignment(Pos.CENTER_RIGHT);

        content.getChildren().add(logoutBox);

        // =========================
        // ROOT
        // =========================

        setLeft(sidebar);
        setCenter(content);
    }


    /**
     * Luo yhden kurssikortin.
     *
     * Tärkeä kohta:
     * Kurssikortin klikkaus kutsuu aina onCourseSelected-callbackia
     * kyseisellä Course-oliolla.
     */
    private VBox courseCard(
            Course course,
            Consumer<Course> onCourseSelected) {

        VBox card = new VBox(6);

        card.setPrefSize(
                190,
                75
        );

        card.setMinSize(
                190,
                75
        );

        card.setMaxSize(
                190,
                75
        );

        card.setPadding(
                new Insets(11, 13, 8, 13)
        );

        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #D7D7D7; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;"
        );

        card.setCursor(
                Cursor.HAND
        );

        // =========================
        // COURSE CODE
        // =========================

        Label codeLabel = text(
                course.getCode(),
                9,
                FontWeight.BOLD,
                "#4B83A0"
        );

        codeLabel.setStyle(
                "-fx-background-color: #D9F0FA; " +
                        "-fx-background-radius: 3; " +
                        "-fx-padding: 3 6;"
        );

        // Lapset eivät tarvitse omia hiirikäsittelijöitä.
        codeLabel.setMouseTransparent(true);

        // =========================
        // COURSE NAME
        // =========================

        Label nameLabel = text(
                course.getName(),
                11,
                FontWeight.BOLD,
                "#171717"
        );

        nameLabel.setMouseTransparent(true);

        // =========================
        // LESSON COUNT
        // =========================

        Label hoursLabel = text(
                course.getLessonCount() + " oppituntia",
                9,
                FontWeight.NORMAL,
                "#6B6B6B"
        );

        hoursLabel.setMouseTransparent(true);

        // =========================
        // CARD CONTENT
        // =========================

        card.getChildren().addAll(
                codeLabel,
                nameLabel,
                hoursLabel
        );

        // =========================
        // CLICK HANDLER
        // =========================

        card.setOnMouseClicked(event -> {

            if (event.getButton().name().equals("PRIMARY")) {
                onCourseSelected.accept(course);
                event.consume();
            }

        });

        return card;
    }


    // ============================================================
    // TEXT HELPERS
    // ============================================================

    private static String introText(
            int courseCount) {

        return "Sinulla on "
                + courseCount
                + " kurssia tällä lukukaudella.";
    }


    private static String initialsOf(
            User user) {

        String first =
                (user.getFirstName() == null
                        || user.getFirstName().isEmpty())
                        ? ""
                        : user.getFirstName().substring(0, 1);

        String last =
                (user.getLastName() == null
                        || user.getLastName().isEmpty())
                        ? ""
                        : user.getLastName().substring(0, 1);

        return (
                first + last
        ).toUpperCase();
    }


    private static String roleLabel(
            User user) {

        switch (user.getRole().name()) {

            case "TEACHER":
                return "Opettaja";

            case "ADMIN":
                return "Ylläpitäjä";

            default:
                return "Opiskelija";
        }
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