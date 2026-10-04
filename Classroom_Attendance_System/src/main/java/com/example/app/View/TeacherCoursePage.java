package com.example.app.View;

import com.example.app.Controller.TeacherController;
import com.example.app.Model.LoginComponents.User;
import com.example.app.Model.Teacher;
import com.example.app.Model.TeacherCourse;
import com.example.app.Model.StudentComponents.AttendanceRecord;
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
import java.util.Optional;

public class TeacherCoursePage extends BorderPane {

    private static final String NAVY = "#202F49";
    private final TeacherController teacherController;
    private final int courseId;
    private final VBox studentList;
    private final Teacher teacher;

    // Header-elementit, joita päivitetään muokkauksen jälkeen
    private Label courseTitle;
    private Label courseCode;

    public TeacherCoursePage(Teacher teacher,
                             int courseId,
                             TeacherController teacherController,
                             Runnable onBack,
                             Runnable onAddLesson) {
        this.teacher = teacher;
        this.teacherController = teacherController;
        this.courseId = courseId;
        this.studentList = new VBox(10);

        setStyle("-fx-background-color: white;");

        // =========================================================
        // SIDEBAR
        // =========================================================

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(160);
        sidebar.setPadding(new Insets(15, 12, 12, 12));
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

        String teacherName = (teacher != null)
                ? teacher.getFirstName() + " " + teacher.getLastName()
                : "Etunimi Sukunimi";

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

        // =========================================================
        // CONTENT
        // =========================================================

        VBox contentBox = new VBox(16);
        contentBox.setPadding(new Insets(25, 30, 25, 30));

        // Haetaan kurssin tiedot oikein (nimi + koodi)
        String courseNameText = "Kurssi " + courseId;
        String courseCodeText = "";

        if (teacherController != null && teacher != null) {
            List<TeacherCourse> courses =
                    teacherController.getTeacherCourses(teacher.getId());

            for (TeacherCourse tc : courses) {
                if (tc.getCourseid() == courseId) {
                    courseNameText = tc.getCoursename();

                    if (tc.getCode() != null && !tc.getCode().isBlank()) {
                        courseCodeText = tc.getCode();
                    } else {
                        // Fallback: jos koodi on tyhjä, näytetään ID
                        courseCodeText = String.format("%02d", courseId);
                    }
                    break;
                }
            }
        }

        courseTitle = text("Kurssi: " + courseNameText, 16, FontWeight.BOLD, "#202F49");
        courseCode = text("Kurssikoodi: " + courseCodeText, 14, FontWeight.NORMAL, "#202F49");
        VBox titleBox = new VBox(2, courseTitle, courseCode);

        // ---------------------------------------------------------
        // NAPIT
        // ---------------------------------------------------------

        Button editCourse = new Button("Muokkaa kurssia");
        editCourse.setStyle("-fx-background-color: transparent; -fx-border-color: #D1D5DB; "
                + "-fx-border-radius: 4; -fx-background-radius: 4; -fx-font-size: 8px; "
                + "-fx-font-weight: bold; -fx-text-fill: #374151; -fx-cursor: hand;");
        editCourse.setOnAction(e -> showEditCourseDialog());

        Button addStudent = new Button("Lisää opiskelija");
        addStudent.setStyle("-fx-background-color: transparent; -fx-border-color: #D1D5DB; "
                + "-fx-border-radius: 4; -fx-background-radius: 4; -fx-font-size: 8px; "
                + "-fx-font-weight: bold; -fx-text-fill: #374151; -fx-cursor: hand;");
        addStudent.setOnAction(e -> ShowaddStudent());

        Button startLesson = new Button("Aloita oppitunti");
        startLesson.setStyle("-fx-background-color: " + NAVY + "; -fx-text-fill: white; "
                + "-fx-font-size: 8px; -fx-font-weight: bold; -fx-background-radius: 4; -fx-cursor: hand;");
        startLesson.setOnAction(e -> teacherController.startLesson(courseId));

        HBox actionButtons = new HBox(10, editCourse, addStudent, startLesson);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        HBox headerBar = new HBox(10, titleBox, headerSpacer, actionButtons);

        // ---------------------------------------------------------
        // OPISKELIJAT
        // ---------------------------------------------------------

        Label studentsHeader = text("Opiskelijat", 12, FontWeight.BOLD, "#111827");
        refreshStudents();

        contentBox.getChildren().addAll(headerBar, studentsHeader, studentList);

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

        // Haetaan nykyiset tiedot
        String currentName = "";
        String currentCode = "";

        List<TeacherCourse> courses =
                teacherController.getTeacherCourses(teacher.getId());

        for (TeacherCourse tc : courses) {
            if (tc.getCourseid() == courseId) {
                currentName = tc.getCoursename();
                currentCode = tc.getCode() == null ? "" : tc.getCode();
                break;
            }
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Muokkaa kurssia");
        dialog.setHeaderText("Muokkaa kurssin tietoja.");

        ButtonType saveButtonType =
                new ButtonType("Tallenna", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(saveButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField(currentName);
        nameField.setPromptText("Kurssin nimi");

        TextField codeField = new TextField(currentCode);
        codeField.setPromptText("Kurssikoodi (esim. CS2026)");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        grid.add(new Label("Kurssin nimi:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Kurssikoodi:"), 0, 1);
        grid.add(codeField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Estä tallennus tyhjällä nimellä
        javafx.scene.Node saveButton =
                dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(currentName == null || currentName.trim().isEmpty());

        nameField.textProperty().addListener((obs, oldVal, newVal) ->
                saveButton.setDisable(newVal == null || newVal.trim().isEmpty())
        );

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isEmpty() || result.get() != saveButtonType) {
            return;
        }

        String newName = nameField.getText().trim();
        String newCode = codeField.getText() == null
                ? ""
                : codeField.getText().trim();

        if (newName.isEmpty()) {
            return;
        }

        boolean updated =
                teacherController.updateCourse(courseId, newName, newCode);

        if (updated) {
            // Päivitä header ilman koko sivun uudelleenlatausta
            courseTitle.setText("Kurssi: " + newName);

            String displayCode = newCode.isEmpty()
                    ? String.format("%02d", courseId)
                    : newCode;

            courseCode.setText("Kurssikoodi: " + displayCode);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Virhe");
            alert.setHeaderText(null);
            alert.setContentText("Kurssin päivittäminen epäonnistui.");
            alert.showAndWait();
        }
    }

    // =============================================================
    // APUMETODIT (ennallaan)
    // =============================================================

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
            status.setStyle("-fx-background-color: #DCFCE7; -fx-background-radius: 12; "
                    + "-fx-border-color: #BBF7D0; -fx-border-radius: 12;");
        } else {
            Label label = text(statusText, 8, FontWeight.BOLD, "#374151");
            status.getChildren().add(label);
            status.setStyle("-fx-background-color: #F3F4F6; -fx-background-radius: 12; "
                    + "-fx-border-color: #E5E7EB; -fx-border-radius: 12;");
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
            studentList.getChildren().add(
                    text("Tälle kurssille ei ole vielä lisätty opiskelijoita.",
                            10, FontWeight.NORMAL, "#6B7280"));
            return;
        }
        students.forEach(student -> {
            List<AttendanceRecord> records =
                    teacherController.getAttendanceForStudentAndCourse(
                            student.getId(), courseId);
            VBox studentInfo = new VBox(2,
                    text(student.getFullName(), 10, FontWeight.BOLD, "#111827"),
                    text("ID: " + student.getId(), 8, FontWeight.NORMAL, "#6B7280"));
            HBox attendance = attendanceSummary(records);
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox card = new HBox(
                    studentInfo, spacer, attendance
            );
            card.setAlignment(Pos.CENTER_LEFT);
            card.setPadding(new Insets(10, 14, 10, 14));
            card.setStyle("-fx-background-color: white; -fx-border-color: #E5E7EB; "
                    + "-fx-border-radius: 6; -fx-background-radius: 6;");
            studentList.getChildren().add(card);
        });
    }

    private HBox attendanceSummary(List<AttendanceRecord> records) {
        HBox summary = new HBox(8);
        summary.setAlignment(Pos.CENTER_RIGHT);

        Label percentage = text("Läsnäolo " + attendancePercentage(records) + " %", 8,
                FontWeight.BOLD, "#374151");
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
        if (records.isEmpty()) {
            return 0;
        }
        long attended = records.stream()
                .filter(r -> "present".equals(r.getStatus()) || "late".equals(r.getStatus()))
                .count();
        return (int) Math.round(attended * 100.0 / records.size());
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

        VBox layout = new VBox(10,
                text("Lisää opiskelijoita", 14, FontWeight.BOLD, "#111827"),
                searchField,
                listView);
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