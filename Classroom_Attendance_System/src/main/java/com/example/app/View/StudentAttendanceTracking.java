package com.example.app.View;

import com.example.app.Localisation.FontSupport;
import com.example.app.Localisation.i18n;
import com.example.app.Model.LoginComponents.User;
import com.example.app.Model.StudentComponents.AttendanceRecord;
import com.example.app.Model.StudentComponents.Course;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.FontWeight;

public class StudentAttendanceTracking extends BorderPane {

    private static final String NAVY = "#202F49";
    private static final String BLUE = "#344A70";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("d.M.yyyy");

    /**
     * @param course
     *            valittu kurssi (määrää otsikon ja koodin)
     * @param records
     *            kurssin oppitunnit + opiskelijan läsnäolomerkinnät (esim.
     *            AttendanceDAO.getAttendanceForStudentAndCourse:n tulos)
     * @param onBack
     *            kutsutaan kun "Takaisin"-nappia painetaan - Controller vaihtaa tällöin näkymän takaisin
     *            StudentStartPageen
     * @param currentUser
     *            kirjautunut opiskelija (näytetään sivupalkissa)
     * @param onLogout
     *            uloskirjautumisen callback
     * @param onProfileUpdated
     *            kutsutaan kun käyttäjä on tallentanut muutokset "Omat tiedot" -ikkunassa (avataan avatarista), jotta
     *            sivu voidaan piirtää uudelleen uusilla tiedoilla
     */
    public StudentAttendanceTracking(Course course, List<AttendanceRecord> records, Runnable onBack, User currentUser,
            Runnable onLogout, Runnable onProfileUpdated) {

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

        Button back = new Button("<   " + i18n.t("app.back"));
        back.setPrefHeight(22);
        back.setMaxWidth(Double.MAX_VALUE);
        back.setAlignment(Pos.CENTER_LEFT);
        back.setStyle("-fx-background-color: #344A70; -fx-text-fill: white; " + "-fx-font-family: '"
                + FontSupport.pickFamily() + "'; "
                + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 4;");
        back.setCursor(Cursor.HAND);
        back.setOnAction(e -> onBack.run());

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

        sidebar.getChildren().addAll(brand, back, sideSpacer, langBox, user);

        // =========================
        // MAIN CONTENT
        // =========================

        VBox content = new VBox();
        content.setPadding(new Insets(41, 24, 20, 25));

        // Otsikko
        Label title = text(course.getName(), 15, FontWeight.BOLD, "#171717");
        Label subtitle = text(course.getCode() + " - " + i18n.t("student.myAttendanceRecords"), 8, FontWeight.BOLD,
                "#171717");
        VBox titleBox = new VBox(2, title, subtitle);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        // Yhteenveto lasketaan oikeasta datasta hardkoodauksen sijaan
        long presentCount = records.stream().filter(r -> "present".equals(r.getStatus())).count();
        long lateCount = records.stream().filter(r -> "late".equals(r.getStatus())).count();
        long absentCount = records.stream()
                .filter(r -> "absent".equals(r.getStatus()) || "excused".equals(r.getStatus())).count();

        HBox summary = new HBox(20);
        summary.setAlignment(Pos.CENTER_RIGHT);
        summary.getChildren().addAll(
                text(presentCount + " " + i18n.t("attendance.present"), 8, FontWeight.BOLD, "#171717"),
                text(lateCount + " " + i18n.t("attendance.late"), 8, FontWeight.BOLD, "#171717"),
                text(absentCount + " " + i18n.t("attendance.absent"), 8, FontWeight.BOLD, "#171717"));

        HBox header = new HBox();
        header.setAlignment(Pos.BOTTOM_LEFT);

        Region pushRight = new Region();
        HBox.setHgrow(pushRight, Priority.ALWAYS);

        header.getChildren().addAll(titleBox, pushRight, summary);
        content.getChildren().add(header);

        HBox attendanceOverview = new HBox(10);
        attendanceOverview.setAlignment(Pos.CENTER_LEFT);
        attendanceOverview.setPadding(new Insets(14, 0, 0, 0));
        attendanceOverview.getChildren().addAll(
                text(i18n.t("course.percentPresent", attendancePercentage(records)), 9, FontWeight.BOLD, "#555555"),
                attendanceBar(records, 220));
        content.getChildren().add(attendanceOverview);

        // =========================
        // ATTENDANCE ROWS
        // =========================

        Region line = new Region();
        line.setPrefHeight(1);
        line.setStyle("-fx-background-color: #EEEEEE;");

        VBox attendanceList = new VBox(14);
        attendanceList.setPadding(new Insets(16, 0, 0, 0));

        if (records.isEmpty()) {
            attendanceList.getChildren().add(text(i18n.t("student.noLessonsYet"), 9, FontWeight.NORMAL, "#858585"));
        }
        else {
            for (AttendanceRecord record : records) {
                attendanceList.getChildren().add(attendanceRow(record));
            }
        }

        ScrollPane scroll = new ScrollPane(attendanceList);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        content.getChildren().addAll(line, scroll);

        // =========================
        // LOGOUT
        // =========================

        Button logout = ViewSupport.createLogoutButton(onLogout);

        VBox logoutBox = new VBox(logout);
        logoutBox.setPadding(new Insets(14, 0, 0, 0));
        logoutBox.setAlignment(Pos.CENTER_RIGHT);

        content.getChildren().add(logoutBox);

        setLeft(sidebar);
        setCenter(content);
    }

    // =====================================================
    // ATTENDANCE ROW
    // =====================================================

    private HBox attendanceRow(AttendanceRecord record) {

        String date = record.getStartTime() != null ? DATE_FMT.format(record.getStartTime()) : "-";
        String day = record.getStartTime() != null
                ? capitalize(record.getStartTime().getDayOfWeek().getDisplayName(TextStyle.SHORT, i18n.getLocale()))
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
        row.setStyle("-fx-background-color: white;" + "-fx-border-color: #D7D7D7;" + "-fx-border-radius: 12;"
                + "-fx-background-radius: 12;");

        // DATE
        VBox dateBox = new VBox(0, text(date, 10, FontWeight.BOLD, "#171717"),
                text(day, 7, FontWeight.BOLD, "#555555"));
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
        statusBox.setStyle("-fx-background-color: " + statusBackground + ";" + "-fx-border-color: " + statusColor + ";"
                + "-fx-border-radius: 20;" + "-fx-background-radius: 20;");

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
            case "present" :
                return new String[]{"#2E9560", "#E5F4EA"};
            case "late" :
                return new String[]{"#C77A00", "#FFF2DF"};
            default : // "absent" ja "excused"
                return new String[]{"#C44D3A", "#FBE6E2"};
        }
    }

