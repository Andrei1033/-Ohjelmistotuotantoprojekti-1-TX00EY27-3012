package com.example.app.Controller;

import com.example.app.DaoElements.LessonDao;
import com.example.app.DaoElements.StudentDao.AttendanceDao;
import com.example.app.DaoElements.StudentDao.CourseDao;
import com.example.app.Model.Lesson;
import com.example.app.Model.LoginComponents.Role;
import com.example.app.Model.LoginComponents.User;
import com.example.app.Model.Teacher;
import com.example.app.Model.TeacherCourse;
import com.example.app.Model.StudentComponents.AttendanceRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TeacherControllerTest {

    private Teacher teacher;
    private User plainUser;
    private Runnable onLogout;

    private MockedConstruction<CourseDao> courseDaoMock;
    private MockedConstruction<AttendanceDao> attendanceDaoMock;
    private MockedConstruction<LessonDao> lessonDaoMock;

    private CourseDao courseDao;
    private AttendanceDao attendanceDao;
    private LessonDao lessonDao;

    @BeforeEach
    void setUp() {
        teacher = new Teacher(1, "Matti", "Meikäläinen", "matti@testi.fi");
        plainUser = new User(2, "Testi", "User", "testi@testi.fi", Role.TEACHER);
        onLogout = () -> {};

        courseDaoMock = mockConstruction(CourseDao.class);
        attendanceDaoMock = mockConstruction(AttendanceDao.class);
        lessonDaoMock = mockConstruction(LessonDao.class);
    }

    @AfterEach
    void tearDown() {
        courseDaoMock.close();
        attendanceDaoMock.close();
        lessonDaoMock.close();
    }

    private TeacherController createController(User user) {
        TeacherController c = new TeacherController(user, onLogout);
        courseDao = courseDaoMock.constructed().get(0);
        attendanceDao = attendanceDaoMock.constructed().get(0);
        lessonDao = lessonDaoMock.constructed().get(0);
        return c;
    }

    // =============================================================
    // KONSTRUKTORI
    // =============================================================

    @Test
    void constructor_acceptsTeacherDirectly() {
        TeacherController controller = createController(teacher);
        assertNotNull(controller);
    }

    @Test
    void constructor_wrapsPlainUserAsTeacher() {
        TeacherController controller = createController(plainUser);
        assertNotNull(controller);
    }

    // =============================================================
    // createCourses
    // =============================================================

    @Test
    void createCourses_returnsFalseForNullName() {
        TeacherController controller = createController(teacher);
        assertFalse(controller.createCourses(null, "CS101", 1));
    }

    @Test
    void createCourses_returnsFalseForBlankName() {
        TeacherController controller = createController(teacher);
        assertFalse(controller.createCourses("   ", "CS101", 1));
    }

    @Test
    void createCourses_returnsFalseForInvalidTeacherId() {
        TeacherController controller = createController(teacher);
        assertFalse(controller.createCourses("Java", "CS101", 0));
        assertFalse(controller.createCourses("Java", "CS101", -1));
    }

    @Test
    void createCourses_delegatesToDao() {
        TeacherController controller = createController(teacher);
        when(courseDao.addCourse(anyString(), anyString(), anyInt())).thenReturn(true);

        boolean result = controller.createCourses("Java", "CS101", 1);

        assertTrue(result);
        verify(courseDao).addCourse("Java", "CS101", 1);
    }

    @Test
    void createCourses_trimsName() {
        TeacherController controller = createController(teacher);
        when(courseDao.addCourse(anyString(), anyString(), anyInt())).thenReturn(true);

        controller.createCourses("   Java   ", "CS101", 1);

        verify(courseDao).addCourse("Java", "CS101", 1);
    }

    // =============================================================
    // updateCourse
    // =============================================================

    @Test
    void updateCourse_returnsFalseForInvalidArgs() {
        TeacherController controller = createController(teacher);
        assertFalse(controller.updateCourse(0, "Java", "CS101"));
        assertFalse(controller.updateCourse(1, null, "CS101"));
        assertFalse(controller.updateCourse(1, "   ", "CS101"));
    }

    @Test
    void updateCourse_delegatesToDao() {
        TeacherController controller = createController(teacher);
        when(courseDao.updateCourse(anyInt(), anyString(), anyString())).thenReturn(true);

        boolean result = controller.updateCourse(5, "New Name", "NEW1");

        assertTrue(result);
        verify(courseDao).updateCourse(5, "New Name", "NEW1");
    }

    // =============================================================
    // getTeacherCourses
    // =============================================================

    @Test
    void getTeacherCourses_returnsEmptyForInvalidId() {
        TeacherController controller = createController(teacher);
        assertTrue(controller.getTeacherCourses(0).isEmpty());
        assertTrue(controller.getTeacherCourses(-5).isEmpty());
        verify(courseDao, never()).getCoursesByTeacherId(anyInt());
    }

    @Test
    void getTeacherCourses_delegatesToDao() {
        TeacherController controller = createController(teacher);
        List<TeacherCourse> expected = List.of(
                new TeacherCourse(1, "Java", "CS101", 1)
        );
        when(courseDao.getCoursesByTeacherId(1)).thenReturn(expected);

        List<TeacherCourse> result = controller.getTeacherCourses(1);

        assertEquals(expected, result);
    }

    // =============================================================
    // getLessonsForCourse
    // =============================================================

    @Test
    void getLessonsForCourse_returnsEmptyForInvalidId() {
        TeacherController controller = createController(teacher);
        assertTrue(controller.getLessonsForCourse(0).isEmpty());
        assertTrue(controller.getLessonsForCourse(-1).isEmpty());
        verify(lessonDao, never()).getLessonsByCourseById(anyInt());
    }

    @Test
    void getLessonsForCourse_delegatesToDao() {
        TeacherController controller = createController(teacher);
        List<Lesson> expected = Collections.emptyList();
        when(lessonDao.getLessonsByCourseById(5)).thenReturn(expected);

        List<Lesson> result = controller.getLessonsForCourse(5);

        assertEquals(expected, result);
    }

    // =============================================================
    // getAllStudents
    // =============================================================

    @Test
    void getAllStudents_delegatesToDao() {
        TeacherController controller = createController(teacher);
        List<User> expected = Collections.emptyList();
        when(courseDao.getAllStudents()).thenReturn(expected);

        List<User> result = controller.getAllStudents(1);

        assertEquals(expected, result);
        verify(courseDao).getAllStudents();
    }

    // =============================================================
    // addStudents
    // =============================================================

    @Test
    void addStudents_returnsFalseForNullOrEmptyList() {
        TeacherController controller = createController(teacher);
        assertFalse(controller.addStudents(null, 1));
        assertFalse(controller.addStudents(Collections.emptyList(), 1));
    }

    @Test
    void addStudents_callsDaoForEachId() {
        TeacherController controller = createController(teacher);

        boolean result = controller.addStudents(List.of(10, 20, 30), 5);

        assertTrue(result);
        verify(courseDao).addStudents(10, 5);
        verify(courseDao).addStudents(20, 5);
        verify(courseDao).addStudents(30, 5);
    }

    // =============================================================
    // getStudentsForCourse
    // =============================================================

    @Test
    void getStudentsForCourse_returnsEmptyForInvalidId() {
        TeacherController controller = createController(teacher);
        assertTrue(controller.getStudentsForCourse(0).isEmpty());
        assertTrue(controller.getStudentsForCourse(-1).isEmpty());
        verify(courseDao, never()).getStudentsForCourse(anyInt());
    }

    @Test
    void getStudentsForCourse_delegatesToDao() {
        TeacherController controller = createController(teacher);
        List<User> expected = Collections.emptyList();
        when(courseDao.getStudentsForCourse(7)).thenReturn(expected);

        List<User> result = controller.getStudentsForCourse(7);

        assertEquals(expected, result);
    }

    // =============================================================
    // getAttendanceForStudentAndCourse
    // =============================================================

    @Test
    void getAttendance_returnsEmptyForInvalidArgs() {
        TeacherController controller = createController(teacher);
        assertTrue(controller.getAttendanceForStudentAndCourse(0, 1).isEmpty());
        assertTrue(controller.getAttendanceForStudentAndCourse(1, 0).isEmpty());
        assertTrue(controller.getAttendanceForStudentAndCourse(-1, -1).isEmpty());
        verify(attendanceDao, never())
                .getAttendanceForStudentAndCourse(anyInt(), anyInt());
    }

    @Test
    void getAttendance_delegatesToDao() {
        TeacherController controller = createController(teacher);
        List<AttendanceRecord> expected = Collections.emptyList();
        when(attendanceDao.getAttendanceForStudentAndCourse(1, 2)).thenReturn(expected);

        List<AttendanceRecord> result =
                controller.getAttendanceForStudentAndCourse(1, 2);

        assertEquals(expected, result);
    }
}