package com.example.app.View;





// SIDEBAR COLOR
// #202F49 - navy


import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class TeacherAttendanceTracking extends BorderPane {

    // Colors
    private  static final String NAVY = "#202F49";

    // Sidebar creation
    private static final int PRESENT = 1;
    private static final int ABSENT = 2;
    private static final int LATE = 3;



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