package com.example.app.Controller;

import com.example.app.Model.LoginComponents.User;
import com.example.app.View.TeacherStartPage;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class TeacherController {

    private final User currentUser;
    private final Runnable onLogout;
    private final BorderPane root = new BorderPane();

    public TeacherController(User currentUser, Runnable onLogout) {
        this.currentUser = currentUser;
        this.onLogout = onLogout;
    }

    public Parent getView() {
        // TODO: replace with real TeacherStartPage signature once the view takes data.
        TeacherStartPage page = new TeacherStartPage(onLogout);
        root.setCenter(page);
        return root;
    }
}