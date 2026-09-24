package com.example.app.View;

import com.example.app.Model.LoginComponents.User;
import com.example.app.Model.StudentComponents.AttendanceRecord;
import com.example.app.Model.StudentComponents.Course;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class StudentAttendanceTracking extends BorderPane {

    private static final String NAVY = "#202F49";
    private static final String BLUE = "#344A70";
    private static final Locale FI = new Locale("fi", "FI");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("d.M.yyyy");

    /**
     * @param course  valittu kurssi (määrää otsikon ja koodin)
     * @param records kurssin oppitunnit + opiskelijan läsnäolomerkinnät
     *                 (esim. AttendanceDAO.getAttendanceForStudentAndCourse:n tulos)
     * @param onBack  kutsutaan kun "Takaisin"-nappia painetaan - Controller
     *                vaihtaa tällöin näkymän takaisin StudentStartPageen
     * @param currentUser kirjautunut opiskelija (näytetään sivupalkissa)
     */
    public StudentAttendanceTracking(Course course, List<AttendanceRecord> records, Runnable onBack,
                                     User currentUser) {

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

        Button back = new Button("<   Takaisin");
        back.setPrefHeight(22);
        back.setMaxWidth(Double.MAX_VALUE);
        back.setAlignment(Pos.CENTER_LEFT);
        back.setStyle("-fx-background-color: #344A70; -fx-text-fill: white; "
                + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 4;");
        back.setOnAction(e -> onBack.run());

        Region sideSpacer = new Region();
        VBox.setVgrow(sideSpacer, Priority.ALWAYS);

        HBox user = new HBox(7);
        user.setAlignment(Pos.CENTER_LEFT);
        Circle avatar = new Circle(10, Color.web("#536FA4"));
        Label initials = text(initialsOf(currentUser), 8, FontWeight.BOLD, "#FFFFFF");
        StackPane avatarBox = new StackPane(avatar, initials);
        avatarBox.setPrefSize(20, 20);

        VBox userInfo = new VBox(0,
                text(currentUser.getFullName(), 8, FontWeight.BOLD, "#FFFFFF"),
                text(roleLabel(currentUser), 6, FontWeight.NORMAL, "#A9B0BD")
        );
        user.getChildren().addAll(avatarBox, userInfo);

        sidebar.getChildren().addAll(brand, new Region(), back, sideSpacer, user);

        // =========================
        // MAIN CONTENT
        // =========================

        VBox content = new VBox();
        content.setPadding(new Insets(41, 24, 20, 25));

        // Otsikko
        Label title = text(course.getName(), 15, FontWeight.BOLD, "#171717");
        Label subtitle = text(course.getCode() + " - omat läsnäolomerkinnät", 8, FontWeight.BOLD, "#171717");
        VBox titleBox = new VBox(2, title, subtitle);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        // Yhteenveto lasketaan oikeasta datasta hardkoodauksen sijaan
        long presentCount = records.stream().filter(r -> "present".equals(r.getStatus())).count();
        long lateCount = records.stream().filter(r -> "late".equals(r.getStatus())).count();
        long absentCount = records.stream()
                .filter(r -> "absent".equals(r.getStatus()) || "excused".equals(r.getStatus()))
                .count();

        HBox summary = new HBox(20);
        summary.setAlignment(Pos.CENTER_RIGHT);
        summary.getChildren().addAll(
                text(presentCount + " paikalla", 8, FontWeight.BOLD, "#171717"),
                text(lateCount + " myöhässä", 8, FontWeight.BOLD, "#171717"),
                text(absentCount + " poissa", 8, FontWeight.BOLD, "#171717")
        );

        HBox header = new HBox();
        header.setAlignment(Pos.BOTTOM_LEFT);

        Region pushRight = new Region();
        HBox.setHgrow(pushRight, Priority.ALWAYS);

        header.getChildren().addAll(titleBox, pushRight, summary);
        content.getChildren().add(header);

        // =========================
        // ATTENDANCE ROWS
        // =========================

        Region line = new Region();
        line.setPrefHeight(1);
        line.setStyle("-fx-background-color: #EEEEEE;");

        VBox attendanceList = new VBox(14);
        attendanceList.setPadding(new Insets(16, 0, 0, 0));

        if (records.isEmpty()) {
            attendanceList.getChildren().add(
                    text("Tällä kurssilla ei ole vielä oppitunteja.", 9, FontWeight.NORMAL, "#858585"));
        } else {
            for (AttendanceRecord record : records) {
                attendanceList.getChildren().add(attendanceRow(record));
            }
        }

        ScrollPane scroll = new ScrollPane(attendanceList);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        content.getChildren().addAll(line, scroll);

        setLeft(sidebar);
        setCenter(content);
    }

    // =====================================================
    // ATTENDANCE ROW
    // =====================================================

    private HBox attendanceRow(AttendanceRecord record) {

        String date = record.getStartTime() != null ? DATE_FMT.format(record.getStartTime()) : "-";
        String day = record.getStartTime() != null
                ? capitalize(record.getStartTime().getDayOfWeek().getDisplayName(TextStyle.SHORT, FI))
                : "";

        String[] colors = statusColors(record.getStatus());
        String statusColor = colors[0];
        String statusBackground = colors[1];
        String statusText = statusLabel(record.getStatus());

        HBox row = new HBox();
        row.setPrefHeight(58);
        row.setMaxWidth(Double.MAX_VALUE);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 9, 8, 8));
        row.setStyle("-fx-background-color: white;"
                + "-fx-border-color: #D7D7D7;"
                + "-fx-border-radius: 12;"
                + "-fx-background-radius: 12;");

        // DATE
        VBox dateBox = new VBox(0,
                text(date, 10, FontWeight.BOLD, "#171717"),
                text(day, 7, FontWeight.BOLD, "#555555")
        );
        dateBox.setPrefWidth(95);

        // LESSON
        Label lessonLabel = text(record.getTopic(), 10, FontWeight.BOLD, "#666666");

        Region pushRight = new Region();
        HBox.setHgrow(pushRight, Priority.ALWAYS);

        // STATUS
        HBox statusBox = new HBox(10);
        statusBox.setPrefWidth(120);
        statusBox.setPrefHeight(28);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        statusBox.setPadding(new Insets(0, 12, 0, 12));
        statusBox.setStyle("-fx-background-color: " + statusBackground + ";"
                + "-fx-border-color: " + statusColor + ";"
                + "-fx-border-radius: 20;"
                + "-fx-background-radius: 20;");

        Circle statusCircle = new Circle(4, Color.web(statusColor));
        Label statusLabel = text(statusText, 10, FontWeight.BOLD, statusColor);

        statusBox.getChildren().addAll(statusCircle, statusLabel);

        row.getChildren().addAll(dateBox, lessonLabel, pushRight, statusBox);

        return row;
    }

    /** @return {reunaväri, taustaväri} annetulle tietokannan status-arvolle. */
    private static String[] statusColors(String status) {
        if (status == null) {
            status = "absent";
        }
        switch (status) {
            case "present":
                return new String[]{"#2E9560", "#E5F4EA"};
            case "late":
                return new String[]{"#C77A00", "#FFF2DF"};
            default: // "absent" ja "excused"
                return new String[]{"#C44D3A", "#FBE6E2"};
        }
    }

    private static String statusLabel(String status) {
        if (status == null) {
            status = "absent";
        }
        switch (status) {
            case "present":
                return "paikalla";
            case "late":
                return "myöhässä";
            case "excused":
                return "poissa (hyväksytty)";
            default:
                return "poissa";
        }
    }

    private static String initialsOf(User user) {
        String first = (user.getFirstName() == null || user.getFirstName().isEmpty())
                ? "" : user.getFirstName().substring(0, 1);
        String last = (user.getLastName() == null || user.getLastName().isEmpty())
                ? "" : user.getLastName().substring(0, 1);
        return (first + last).toUpperCase();
    }

    private static String roleLabel(User user) {
        switch (user.getRole().name()) {
            case "TEACHER":
                return "Opettaja";
            case "ADMIN":
                return "Ylläpitäjä";
            default:
                return "Opiskelija";
        }
    }

    private static String capitalize(String s) {
        return (s == null || s.isEmpty()) ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    // =====================================================
    // TEXT HELPER
    // =====================================================

    private static Label text(String value, double size, FontWeight weight, String color) {
        Label label = new Label(value);
        label.setFont(Font.font("System", weight, size));
        label.setTextFill(Color.web(color));
        return label;
    }
}
