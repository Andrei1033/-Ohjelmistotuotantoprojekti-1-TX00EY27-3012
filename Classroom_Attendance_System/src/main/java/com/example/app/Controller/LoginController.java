package com.example.app.Controller;

import com.example.app.DaoElements.LoginDao.UserDao;
import com.example.app.Model.LoginComponents.Role;
import com.example.app.Model.LoginComponents.User;
import com.example.app.View.Admin;
import javafx.scene.Parent;

import java.util.Optional;

public class LoginController {
    private final UserDao userDao;

    public LoginController() {
        this(new UserDao());
    }

    public LoginController(UserDao userDao) {
        this.userDao = userDao;
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
            case STUDENT -> new StudentController(user, onLogout).getView();
            case TEACHER -> new TeacherController(user, onLogout).getView();
            case ADMIN -> new Admin(user, onLogout).getView();
        };
    }
}