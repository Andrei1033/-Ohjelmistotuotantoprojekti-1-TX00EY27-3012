package com.example.app.View;

import com.example.app.Controller.TeacherController;
import com.example.app.Localisation.FontSupport;
import com.example.app.Localisation.i18n;
import com.example.app.Model.LoginComponents.User;
import com.example.app.Model.StudentComponents.AttendanceRecord;
import com.example.app.Model.Teacher;
import com.example.app.Model.TeacherCourse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;

public class TeacherCoursePage extends BorderPane {

    private static final String NAVY = "#202F49";
    private static final String BLUE = "#344A70";

    private final TeacherController teacherController;
    private final int courseId;
    private final VBox studentList;
    private final Teacher teacher;

    // Header-elementit, joita päivitetään muokkauksen jälkeen
    private Label courseTitle;
    private Label courseCode;

    public TeacherCoursePage(Teacher teacher, int courseId, TeacherController teacherController, Runnable onBack,
            Runnable onAddLesson, User currentUser, Runnable onLogout, Runnable onProfileUpdated) {
        this.teacher = teacher;
        this.teacherController = teacherController;
        this.courseId = courseId;
        this.studentList = new VBox(10);

        setStyle("-fx-background-color: white;");
        FontSupport.applyTo(this);

        // RTL-tuki arabialle
        if (FontSupport.isRtl()) {
            setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        }

        // =========================================================
        // SIDEBAR
        // =========================================================

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(158);
        sidebar.setMinWidth(158);
        sidebar.setMaxWidth(158);
        sidebar.setPadding(new Insets(12, 14, 10, 10));
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");

        HBox brand = ViewSupport.createBrandBox();

        Button back = new Button("•   " + i18n.t("teacher.myCourses"));
        back.setPrefHeight(22);
        back.setMaxWidth(Double.MAX_VALUE);
        back.setAlignment(Pos.CENTER_LEFT);
        back.setFocusTraversable(false);
        back.setCursor(Cursor.HAND);
        back.setStyle("-fx-background-color: " + BLUE + "; " + "-fx-text-fill: white; " + "-fx-font-family: '"
                + FontSupport.pickFamily() + "'; " + "-fx-font-size: 10px; " + "-fx-font-weight: bold; "
                + "-fx-background-radius: 4;");
        back.setOnAction(e -> {
            if (onBack != null)
                onBack.run();
        });

        Region sideSpacer = new Region();
        VBox.setVgrow(sideSpacer, Priority.ALWAYS);

        javafx.scene.control.ComboBox<i18n.Lang> langCombo = ViewSupport.createLanguageSelector(() -> {
            if (teacherController != null) {
                teacherController.openCoursePage(courseId);
            }
        });
        VBox langBox = new VBox(4, langCombo);
        langBox.setPadding(new Insets(0, 0, 12, 0));

        HBox user = ViewSupport.createUserRow(currentUser, i18n.t("app.teacher"), event -> {
            javafx.scene.Node source = (javafx.scene.Node) event.getSource();
            if (source.getScene() != null && currentUser != null) {
                ProfileEditWindow.show(source.getScene().getWindow(), currentUser, onProfileUpdated);
            }
        });

        sidebar.getChildren().addAll(brand, back, sideSpacer, langBox, user);

        // =========================================================
        // CONTENT
        // =========================================================

        VBox contentBox = new VBox(16);
        contentBox.setPadding(new Insets(41, 30, 20, 31));

        // HEADER – kurssin nimi + koodi + napit
        String courseNameText = i18n.t("course.name") + " " + courseId;
        String courseCodeText = "";

        if (teacherController != null && teacher != null) {
            List<TeacherCourse> courses = teacherController.getTeacherCourses(teacher.getId());

            if (courses != null) {
                for (TeacherCourse tc : courses) {
                    if (tc.getCourseid() == courseId) {
                        courseNameText = tc.getCoursename();

                        if (tc.getCode() != null && !tc.getCode().isBlank()) {
                            courseCodeText = tc.getCode();
                        }
                        else {
                            courseCodeText = String.format("%02d", courseId);
                        }
                        break;
                    }
                }
            }
        }

        courseTitle = text(courseNameText, 15, FontWeight.BOLD, "#171717");
        courseCode = text(courseCodeText, 8, FontWeight.BOLD, "#555555");
        VBox titleBox = new VBox(2, courseTitle, courseCode);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Button editCourse = new Button(i18n.t("teacher.editCourse"));
        editCourse.setFocusTraversable(false);
        editCourse.setCursor(Cursor.HAND);
        editCourse.setStyle("-fx-background-color: transparent; " + "-fx-border-color: #D1D5DB; "
                + "-fx-border-radius: 4; " + "-fx-background-radius: 4; " + "-fx-font-family: '"
                + FontSupport.pickFamily() + "'; " + "-fx-font-size: 9px; " + "-fx-font-weight: bold; "
                + "-fx-text-fill: #374151; " + "-fx-padding: 5 10; " + "-fx-cursor: hand;");
        editCourse.setOnAction(e -> showEditCourseDialog());

        Button addStudent = new Button(i18n.t("teacher.addStudent"));
        addStudent.setFocusTraversable(false);
        addStudent.setCursor(Cursor.HAND);
        addStudent.setStyle("-fx-background-color: transparent; " + "-fx-border-color: #D1D5DB; "
                + "-fx-border-radius: 4; " + "-fx-background-radius: 4; " + "-fx-font-family: '"
                + FontSupport.pickFamily() + "'; " + "-fx-font-size: 9px; " + "-fx-font-weight: bold; "
                + "-fx-text-fill: #374151; " + "-fx-padding: 5 10; " + "-fx-cursor: hand;");
        addStudent.setOnAction(e -> ShowaddStudent());

        Button startLesson = new Button(i18n.t("teacher.startLesson"));
        startLesson.setFocusTraversable(false);
        startLesson.setCursor(Cursor.HAND);
        startLesson.setStyle("-fx-background-color: " + NAVY + "; " + "-fx-text-fill: white; " + "-fx-font-family: '"
                + FontSupport.pickFamily() + "'; " + "-fx-font-size: 9px; " + "-fx-font-weight: bold; "
                + "-fx-background-radius: 4; " + "-fx-padding: 5 12; " + "-fx-cursor: hand;");
        startLesson.setOnAction(e -> {
            if (teacherController != null) {
                teacherController.startLesson(courseId);
            }
        });

        HBox actionButtons = new HBox(10, editCourse, addStudent, startLesson);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        HBox headerBar = new HBox(10, titleBox, headerSpacer, actionButtons);
        headerBar.setAlignment(Pos.CENTER_LEFT);

        // OPISKELIJAT
        Label studentsHeader = text(i18n.t("teacher.students"), 12, FontWeight.BOLD, "#111827");

        refreshStudents();

        ScrollPane scroll = new ScrollPane(studentList);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; " + "-fx-background: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        contentBox.getChildren().addAll(headerBar, studentsHeader, scroll);

        // LOGOUT
        Button logout = ViewSupport.createLogoutButton(onLogout);

        VBox logoutBox = new VBox(logout);
        logoutBox.setPadding(new Insets(14, 0, 0, 0));
        logoutBox.setAlignment(Pos.CENTER_RIGHT);

        contentBox.getChildren().add(logoutBox);

        setLeft(sidebar);
        setCenter(contentBox);
    }

