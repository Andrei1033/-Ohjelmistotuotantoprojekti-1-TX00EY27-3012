package com.example.app.Controller;

import com.example.app.DaoElements.LessonDao;
import com.example.app.DaoElements.StudentDao.CourseDao;

import com.example.app.Model.TeacherCourse;
import com.example.app.Model.Lesson;
import com.example.app.Model.LoginComponents.User;
import com.example.app.Model.Teacher;
import com.example.app.View.TeacherAttendanceTracking;
import com.example.app.View.TeacherCoursePage;
import com.example.app.View.TeacherStartPage;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;


import java.util.Collections;
import java.util.List;



public class TeacherController {

    private final Teacher currentTeacher;
    private final Runnable onLogout;
    private final CourseDao courseDao = new CourseDao();
    private final LessonDao lessonDao = new LessonDao();
    private final BorderPane root = new BorderPane();

    public TeacherController(User user, Runnable onLogout) {

        if (user instanceof Teacher teacher) {
            this.currentTeacher = teacher;
        } else {
            this.currentTeacher = new Teacher(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
        }
        this.onLogout = onLogout;
    }


    public Parent getView() {
        showStartPage();
        return root;
    }


    public void showStartPage() {
        TeacherStartPage startPage = new TeacherStartPage(currentTeacher, this, onLogout);
        root.setCenter(startPage);
    }


    public boolean createCourses(String courseName, int teacherId) {
        if (courseName == null) {
            return false;
        }
        return courseDao.addCourse(courseName.trim(), teacherId);
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

    public void startLesson(int courseId) {

        int lessonId = lessonDao.startLesson(courseId);

        if (lessonId == -1) {

            javafx.scene.control.Alert alert =
                    new javafx.scene.control.Alert(
                            javafx.scene.control.Alert.AlertType.ERROR
                    );

            alert.setTitle("Virhe");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Oppitunnin aloittaminen epäonnistui."
            );

            alert.showAndWait();

            return;
        }

        TeacherCourse course = null;

        for (TeacherCourse c : getTeacherCourses(currentTeacher.getId())) {

            if (c.getCourseid() == courseId) {
                course = c;
                break;
            }
        }

        if (course == null) {

            javafx.scene.control.Alert alert =
                    new javafx.scene.control.Alert(
                            javafx.scene.control.Alert.AlertType.ERROR
                    );

            alert.setTitle("Virhe");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Kurssia ei löytynyt."
            );

            alert.showAndWait();

            return;
        }

        TeacherCourse finalCourse = course;

        TeacherAttendanceTracking tracking =
                new TeacherAttendanceTracking(
                        currentTeacher,
                        finalCourse,
                        lessonId,
                        () -> {
                            // Päivitetään kurssisivu tarvittaessa.
                        }
                );

        tracking.show();
    }

    public void openExistingLesson(int courseId, int lessonId) {

        TeacherCourse course = null;

        for (TeacherCourse c :
                getTeacherCourses(currentTeacher.getId())) {

            if (c.getCourseid() == courseId) {
                course = c;
                break;
            }
        }

        if (course == null) {

            javafx.scene.control.Alert alert =
                    new javafx.scene.control.Alert(
                            javafx.scene.control.Alert.AlertType.ERROR
                    );

            alert.setTitle("Virhe");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Kurssia ei löytynyt."
            );

            alert.showAndWait();

            return;
        }

        TeacherCourse finalCourse = course;

        TeacherAttendanceTracking tracking =
                new TeacherAttendanceTracking(
                        currentTeacher,
                        finalCourse,
                        lessonId,
                        this::showStartPage
                );

        tracking.show();
    }
    //////////////

        public void openCoursePage ( int courseId){
            TeacherCoursePage coursePage = new TeacherCoursePage(
                    currentTeacher,
                    courseId,
                    this,
                    this::showStartPage,
                    () -> System.out.println("Lisää opiskelijoita")
            );
            root.setCenter(coursePage);

        }

    }
