package com.example.app.Model;

public enum Role {
    STUDENT,
    TEACHER,
    ADMIN;

    public static Role fromString(String value) {
        if (value == null) return null;
        return switch (value.toLowerCase()) {
            case "student" -> STUDENT;
            case "teacher" -> TEACHER;
            case "admin" -> ADMIN;
            default -> null;
        };
    }
}