    // =============================================================
    // EDIT COURSE DIALOG
    // =============================================================

    private void showEditCourseDialog() {

        if (teacherController == null || teacher == null) {
            return;
        }

        String currentName = "";
        String currentCode = "";

        List<TeacherCourse> courses = teacherController.getTeacherCourses(teacher.getId());

        if (courses != null) {
            for (TeacherCourse tc : courses) {
                if (tc.getCourseid() == courseId) {
                    currentName = tc.getCoursename();
                    currentCode = tc.getCode() == null ? "" : tc.getCode();
                    break;
                }
            }
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(i18n.t("course.editDialogTitle"));
        dialog.setHeaderText(i18n.t("course.editDialogHeader"));

        ButtonType saveButtonType = new ButtonType(i18n.t("course.save"), ButtonBar.ButtonData.OK_DONE);

        ButtonType cancelButtonType = new ButtonType(i18n.t("app.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        TextField nameField = new TextField(currentName);
        nameField.setPromptText(i18n.t("course.name"));
        nameField.setStyle("-fx-font-size: 11px;");

        TextField codeField = new TextField(currentCode);
        codeField.setPromptText(i18n.t("teacher.courseCodeHint"));
        codeField.setStyle("-fx-font-size: 11px;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        grid.add(new Label(i18n.t("teacher.courseNameLabel")), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label(i18n.t("teacher.courseCodeLabel")), 0, 1);
        grid.add(codeField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(currentName == null || currentName.trim().isEmpty());

        nameField.textProperty()
                .addListener((obs, oldVal, newVal) -> saveButton.setDisable(newVal == null || newVal.trim().isEmpty()));

        com.example.app.Localisation.FontSupport.applyTo(dialog.getDialogPane());
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isEmpty() || result.get() != saveButtonType) {
            return;
        }

        String newName = nameField.getText().trim();
        String newCode = codeField.getText() == null ? "" : codeField.getText().trim();

        if (newName.isEmpty()) {
            return;
        }

        boolean updated = teacherController.updateCourse(courseId, newName, newCode);

        if (updated) {
            courseTitle.setText(newName);

            String displayCode = newCode.isEmpty() ? String.format("%02d", courseId) : newCode;

            courseCode.setText(displayCode);
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(i18n.t("app.error"));
            alert.setHeaderText(null);
            alert.setContentText(i18n.t("system.error"));
            com.example.app.Localisation.FontSupport.applyTo(alert.getDialogPane());
            alert.showAndWait();
        }
    }

    // =============================================================
    // STUDENT LIST
    // =============================================================

    private void refreshStudents() {

        studentList.getChildren().clear();

        if (teacherController == null) {
            return;
        }

        List<User> students = teacherController.getStudentsForCourse(courseId);

        if (students == null || students.isEmpty()) {
            studentList.getChildren().add(text(i18n.t("teacher.noStudents"), 10, FontWeight.NORMAL, "#6B7280"));
            return;
        }

        for (User student : students) {

            List<AttendanceRecord> records = teacherController.getAttendanceForStudentAndCourse(student.getId(),
                    courseId);

            VBox studentInfo = new VBox(2, text(student.getFullName(), 10, FontWeight.BOLD, "#111827"),
                    text("ID: " + student.getId(), 8, FontWeight.NORMAL, "#6B7280"));

            HBox attendance = attendanceSummary(records);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox card = new HBox(10, studentInfo, spacer, attendance);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setPadding(new Insets(10, 14, 10, 14));
            card.setStyle("-fx-background-color: white; " + "-fx-border-color: #E5E7EB; " + "-fx-border-radius: 6; "
                    + "-fx-background-radius: 6;");

            studentList.getChildren().add(card);
        }
    }

    // =============================================================
    // ATTENDANCE SUMMARY + BAR
    // =============================================================

    private HBox attendanceSummary(List<AttendanceRecord> records) {
        HBox summary = new HBox(8);
        summary.setAlignment(Pos.CENTER_RIGHT);

        Label percentage = text(i18n.t("teacher.attendance", attendancePercentage(records)), 8, FontWeight.BOLD,
                "#374151");

        HBox bar = attendanceBar(records, 100);
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
        if (records == null || records.isEmpty()) {
            return 0;
        }
        long attended = records.stream().filter(r -> "present".equals(r.getStatus()) || "late".equals(r.getStatus()))
                .count();
        return (int) Math.round(attended * 100.0 / records.size());
    }

    // =============================================================
    // ADD STUDENT DIALOG
    // =============================================================

    private void ShowaddStudent() {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(i18n.t("course.addStudentsDialogTitle"));

        List<User> allStudents = teacherController.getAllStudents(courseId);
        Map<User, CheckBox> selectedMap = new HashMap<>();

        TextField searchField = new TextField();
        searchField.setPromptText(i18n.t("course.addStudentSearch"));
        searchField.setStyle("-fx-font-size: 11px;");

        ListView<User> listView = new ListView<>();
        listView.getItems().addAll(allStudents);
        listView.setPrefHeight(300);

        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setGraphic(null);
                }
                else {
                    CheckBox cb = selectedMap.computeIfAbsent(user, u -> new CheckBox());
                    HBox row = new HBox(10, cb, new Label(user.getFullName()));
                    row.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(row);
                }
            }
        });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal == null ? "" : newVal.toLowerCase().trim();
            listView.getItems()
                    .setAll(allStudents.stream().filter(u -> u.getFullName().toLowerCase().contains(filter)).toList());
        });

        VBox layout = new VBox(10, text(i18n.t("course.addStudentHeader"), 14, FontWeight.BOLD, "#111827"), searchField,
                listView);
        layout.setPadding(new Insets(15));
        layout.setPrefWidth(350);

        ButtonType saveBtn = new ButtonType(i18n.t("course.save"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType(i18n.t("app.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().setContent(layout);
        dialog.getDialogPane().getButtonTypes().addAll(cancelBtn, saveBtn);

        com.example.app.Localisation.FontSupport.applyTo(dialog.getDialogPane());
        dialog.showAndWait().ifPresent(btn -> {
            if (btn == saveBtn) {
                List<Integer> ids = selectedMap.entrySet().stream().filter(e -> e.getValue().isSelected())
                        .map(e -> e.getKey().getId()).toList();

                if (!ids.isEmpty() && teacherController.addStudents(ids, courseId)) {
                    refreshStudents();
                }
            }
        });
    }

    // =============================================================
    // HELPERS
    // =============================================================

    private static String initialsOf(User user) {
        return ViewSupport.initialsOf(user);
    }

    private static Label text(String value, double size, FontWeight weight, String color) {
        return ViewSupport.text(value, size, weight, color);
    }

    private static String weightToCss(FontWeight weight) {
        return ViewSupport.weightToCss(weight);
    }
}
