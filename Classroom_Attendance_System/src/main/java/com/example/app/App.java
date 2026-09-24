package com.example.app;

import com.example.app.Controller.LoginController;
import com.example.app.Model.LoginComponents.User;
import com.example.app.View.Login;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Optional;

public class App extends Application {
    private Stage primaryStage;
    private final LoginController loginController = new LoginController();

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Classroom Attendance System");
        showLogin();
        stage.setMaximized(true);
        stage.show();
    }

    private void showLogin() {
        Login loginView = new Login((email, password) -> {
            Optional<User> userOptional = loginController.login(email, password);
            if (userOptional.isEmpty()) {
                return;
            }

            User user = userOptional.get();
            Parent page = loginController.getStartPageFor(user, this::showLogin);
            primaryStage.setScene(new Scene(page, 800, 600));
        });

        primaryStage.setScene(new Scene(loginView, 800, 600));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
