package com.example.app.Model;

import com.example.app.Model.LoginComponents.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AdminTest {

    @Test
    void createsAdminAndUpdatesItsDetails() {
        Admin admin = new Admin(1, " Ada Lovelace ", " ada@example.com ",
                Role.ADMIN, "Mathematics");

        assertEquals(1, admin.getId());
        assertEquals("Ada Lovelace", admin.getName());
        assertEquals("ada@example.com", admin.getEmail());
        assertEquals(Role.ADMIN, admin.getRole());
        assertEquals("Mathematics", admin.getCourses());

        admin.update("Grace Hopper", "grace@example.com", Role.TEACHER, "Programming");

        assertEquals("Grace Hopper", admin.getName());
        assertEquals("grace@example.com", admin.getEmail());
        assertEquals(Role.TEACHER, admin.getRole());
        assertEquals("Programming", admin.getCourses());
    }

    @Test
    void rejectsEmptyRequiredFields() {
        assertThrows(IllegalArgumentException.class,
                () -> new Admin(1, "", "user@example.com", Role.STUDENT, "—"));
        assertThrows(IllegalArgumentException.class,
                () -> new Admin(1, "User", "", Role.STUDENT, "—"));
        assertThrows(IllegalArgumentException.class,
                () -> new Admin(1, "User", "user@example.com", Role.STUDENT, ""));
    }
}
