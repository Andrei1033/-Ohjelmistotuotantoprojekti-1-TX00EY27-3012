package com.example.app.View;

import com.example.app.View.Admin;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.startup(latch::countDown);

        latch.await();
    }

    @Test
    void getViewShouldReturnBorderPane() {
        Admin admin = new Admin();

        BorderPane view = admin.getView();

        assertNotNull(view);
    }

    @Test
    void viewShouldHaveSidebar() {
        Admin admin = new Admin();

        BorderPane view = admin.getView();

        assertNotNull(view.getLeft());
        assertTrue(view.getLeft() instanceof VBox);
    }

    @Test
    void viewShouldHaveContent() {
        Admin admin = new Admin();

        BorderPane view = admin.getView();

        assertNotNull(view.getCenter());
        assertTrue(view.getCenter() instanceof VBox);
    }

    @Test
    void viewShouldHaveCorrectStyleClass() {
        Admin admin = new Admin();

        BorderPane view = admin.getView();

        assertTrue(view.getStyleClass().contains("root"));
    }

    @Test
    void sidebarShouldHaveCorrectWidth() {
        Admin admin = new Admin();

        BorderPane view = admin.getView();

        VBox sidebar = (VBox) view.getLeft();

        assertEquals(240, sidebar.getPrefWidth());
    }

    @Test
    void tableShouldHaveFiveColumns() {
        Admin admin = new Admin();

        BorderPane view = admin.getView();

        VBox content = (VBox) view.getCenter();

        TableView<?> table = null;

        for (var node : content.getChildren()) {
            if (node instanceof TableView<?>) {
                table = (TableView<?>) node;
            }
        }

        assertNotNull(table);
        assertEquals(5, table.getColumns().size());
    }

    @Test
    void tableShouldShowCorrectPlaceholder() {
        Admin admin = new Admin();

        BorderPane view = admin.getView();

        VBox content = (VBox) view.getCenter();

        TableView<?> table = null;

        for (var node : content.getChildren()) {
            if (node instanceof TableView<?>) {
                table = (TableView<?>) node;
            }
        }

        assertNotNull(table);
        assertNotNull(table.getPlaceholder());
        assertTrue(table.getPlaceholder() instanceof Label);

        Label placeholder = (Label) table.getPlaceholder();

        assertEquals("Ei käyttäjiä", placeholder.getText());
    }
}
