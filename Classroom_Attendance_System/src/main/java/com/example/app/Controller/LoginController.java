package com.example.app.Controller;

import com.example.app.DaoElements.LoginDao.UserDao;
import com.example.app.Model.LoginComponents.Role;
import com.example.app.Model.LoginComponents.User;
import com.example.app.View.Admin;
import java.util.Locale;
import java.util.Optional;
import javafx.scene.Parent;

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

    public boolean savePreferredLanguage(User user, String languageCode) {
        if (user == null || user.getId() <= 0) {
            return false;
        }
        if (languageCode == null || languageCode.isBlank()) {
            return false;
        }

        return userDao.updatePreferredLanguage(user.getId(), languageCode.trim().toLowerCase(Locale.ROOT));
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
