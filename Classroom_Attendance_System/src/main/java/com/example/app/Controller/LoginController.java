package com.example.app.Controller;

import com.example.app.DaoElements.UserDao;
import com.example.app.Model.Role;
import com.example.app.Model.User;
import com.example.app.View.Admin;
import com.example.app.View.StudentStartPage;
import com.example.app.View.TeacherStartPage;
import javafx.scene.Parent;

import java.util.Optional;

public class LoginController {
    private final UserDao userDao;

    public LoginController() {
        this.userDao = new UserDao();
    }

    public Optional<User> login(String email, String password) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        if (password == null || password.isEmpty()) {
            return Optional.empty();
        }
        return userDao.findByEmailAndPassword(email.trim(), password);
    }

    public Parent getStartPageFor(User user, Runnable onLogout) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        Role role = user.getRole();
        return switch (role) {
            case STUDENT -> new StudentStartPage(onLogout);
            case TEACHER -> new TeacherStartPage(onLogout);
            case ADMIN -> new Admin().getView();
        };
    }
}
