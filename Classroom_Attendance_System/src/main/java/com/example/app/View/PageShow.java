/* Temporary file for testing purposes. This file will be deleted in the future. */
/* only for ui debug*/
package com.example.app.View;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Scanner;

public class PageShow {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("=================================");
        System.out.println("      Läsänolojärjestelmä");
        System.out.println("=================================");
        System.out.println();
        System.out.println("Valitse avattava sivu:");
        System.out.println();
        System.out.println("1 - Login");
        System.out.println("2 - StudentStartPage");
        System.out.println("3 - StudentAttendanceTracking");
        System.out.println("4 - TeacherStartPage");
        System.out.println("5 - TeacherCoursePage");
        System.out.println("6 - TeacherAttendanceTracking");
        System.out.println("7 - Admin");
        System.out.println("0 - Lopeta");
        System.out.println();

        System.out.print("Valinta: ");

        int page;

        try {
            page = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Virheellinen valinta.");
            scanner.close();
            return;
        }

        if (page == 0) {
            System.out.println("Ohjelma lopetetaan.");
            scanner.close();
            return;
        }

        // Tallennetaan valittu sivu JavaFX:lle
        SelectedPage.page = page;

        scanner.close();

        // Käynnistetään JavaFX
        Application.launch(App.class, args);
    }


    // Säilyttää Mainissa tehdyn sivuvalinnan
    public static class SelectedPage {
        public static int page;
    }


    public static class App extends Application {

        private Stage stage;

        @Override
        public void start(Stage stage) {

            this.stage = stage;

            stage.setTitle("Läsnäolo");
            stage.setResizable(false);

            openSelectedPage();

            stage.show();
        }


        private void openSelectedPage() {

            switch (SelectedPage.page) {

                case 1:
                    openLogin();
                    break;

                case 2:
                    openStudentStartPage();
                    break;

                case 3:
                    openStudentAttendanceTracking();
                    break;

                case 4:
                    openTeacherStartPage();
                    break;

                case 5:
                    openTeacherCoursePage();
                    break;

                case 6:
                    openTeacherAttendanceTracking();
                    break;

                case 7:
                    openAdminPage();
                    break;

                default:
                    System.out.println(
                            "Tätä sivua ei ole vielä tehty: "
                                    + SelectedPage.page
                    );

                    // Debuggausta varten avataan kirjautumissivu
                    openLogin();
                    break;
            }
        }


        private void openLogin() {

            Login view = new Login(
                    this::openLogin
            );

            stage.setScene(
                    new Scene(view, 1024, 399)
            );
        }


        private void openStudentStartPage() {

            StudentStartPage view = new StudentStartPage(
                    this::openLogin
            );

            stage.setScene(
                    new Scene(view, 1024, 399)
            );
        }

        private void openStudentAttendanceTracking() {

            // TODO:
            // Tehdään myöhemmin oikea kurssisivu.

            System.out.println("openStudentAttendanceTracking puututuu vielä.");

            openStudentAttendanceTracking();
        }


        private void openTeacherStartPage() {

            // TODO:
            // Tehdään myöhemmin opiskelijan tietosivu.

            System.out.println("openTeacherStartPage puututuu vielä.");

            openStudentStartPage();
        }

        private void openTeacherCoursePage() {

            // TODO:
            // Tehdään myöhemmin opiskelijan tietosivu.

            System.out.println("openTeacherCoursePage puututuu vielä.");

            openStudentStartPage();
        }

        private void openTeacherAttendanceTracking() {

            TeacherAttendanceTracking view =
                    new TeacherAttendanceTracking(
                            this::openTeacherStartPage
                    );

            stage.setScene(
                    new Scene(view, 1024, 399)
            );
        }

        private void openAdminPage() {
            // TODO:
            // Tehdään myöhemmin opiskelijan tietosivu.

            System.out.println("openAdminPage puututuu vielä.");

            openStudentStartPage();
        }
    }
}