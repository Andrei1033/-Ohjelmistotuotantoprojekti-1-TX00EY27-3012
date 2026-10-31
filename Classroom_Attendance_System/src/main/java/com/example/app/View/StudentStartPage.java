package com.example.app.View;

import com.example.app.Controller.StudentController;
import com.example.app.Localisation.FontSupport;
import com.example.app.Localisation.i18n;
import com.example.app.Model.LoginComponents.User;
import com.example.app.Model.StudentComponents.AttendanceRecord;
import com.example.app.Model.StudentComponents.Course;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class StudentStartPage extends BorderPane {

    private static final String NAVY = "#202F49";
    private static final String BLUE = "#344A70";

    /**
     * @param currentUser       kirjautunut opiskelija (näytetään sivupalkissa)
     * @param courses           opiskelijan kurssit
     * @param onCourseSelected  kutsutaan kun kurssikorttia klikataan
     * @param onLogout          uloskirjautumisen callback
     * @param onProfileUpdated  kutsutaan kun käyttäjä on tallentanut muutokset
     *                          "Omat tiedot" -ikkunassa tai vaihtaa kieltä,
     *                          jotta sivu voidaan piirtää uudelleen
     */
    public StudentStartPage(User currentUser,
                            List<Course> courses,
                            Consumer<Course> onCourseSelected,
                            Runnable onLogout,
                            Runnable onProfileUpdated) {
        this(currentUser, courses, Map.of(), onCourseSelected, onLogout, onProfileUpdated);
    }

    public StudentStartPage(User currentUser,
                            List<Course> courses,
                            Map<Integer, List<AttendanceRecord>> attendanceByCourse,
                            Consumer<Course> onCourseSelected,
                            Runnable onLogout,
                            Runnable onProfileUpdated) {

        if (FontSupport.isRtl()) {
            setNodeOrientation(javafx.geometry.NodeOrientation.RIGHT_TO_LEFT);
        }

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

        Circle logo = new Circle(13, Color.web("#536FA4"));
        Label lo = text(i18n.t("app.logo"), 12, FontWeight.BOLD, "#FFFFFF");
        StackPane logoBox = new StackPane(logo, lo);
        logoBox.setPrefSize(20, 20);

        Label brandText = text(i18n.t("app.attendance"), 13, FontWeight.BOLD, "#FFFFFF");
        brand.getChildren().addAll(logoBox, brandText);

        Region sideSpacer = new Region();
        VBox.setVgrow(sideSpacer, Priority.ALWAYS);

        // ========== LANGUAGE SWITCHER ==========
        ComboBox<i18n.Lang> langCombo = new ComboBox<>();
        langCombo.getItems().addAll(i18n.SUPPORTED);
        langCombo.setValue(i18n.getCurrentLang());
        langCombo.setMaxWidth(Double.MAX_VALUE);
        langCombo.setFocusTraversable(false);
        langCombo.setCursor(Cursor.HAND);
        langCombo.setStyle(
                "-fx-background-color: " + BLUE + "; "
                        + "-fx-background-radius: 4; "
                        + "-fx-font-family: '" + FontSupport.pickFamily() + "'; "
                        + "-fx-font-size: 11px; "
                        + "-fx-text-fill: white;"
        );

        langCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(i18n.Lang lang) {
                return lang == null ? "" : lang.label();
            }

            @Override
            public i18n.Lang fromString(String string) {
                return null;
            }
        });

        // Solukuvaukset alasvetolistan riveille
        langCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(i18n.Lang item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.label());
            }
        });

        // Solukuvaus valitulle painikkeelle
        langCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(i18n.Lang item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.label());
            }
        });

        // Päivitetty kuuntelija: vaihtaa kielen ja suorittaa sivun uudelleenpiirron
        langCombo.setOnAction(e -> {
            i18n.Lang selected = langCombo.getValue();
            if (selected == null || selected.equals(i18n.getCurrentLang())) return;
            i18n.setLanguage(selected);

            if (onProfileUpdated != null) {
                onProfileUpdated.run();
            }
        });

        VBox langBox = new VBox(4, langCombo);
        langBox.setPadding(new Insets(0, 0, 12, 0));

        HBox user = new HBox(7);
        user.setAlignment(Pos.CENTER_LEFT);
        user.setCursor(Cursor.HAND);

        Circle avatar = new Circle(15, Color.web("#536FA4"));
        Label initials = text(initialsOf(currentUser), 12, FontWeight.BOLD, "#FFFFFF");
        StackPane avatarBox = new StackPane(avatar, initials);
        avatarBox.setPrefSize(20, 20);
        avatarBox.setCursor(Cursor.HAND);

        VBox userInfo = new VBox(0,
                text(currentUser != null ? currentUser.getFullName() : "", 12, FontWeight.BOLD, "#FFFFFF"),
                text(roleLabel(currentUser), 11, FontWeight.NORMAL, "#A9B0BD")
        );
        user.getChildren().addAll(avatarBox, userInfo);

        user.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().name().equals("PRIMARY")) {
                ProfileEditWindow.show(user.getScene().getWindow(), currentUser, onProfileUpdated);
                event.consume();
            }
        });

        sidebar.getChildren().addAll(brand, sideSpacer, langBox, user);

        // =========================
        // MAIN CONTENT
        // =========================

        VBox content = new VBox(0);
        content.setPadding(new Insets(41, 30, 20, 31));

        Label heading = text(i18n.t("student.myCourses"), 15, FontWeight.BOLD, "#171717");
        Label intro = text(introText(courses.size()), 10, FontWeight.NORMAL, "#555555");

        VBox headingBox = new VBox(2, heading, intro);
        headingBox.setPadding(new Insets(0, 0, 18, 0));

        content.getChildren().add(headingBox);

        // =========================
        // COURSES
        // =========================

        if (courses.isEmpty()) {
            content.getChildren().add(
                    text(i18n.t("student.noCourses"), 11, FontWeight.NORMAL, "#858585")
            );
        } else {
            FlowPane cards = new FlowPane(22, 22);

            for (Course course : courses) {
                cards.getChildren().add(
                        courseCard(
                                course,
                                attendanceByCourse.getOrDefault(course.getId(), List.of()),
                                !attendanceByCourse.isEmpty(),
                                onCourseSelected
                        )
                );
            }

            ScrollPane scroll = new ScrollPane(cards);
            scroll.setFitToWidth(true);
            scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
            VBox.setVgrow(scroll, Priority.ALWAYS);

            content.getChildren().add(scroll);
        }

        // =========================
        // LOGOUT
        // =========================

        Button logout = new Button(i18n.t("app.logout"));
        logout.setStyle(
                "-fx-background-color: " + BLUE + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-family: '" + FontSupport.pickFamily() + "'; " +
                        "-fx-font-size: 11px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 4; " +
                        "-fx-padding: 6 14;"
        );
        logout.setCursor(Cursor.HAND);
        logout.setOnAction(e -> onLogout.run());

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

    private VBox courseCard(
            Course course,
            List<AttendanceRecord> records,
            boolean showAttendance,
            Consumer<Course> onCourseSelected) {

        VBox card = new VBox(6);
        double cardHeight = showAttendance ? 112 : 90;
        card.setPrefSize(190, cardHeight);
        card.setMinSize(190, cardHeight);
        card.setMaxSize(190, cardHeight);
        card.setPadding(new Insets(11, 13, 8, 13));
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #D7D7D7; " +
                        "-fx-border-radius: 5; " +
                        "-fx-background-radius: 5;"
        );
        card.setCursor(Cursor.HAND);

        Label codeLabel = text(course.getCode(), 9, FontWeight.BOLD, "#4B83A0");
        codeLabel.setStyle(
                codeLabel.getStyle() +
                        "-fx-background-color: #D9F0FA; " +
                        "-fx-background-radius: 3; " +
                        "-fx-padding: 3 6;"
        );
        codeLabel.setMouseTransparent(true);

        Label nameLabel = text(course.getName(), 11, FontWeight.BOLD, "#171717");
        nameLabel.setMouseTransparent(true);

        Label hoursLabel = text(
                i18n.t("course.lessonCount", course.getLessonCount()),
                9,
                FontWeight.NORMAL,
                "#6B6B6B"
        );
        hoursLabel.setMouseTransparent(true);

        card.getChildren().addAll(codeLabel, nameLabel, hoursLabel);

        if (showAttendance) {
            card.getChildren().add(attendanceSummary(records));
        }

        card.setOnMouseClicked(event -> {
            if (event.getButton().name().equals("PRIMARY")) {
                onCourseSelected.accept(course);
                event.consume();
            }
        });

        return card;
    }

    private HBox attendanceSummary(List<AttendanceRecord> records) {
        HBox summary = new HBox(6);
        summary.setAlignment(Pos.CENTER_LEFT);

        Label percentage = text(
                i18n.t("course.percentPresent", attendancePercentage(records)),
                8,
                FontWeight.BOLD,
                "#555555"
        );
        HBox bar = attendanceBar(records, 105);
        HBox.setHgrow(bar, Priority.ALWAYS);
        summary.getChildren().addAll(bar, percentage);
        return summary;
    }

    private static HBox attendanceBar(List<AttendanceRecord> records, double width) {
        long present = records.stream().filter(r -> "present".equals(r.getStatus())).count();
        long late = records.stream().filter(r -> "late".equals(r.getStatus())).count();
        long absent = records.size() - present - late;

        HBox bar = new HBox();
        bar.setPrefWidth(width);
        bar.setMinWidth(0);
        bar.setPrefHeight(6);
        bar.setMaxHeight(6);
        bar.setStyle("-fx-background-color: #EEEEEE; -fx-background-radius: 4;");

        addSegment(bar, present, records.size(), "#2E9560");
        addSegment(bar, late, records.size(), "#D99A20");
        addSegment(bar, absent, records.size(), "#C44D3A");
        return bar;
    }

    private static void addSegment(HBox bar, long count, int total, String color) {
        if (count == 0 || total == 0) return;
        Region segment = new Region();
        segment.setPrefWidth(bar.getPrefWidth() * count / total);
        segment.setMinWidth(0);
        segment.setPrefHeight(6);
        segment.setStyle("-fx-background-color: " + color + ";");
        bar.getChildren().add(segment);
    }

    private static int attendancePercentage(List<AttendanceRecord> records) {
        if (records.isEmpty()) return 0;
        long attended = records.stream()
                .filter(r -> "present".equals(r.getStatus()) || "late".equals(r.getStatus()))
                .count();
        return (int) Math.round(attended * 100.0 / records.size());
    }

    private static String introText(int courseCount) {
        return i18n.t("student.courseCount", courseCount);
    }

    private static String initialsOf(User user) {
        if (user == null) return "";
        String first = (user.getFirstName() == null || user.getFirstName().isEmpty()) ? "" : user.getFirstName().substring(0, 1);
        String last = (user.getLastName() == null || user.getLastName().isEmpty()) ? "" : user.getLastName().substring(0, 1);
        return (first + last).toUpperCase();
    }

    private static String roleLabel(User user) {
        if (user == null || user.getRole() == null) return "";
        switch (user.getRole().name()) {
            case "TEACHER": return i18n.t("app.teacher");
            case "ADMIN": return i18n.t("app.admin");
            default: return i18n.t("app.student");
        }
    }

    private static Label text(String value, double size, FontWeight weight, String color) {
        Label label = new Label(value);
        label.setStyle(
                "-fx-font-family: '" + FontSupport.pickFamily() + "'; " +
                        "-fx-font-size: " + size + "px; " +
                        "-fx-font-weight: " + weightToCss(weight) + "; " +
                        "-fx-text-fill: " + color + ";"
        );
        return label;
    }

    private static String weightToCss(FontWeight weight) {
        return weight == FontWeight.BOLD ? "bold" : "normal";
    }
}