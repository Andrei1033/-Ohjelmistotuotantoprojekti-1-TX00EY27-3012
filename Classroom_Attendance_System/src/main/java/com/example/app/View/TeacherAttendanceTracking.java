package com.example.app.View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class TeacherAttendanceTracking extends BorderPane {


    // VÄRIT


    private static final String NAVY = "#202F49";
    private static final String BLUE = "#344A70";
    private static final String LOGO_BLUE = "#536FA4";

    private static final String GREEN = "#409566";
    private static final String ORANGE = "#D38A20";
    private static final String RED = "#BD4E3B";

    private static final String BORDER = "#D8D8D8";
    private static final String MUTED = "#858585";
    private static final String LIGHT_BLUE = "#EAF0F8";


    // OPISKELIJOIDEN TILAT


    private static final int PRESENT = 1;
    private static final int LATE = 2;
    private static final int ABSENT = 3;

    private final List<StudentRow> studentRows = new ArrayList<>();

    private Label presentCount;
    private Label lateCount;
    private Label absentCount;


    // CONSTRUCTOR


    public TeacherAttendanceTracking(Runnable onFinish) {

        setStyle(
                "-fx-background-color: white;"
        );


        // VASEN SIDEBAR


        VBox sidebar = createSidebar();

        setLeft(sidebar);


        // PÄÄSISÄLTÖ


        VBox content = createMainContent(onFinish);

        setCenter(content);
    }


    // SIDEBAR


    private VBox createSidebar() {

        VBox sidebar = new VBox();

        sidebar.setPrefWidth(220);
        sidebar.setMinWidth(220);
        sidebar.setMaxWidth(220);

        sidebar.setStyle(
                "-fx-background-color: " + NAVY + ";"
        );


        // LOGO


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

        logoBox.setAlignment(
                Pos.CENTER
        );


        // LÄSNÄOLO


        Label applicationName = new Label(
                "Läsnäolo"
        );

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

        HBox logoRow = new HBox(
                10,
                logoBox,
                applicationName
        );

        logoRow.setAlignment(
                Pos.CENTER_LEFT
        );

        logoRow.setPadding(
                new Insets(
                        14,
                        10,
                        14,
                        20
                )
        );


        // SIDEBAR SEPARATOR


        Region separator = new Region();

        separator.setPrefHeight(1);
        separator.setMaxHeight(1);

        separator.setStyle(
                "-fx-background-color: #2B3A54;"
        );


        // KURSSINI


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
                        "-fx-background-radius: 4;" +
                        "-fx-padding: 0 10;" +
                        "-fx-cursor: hand;";

        courseButton.setStyle(courseButtonBase);

        courseButton.setOnMouseEntered(e ->
                courseButton.setStyle(
                        courseButtonBase.replace(BLUE, "#3F567E")
                )
        );

        courseButton.setOnMouseExited(e ->
                courseButton.setStyle(courseButtonBase)
        );

        VBox courseContainer = new VBox(
                courseButton
        );

        courseContainer.setPadding(
                new Insets(
                        16,
                        14,
                        0,
                        14
                )
        );


        // SIDEBARIN ALAOSA


        Region sidebarSpacer = new Region();

        VBox.setVgrow(
                sidebarSpacer,
                Priority.ALWAYS
        );

        Circle userCircle = new Circle(
                15,
                Color.web(LOGO_BLUE)
        );

        Label userInitials = new Label(
                "MA"
        );

        userInitials.setTextFill(
                Color.WHITE
        );

        userInitials.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        11
                )
        );

        StackPane userIcon = new StackPane(
                userCircle,
                userInitials
        );

        userIcon.setPrefSize(30, 30);
        userIcon.setMaxSize(30, 30);

        Label userName = new Label(
                "Etunimi Sukunimi"
        );

        userName.setTextFill(
                Color.WHITE
        );

        userName.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        12
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
                        10
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

        // SIDEBAR KOKONAISUUDESSAAN

        sidebar.getChildren().addAll(
                logoRow,
                separator,
                courseContainer,
                sidebarSpacer,
                sidebarBottom
        );

        return sidebar;
    }


    // MAIN CONTENT


    private VBox createMainContent(
            Runnable onFinish
    ) {

        VBox content = new VBox();

        content.setPadding(
                new Insets(
                        40,
                        40,
                        40,
                        40
                )
        );

        content.setStyle(
                "-fx-background-color: white;"
        );


        // OTSIKKO


        Label title = new Label(
                "Oppitunti11111111111111 — 16.9.2026"
        );

        title.setTextFill(
                Color.web("#171717")
        );

        title.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        22
                )
        );


        // TALLENNA JA LOPETA


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
                        13
                )
        );

        String saveButtonBase =
                "-fx-background-color: " + NAVY + ";" +
                        "-fx-background-radius: 3;" +
                        "-fx-cursor: hand;";

        saveButton.setStyle(saveButtonBase);

        saveButton.setOnMouseEntered(e ->
                saveButton.setStyle(
                        saveButtonBase.replace(NAVY, "#2C4066")
                )
        );

        saveButton.setOnMouseExited(e ->
                saveButton.setStyle(saveButtonBase)
        );

        saveButton.setOnAction(event -> {

            /*
             * Tähän voidaan myöhemmin lisätä tietokantaan
             * tallentaminen.
             */

            if (onFinish != null) {
                onFinish.run();
            }
        });

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


        // TYHJÄ TILA


        Region topSpace = new Region();

        topSpace.setPrefHeight(44);
        topSpace.setMinHeight(44);


        // OPISKELIJOIDEN KORTTI


        VBox attendanceCard =
                createAttendanceCard();

        // Kortti venyy nyt ikkunan levyiseksi ja kasvaa/pienenee
        // ikkunan koon mukana.
        attendanceCard.setMaxWidth(Double.MAX_VALUE);

        // Jos opiskelijoita on paljon, lista vierittyy sen sijaan
        // että se leikkautuisi tai venyttäisi ikkunaa loputtomiin.
        ScrollPane scrollPane = new ScrollPane(attendanceCard);

        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background: transparent;"
        );

        VBox.setVgrow(
                scrollPane,
                Priority.ALWAYS
        );


        // MAIN CONTENT


        VBox.setVgrow(content, Priority.ALWAYS);

        content.getChildren().addAll(
                titleRow,
                topSpace,
                scrollPane
        );

        return content;
    }


    // ATTENDANCE CARD


    private VBox createAttendanceCard() {

        VBox card = new VBox();

        card.setPrefWidth(1000);
        card.setMaxWidth(
                Double.MAX_VALUE
        );

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 16;" +
                        "-fx-background-radius: 16;"
        );

        // Kevyt varjostus, jotta kortti erottuu valkoisesta
        // taustasta paremmin.
        DropShadow shadow = new DropShadow();
        shadow.setRadius(18);
        shadow.setOffsetY(4);
        shadow.setColor(Color.rgb(0, 0, 0, 0.08));

        card.setEffect(shadow);


        // HEADER


        Label studentHeader = new Label(
                "Opiskelijat (7)"
        );

        studentHeader.setTextFill(
                Color.web("#333333")
        );

        studentHeader.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        14
                )
        );

        presentCount = createCountLabel(GREEN);
        lateCount = createCountLabel(ORANGE);
        absentCount = createCountLabel(RED);

        HBox counts = new HBox(
                36,
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
                        24,
                        0,
                        30
                )
        );

        header.setPrefHeight(56);
        header.setMinHeight(56);
        header.setMaxHeight(56);


        // HEADER LINE


        Region headerLine = new Region();

        headerLine.setPrefHeight(1);
        headerLine.setMaxHeight(1);

        headerLine.setStyle(
                "-fx-background-color: " + BORDER + ";"
        );

        card.getChildren().addAll(
                header,
                headerLine
        );


        // OPISKELIJAT

        String[] names = {
                "Etunimi Sukunimi",
                "Etunimi Sukunimi",
                "Etunimi Sukunimi",
                "Etunimi Sukunimi",
                "Etunimi Sukunimi",
                "Etunimi Sukunimi",
                "Etunimi Sukunimi"
        };

        /*
         * Kuvan mukainen lähtötilanne:
         *
         * 1. Paikalla
         * 2. Paikalla
         * 3. Myöhässä
         * 4. Poissa
         * 5. Paikalla
         * 6. Paikalla
         * 7. Paikalla
         */

        int[] states = {
                PRESENT,
                PRESENT,
                LATE,
                ABSENT,
                PRESENT,
                PRESENT,
                PRESENT
        };

        for (int i = 0; i < names.length; i++) {

            StudentRow row = new StudentRow(
                    names[i],
                    states[i]
            );

            studentRows.add(row);

            card.getChildren().add(
                    row.getRow()
            );
        }

        updateCounters();

        return card;
    }


    // COUNT LABEL

    private Label createCountLabel(String accentColor) {

        Label label = new Label();

        label.setTextFill(
                Color.web(accentColor)
        );

        label.setFont(
                Font.font(
                        "System",
                        FontWeight.BOLD,
                        13
                )
        );

        return label;
    }


    // PÄIVITÄ LUKUMÄÄRÄT


    private void updateCounters() {

        int present = 0;
        int late = 0;
        int absent = 0;

        for (StudentRow row : studentRows) {

            if (row.getState() == PRESENT) {
                present++;
            }

            else if (row.getState() == LATE) {
                late++;
            }

            else if (row.getState() == ABSENT) {
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


    // STUDENT ROW


    private class StudentRow {

        private final String name;

        private int state;

        private final HBox row;

        private final Button presentButton;
        private final Button lateButton;
        private final Button absentButton;


        // CONSTRUCTOR


        StudentRow(
                String name,
                int initialState
        ) {

            this.name = name;
            this.state = initialState;


            // AVATAR


            Circle avatarCircle = new Circle(
                    17,
                    Color.web(LIGHT_BLUE)
            );

            Label initials = new Label(
                    "MA"
            );

            initials.setTextFill(
                    Color.web("#4C6D9D")
            );

            initials.setFont(
                    Font.font(
                            "System",
                            FontWeight.BOLD,
                            12
                    )
            );

            StackPane avatar = new StackPane(
                    avatarCircle,
                    initials
            );

            avatar.setPrefSize(34, 34);
            avatar.setMinSize(34, 34);
            avatar.setMaxSize(34, 34);


            // OPISKELIJAN NIMI


            Label nameLabel = new Label(
                    name
            );

            nameLabel.setTextFill(
                    Color.web("#333333")
            );

            nameLabel.setFont(
                    Font.font(
                            "System",
                            FontWeight.BOLD,
                            13
                    )
            );

            Label roleLabel = new Label(
                    "Opiskelija"
            );

            roleLabel.setTextFill(
                    Color.web("#888888")
            );

            roleLabel.setFont(
                    Font.font(
                            "System",
                            FontWeight.NORMAL,
                            10
                    )
            );

            VBox studentInfo = new VBox(
                    2,
                    nameLabel,
                    roleLabel
            );

            studentInfo.setAlignment(
                    Pos.CENTER_LEFT
            );


            // STUDENT INFO

            HBox student = new HBox(
                    16,
                    avatar,
                    studentInfo
            );

            student.setAlignment(
                    Pos.CENTER_LEFT
            );


            // NAPIT


            presentButton =
                    createStatusButton(
                            "Paikalla"
                    );

            lateButton =
                    createStatusButton(
                            "Myöhässä"
                    );

            absentButton =
                    createStatusButton(
                            "Poissa"
                    );


            // BUTTON ACTIONS


            presentButton.setOnAction(
                    event -> setState(PRESENT)
            );

            lateButton.setOnAction(
                    event -> setState(LATE)
            );

            absentButton.setOnAction(
                    event -> setState(ABSENT)
            );


            // BUTTON CONTAINER


            HBox buttons = new HBox(
                    10,
                    presentButton,
                    lateButton,
                    absentButton
            );

            buttons.setAlignment(
                    Pos.CENTER_RIGHT
            );


            // SPACER


            Region spacer = new Region();

            HBox.setHgrow(
                    spacer,
                    Priority.ALWAYS
            );


            // ROW


            row = new HBox(
                    student,
                    spacer,
                    buttons
            );

            row.setAlignment(
                    Pos.CENTER_LEFT
            );

            row.setPadding(
                    new Insets(
                            0,
                            24,
                            0,
                            30
                    )
            );

            row.setPrefHeight(66);
            row.setMinHeight(66);
            row.setMaxHeight(66);

            row.setStyle(
                    "-fx-border-color: transparent transparent "
                            + BORDER + " transparent;"
            );

            // Rivi korostuu hiiren alla, jotta on selkeämpää
            // mitä opiskelijaa ollaan muokkaamassa.
            row.setOnMouseEntered(e ->
                    row.setStyle(
                            "-fx-background-color: #FAFBFD;" +
                                    "-fx-border-color: transparent transparent "
                                    + BORDER + " transparent;"
                    )
            );

            row.setOnMouseExited(e ->
                    row.setStyle(
                            "-fx-border-color: transparent transparent "
                                    + BORDER + " transparent;"
                    )
            );

            updateButtonStyles();
        }


        // STATUS BUTTON


        private Button createStatusButton(
                String text
        ) {

            Button button = new Button(
                    text
            );

            button.setPrefWidth(104);
            button.setPrefHeight(30);

            button.setMinWidth(104);
            button.setMinHeight(30);

            button.setMaxWidth(104);
            button.setMaxHeight(30);

            button.setFont(
                    Font.font(
                            "System",
                            FontWeight.NORMAL,
                            11
                    )
            );

            button.setStyle(
                    button.getStyle() + "-fx-cursor: hand;"
            );

            return button;
        }

        // SET STATE


        private void setState(
                int newState
        ) {

            state = newState;

            updateButtonStyles();

            updateCounters();
        }


        // GET STATE


        private int getState() {

            return state;
        }


        // GET ROW


        private HBox getRow() {

            return row;
        }


        // BUTTON STYLES


        private void updateButtonStyles() {

            String activeSuffix =
                    "-fx-background-radius: 15;" +
                            "-fx-border-radius: 15;" +
                            "-fx-font-weight: bold;" +
                            "-fx-cursor: hand;";

            String inactiveSuffix =
                    "-fx-background-color: white;" +
                            "-fx-text-fill: #777777;" +
                            "-fx-border-color: #D8D8D8;" +
                            "-fx-border-width: 1;" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-radius: 15;" +
                            "-fx-cursor: hand;";

            // PAIKALLA


            presentButton.setStyle(
                    state == PRESENT
                            ? "-fx-background-color: " + GREEN + ";"
                            + "-fx-text-fill: white;" + activeSuffix
                            : inactiveSuffix
            );

            // MYÖHÄSSÄ


            lateButton.setStyle(
                    state == LATE
                            ? "-fx-background-color: " + ORANGE + ";"
                            + "-fx-text-fill: white;" + activeSuffix
                            : inactiveSuffix
            );


            // POISSA


            absentButton.setStyle(
                    state == ABSENT
                            ? "-fx-background-color: " + RED + ";"
                            + "-fx-text-fill: white;" + activeSuffix
                            : inactiveSuffix
            );
        }
    }
}