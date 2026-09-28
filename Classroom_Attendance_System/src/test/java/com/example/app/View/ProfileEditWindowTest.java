
package com.example.app.View;

import com.example.app.DaoElements.LoginDao.UserDao;
import com.example.app.Model.LoginComponents.User;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class ProfileEditWindowTest {

    private Stage owner;
    private User currentUser;
    private UserDao userDao;

    private AtomicInteger callbackCount;

    @Start
    void start(Stage stage) {
        owner = stage;
        owner.setScene(new Scene(new javafx.scene.layout.VBox(), 400, 300));
        owner.show();
    }

    @BeforeEach
    void setUp() {
        currentUser = mock(User.class);
        userDao = mock(UserDao.class);
        callbackCount = new AtomicInteger(0);

        when(currentUser.getId()).thenReturn(1);
        when(currentUser.getFirstName()).thenReturn("Matti");
        when(currentUser.getLastName()).thenReturn("Meikalainen");
        when(currentUser.getEmail()).thenReturn("matti@example.com");

        when(userDao.isEmailTaken(anyString(), anyInt()))
                .thenReturn(false);

        when(userDao.updateProfile(
                anyInt(), anyString(), anyString(), anyString()))
                .thenReturn(true);

        when(userDao.updatePassword(anyInt(), anyString()))
                .thenReturn(true);

        when(userDao.verifyPassword(anyInt(), anyString()))
                .thenReturn(true);
    }

    private void openDialog() {
        // Älä suorita showAndWait()-metodia suoraan
        // testisäikeessä. JavaFX käsittelee sen omassa säikeessään.
        Platform.runLater(() ->
                ProfileEditWindow.show(
                        owner,
                        currentUser,
                        callbackCount::incrementAndGet,
                        userDao
                )
        );

        WaitForAsyncUtils.waitForFxEvents();
    }

    private void closeDialog(FxRobot robot) {
        robot.clickOn("#backButton");
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void dialogContainsAllExpectedFields(FxRobot robot) {
        openDialog();

        assertNotNull(robot.lookup("#firstNameField")
                .queryAs(TextField.class));
        assertNotNull(robot.lookup("#lastNameField")
                .queryAs(TextField.class));
        assertNotNull(robot.lookup("#emailField")
                .queryAs(TextField.class));

        assertNotNull(robot.lookup("#currentPasswordField")
                .queryAs(PasswordField.class));
        assertNotNull(robot.lookup("#newPasswordField")
                .queryAs(PasswordField.class));
        assertNotNull(robot.lookup("#confirmPasswordField")
                .queryAs(PasswordField.class));

        assertNotNull(robot.lookup("#saveButton")
                .queryAs(Button.class));
        assertNotNull(robot.lookup("#backButton")
                .queryAs(Button.class));

        closeDialog(robot);
    }

    @Test
    void validProfileIsSavedAndCallbackIsCalled(FxRobot robot) {
        openDialog();

        TextField firstName = robot.lookup("#firstNameField")
                .queryAs(TextField.class);
        TextField lastName = robot.lookup("#lastNameField")
                .queryAs(TextField.class);
        TextField email = robot.lookup("#emailField")
                .queryAs(TextField.class);

        Platform.runLater(() -> {
            firstName.setText("Liisa");
            lastName.setText("Virtanen");
            email.setText("liisa@example.com");
        });

        WaitForAsyncUtils.waitForFxEvents();

        robot.clickOn("#saveButton");

        WaitForAsyncUtils.waitForFxEvents();

        verify(userDao).updateProfile(
                1,
                "Liisa",
                "Virtanen",
                "liisa@example.com"
        );

        verify(currentUser).setFirstName("Liisa");
        verify(currentUser).setLastName("Virtanen");
        verify(currentUser).setEmail("liisa@example.com");

        assertEquals(1, callbackCount.get());
    }

    @Test
    void emptyRequiredFieldShowsError(FxRobot robot) {
        openDialog();

        robot.clickOn("#firstNameField")
                .eraseText("Matti".length());

        robot.clickOn("#saveButton");
        WaitForAsyncUtils.waitForFxEvents();

        Label error = robot.lookup("#errorLabel")
                .queryAs(Label.class);

        assertEquals(
                "Etunimi, sukunimi ja sähköposti ovat pakollisia.",
                error.getText()
        );

        verify(userDao, never()).updateProfile(
                anyInt(), anyString(), anyString(), anyString());

        assertEquals(0, callbackCount.get());

        closeDialog(robot);
    }

    @Test
    void invalidEmailShowsError(FxRobot robot) {
        openDialog();

        robot.clickOn("#emailField")
                .eraseText("matti@example.com".length())
                .write("virheellinen");

        robot.clickOn("#saveButton");
        WaitForAsyncUtils.waitForFxEvents();

        Label error = robot.lookup("#errorLabel")
                .queryAs(Label.class);

        assertEquals(
                "Sähköpostiosoite ei ole kelvollinen.",
                error.getText()
        );

        verify(userDao, never()).updateProfile(
                anyInt(), anyString(), anyString(), anyString());

        assertEquals(0, callbackCount.get());

        closeDialog(robot);
    }

    @Test
    void emailAlreadyInUseShowsError(FxRobot robot) {
        when(userDao.isEmailTaken(
                "other@example.com", 1)).thenReturn(true);

        openDialog();

        robot.clickOn("#emailField")
                .eraseText("matti@example.com".length())
                .write("other@example.com");

        robot.clickOn("#saveButton");
        WaitForAsyncUtils.waitForFxEvents();

        Label error = robot.lookup("#errorLabel")
                .queryAs(Label.class);

        assertEquals(
                "Sähköpostiosoite on jo käytössä.",
                error.getText()
        );

        verify(userDao, never()).updateProfile(
                anyInt(), anyString(), anyString(), anyString());

        assertEquals(0, callbackCount.get());

        closeDialog(robot);
    }

    @Test
    void passwordChangeRequiresCurrentPassword(FxRobot robot) {
        openDialog();

        robot.clickOn("#newPasswordField").write("salasana123");
        robot.clickOn("#confirmPasswordField").write("salasana123");

        robot.clickOn("#saveButton");
        WaitForAsyncUtils.waitForFxEvents();

        Label error = robot.lookup("#errorLabel")
                .queryAs(Label.class);

        assertEquals(
                "Anna nykyinen salasana vaihtaaksesi salasanan.",
                error.getText()
        );

        verify(userDao, never()).updateProfile(
                anyInt(), anyString(), anyString(), anyString());

        assertEquals(0, callbackCount.get());

        closeDialog(robot);
    }

    @Test
    void incorrectCurrentPasswordShowsError(FxRobot robot) {
        when(userDao.verifyPassword(1, "väärä"))
                .thenReturn(false);

        openDialog();

        robot.clickOn("#currentPasswordField").write("väärä");
        robot.clickOn("#newPasswordField").write("salasana123");
        robot.clickOn("#confirmPasswordField").write("salasana123");

        robot.clickOn("#saveButton");
        WaitForAsyncUtils.waitForFxEvents();

        Label error = robot.lookup("#errorLabel")
                .queryAs(Label.class);

        assertEquals("Nykyinen salasana on väärin.", error.getText());

        verify(userDao, never()).updateProfile(
                anyInt(), anyString(), anyString(), anyString());

        assertEquals(0, callbackCount.get());

        closeDialog(robot);
    }

    @Test
    void newPasswordMustHaveAtLeastSixCharacters(FxRobot robot) {
        openDialog();

        robot.clickOn("#currentPasswordField").write("vanha123");
        robot.clickOn("#newPasswordField").write("12345");
        robot.clickOn("#confirmPasswordField").write("12345");

        robot.clickOn("#saveButton");
        WaitForAsyncUtils.waitForFxEvents();

        Label error = robot.lookup("#errorLabel")
                .queryAs(Label.class);

        assertEquals(
                "Uuden salasanan on oltava vähintään 6 merkkiä.",
                error.getText()
        );

        assertEquals(0, callbackCount.get());

        closeDialog(robot);
    }

    @Test
    void mismatchedPasswordsShowError(FxRobot robot) {
        openDialog();

        robot.clickOn("#currentPasswordField").write("vanha123");
        robot.clickOn("#newPasswordField").write("salasana123");
        robot.clickOn("#confirmPasswordField").write("toinen123");

        robot.clickOn("#saveButton");
        WaitForAsyncUtils.waitForFxEvents();

        Label error = robot.lookup("#errorLabel")
                .queryAs(Label.class);

        assertEquals(
                "Uudet salasanat eivät täsmää.",
                error.getText()
        );

        assertEquals(0, callbackCount.get());

        closeDialog(robot);
    }

    @Test
    void failedProfileSaveDoesNotCallCallback(FxRobot robot) {
        when(userDao.updateProfile(
                anyInt(), anyString(), anyString(), anyString()))
                .thenReturn(false);

        openDialog();

        robot.clickOn("#saveButton");
        WaitForAsyncUtils.waitForFxEvents();

        Label error = robot.lookup("#errorLabel")
                .queryAs(Label.class);

        assertEquals(
                "Tietojen tallennus epäonnistui. Yritä uudelleen.",
                error.getText()
        );

        assertEquals(0, callbackCount.get());

        closeDialog(robot);
    }

    @Test
    void successfulPasswordChangeCallsCallbackOnce(FxRobot robot) {
        openDialog();

        robot.clickOn("#currentPasswordField").write("vanha123");
        robot.clickOn("#newPasswordField").write("uusi123456");
        robot.clickOn("#confirmPasswordField").write("uusi123456");

        robot.clickOn("#saveButton");
        WaitForAsyncUtils.waitForFxEvents();

        verify(userDao).verifyPassword(1, "vanha123");
        verify(userDao).updateProfile(
                1, "Matti", "Meikalainen", "matti@example.com");
        verify(userDao).updatePassword(1, "uusi123456");

        assertEquals(1, callbackCount.get());
    }

    @Test
    void failedPasswordChangeDoesNotCallCallback(FxRobot robot) {
        when(userDao.updatePassword(1, "uusi123456"))
                .thenReturn(false);

        openDialog();

        robot.clickOn("#currentPasswordField").write("vanha123");
        robot.clickOn("#newPasswordField").write("uusi123456");
        robot.clickOn("#confirmPasswordField").write("uusi123456");

        robot.clickOn("#saveButton");
        WaitForAsyncUtils.waitForFxEvents();

        Label error = robot.lookup("#errorLabel")
                .queryAs(Label.class);

        assertEquals(
                "Salasanan vaihto epäonnistui. "
                        + "Tiedot tallennettiin, mutta salasana ei vaihtunut.",
                error.getText()
        );

        assertEquals(0, callbackCount.get());

        closeDialog(robot);
    }
}
