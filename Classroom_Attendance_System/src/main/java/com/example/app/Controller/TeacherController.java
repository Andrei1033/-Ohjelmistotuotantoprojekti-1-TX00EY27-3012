package com.example.app.Controller;

import com.example.app.DaoElements.LessonDao;
import com.example.app.DaoElements.StudentDao.CourseDao;
import com.example.app.DaoElements.StudentDao.AttendanceDao;

import com.example.app.Model.TeacherCourse;
import com.example.app.Model.Lesson;
import com.example.app.Model.LoginComponents.User;
import com.example.app.Model.Teacher;
import com.example.app.Model.StudentComponents.AttendanceRecord;
import com.example.app.View.TeacherAttendanceTracking;
import com.example.app.View.TeacherCoursePage;
import com.example.app.View.TeacherStartPage;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;

import java.util.Collections;
import java.util.List;

public class TeacherController {

    private final Teacher currentTeacher;
    private final Runnable onLogout;
    private final CourseDao courseDao = new CourseDao();
    private final AttendanceDao attendanceDao = new AttendanceDao();
    private final LessonDao lessonDao = new LessonDao();
    private final BorderPane root = new BorderPane();

    public TeacherController(User user, Runnable onLogout) {

        if (user instanceof Teacher teacher) {
            this.currentTeacher = teacher;
        } else {
            this.currentTeacher = new Teacher(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail()
            );
        }
        this.onLogout = onLogout;
    }

    public Parent getView() {
        showStartPage();
        return root;
    }

    // =============================================================
    // NAVIGATION
    // =============================================================

    public void showStartPage() {
        TeacherStartPage startPage =
                new TeacherStartPage(currentTeacher, this, onLogout);
        root.setCenter(startPage);
    }

    /**
     * Avaa kurssisivun. Välittää currentTeacher-olion sekä User-parametrina
     * (profiilin muokkausta varten) että Teacher-parametrina.
     *
     * onProfileUpdated päivittää koko kurssisivun, jotta sivupalkin nimi
     * päivittyy heti profiilimuutoksen jälkeen.
     */
    public void openCoursePage(int courseId) {
        TeacherCoursePage coursePage = new TeacherCoursePage(
                currentTeacher,
                courseId,
                this,
                this::showStartPage,           // onBack
                () -> startLesson(courseId),   // onAddLesson (varalla)
                currentTeacher,                // currentUser (Teacher on User)
                onLogout,                      // onLogout
                () -> openCoursePage(courseId) // onProfileUpdated → päivitä sivu
        );
        root.setCenter(coursePage);
    }

    // =============================================================
    // COURSES
    // =============================================================

    public boolean createCourses(String courseName, String courseCode, int teacherId) {
        if (courseName == null || courseName.trim().isEmpty() || teacherId <= 0) {
            return false;
        }
        return courseDao.addCourse(courseName.trim(), courseCode, teacherId);
    }

    public boolean updateCourse(int courseId, String newName, String newCode) {
        if (courseId <= 0 || newName == null || newName.trim().isEmpty()) {
            return false;
        }
        return courseDao.updateCourse(courseId, newName.trim(), newCode);
    }

    public List<TeacherCourse> getTeacherCourses(int teacherId) {
        if (teacherId <= 0) {
            return Collections.emptyList();
        }
        return courseDao.getCoursesByTeacherId(teacherId);
    }

    public List<Lesson> getLessonsForCourse(int courseId) {
        if (courseId <= 0) {
            return Collections.emptyList();
        }
        return lessonDao.getLessonsByCourseById(courseId);
    }

    // =============================================================
    // LESSONS
    // =============================================================

    public void startLesson(int courseId) {

        int lessonId = lessonDao.startLesson(courseId);

        if (lessonId == -1) {
            showError("Oppitunnin aloittaminen epäonnistui.");
            return;
        }

        TeacherCourse course = findCourse(courseId);

        if (course == null) {
            showError("Kurssia ei löytynyt.");
            return;
        }

        TeacherAttendanceTracking tracking =
                new TeacherAttendanceTracking(
                        currentTeacher,
                        course,
                        lessonId,
                        () -> {
                            // Päivitetään kurssisivu tarvittaessa.
                        }
                );

        tracking.show();
    }

    public void openExistingLesson(int courseId, int lessonId) {

        TeacherCourse course = findCourse(courseId);

        if (course == null) {
            showError("Kurssia ei löytynyt.");
            return;
        }

        TeacherAttendanceTracking tracking =
                new TeacherAttendanceTracking(
                        currentTeacher,
                        course,
                        lessonId,
                        this::showStartPage
                );

        tracking.show();
    }

    // =============================================================
    // STUDENTS
    // =============================================================

    public List<User> getAllStudents(int courseId) {
        return courseDao.getAllStudents();
    }

    public boolean addStudents(List<Integer> studentIds, int courseId) {
        if (studentIds == null || studentIds.isEmpty()) {
            return false;
        }
        for (int studentId : studentIds) {
            courseDao.addStudents(studentId, courseId);
        }
        return true;
    }

    public List<User> getStudentsForCourse(int courseId) {
        if (courseId <= 0) {
            return Collections.emptyList();
        }
        return courseDao.getStudentsForCourse(courseId);
    }

    public List<AttendanceRecord> getAttendanceForStudentAndCourse(
            int studentId,
            int courseId
    ) {
        if (studentId <= 0 || courseId <= 0) {
            return Collections.emptyList();
        }
        return attendanceDao.getAttendanceForStudentAndCourse(studentId, courseId);
    }

    // =============================================================
    // HELPERS
    // =============================================================

    private TeacherCourse findCourse(int courseId) {
        List<TeacherCourse> courses =
                getTeacherCourses(currentTeacher.getId());

        for (TeacherCourse c : courses) {
            if (c.getCourseid() == courseId) {
                return c;
            }
        }
        return null;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Virhe");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}