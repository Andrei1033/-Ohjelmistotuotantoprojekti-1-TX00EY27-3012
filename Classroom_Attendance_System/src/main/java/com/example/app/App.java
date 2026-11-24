package com.example.app;

import com.example.app.Controller.LoginController;
import com.example.app.Localisation.i18n;
import com.example.app.Model.LoginComponents.User;
import com.example.app.View.Login;
import java.util.Optional;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private Stage primaryStage;
    private final LoginController loginController = new LoginController();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        System.out.println("[BOOT] JavaFX start() called. Stage: " + stage);

        this.primaryStage = stage;
        stage.setTitle("Classroom Attendance System");

        showLogin();

        stage.setWidth(1200);
        stage.setHeight(800);
        stage.setMinWidth(1000);
        stage.setMinHeight(700);

        stage.show();
        stage.setMaximized(true);
    }

    private void showLogin() {
        Login loginView = new Login((email, password) -> {
            Optional<User> userOptional = loginController.login(email, password);
            if (userOptional.isEmpty()) {
                return;
            }

            User user = userOptional.get();
            loginController.savePreferredLanguage(user, i18n.getCurrentLang().code());
            Parent page = loginController.getStartPageFor(user, this::showLogin);

            if (primaryStage.getScene() != null) {
                primaryStage.getScene().setRoot(page);
            }
            else {
                primaryStage.setScene(new Scene(page));
            }
            primaryStage.setMaximized(true);
        });

        if (primaryStage.getScene() == null) {
            primaryStage.setScene(new Scene(loginView));
        }
        else {
            primaryStage.getScene().setRoot(loginView);
        }
    }
}
