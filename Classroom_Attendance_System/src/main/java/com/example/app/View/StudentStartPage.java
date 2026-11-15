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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.FontWeight;

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
        FontSupport.applyTo(this);

        // =========================
        // SIDEBAR
        // =========================

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(158);
        sidebar.setPadding(new Insets(12, 14, 10, 10));
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");

        HBox brand = ViewSupport.createBrandBox();
        Region sideSpacer = new Region();
        VBox.setVgrow(sideSpacer, Priority.ALWAYS);

        javafx.scene.control.ComboBox<i18n.Lang> langCombo = ViewSupport.createLanguageSelector(() -> {
            if (onProfileUpdated != null) {
                onProfileUpdated.run();
            }
        });
        VBox langBox = new VBox(4, langCombo);
        langBox.setPadding(new Insets(0, 0, 12, 0));

        HBox user = ViewSupport.createUserRow(currentUser, roleLabel(currentUser), event -> {
            javafx.scene.Node source = (javafx.scene.Node) event.getSource();
            if (source.getScene() != null) {
                ProfileEditWindow.show(source.getScene().getWindow(), currentUser, onProfileUpdated);
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

        Button logout = ViewSupport.createLogoutButton(onLogout);

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
        return ViewSupport.initialsOf(user);
    }

    private static String roleLabel(User user) {
        return ViewSupport.roleLabel(user);
    }

    private static Label text(String value, double size, FontWeight weight, String color) {
        return ViewSupport.text(value, size, weight, color);
    }

    private static String weightToCss(FontWeight weight) {
        return ViewSupport.weightToCss(weight);
    }
}