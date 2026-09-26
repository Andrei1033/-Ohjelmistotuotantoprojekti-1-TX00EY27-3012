package com.example.app.Controller;

import com.example.app.DaoElements.StudentDao.AttendanceDao;
import com.example.app.DaoElements.StudentDao.CourseDao;
import com.example.app.Model.LoginComponents.User;

import com.example.app.Model.StudentComponents.AttendanceRecord;
import com.example.app.Model.StudentComponents.Course;
import com.example.app.View.StudentAttendanceTracking;
import com.example.app.View.StudentStartPage;

import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.util.List;

/**
 * Manages navigation between student views and fetches the necessary data
 * from DAO classes. Keeps the View classes "dumb": they merely render the
 * provided data and invoke callbacks in response to user actions.
 */
public class StudentController {

    private final User currentUser;
    private final Runnable onLogout;
    private final CourseDao courseDao = new CourseDao();
    private final AttendanceDao attendanceDao = new AttendanceDao();
    private final BorderPane root = new BorderPane();

    // Muistaa mikä kurssi on tällä hetkellä auki (null = kurssilistanäkymä),
    // jotta "Omat tiedot" -ikkunan sulkemisen jälkeen osataan piirtää
    // oikea sivu uudelleen päivitetyillä käyttäjätiedoilla.
    private Course currentCourse;

    public StudentController(User currentUser, Runnable onLogout) {
        this.currentUser = currentUser;
        this.onLogout = onLogout;
    }

    /**
     * Palauttaa opiskelijan näkymän (aloittaa kurssilistasta).
     * Käytä tätä LoginControllerista StudentStartPagen suoran konstruoinnin sijaan.
     */
    public Parent getView() {
        showStartPage();
        return root;
    }

    /** Näyttää opiskelijan kurssilistan (StudentStartPage). */
    private void showStartPage() {
        currentCourse = null;

        List<Course> courses = courseDao.getCoursesForStudent(currentUser.getId());

        StudentStartPage page = new StudentStartPage(
                currentUser,
                courses,
                this::showAttendancePage,
                onLogout,
                this::refreshCurrentPage
        );

        root.setCenter(page);
    }

    /** Näyttää valitun kurssin läsnäolohistorian (StudentAttendanceTracking). */
    private void showAttendancePage(Course course) {
        currentCourse = course;

        List<AttendanceRecord> records =
                attendanceDao.getAttendanceForStudentAndCourse(currentUser.getId(), course.getId());

        StudentAttendanceTracking page = new StudentAttendanceTracking(
                course,
                records,
                this::showStartPage,
                currentUser,
                onLogout,
                this::refreshCurrentPage
        );

        root.setCenter(page);
    }

    /**
     * Piirtää nykyisen sivun uudelleen. Kutsutaan kun "Omat tiedot" -ikkunassa
     * on tallennettu muutoksia, jotta esim. sivupalkin nimi päivittyy heti.
     */
    private void refreshCurrentPage() {
        if (currentCourse == null) {
            showStartPage();
        } else {
            showAttendancePage(currentCourse);
        }
    }
}
 
