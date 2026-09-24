package com.example.app.Controller;

import com.example.app.Model.Admin;
import com.example.app.Model.LoginComponents.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AdminControllerTest {
    private AdminController controller;

    @BeforeEach
    void setUp() {
        controller = new AdminController();
        assertNull(controller.getDatabaseError(),
                "AdminController could not load users from the database");
    }

    @Test
    void loadsUsersFromDatabase() {
        assertFalse(controller.getUsers().isEmpty());
        assertTrue(controller.getUsers().stream()
                .allMatch(user -> user.getName() != null && !user.getName().isBlank()));
    }

    @Test
    void collectsCoursesWithoutThePlaceholder() {
        Set<String> courses = controller.getCourses();

        assertFalse(courses.isEmpty());
        assertFalse(courses.contains("—"));
        assertTrue(controller.getUsers().stream()
                .filter(user -> !user.getCourses().equals("—"))
                .flatMap(user -> java.util.Arrays.stream(user.getCourses().split(",\\s*")))
                .allMatch(courses::contains));
    }

    @Test
    void filtersByRoleSearchAndCourse() {
        Admin user = controller.getUsers().get(0);
        String course = user.getCourses().equals("—")
                ? "Kaikki"
                : user.getCourses().split(",\\s*")[0];

        assertTrue(controller.filterUsers(user.getRole(), user.getName(), "Kaikki")
                .contains(user));
        assertTrue(controller.filterUsers(null, null, course).contains(user));
        assertTrue(controller.filterUsers(null, null, "Kaikki").containsAll(controller.getUsers()));
    }

    @Test
    void addsUserWithNextId() {
        int expectedId = controller.getUsers().stream()
                .mapToInt(Admin::getId)
                .max()
                .orElse(0) + 1;

        Admin added = controller.addUser(
                "Test User",
                "test.user@example.com",
                Role.STUDENT,
                "Test course"
        );

        assertEquals(expectedId, added.getId());
        assertTrue(controller.getUsers().contains(added));
    }

    @Test
    void updatesExistingUser() {
        Admin user = controller.getUsers().get(0);

        controller.updateUser(
                user,
                "Updated User",
                "updated.user@example.com",
                Role.TEACHER,
                "Updated course"
        );

        assertEquals("Updated User", user.getName());
        assertEquals("updated.user@example.com", user.getEmail());
        assertEquals(Role.TEACHER, user.getRole());
        assertEquals("Updated course", user.getCourses());
    }

    @Test
    void rejectsUnknownUserUpdate() {
        Admin unknownUser = new Admin(
                999999,
                "Unknown User",
                "unknown@example.com",
                Role.STUDENT,
                "—"
        );

        assertThrows(IllegalArgumentException.class, () ->
                controller.updateUser(
                        unknownUser,
                        "Updated User",
                        "updated@example.com",
                        Role.ADMIN,
                        "—"
                ));
    }

}