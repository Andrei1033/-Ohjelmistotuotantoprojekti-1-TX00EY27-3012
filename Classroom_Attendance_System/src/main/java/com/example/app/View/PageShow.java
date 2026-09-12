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
                    this::openStudentStartPage
            );

            stage.setScene(
                    new Scene(view, 1024, 399)
            );
        }


        private void openStudentAttendanceTracking() {

            StudentAttendanceTracking view = new StudentAttendanceTracking(
                    this::openStudentStartPage
            );

            stage.setScene(
                    new Scene(view, 1024, 399)
            );
        }


        private void openTeacherStartPage() {

            TeacherStartPage view = new TeacherStartPage(
                    this::openTeacherStartPage
            );

            stage.setScene(
                    new Scene(view, 1024, 399)
            );
        }

        private void openTeacherCoursePage() {

            TeacherCoursePage view = new TeacherCoursePage(
                    this::openTeacherStartPage
            );

            stage.setScene(
                    new Scene(view, 1024, 399)
            );
        }

        private void openTeacherAttendanceTracking() {

            TeacherAttendanceTracking view =
                    new TeacherAttendanceTracking(
                            this::openTeacherStartPage
                    );

            Scene scene = new Scene(view, 1400, 900);

            stage.setScene(scene);

            // Vähimmäiskoko, johon asti sisältö skaalautuu pienemmäksi.
            // Alle tämän ikkunaa ei voi pienentää.
            stage.setMinWidth(875);
            stage.setMinHeight(560);

            // Oletuskoko
            stage.setWidth(1400);
            stage.setHeight(900);

            stage.centerOnScreen();

            // Ikkunan koon muuttaminen on sallittu — kaikki elementit
            // skaalautuvat automaattisesti bindingien ansiosta.
            stage.setResizable(true);
        }

        private void openAdminPage() {

            Admin view = new Admin(
            );

            stage.setScene(
                    new Scene(view.getView(), 1024, 399)
            );
        }
    }
}