    private static HBox attendanceBar(List<AttendanceRecord> records, double width) {
        long present = records.stream().filter(r -> "present".equals(r.getStatus())).count();
        long late = records.stream().filter(r -> "late".equals(r.getStatus())).count();
        long absent = records.size() - present - late;

        HBox bar = new HBox();
        bar.setPrefWidth(width);
        bar.setMinWidth(0);
        bar.setPrefHeight(7);
        bar.setMaxHeight(7);
        bar.setStyle("-fx-background-color: #EEEEEE; -fx-background-radius: 4;");

        addSegment(bar, present, records.size(), "#2E9560");
        addSegment(bar, late, records.size(), "#D99A20");
        addSegment(bar, absent, records.size(), "#C44D3A");
        return bar;
    }

    private static void addSegment(HBox bar, long count, int total, String color) {
        if (count == 0 || total == 0) {
            return;
        }
        Region segment = new Region();
        segment.setPrefWidth(bar.getPrefWidth() * count / total);
        segment.setMinWidth(0);
        segment.setPrefHeight(7);
        segment.setStyle("-fx-background-color: " + color + ";");
        bar.getChildren().add(segment);
    }

    private static int attendancePercentage(List<AttendanceRecord> records) {
        if (records.isEmpty()) {
            return 0;
        }
        long attended = records.stream().filter(r -> "present".equals(r.getStatus()) || "late".equals(r.getStatus()))
                .count();
        return (int) Math.round(attended * 100.0 / records.size());
    }

    private static String statusLabel(String status) {
        if (status == null) {
            status = "absent";
        }
        switch (status) {
            case "present" :
                return i18n.t("attendance.present");
            case "late" :
                return i18n.t("attendance.late");
            case "excused" :
                return i18n.t("attendance.excused");
            default :
                return i18n.t("attendance.absent");
        }
    }

    private static String initialsOf(User user) {
        return ViewSupport.initialsOf(user);
    }

    private static String roleLabel(User user) {
        return ViewSupport.roleLabel(user);
    }

    private static String capitalize(String s) {
        return (s == null || s.isEmpty()) ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static Label text(String value, double size, FontWeight weight, String color) {
        return ViewSupport.text(value, size, weight, color);
    }

    private static String weightToCss(FontWeight weight) {
        return ViewSupport.weightToCss(weight);
    }
}
