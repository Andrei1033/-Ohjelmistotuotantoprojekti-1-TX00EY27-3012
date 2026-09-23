package com.example.app.Model;

public class Admin {
    private final int id;
    private String name;
    private String email;
    private Role role;
    private String courses;

    public Admin(int id, String name, String email, Role role, String courses) {
        this.id = id;
        this.name = requireText(name, "name");
        this.email = requireText(email, "email");
        this.role = role == null ? Role.STUDENT : role;
        this.courses = requireText(courses, "courses");
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public String getCourses() { return courses; }

    public void update(String name, String email, Role role, String courses) {
        this.name = requireText(name, "name");
        this.email = requireText(email, "email");
        this.role = role == null ? Role.STUDENT : role;
        this.courses = requireText(courses, "courses");
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be empty");
        }
        return value.trim();
    }
}
