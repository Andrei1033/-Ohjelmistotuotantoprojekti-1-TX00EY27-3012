package com.example.app.View;

import com.example.app.DaoElements.StudentDao.AttendanceDao;
import com.example.app.Model.LoginComponents.User;
import com.example.app.Model.Teacher;
import com.example.app.Model.TeacherCourse;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherAttendanceTracking {

    // =========================================================
    // VÄRIT
    // =========================================================

    private static final String NAVY = "#202F49";
    private static final String BLUE = "#344A70";
    private static final String LOGO_BLUE = "#536FA4";

    private static final String GREEN = "#409566";
    private static final String ORANGE = "#D38A20";
    private static final String RED = "#BD4E3B";

    private static final String BORDER = "#D8D8D8";
    private static final String LIGHT_BLUE = "#EAF0F8";

    // =========================================================
    // LÄSNÄOLOTILAT
    // =========================================================

    private static final String PRESENT = "present";
    private static final String LATE = "late";
    private static final String ABSENT = "absent";

    // =========================================================
    // DATA
    // =========================================================

    private final Teacher teacher;
    private final TeacherCourse course;
    private final int lessonId;

    private final AttendanceDao attendanceDao;

    private final Map<Integer, String> attendanceStatuses =
            new HashMap<>();

    private final VBox studentList = new VBox();

    private Label presentCount;
    private Label lateCount;
    private Label absentCount;

    private final Runnable onFinished;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public TeacherAttendanceTracking(
            Teacher teacher,
            TeacherCourse course,
            int lessonId,
            Runnable onFinished
    ) {
        this.teacher = teacher;
        this.course = course;
        this.lessonId = lessonId;
        this.onFinished = onFinished;

        this.attendanceDao = new AttendanceDao();
    }

    // =========================================================
    // SHOW
    // =========================================================

    public void show() {

        Stage stage = new Stage();

        stage.setTitle(
                "Oppitunti - " + course.getCoursename()
        );

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color: white;"
        );

        // -----------------------------------------------------
        // SIDEBAR
        // -----------------------------------------------------

        VBox sidebar = createSidebar();

        root.setLeft(sidebar);

        // -----------------------------------------------------
        // MAIN CONTENT
        // -----------------------------------------------------

        VBox content = createMainContent(stage);

        root.setCenter(content);

        // -----------------------------------------------------
        // LATAA OPISKELIJAT
        // -----------------------------------------------------

        loadStudents();

        // -----------------------------------------------------
        // SCENE
        // -----------------------------------------------------

        Scene scene = new Scene(
                root,
                1000,
                650
        );

        stage.setScene(scene);

        stage.show();
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private VBox createSidebar() {

        VBox sidebar = new VBox();

        sidebar.setPrefWidth(182);
        sidebar.setMinWidth(182);
        sidebar.setMaxWidth(182);

        sidebar.setStyle(
                "-fx-background-color: " + NAVY + ";"
        );

        // -----------------------------------------------------
        // LOGO
        // -----------------------------------------------------

        Rectangle logoRectangle = new Rectangle(
                28,
                28
        );

        logoRectangle.setArcWidth(8);
        logoRectangle.setArcHeight(8);

        logoRectangle.setFill(
                Color.web(LOGO_BLUE)
        );

        Label logoText = new Label("LO");

        logoText.setTextFill(Color.WHITE);

        logoText.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        12
                )
        );

        StackPane logoBox = new StackPane(
                logoRectangle,
                logoText
        );

        logoBox.setPrefSize(28, 28);
        logoBox.setMaxSize(28, 28);

        HBox logoRow = new HBox(
                10,
                logoBox,
                new Label("Läsnäolo")
        );

        Label applicationName =
                (Label) logoRow.getChildren().get(1);

        applicationName.setTextFill(
                Color.WHITE
        );

        applicationName.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        15
                )
        );

        logoRow.setAlignment(
                Pos.CENTER_LEFT
        );

        logoRow.setPadding(
                new Insets(
                        16,
                        10,
                        16,
                        18
                )
        );

        // -----------------------------------------------------
        // KURSSINI
        // -----------------------------------------------------

        Circle courseDot = new Circle(
                2.5,
                Color.web("#6681B1")
        );

        Label courseLabel = new Label(
                "Kurssini"
        );

        courseLabel.setTextFill(
                Color.WHITE
        );

        courseLabel.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        13
                )
        );

        HBox courseContent = new HBox(
                10,
                courseDot,
                courseLabel
        );

        courseContent.setAlignment(
                Pos.CENTER_LEFT
        );

        Button courseButton = new Button();

        courseButton.setGraphic(
                courseContent
        );

        courseButton.setPrefHeight(32);
        courseButton.setMinHeight(32);
        courseButton.setMaxHeight(32);

        courseButton.setMaxWidth(
                Double.MAX_VALUE
        );

        courseButton.setAlignment(
                Pos.CENTER_LEFT
        );

        String courseButtonBase =
                "-fx-background-color: " + BLUE + ";" +
                        "-fx-background-radius: 5;" +
                        "-fx-padding: 0 10;" +
                        "-fx-cursor: hand;";

        courseButton.setStyle(
                courseButtonBase
        );

        courseButton.setOnMouseEntered(e ->
                courseButton.setStyle(
                        courseButtonBase.replace(
                                BLUE,
                                "#3F567E"
                        )
                )
        );

        courseButton.setOnMouseExited(e ->
                courseButton.setStyle(
                        courseButtonBase
                )
        );

        VBox courseContainer = new VBox(
                courseButton
        );

        courseContainer.setPadding(
                new Insets(
                        16,
                        16,
                        0,
                        16
                )
        );

        // -----------------------------------------------------
        // SPACER
        // -----------------------------------------------------

        Region sidebarSpacer = new Region();

        VBox.setVgrow(
                sidebarSpacer,
                Priority.ALWAYS
        );

        // -----------------------------------------------------
        // OPETTAJAN TIEDOT
        // -----------------------------------------------------

        Circle userCircle = new Circle(
                15,
                Color.web(LOGO_BLUE)
        );

        String teacherName = getTeacherName();

        Label userInitials = new Label(
                getInitials(teacherName)
        );

        userInitials.setTextFill(
                Color.WHITE
        );

        userInitials.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        10
                )
        );

        StackPane userIcon = new StackPane(
                userCircle,
                userInitials
        );

        userIcon.setPrefSize(30, 30);
        userIcon.setMaxSize(30, 30);

        Label userName = new Label(
                teacherName
        );

        userName.setTextFill(
                Color.WHITE
        );

        userName.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        11
                )
        );

        Label userRole = new Label(
                "Opettaja"
        );

        userRole.setTextFill(
                Color.web("#9BA8BB")
        );

        userRole.setFont(
                Font.font(
                        "System",
                        FontWeight.NORMAL,
                        9
                )
        );

        VBox userText = new VBox(
                0,
                userName,
                userRole
        );

        HBox userBox = new HBox(
                8,
                userIcon,
                userText
        );

        userBox.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox sidebarBottom = new VBox(
                userBox
        );

        sidebarBottom.setPadding(
                new Insets(
                        0,
                        10,
                        12,
                        14
                )
        );

        sidebar.getChildren().addAll(
                logoRow,
                courseContainer,
                sidebarSpacer,
                sidebarBottom
        );

        return sidebar;
    }

    // =========================================================
    // MAIN CONTENT
    // =========================================================

    private VBox createMainContent(Stage stage) {

        VBox content = new VBox();

        content.setPadding(
                new Insets(
                        38,
                        30,
                        30,
                        28
                )
        );

        content.setStyle(
                "-fx-background-color: white;"
        );

        // -----------------------------------------------------
        // OTSIKKO
        // -----------------------------------------------------

        Label title = new Label(
                "Oppitunti — " + getCurrentDate()
        );

        title.setTextFill(
                Color.web("#171717")
        );

        title.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        20
                )
        );

        // -----------------------------------------------------
        // TALLENNA
        // -----------------------------------------------------

        Button saveButton = new Button(
                "Tallenna ja lopeta"
        );

        saveButton.setPrefWidth(150);
        saveButton.setPrefHeight(36);

        saveButton.setMinWidth(150);
        saveButton.setMinHeight(36);

        saveButton.setMaxWidth(150);
        saveButton.setMaxHeight(36);

        saveButton.setTextFill(
                Color.WHITE
        );

        saveButton.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        12
                )
        );

        String saveButtonBase =
                "-fx-background-color: " + NAVY + ";" +
                        "-fx-background-radius: 4;" +
                        "-fx-cursor: hand;";

        saveButton.setStyle(
                saveButtonBase
        );

        saveButton.setOnMouseEntered(e ->
                saveButton.setStyle(
                        saveButtonBase.replace(
                                NAVY,
                                "#2C4066"
                        )
                )
        );

        saveButton.setOnMouseExited(e ->
                saveButton.setStyle(
                        saveButtonBase
                )
        );

        saveButton.setOnAction(event ->
                saveAndFinish(stage)
        );

        Region titleSpacer = new Region();

        HBox.setHgrow(
                titleSpacer,
                Priority.ALWAYS
        );

        HBox titleRow = new HBox(
                title,
                titleSpacer,
                saveButton
        );

        titleRow.setAlignment(
                Pos.CENTER_LEFT
        );

        // -----------------------------------------------------
        // VÄLI
        // -----------------------------------------------------

        Region topSpace = new Region();

        topSpace.setPrefHeight(38);
        topSpace.setMinHeight(38);

        // -----------------------------------------------------
        // ATTENDANCE CARD
        // -----------------------------------------------------

        VBox attendanceCard =
                createAttendanceCard();

        attendanceCard.setMaxWidth(
                Double.MAX_VALUE
        );

        ScrollPane scrollPane =
                new ScrollPane(
                        attendanceCard
                );

        scrollPane.setFitToWidth(true);

        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background: transparent;"
        );

        VBox.setVgrow(
                scrollPane,
                Priority.ALWAYS
        );

        content.getChildren().addAll(
                titleRow,
                topSpace,
                scrollPane
        );

        VBox.setVgrow(
                content,
                Priority.ALWAYS
        );

        return content;
    }

    // =========================================================
    // ATTENDANCE CARD
    // =========================================================

    private VBox createAttendanceCard() {

        VBox card = new VBox();

        card.setMaxWidth(
                Double.MAX_VALUE
        );

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 16;" +
                        "-fx-background-radius: 16;"
        );

        DropShadow shadow = new DropShadow();

        shadow.setRadius(14);
        shadow.setOffsetY(3);
        shadow.setColor(
                Color.rgb(
                        0,
                        0,
                        0,
                        0.07
                )
        );

        card.setEffect(shadow);

        // -----------------------------------------------------
        // HEADER
        // -----------------------------------------------------

        Label studentHeader = new Label(
                "Opiskelijat"
        );

        studentHeader.setTextFill(
                Color.web("#333333")
        );

        studentHeader.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        13
                )
        );

        presentCount =
                createCountLabel(GREEN);

        lateCount =
                createCountLabel(ORANGE);

        absentCount =
                createCountLabel(RED);

        HBox counts = new HBox(
                28,
                presentCount,
                lateCount,
                absentCount
        );

        counts.setAlignment(
                Pos.CENTER_RIGHT
        );

        Region headerSpacer = new Region();

        HBox.setHgrow(
                headerSpacer,
                Priority.ALWAYS
        );

        HBox header = new HBox(
                studentHeader,
                headerSpacer,
                counts
        );

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        header.setPadding(
                new Insets(
                        0,
                        20,
                        0,
                        18
                )
        );

        header.setPrefHeight(52);
        header.setMinHeight(52);
        header.setMaxHeight(52);

        Region headerLine = new Region();

        headerLine.setPrefHeight(1);
        headerLine.setMaxHeight(1);

        headerLine.setStyle(
                "-fx-background-color: " + BORDER + ";"
        );

        card.getChildren().addAll(
                header,
                headerLine,
                studentList
        );

        return card;
    }

    // =========================================================
    // OPISKELIJOIDEN LATAUS
    // =========================================================

    private void loadStudents() {

        studentList.getChildren().clear();

        attendanceStatuses.clear();

        List<User> students =
                attendanceDao.getStudentsForCourse(
                        course.getCourseid()
                );

        if (students.isEmpty()) {

            Label emptyLabel =
                    new Label(
                            "Kurssilla ei ole opiskelijoita."
                    );

            emptyLabel.setPadding(
                    new Insets(25)
            );

            studentList.getChildren().add(
                    emptyLabel
            );

            updateCounters();

            return;
        }

        for (User student : students) {

            createStudentRow(student);
        }

        updateCounters();
    }

    // =========================================================
    // OPISKELIJARIVI
    // =========================================================

    private void createStudentRow(User student) {

        int studentId =
                student.getId();

        // Oletuksena POISSA
        attendanceStatuses.putIfAbsent(
                studentId,
                ABSENT
        );

        String studentName =
                student.getFullName();

        // -----------------------------------------------------
        // AVATAR
        // -----------------------------------------------------

        Circle avatarCircle = new Circle(
                17,
                Color.web(LIGHT_BLUE)
        );

        Label initials = new Label(
                getInitials(studentName)
        );

        initials.setTextFill(
                Color.web("#4C6D9D")
        );

        initials.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        10
                )
        );

        StackPane avatar = new StackPane(
                avatarCircle,
                initials
        );

        avatar.setPrefSize(
                34,
                34
        );

        avatar.setMinSize(
                34,
                34
        );

        avatar.setMaxSize(
                34,
                34
        );

        // -----------------------------------------------------
        // NIMI
        // -----------------------------------------------------

        Label nameLabel =
                new Label(studentName);

        nameLabel.setTextFill(
                Color.BLACK
        );
        nameLabel.setStyle(
                "-fx-text-fill: #000000;" +
                        "-fx-font-weight: bold;"
        );

        nameLabel.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        12
                )

        );

        Label roleLabel =
                new Label("Opiskelija");

        roleLabel.setTextFill(
                Color.web("#888888")
        );

        roleLabel.setFont(
                Font.font(
                        "System",
                        FontWeight.NORMAL,
                        9
                )
        );

        VBox studentInfo = new VBox(
                1,
                nameLabel,
                roleLabel
        );

        studentInfo.setAlignment(
                Pos.CENTER_LEFT
        );

        HBox studentBox = new HBox(
                12,
                avatar,
                studentInfo
        );

        studentBox.setAlignment(
                Pos.CENTER_LEFT
        );

        // -----------------------------------------------------
        // NAPIT
        // -----------------------------------------------------

        Button presentButton =
                createStatusButton("Paikalla");

        Button lateButton =
                createStatusButton("Myöhässä");

        Button absentButton =
                createStatusButton("Poissa");

        presentButton.setOnAction(event -> {

            attendanceStatuses.put(
                    studentId,
                    PRESENT
            );

            updateButtonStyles(
                    presentButton,
                    lateButton,
                    absentButton,
                    PRESENT
            );

            updateCounters();
        });

        lateButton.setOnAction(event -> {

            attendanceStatuses.put(
                    studentId,
                    LATE
            );

            updateButtonStyles(
                    presentButton,
                    lateButton,
                    absentButton,
                    LATE
            );

            updateCounters();
        });

        absentButton.setOnAction(event -> {

            attendanceStatuses.put(
                    studentId,
                    ABSENT
            );

            updateButtonStyles(
                    presentButton,
                    lateButton,
                    absentButton,
                    ABSENT
            );

            updateCounters();
        });

        HBox buttons = new HBox(
                8,
                presentButton,
                lateButton,
                absentButton
        );

        buttons.setAlignment(
                Pos.CENTER_RIGHT
        );

        // -----------------------------------------------------
        // SPACER
        // -----------------------------------------------------

        Region spacer = new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        // -----------------------------------------------------
        // RIVI
        // -----------------------------------------------------

        HBox row = new HBox(
                studentBox,
                spacer,
                buttons
        );

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(
                        0,
                        18,
                        0,
                        18
                )
        );

        row.setPrefHeight(58);
        row.setMinHeight(58);
        row.setMaxHeight(58);

        String normalStyle =
                "-fx-background-color: white;" +
                        "-fx-border-color: transparent transparent "
                        + BORDER + " transparent;";

        String hoverStyle =
                "-fx-background-color: #FAFBFD;" +
                        "-fx-border-color: transparent transparent "
                        + BORDER + " transparent;";

        row.setStyle(
                normalStyle
        );

        row.setOnMouseEntered(event ->
                row.setStyle(
                        hoverStyle
                )
        );

        row.setOnMouseExited(event ->
                row.setStyle(
                        normalStyle
                )
        );

        // -----------------------------------------------------
        // ALUSTA POISSA
        // -----------------------------------------------------

        updateButtonStyles(
                presentButton,
                lateButton,
                absentButton,
                attendanceStatuses.get(studentId)
        );

        studentList.getChildren().add(
                row
        );
    }

    // =========================================================
    // STATUS BUTTON
    // =========================================================

    private Button createStatusButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setPrefWidth(94);
        button.setMinWidth(94);
        button.setMaxWidth(94);

        button.setPrefHeight(30);
        button.setMinHeight(30);
        button.setMaxHeight(30);

        button.setFont(
                Font.font(
                        "System",
                        FontWeight.NORMAL,
                        10
                )
        );

        return button;
    }

    // =========================================================
    // BUTTONIEN VÄRIT
    // =========================================================

    private void updateButtonStyles(
            Button presentButton,
            Button lateButton,
            Button absentButton,
            String selectedStatus
    ) {

        String inactiveStyle =
                "-fx-background-color: white;" +
                        "-fx-text-fill: #777777;" +
                        "-fx-border-color: #D8D8D8;" +
                        "-fx-border-width: 1;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-cursor: hand;";

        String presentStyle =
                "-fx-background-color: " + GREEN + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: " + GREEN + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;";

        String lateStyle =
                "-fx-background-color: " + ORANGE + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: " + ORANGE + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;";

        String absentStyle =
                "-fx-background-color: " + RED + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: " + RED + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-radius: 16;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;";

        // Paikalla
        presentButton.setStyle(
                PRESENT.equals(selectedStatus)
                        ? presentStyle
                        : inactiveStyle
        );

        // Myöhässä
        lateButton.setStyle(
                LATE.equals(selectedStatus)
                        ? lateStyle
                        : inactiveStyle
        );

        // Poissa
        absentButton.setStyle(
                ABSENT.equals(selectedStatus)
                        ? absentStyle
                        : inactiveStyle
        );
    }

    // =========================================================
    // LASKURIT
    // =========================================================

    private void updateCounters() {

        if (presentCount == null ||
                lateCount == null ||
                absentCount == null) {
            return;
        }

        int present = 0;
        int late = 0;
        int absent = 0;

        for (String status :
                attendanceStatuses.values()) {

            if (PRESENT.equals(status)) {

                present++;

            } else if (LATE.equals(status)) {

                late++;

            } else if (ABSENT.equals(status)) {

                absent++;
            }
        }

        presentCount.setText(
                present + " Paikalla"
        );

        lateCount.setText(
                late + " Myöhässä"
        );

        absentCount.setText(
                absent + " Poissa"
        );
    }

    // =========================================================
    // COUNT LABEL
    // =========================================================

    private Label createCountLabel(
            String color
    ) {

        Label label = new Label();

        label.setTextFill(
                Color.web(color)
        );

        label.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        11
                )
        );

        return label;
    }

    // =========================================================
    // TALLENNUS
    // =========================================================

    private void saveAndFinish(
            Stage stage
    ) {

        boolean success = true;

        for (Map.Entry<Integer, String> entry :
                attendanceStatuses.entrySet()) {

            boolean saved =
                    attendanceDao.saveAttendance(
                            lessonId,
                            entry.getKey(),
                            entry.getValue()
                    );

            if (!saved) {

                success = false;
            }
        }

        if (success) {

            boolean lessonEnded =
                    attendanceDao.endLesson(
                            lessonId
                    );

            if (lessonEnded) {

                stage.close();

                if (onFinished != null) {

                    onFinished.run();
                }

            } else {

                showError(
                        "Oppitunnin lopettaminen epäonnistui."
                );
            }

        } else {

            showError(
                    "Läsnäolojen tallentamisessa tapahtui virhe."
            );
        }
    }

    // =========================================================
    // VIRHE
    // =========================================================

    private void showError(
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(
                "Virhe"
        );

        alert.setHeaderText(
                null
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }

    // =========================================================
    // OPETTAJAN NIMI
    // =========================================================

    private String getTeacherName() {

        if (teacher == null) {

            return "Etunimi Sukunimi";
        }

        try {

            String name =
                    teacher.getFullName();

            if (name != null &&
                    !name.isBlank()) {

                return name;
            }

        } catch (Exception ignored) {
        }

        return "Etunimi Sukunimi";
    }

    // =========================================================
    // INITIALS
    // =========================================================

    private String getInitials(
            String name
    ) {

        if (name == null ||
                name.isBlank()) {

            return "MA";
        }

        String[] parts =
                name.trim().split("\\s+");

        if (parts.length == 1) {

            return parts[0]
                    .substring(
                            0,
                            Math.min(
                                    2,
                                    parts[0].length()
                            )
                    )
                    .toUpperCase();
        }

        return (
                parts[0].substring(0, 1) +
                        parts[parts.length - 1]
                                .substring(0, 1)
        ).toUpperCase();
    }

    // =========================================================
    // PÄIVÄMÄÄRÄ
    // =========================================================

    private String getCurrentDate() {

        java.time.LocalDate date =
                java.time.LocalDate.now();

        return date.getDayOfMonth()
                + "."
                + date.getMonthValue()
                + "."
                + date.getYear();
    }
}
