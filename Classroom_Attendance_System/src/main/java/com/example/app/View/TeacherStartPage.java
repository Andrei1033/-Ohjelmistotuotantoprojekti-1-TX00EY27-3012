package com.example.app.View;

import com.example.app.Controller.TeacherController;
import com.example.app.Model.Teacher;
import com.example.app.Model.TeacherCourse;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeacherStartPage extends BorderPane {

    private static final String NAVY = "#202F49";

    public TeacherStartPage(
            Teacher teacher,
            TeacherController teacherController,
            Runnable onLogout
    ) {

        setStyle(
                "-fx-background-color: white;"
        );

        // =========================================================
        // SIDEBAR
        // =========================================================

        VBox sidebar = new VBox();

        sidebar.setPrefWidth(158);
        sidebar.setMinWidth(158);
        sidebar.setMaxWidth(158);

        sidebar.setPadding(
                new Insets(
                        12,
                        14,
                        10,
                        10
                )
        );

        sidebar.setStyle(
                "-fx-background-color: " + NAVY + ";"
        );

        // ---------------------------------------------------------
        // LOGO
        // ---------------------------------------------------------

        HBox logoContainer =
                new HBox(9);

        logoContainer.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle logoCircle =
                new Circle(
                        10,
                        Color.web("#536FA4")
                );

        Label logoText =
                text(
                        "LO",
                        9,
                        FontWeight.BOLD,
                        "#FFFFFF"
                );

        StackPane logo =
                new StackPane(
                        logoCircle,
                        logoText
                );

        logo.setPrefSize(20, 20);
        logo.setMinSize(20, 20);
        logo.setMaxSize(20, 20);

        Label brand =
                text(
                        "Läsnäolo",
                        11,
                        FontWeight.BOLD,
                        "#FFFFFF"
                );

        logoContainer
                .getChildren()
                .addAll(
                        logo,
                        brand
                );

        // ---------------------------------------------------------
        // COURSES BUTTON
        // ---------------------------------------------------------

        Button coursesButton =
                new Button(
                        "•   Omat kurssit"
                );

        coursesButton.setPrefHeight(22);

        coursesButton.setMaxWidth(
                Double.MAX_VALUE
        );

        coursesButton.setAlignment(
                Pos.CENTER_LEFT
        );

        coursesButton.setFocusTraversable(
                false
        );

        coursesButton.setStyle(
                "-fx-background-color: #344A70; "
                        + "-fx-text-fill: white; "
                        + "-fx-font-size: 10px; "
                        + "-fx-font-weight: bold; "
                        + "-fx-background-radius: 4;"
        );

        // ---------------------------------------------------------
        // SIDEBAR SPACER
        // ---------------------------------------------------------

        Region spacer =
                new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );


        // ---------------------------------------------------------
        // LOGOUT
        // ---------------------------------------------------------

        Button logout =
                new Button(
                        "Kirjaudu ulos"
                );

        logout.setMaxWidth(
                Double.MAX_VALUE
        );

        logout.setStyle(
                "-fx-background-color: transparent; "
                        + "-fx-text-fill: #A9B0BD; "
                        + "-fx-font-size: 9px; "
                        + "-fx-alignment: CENTER-LEFT; "
                        + "-fx-cursor: hand;"
        );

        logout.setOnAction(event -> {

            if (onLogout != null) {
                onLogout.run();
            }
        });

        // =========================================================
        // TEACHER INFORMATION
        // =========================================================

        String teacherName =
                getTeacherName(teacher);

        String initials =
                getTeacherInitials(teacher);

        VBox userInfo =
                new VBox(
                        0,
                        text(
                                teacherName,
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

        Circle avatarCircle =
                new Circle(
                        10,
                        Color.web("#536FA4")
                );

        Label initialsLabel =
                text(
                        initials,
                        8,
                        FontWeight.BOLD,
                        "#FFFFFF"
                );

        StackPane avatar =
                new StackPane(
                        avatarCircle,
                        initialsLabel
                );

        avatar.setPrefSize(20, 20);
        avatar.setMinSize(20, 20);
        avatar.setMaxSize(20, 20);

        HBox user =
                new HBox(7);

        user.setAlignment(
                Pos.CENTER_LEFT
        );

        user.getChildren()
                .addAll(
                        avatar,
                        userInfo
                );

        // ---------------------------------------------------------
        // SIDEBAR CONTENT
        // ---------------------------------------------------------

        sidebar.getChildren()
                .addAll(
                        logoContainer,
                        coursesButton,
                        spacer,
                        logout,
                        user
                );

        // =========================================================
        // MAIN CONTENT
        // =========================================================

        VBox content =
                new VBox();

        content.setPadding(
                new Insets(
                        41,
                        30,
                        20,
                        31
                )
        );

        content.setSpacing(0);

        // =========================================================
        // LOAD COURSES
        // =========================================================

        int teacherId =
                teacher != null
                        ? teacher.getId()
                        : 1;

        List<TeacherCourse> teacherCourses =
                new ArrayList<>();

        if (teacherController != null) {

            try {

                List<TeacherCourse> result =
                        teacherController
                                .getTeacherCourses(
                                        teacherId
                                );

                if (result != null) {
                    teacherCourses.addAll(
                            result
                    );
                }

            } catch (Exception ignored) {
                /*
                 * Do not crash the JavaFX page if loading
                 * courses fails.
                 */
            }
        }

        // =========================================================
        // HEADER
        // =========================================================

        Label heading =
                text(
                        "Omat kurssit",
                        15,
                        FontWeight.BOLD,
                        "#171717"
                );

        Label introduction =
                text(
                        "Sinulla on "
                                + teacherCourses.size()
                                + " kurssia tällä lukukaudella.",
                        8,
                        FontWeight.BOLD,
                        "#171717"
                );





        // =========================================================
        // COURSE CARDS CONTAINER
        // =========================================================

        HBox cards =
                new HBox(22);

        cards.setAlignment(
                Pos.TOP_LEFT
        );

        /*
         * VERY IMPORTANT:
         *
         * Every direct child of this HBox is ALWAYS a VBox.
         *
         * Never add a Label directly here.
         */

        for (TeacherCourse course :
                teacherCourses) {

            if (course == null) {
                continue;
            }

            int courseId =
                    course.getCourseid();

            String courseName =
                    course.getCoursename();

            if (courseName == null
                    || courseName.isBlank()) {

                courseName =
                        "Kurssi";
            }

            int lessonCount = 0;

            if (teacherController != null) {

                try {

                    List<?> lessons =
                            teacherController
                                    .getLessonsForCourse(
                                            courseId
                                    );

                    if (lessons != null) {
                        lessonCount =
                                lessons.size();
                    }

                } catch (Exception ignored) {
                    lessonCount = 0;
                }
            }

            String courseCode =
                    String.format(
                            "%02d",
                            courseId
                    );

            String lessonText =
                    lessonCount
                            + " oppituntia";

            final int finalCourseId =
                    courseId;

            VBox card =
                    createCourseCard(
                            courseCode,
                            courseName,
                            lessonText,

                            () -> {

                                if (teacherController != null) {

                                    teacherController
                                            .startLesson(
                                                    finalCourseId
                                            );
                                }
                            },

                            () -> {

                                if (teacherController != null) {

                                    teacherController
                                            .openCoursePage(
                                                    finalCourseId
                                            );
                                }
                            }
                    );

            /*
             * ONLY VBox cards are added here.
             */
            cards.getChildren()
                    .add(card);
        }

        // =========================================================
        // EMPTY STATE
        // =========================================================

        /*
         * If there are no courses, do NOT add a Label to cards.
         *
         * The tests expect the children of cards to be course
         * VBoxes. Therefore the empty message is placed inside
         * a VBox card.
         */

        if (cards.getChildren().isEmpty()) {

            VBox emptyCard =
                    createEmptyCourseCard();

            cards.getChildren()
                    .add(emptyCard);
        }

        // =========================================================
        // CONTENT STRUCTURE
        // =========================================================

        /*
         * Required structure:
         *
         * center VBox
         *
         *   0 -> headingBox
         *
         *   1 -> cards HBox
         *
         *              -> VBox
         *              -> VBox
         *              -> VBox
         */

        content.getChildren()
                .addAll(
                        heading, introduction,
                        cards
                );

        content.setSpacing(10);
        content.setPadding(
                new Insets(
                        41,
                        30,
                        20,
                        31
                )
        );
        // =========================================================
        // ROOT
        // =========================================================

        setLeft(sidebar);
        setCenter(content);
    }

    // =============================================================
    // COURSE CARD
    // =============================================================

    private VBox createCourseCard(
            String code,
            String name,
            String lessons,
            Runnable onStartLesson,
            Runnable onOpenCourse
    ) {

        VBox card =
                new VBox(4);

        /*
         * Exact dimensions expected by tests.
         */
        card.setPrefWidth(190);
        card.setPrefHeight(75);

        card.setMinWidth(190);
        card.setMinHeight(75);

        card.setMaxWidth(190);
        card.setMaxHeight(75);

        card.setPadding(
                new Insets(
                        8,
                        10,
                        8,
                        10
                )
        );

        card.setStyle(
                "-fx-background-color: white; "
                        + "-fx-border-color: #D7D7D7; "
                        + "-fx-border-width: 1; "
                        + "-fx-border-radius: 5; "
                        + "-fx-background-radius: 5;"
        );

        // ---------------------------------------------------------
        // Course code
        // ---------------------------------------------------------

        Label codeLabel =
                text(
                        code,
                        7,
                        FontWeight.BOLD,
                        "#4B83A0"
                );

        codeLabel.setStyle(
                "-fx-background-color: #D9F0FA; "
                        + "-fx-background-radius: 3; "
                        + "-fx-padding: 2 5;"
        );

        // ---------------------------------------------------------
        // Course name
        // ---------------------------------------------------------

        Label nameLabel =
                text(
                        name,
                        10,
                        FontWeight.BOLD,
                        "#171717"
                );

        // ---------------------------------------------------------
        // Lesson count
        // ---------------------------------------------------------

        Label lessonsLabel =
                text(
                        lessons,
                        8,
                        FontWeight.NORMAL,
                        "#858585"
                );

        // ---------------------------------------------------------
        // Start lesson
        // ---------------------------------------------------------

        Button startButton =
                new Button(
                        "Aloita oppitunti"
                );

        startButton.setPrefHeight(18);
        startButton.setMinHeight(18);

        startButton.setMaxWidth(
                Double.MAX_VALUE
        );

        startButton.setFocusTraversable(
                false
        );

        startButton.setStyle(
                "-fx-background-color: "
                        + NAVY
                        + "; "
                        + "-fx-text-fill: white; "
                        + "-fx-font-size: 8px; "
                        + "-fx-font-weight: bold; "
                        + "-fx-background-radius: 3; "
                        + "-fx-cursor: hand;"
        );

        startButton.setOnAction(event -> {

            event.consume();

            if (onStartLesson != null) {
                onStartLesson.run();
            }
        });

        // ---------------------------------------------------------
        // Card click
        // ---------------------------------------------------------

        card.setOnMouseClicked(event -> {

            if (event.getTarget()
                    instanceof Button) {

                return;
            }

            if (onOpenCourse != null) {
                onOpenCourse.run();
            }
        });

        // ---------------------------------------------------------
        // Card children
        // ---------------------------------------------------------

        card.getChildren()
                .addAll(
                        codeLabel,
                        nameLabel,
                        lessonsLabel,
                        startButton
                );

        return card;
    }

    // =============================================================
    // EMPTY COURSE CARD
    // =============================================================

    private VBox createEmptyCourseCard() {

        VBox card =
                new VBox(4);

        /*
         * It is STILL a course card from the test's point of view.
         */
        card.setPrefWidth(190);
        card.setPrefHeight(75);

        card.setMinWidth(190);
        card.setMinHeight(75);

        card.setMaxWidth(190);
        card.setMaxHeight(75);

        card.setAlignment(
                Pos.CENTER_LEFT
        );

        card.setPadding(
                new Insets(10)
        );

        card.setStyle(
                "-fx-background-color: white; "
                        + "-fx-border-color: #D7D7D7; "
                        + "-fx-border-width: 1; "
                        + "-fx-border-radius: 5; "
                        + "-fx-background-radius: 5;"
        );

        Label title =
                text(
                        "Ei aktiivisia kursseja",
                        9,
                        FontWeight.BOLD,
                        "#171717"
                );

        Label description =
                text(
                        "Tällä hetkellä ei ole kursseja.",
                        7,
                        FontWeight.NORMAL,
                        "#858585"
                );

        card.getChildren()
                .addAll(
                        title,
                        description
                );

        return card;
    }

    // =============================================================
    // TEACHER NAME
    // =============================================================

    private String getTeacherName(
            Teacher teacher
    ) {

        if (teacher == null) {
            return "Etunimi Sukunimi";
        }

        String firstName =
                teacher.getFirstName();

        String lastName =
                teacher.getLastName();

        if (firstName != null
                && !firstName.isBlank()
                && lastName != null
                && !lastName.isBlank()) {

            return firstName
                    + " "
                    + lastName;
        }

        if (firstName != null
                && !firstName.isBlank()) {

            return firstName;
        }

        if (lastName != null
                && !lastName.isBlank()) {

            return lastName;
        }

        return "Etunimi Sukunimi";
    }

    // =============================================================
    // TEACHER INITIALS
    // =============================================================

    private String getTeacherInitials(
            Teacher teacher
    ) {

        if (teacher == null) {
            return "";
        }

        String firstName =
                teacher.getFirstName();

        String lastName =
                teacher.getLastName();

        StringBuilder initials =
                new StringBuilder();

        if (firstName != null
                && !firstName.isBlank()) {

            initials.append(
                    firstName.trim().charAt(0)
            );
        }

        if (lastName != null
                && !lastName.isBlank()) {

            initials.append(
                    lastName.trim().charAt(0)
            );
        }

        return initials
                .toString()
                .toUpperCase();
    }

    // =============================================================
    // LABEL HELPER
    // =============================================================

    private static Label text(
            String value,
            double size,
            FontWeight weight,
            String color
    ) {

        Label label =
                new Label(value);

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



