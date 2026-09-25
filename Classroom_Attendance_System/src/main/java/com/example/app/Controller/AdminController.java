package com.example.app.Controller;

import com.example.app.DaoElements.AdminDao;
import com.example.app.Model.Admin;
import com.example.app.Model.LoginComponents.Role;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class AdminController {
    private final ObservableList<Admin> users = FXCollections.observableArrayList();
    private final AdminDao adminDao;
    private String databaseError;
    private int nextId;

    public AdminController() {
        this(new AdminDao());
    }

    AdminController(AdminDao adminDao) {
        this.adminDao = adminDao;
        try {
            users.addAll(adminDao.findAll());
        } catch (RuntimeException exception) {
            databaseError = exception.getMessage();
        }
        nextId = users.stream().mapToInt(Admin::getId).max().orElse(0) + 1;
    }

    public ObservableList<Admin> getUsers() {
        return users;
    }

    public ObservableList<Admin> filterUsers(Role role, String search, String course) {
        String query = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        String selectedCourse = course == null || course.equals("Kaikki") ? "" : course.trim();

        return users.filtered(user ->
                (role == null || user.getRole() == role)
                        && (query.isEmpty()
                        || user.getName().toLowerCase(Locale.ROOT).contains(query)
                        || user.getEmail().toLowerCase(Locale.ROOT).contains(query))
                        && (selectedCourse.isEmpty()
                        || user.getCourses().contains(selectedCourse)));
    }

    public Set<String> getCourses() {
        Set<String> courses = new LinkedHashSet<>();
        users.forEach(user -> {
            if (!user.getCourses().equals("—")) {
                for (String course : user.getCourses().split(",\\s*")) {
                    courses.add(course);
                }
            }
        });
        return courses;
    }

    public String getDatabaseError() {
        return databaseError;
    }

    public Admin addUser(String name, String email, Role role, String courses) {
        Admin user = adminDao.insert(name, email, role, courses);
        users.add(user);
        nextId = Math.max(nextId, user.getId() + 1);
        return user;
    }

    public void updateUser(Admin user, String name, String email, Role role, String courses) {
        if (user == null || !users.contains(user)) {
            throw new IllegalArgumentException("Unknown user");
        }
        adminDao.update(user, name, email, role, courses);
        user.update(name, email, role, courses);
    }

    public void deleteUser(Admin user) {
        if (user == null || !users.contains(user)) {
            throw new IllegalArgumentException("Unknown user");
        }
        adminDao.delete(user);
        users.remove(user);
    }
}
