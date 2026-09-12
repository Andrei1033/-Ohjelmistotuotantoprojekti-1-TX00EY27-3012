package com.example.app;

import com.example.app.View.Admin;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        Admin admin = new Admin();

        Scene scene = new Scene(admin.getView(), 800, 600);

        stage.setTitle("Classroom Attendance System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}