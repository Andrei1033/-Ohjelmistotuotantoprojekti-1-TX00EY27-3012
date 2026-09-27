package com.example.app.Controller;

import com.example.app.DaoElements.AdminDao;
import com.example.app.Model.AdminTest;
import com.example.app.Model.LoginComponents.Role;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AdminControllerTest {
    @Test
    void loadsUsersAndCalculatesNextIdFromDao() {
        AdminTest student = student();
        AdminTest teacher = teacher();
        AdminController controller = new AdminController(
                new StubAdminDao(List.of(student, teacher)));

        assertSame(student, controller.getUsers().get(0));
        assertSame(teacher, controller.getUsers().get(1));
        assertNull(controller.getDatabaseError());
    }

    @Test
    void storesDistinctCoursesAndExcludesPlaceholder() {
        AdminController controller = new AdminController(new StubAdminDao(List.of(
                student(),
                new AdminTest(12, "No Course", "none@example.com", Role.ADMIN, "—"),
                new AdminTest(15, "Another Student", "another@example.com",
                        Role.STUDENT, "Physics, Chemistry"))));

        assertEquals(Set.of("Mathematics", "Physics", "Chemistry"), controller.getCourses());
    }

    @Test
    void filtersByRoleCaseInsensitiveSearchAndCourse() {
        AdminTest student = student();
        AdminTest teacher = teacher();
        AdminController controller = new AdminController(new StubAdminDao(List.of(student, teacher)));

        assertSingleMatch(student,
                controller.filterUsers(Role.STUDENT, "  ADA  ", "Kaikki"));
        assertSingleMatch(student,
                controller.filterUsers(null, "EXAMPLE.COM", "Physics"));
        assertSingleMatch(teacher,
                controller.filterUsers(Role.TEACHER, null, "Programming"));
    }

    @Test
    void treatsNullAndAllCourseAsNoFilter() {
        AdminController controller = controllerWithUsers();

        assertEquals(controller.getUsers(), controller.filterUsers(null, null, null));
        assertEquals(controller.getUsers(), controller.filterUsers(null, "", "Kaikki"));
        assertTrue(controller.filterUsers(null, null, "Missing").isEmpty());
    }

    @Test
    void addUserDelegatesToDaoAndAddsReturnedUser() {
        StubAdminDao dao = new StubAdminDao(List.of(student(), teacher()));
        AdminTest added = new AdminTest(20, "Katherine Johnson", "katherine@example.com",
                Role.STUDENT, "Physics");
        dao.insertedUser = added;
        AdminController controller = new AdminController(dao);

        assertSame(added, controller.addUser(
                "Katherine Johnson", "katherine@example.com", Role.STUDENT, "Physics"));
        assertEquals(List.of("Katherine Johnson|katherine@example.com|STUDENT|Physics"),
                dao.insertCalls);
        assertTrue(controller.getUsers().contains(added));
    }

    @Test
    void updateUserDelegatesToDaoAndUpdatesTheObservableUser() {
        AdminTest user = student();
        StubAdminDao dao = new StubAdminDao(List.of(user));
        AdminController controller = new AdminController(dao);

        controller.updateUser(user, "Ada Byron", "byron@example.com",
                Role.TEACHER, "Programming");

        assertEquals("Ada Byron", user.getName());
        assertEquals("byron@example.com", user.getEmail());
        assertEquals(Role.TEACHER, user.getRole());
        assertEquals("Programming", user.getCourses());
        assertEquals(List.of("4|Ada Byron|byron@example.com|TEACHER|Programming"),
                dao.updateCalls);
    }

    @Test
    void deleteUserDelegatesToDaoAndRemovesTheUser() {
        AdminTest user = student();
        StubAdminDao dao = new StubAdminDao(List.of(user, teacher()));
        AdminController controller = new AdminController(dao);

        controller.deleteUser(user);

        assertFalse(controller.getUsers().contains(user));
        assertEquals(List.of("4"), dao.deleteCalls);
    }

    @Test
    void rejectsNullAndUnknownUsersBeforeCallingDao() {
        StubAdminDao dao = new StubAdminDao(List.of(student()));
        AdminController controller = new AdminController(dao);
        AdminTest unknown = new AdminTest(99, "Unknown User", "unknown@example.com", Role.ADMIN, "—");

        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> controller.updateUser(null, "Name", "name@example.com",
                                Role.STUDENT, "—")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> controller.updateUser(unknown, "Name", "name@example.com",
                                Role.STUDENT, "—")));
        assertTrue(dao.updateCalls.isEmpty());
        assertTrue(dao.deleteCalls.isEmpty());
    }

    @Test
    void exposesDatabaseErrorWhenInitialLoadFails() {
        AdminController controller = new AdminController(new StubAdminDao(new IllegalStateException("database down")));

        assertEquals("database down", controller.getDatabaseError());
        assertTrue(controller.getUsers().isEmpty());
    }

    private static AdminController controllerWithUsers() {
        return new AdminController(new StubAdminDao(List.of(student(), teacher())));
    }

    private static void assertSingleMatch(AdminTest expected, List<AdminTest> actual) {
        assertEquals(1, actual.size());
        assertSame(expected, actual.get(0));
    }

    private static AdminTest student() {
        return new AdminTest(4, "Ada Lovelace", "ada@example.com",
                Role.STUDENT, "Mathematics, Physics");
    }

    private static AdminTest teacher() {
        return new AdminTest(9, "Grace Hopper", "grace@example.com",
                Role.TEACHER, "Programming");
    }

    private static final class StubAdminDao extends AdminDao {
        private final List<AdminTest> users;
        private final RuntimeException loadException;
        private AdminTest insertedUser;
        private final List<String> insertCalls = new java.util.ArrayList<>();
        private final List<String> updateCalls = new java.util.ArrayList<>();
        private final List<String> deleteCalls = new java.util.ArrayList<>();

        private StubAdminDao(List<AdminTest> users) {
            this.users = users;
            this.loadException = null;
        }

        private StubAdminDao(RuntimeException loadException) {
            this.users = List.of();
            this.loadException = loadException;
        }

        @Override
        public List<AdminTest> findAll() {
            if (loadException != null) {
                throw loadException;
            }
            return users;
        }

        @Override
        public AdminTest insert(String name, String email, Role role, String courses) {
            insertCalls.add(name + "|" + email + "|" + role + "|" + courses);
            return insertedUser;
        }

        @Override
        public void update(AdminTest user, String name, String email, Role role, String courses) {
            updateCalls.add(user.getId() + "|" + name + "|" + email + "|" + role + "|" + courses);
        }

        @Override
        public void delete(AdminTest user) {
            deleteCalls.add(String.valueOf(user.getId()));
        }
    }
}
