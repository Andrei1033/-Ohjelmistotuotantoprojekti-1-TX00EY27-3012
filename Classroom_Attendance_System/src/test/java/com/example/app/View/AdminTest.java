package com.example.app.View;


import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

class AdminTest extends ApplicationTest {

    private Admin admin;
    private BorderPane view;

    @Override
    public void start(javafx.stage.Stage stage) {
        admin = new Admin();
        view = admin.getView();

        stage.setScene(new javafx.scene.Scene(view, 800, 600));
        stage.show();
    }

    @Test
    void getViewShouldReturnBorderPane() {
        assertNotNull(view);
    }

    @Test
    void viewShouldHaveSidebarAndContent() {
        assertNotNull(view.getLeft());
        assertNotNull(view.getCenter());

        assertTrue(view.getLeft() instanceof VBox);
        assertTrue(view.getCenter() instanceof VBox);
    }

    @Test
    void tableShouldContainUsersAndColumns() {
        VBox content = (VBox) view.getCenter();

        TableView<?> table = null;

        for (Node node : content.getChildren()) {
            if (node instanceof TableView) {
                table = (TableView<?>) node;
                break;
            }
        }

        assertNotNull(table);

        assertEquals(5, table.getColumns().size());
        assertEquals(6, table.getItems().size());
    }

    @Test
    void sidebarShouldContainCoursesButton() {
        VBox sidebar = (VBox) view.getLeft();

        boolean found = false;

        for (Node node : sidebar.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;

                if (button.getText().contains("Kurssini")) {
                    found = true;
                    break;
                }
            }
        }

        assertTrue(found);
    }
}
