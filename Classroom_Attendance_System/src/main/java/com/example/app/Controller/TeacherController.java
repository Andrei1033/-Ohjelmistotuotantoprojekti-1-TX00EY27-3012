package com.example.app.Controller;

import com.example.app.DaoElements.CourseDao;
import com.example.app.DaoElements.LessonDao;
import com.example.app.Model.TeacherCourse;
import com.example.app.Model.Lesson;
import com.example.app.Model.LoginComponents.User;
import com.example.app.Model.Teacher;
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
        TeacherStartPage startPage = new TeacherStartPage(currentTeacher, this ,onLogout);
        root.setCenter(startPage);
    }



    public boolean createCourses(String courseName  ,int teacherId) {
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

    public List<Lesson> getLessonsForCourse(int courseId){
        if (courseId <= 0) {
            return Collections.emptyList();
        }
        return lessonDao.getLessonsByCourseById(courseId);
    }
}
