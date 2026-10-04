package com.example.app.View;

import com.example.app.Controller.TeacherController;
import com.example.app.Model.Teacher;
import com.example.app.Model.TeacherCourse;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeacherStartPage extends BorderPane {

    private static final String NAVY = "#202F49";
    private static final String BLUE = "#344A70";

    private final Teacher teacher;
    private final TeacherController teacherController;

    public TeacherStartPage(
            Teacher teacher,
            TeacherController teacherController,
            Runnable onLogout
    ) {

        this.teacher = teacher;
        this.teacherController = teacherController;

        setStyle("-fx-background-color: white;");

        // =========================================================
        // SIDEBAR
        // =========================================================

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(158);
        sidebar.setMinWidth(158);
        sidebar.setMaxWidth(158);
        sidebar.setPadding(new Insets(12, 14, 10, 10));
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");

        // ---------------------------------------------------------
        // LOGO
        // ---------------------------------------------------------

        HBox brand = new HBox(9);
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPadding(new Insets(0, 0, 20, 0));

        Circle logo = new Circle(13, Color.web("#536FA4"));
        Label lo = text("LO", 12, FontWeight.BOLD, "#FFFFFF");
        StackPane logoBox = new StackPane(logo, lo);
        logoBox.setPrefSize(20, 20);

        Label brandText = text("Läsnäolo", 13, FontWeight.BOLD, "#FFFFFF");
        brand.getChildren().addAll(logoBox, brandText);

        // ---------------------------------------------------------
        // SIDEBAR SPACER
        // ---------------------------------------------------------

        Region sideSpacer = new Region();
        VBox.setVgrow(sideSpacer, Priority.ALWAYS);

        // ---------------------------------------------------------
        // USER ROW (avatar + name + role)
        // Klikkaus avaa "Omat tiedot" -ikkunan.
        // ---------------------------------------------------------

        HBox user = new HBox(7);
        user.setAlignment(Pos.CENTER_LEFT);
        user.setCursor(Cursor.HAND);

        Circle avatar = new Circle(15, Color.web("#536FA4"));
        Label initials = text(
                getTeacherInitials(teacher),
                12,
                FontWeight.BOLD,
                "#FFFFFF"
        );

        StackPane avatarBox = new StackPane(avatar, initials);
        avatarBox.setPrefSize(20, 20);
        avatarBox.setCursor(Cursor.HAND);

        VBox userInfo = new VBox(
                0,
                text(getTeacherName(teacher), 12, FontWeight.BOLD, "#FFFFFF"),
                text("Opettaja", 11, FontWeight.NORMAL, "#A9B0BD")
        );

        user.getChildren().addAll(avatarBox, userInfo);

        // Avaa profiilin muokkaus, kun käyttäjärivistä klikataan.
        user.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().name().equals("PRIMARY")
                    && user.getScene() != null) {

                // Teacher ei ole User-tyyppiä, joten ProfileEditWindow
                // vaatii User-olion. Jos Teacher perii Userin, tämä toimii
                // suoraan. Muussa tapauksessa välitä soveltuva User-olio.
                ProfileEditWindow.show(
                        user.getScene().getWindow(),
                        teacher,
                        () -> {
                            // Päivitä sivupalkin nimi uudelleen piirtämällä sivu.
                            if (teacherController != null) {
                                teacherController.showStartPage();
                            }
                        }
                );
                event.consume();
            }
        });

        sidebar.getChildren().addAll(
                brand,
                sideSpacer,
                user
        );

        // =========================================================
        // MAIN CONTENT
        // =========================================================

        VBox content = new VBox(0);
        content.setPadding(new Insets(41, 30, 20, 31));

        // ---------------------------------------------------------
        // HEADER
        // ---------------------------------------------------------

        Label heading = text("Omat kurssit", 15, FontWeight.BOLD, "#171717");

        Region headingSpacer = new Region();
        HBox.setHgrow(headingSpacer, Priority.ALWAYS);

        Button createCourseButton = new Button("+ Uusi kurssi");
        createCourseButton.setFocusTraversable(false);
        createCourseButton.setCursor(Cursor.HAND);
        createCourseButton.setStyle(
                "-fx-background-color: " + NAVY + "; "
                        + "-fx-text-fill: white; "
                        + "-fx-font-size: 9px; "
                        + "-fx-font-weight: bold; "
                        + "-fx-background-radius: 4; "
                        + "-fx-padding: 5 12; "
                        + "-fx-cursor: hand;"
        );
        createCourseButton.setOnAction(e -> showCreateCourseDialog());

        HBox headingBox = new HBox(10, heading, headingSpacer, createCourseButton);
        headingBox.setAlignment(Pos.CENTER_LEFT);

        // ---------------------------------------------------------
        // INTRO
        // ---------------------------------------------------------

        int teacherId = teacher != null ? teacher.getId() : 1;
        List<TeacherCourse> teacherCourses = loadTeacherCourses(teacherId);

        Label intro = text(
                "Sinulla on " + teacherCourses.size()
                        + " kurssia tällä lukukaudella.",
                10,
                FontWeight.NORMAL,
                "#555555"
        );

        VBox headingWrapper = new VBox(2, headingBox, intro);
        headingWrapper.setPadding(new Insets(0, 0, 18, 0));

        content.getChildren().add(headingWrapper);

        // ---------------------------------------------------------
        // COURSES – responsiivinen FlowPane + ScrollPane
        // ---------------------------------------------------------

        if (teacherCourses.isEmpty()) {

            content.getChildren().add(
                    text(
                            "Sinulla ei ole vielä kursseja.",
                            11,
                            FontWeight.NORMAL,
                            "#858585"
                    )
            );

        } else {

            FlowPane cards = new FlowPane(22, 22);
            cards.setAlignment(Pos.TOP_LEFT);

            fillCourseCards(cards, teacherCourses);

            ScrollPane scroll = new ScrollPane(cards);
            scroll.setFitToWidth(true);
            scroll.setStyle(
                    "-fx-background-color: transparent; "
                            + "-fx-background: transparent;"
            );

            VBox.setVgrow(scroll, Priority.ALWAYS);

            content.getChildren().add(scroll);
        }

        // ---------------------------------------------------------
        // LOGOUT – oikeassa alakulmassa, kuten StudentStartPage:ssa
        // ---------------------------------------------------------

        Button logout = new Button("Kirjaudu ulos");
        logout.setCursor(Cursor.HAND);
        logout.setStyle(
                "-fx-background-color: " + BLUE + "; "
                        + "-fx-text-fill: white; "
                        + "-fx-font-size: 11px; "
                        + "-fx-font-weight: bold; "
                        + "-fx-background-radius: 4; "
                        + "-fx-padding: 6 14;"
        );

        logout.setOnAction(e -> {
            if (onLogout != null) {
                onLogout.run();
            }
        });

        VBox logoutBox = new VBox(logout);
        logoutBox.setPadding(new Insets(14, 0, 0, 0));
        logoutBox.setAlignment(Pos.CENTER_RIGHT);

        content.getChildren().add(logoutBox);

        // =========================================================
        // ROOT
        // =========================================================

        setLeft(sidebar);
        setCenter(content);
    }

    // =============================================================
    // CREATE COURSE DIALOG
    // =============================================================

    private void showCreateCourseDialog() {

        if (teacherController == null) {
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Luo uusi kurssi");
        dialog.setHeaderText("Syötä uuden kurssin tiedot.");

        ButtonType createButtonType = new ButtonType(
                "Luo kurssi",
                ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane().getButtonTypes().addAll(
                createButtonType,
                ButtonType.CANCEL
        );

        TextField nameField = new TextField();
        nameField.setPromptText("Esim. Introduction to Computer Science");
        nameField.setStyle("-fx-font-size: 11px;");

        TextField codeField = new TextField();
        codeField.setPromptText("Esim. CS2026 (valinnainen)");
        codeField.setStyle("-fx-font-size: 11px;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        grid.add(new Label("Kurssin nimi:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Kurssikoodi:"), 0, 1);
        grid.add(codeField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        javafx.scene.Node createButton =
                dialog.getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(true);

        nameField.textProperty().addListener(
                (obs, oldV, newV) ->
                        createButton.setDisable(
                                newV == null || newV.trim().isEmpty()
                        )
        );

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isEmpty() || result.get() != createButtonType) {
            return;
        }

        String courseName = nameField.getText().trim();
        if (courseName.isEmpty()) {
            return;
        }

        int teacherId = teacher != null ? teacher.getId() : -1;

        boolean created = teacherController.createCourses(
                courseName,
                codeField.getText().trim(),
                teacherId
        );

        if (created) {
            teacherController.showStartPage();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Virhe");
            alert.setHeaderText(null);
            alert.setContentText("Kurssin luominen epäonnistui.");
            alert.showAndWait();
        }
    }

    // =============================================================
    // LOAD COURSES
    // =============================================================

    private List<TeacherCourse> loadTeacherCourses(int teacherId) {

        List<TeacherCourse> teacherCourses = new ArrayList<>();

        if (teacherController == null) {
            return teacherCourses;
        }

        try {
            List<TeacherCourse> result =
                    teacherController.getTeacherCourses(teacherId);
            if (result != null) {
                teacherCourses.addAll(result);
            }
        } catch (Exception ignored) {
            // Ei kaadeta näkymää, jos kurssien lataus epäonnistuu.
        }

        return teacherCourses;
    }

    // =============================================================
    // FILL COURSE CARDS
    // =============================================================

    private void fillCourseCards(
            FlowPane cards,
            List<TeacherCourse> teacherCourses
    ) {

        for (TeacherCourse course : teacherCourses) {

            if (course == null) {
                continue;
            }

            int courseId = course.getCourseid();

            String courseName = course.getCoursename();
            if (courseName == null || courseName.isBlank()) {
                courseName = "Kurssi";
            }

            int lessonCount = 0;
            if (teacherController != null) {
                try {
                    List<?> lessons =
                            teacherController.getLessonsForCourse(courseId);
                    if (lessons != null) {
                        lessonCount = lessons.size();
                    }
                } catch (Exception ignored) {
                    lessonCount = 0;
                }
            }

            String courseCode = course.getCode();
            if (courseCode == null || courseCode.isBlank()) {
                courseCode = String.format("%02d", courseId);
            }

            String lessonText = lessonCount + " oppituntia";
            final int finalCourseId = courseId;

            VBox card = createCourseCard(
                    courseCode,
                    courseName,
                    lessonText,
                    () -> {
                        if (teacherController != null) {
                            teacherController.startLesson(finalCourseId);
                        }
                    },
                    () -> {
                        if (teacherController != null) {
                            teacherController.openCoursePage(finalCourseId);
                        }
                    }
            );

            cards.getChildren().add(card);
        }
    }

    // =============================================================
    // COURSE CARD (yhtenäinen StudentStartPage:n kanssa)
    // =============================================================

    private VBox createCourseCard(
            String code,
            String name,
            String lessons,
            Runnable onStartLesson,
            Runnable onOpenCourse
    ) {

        VBox card = new VBox(6);

        card.setPrefSize(190, 112);
        card.setMinSize(190, 112);
        card.setMaxSize(190, 112);

        card.setPadding(new Insets(11, 13, 8, 13));

        card.setStyle(
                "-fx-background-color: white; "
                        + "-fx-border-color: #D7D7D7; "
                        + "-fx-border-radius: 5; "
                        + "-fx-background-radius: 5;"
        );

        card.setCursor(Cursor.HAND);

        // Kurssikoodi
        Label codeLabel = text(code, 9, FontWeight.BOLD, "#4B83A0");
        codeLabel.setStyle(
                codeLabel.getStyle()
                        + "-fx-background-color: #D9F0FA; "
                        + "-fx-background-radius: 3; "
                        + "-fx-padding: 3 6;"
        );
        codeLabel.setMouseTransparent(true);

        // Kurssin nimi
        Label nameLabel = text(name, 11, FontWeight.BOLD, "#171717");
        nameLabel.setMouseTransparent(true);

        // Oppituntien määrä
        Label lessonsLabel = text(lessons, 9, FontWeight.NORMAL, "#6B6B6B");
        lessonsLabel.setMouseTransparent(true);

        // Aloita oppitunti -nappi
        Button startButton = new Button("Aloita oppitunti");
        startButton.setPrefHeight(22);
        startButton.setMinHeight(22);
        startButton.setMaxWidth(Double.MAX_VALUE);
        startButton.setFocusTraversable(false);
        startButton.setCursor(Cursor.HAND);
        startButton.setStyle(
                "-fx-background-color: " + NAVY + "; "
                        + "-fx-text-fill: white; "
                        + "-fx-font-size: 9px; "
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

        // Kortin klikkaus avaa kurssisivun
        card.setOnMouseClicked(event -> {
            if (event.getButton().name().equals("PRIMARY")) {
                if (onOpenCourse != null) {
                    onOpenCourse.run();
                }
                event.consume();
            }
        });

        card.getChildren().addAll(
                codeLabel,
                nameLabel,
                lessonsLabel,
                startButton
        );

        return card;
    }

    // =============================================================
    // TEACHER NAME / INITIALS
    // =============================================================

    private String getTeacherName(Teacher teacher) {

        if (teacher == null) {
            return "Etunimi Sukunimi";
        }

        String firstName = teacher.getFirstName();
        String lastName = teacher.getLastName();

        if (firstName != null && !firstName.isBlank()
                && lastName != null && !lastName.isBlank()) {
            return firstName + " " + lastName;
        }
        if (firstName != null && !firstName.isBlank()) {
            return firstName;
        }
        if (lastName != null && !lastName.isBlank()) {
            return lastName;
        }
        return "Etunimi Sukunimi";
    }

    private String getTeacherInitials(Teacher teacher) {

        if (teacher == null) {
            return "";
        }

        String firstName = teacher.getFirstName();
        String lastName = teacher.getLastName();

        StringBuilder initials = new StringBuilder();

        if (firstName != null && !firstName.isBlank()) {
            initials.append(firstName.trim().charAt(0));
        }
        if (lastName != null && !lastName.isBlank()) {
            initials.append(lastName.trim().charAt(0));
        }

        return initials.toString().toUpperCase();
    }

    // =============================================================
    // LABEL HELPER – inline-tyyli voittaa CSS:n
    // =============================================================

    private static Label text(
            String value,
            double size,
            FontWeight weight,
            String color
    ) {
        Label label = new Label(value);
        label.setStyle(
                "-fx-font-family: 'System'; "
                        + "-fx-font-size: " + size + "px; "
                        + "-fx-font-weight: " + weightToCss(weight) + "; "
                        + "-fx-text-fill: " + color + ";"
        );
        return label;
    }

    private static String weightToCss(FontWeight weight) {
        switch (weight) {
            case BOLD:
                return "bold";
            case NORMAL:
            default:
                return "normal";
        }
    }
}