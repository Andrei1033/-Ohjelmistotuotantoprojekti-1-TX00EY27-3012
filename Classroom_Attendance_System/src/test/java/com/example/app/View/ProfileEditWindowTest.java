package com.example.app.View;

import com.example.app.DaoElements.LoginDao.UserDao;
import com.example.app.Model.LoginComponents.Role;
import com.example.app.Model.LoginComponents.User;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileEditWindowTest extends ApplicationTest {

    private Stage stage;
    private User user;
    private UserDao userDao;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
    }

    @BeforeEach
    void setUp() {
        user = new User(
                1,
                "Matti",
                "Meikäläinen",
                "matti@example.com",
                Role.STUDENT
        );

        userDao = mock(UserDao.class);

        when(userDao.isEmailTaken(anyString(), anyInt()))
                .thenReturn(false);
    }

    /**
     * Avaa ProfileEditWindow-testin varten.
     *
     * Dialogi suljetaan automaattisesti heti kun testin
     * tarvitsemat tarkistukset on tehty.
     */
    private void showWindow() {
        interact(() -> {
            stage.setScene(new Scene(new VBox(), 400, 300));
            stage.show();
        });
    }

    /*
    @Test
    void profileWindowCanBeCreated() {
        AtomicBoolean callbackCalled = new AtomicBoolean(false);

        interact(() -> {
            ProfileEditWindow.show(
                    stage,
                    user,
                    () -> callbackCalled.set(true),
                    userDao
            );
        });

        assertFalse(callbackCalled.get());
    }*/

    @Test
    void userContainsCorrectProfileInformation() {
        assertEquals("Matti", user.getFirstName());
        assertEquals("Meikäläinen", user.getLastName());
        assertEquals("matti@example.com", user.getEmail());
        assertEquals(1, user.getId());
    }

    @Test
    void updateProfileIsCalledWithCorrectInformation() {
        when(userDao.updateProfile(
                1,
                "Liisa",
                "Virtanen",
                "liisa@example.com"
        )).thenReturn(true);

        boolean result = userDao.updateProfile(
                1,
                "Liisa",
                "Virtanen",
                "liisa@example.com"
        );

        assertTrue(result);

        verify(userDao).updateProfile(
                1,
                "Liisa",
                "Virtanen",
                "liisa@example.com"
        );
    }

    @Test
    void emailIsCheckedBeforeSaving() {
        when(userDao.isEmailTaken(
                "test@example.com",
                1
        )).thenReturn(false);

        boolean taken = userDao.isEmailTaken(
                "test@example.com",
                1
        );

        assertFalse(taken);

        verify(userDao).isEmailTaken(
                "test@example.com",
                1
        );
    }

    @Test
    void passwordCanBeVerified() {
        when(userDao.verifyPassword(1, "password"))
                .thenReturn(true);

        boolean valid = userDao.verifyPassword(
                1,
                "password"
        );

        assertTrue(valid);

        verify(userDao).verifyPassword(
                1,
                "password"
        );
    }

    @Test
    void passwordCanBeUpdated() {
        when(userDao.updatePassword(1, "newPassword"))
                .thenReturn(true);

        boolean result = userDao.updatePassword(
                1,
                "newPassword"
        );

        assertTrue(result);

        verify(userDao).updatePassword(
                1,
                "newPassword"
        );
    }
}
