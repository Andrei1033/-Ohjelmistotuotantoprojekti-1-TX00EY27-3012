package com.example.app.View;





// SIDEBAR COLOR
// #202F49 - navy


import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class TeacherAttendanceTracking extends BorderPane {

    // Colors
    private static final String NAVY = "#202F49";
    private static final String BULE = "#344A70";
    private static final String LOGO_BLUE = "#536FA4";

    private static final String GREEN = "#409566";
    private static final String ORANGE = "#D38A2D";
    private static final String RED = "#D32F2F";

    private static final String BORDER = "#D8D8D8";
    private static final String MUTED = "#858585";
    private static final String LIGHT_BULE = "#F5F5F5";

    // Sidebar creation
    private static final int PRESENT = 1;
    private static final int ABSENT = 2;
    private static final int LATE = 3;

    //private final List<StudentRow> studentRows = new ArrayList<>();

    /*
    private Label persentLabel;
    private Label absentLabel;
    private Label lateLabel;
*/


    // Constructor
    public TeacherAttendanceTracking(Runnable onFinish) {
        setStyle(
                "-fx-background-color: white;"
        );
        // Left sidebar
        VBox sidebar = createSidebar();
        setLeft(sidebar);
        // Main content
        VBox content = createMainContent(onFinish);
        setCenter(content);
    }

    private VBox createMainContent(
            Runnable onFinish
    ) {
        VBox content = new VBox();

        content.setPadding(
                new Insets(
                        40, 40, 40, 40
                )
        );
        content.setStyle(
                "-fx-background-color: white;"
        );


        // OTSIKKO


        Label title = new Label(
                "Oppitunti — 16.9.2026"
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






        return content;
    }





    private VBox createSidebar() {

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(220);
        sidebar.setMaxWidth(220);
        sidebar.setMinWidth(220);

        sidebar.setStyle(
                "-fx-background-color: " + NAVY + ";"
        );
        return sidebar;
    }




}