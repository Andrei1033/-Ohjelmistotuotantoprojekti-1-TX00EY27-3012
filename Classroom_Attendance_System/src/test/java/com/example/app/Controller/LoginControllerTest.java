package com.example.app.Controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.app.DaoElements.LoginDao.UserDao;
import com.example.app.Model.LoginComponents.Role;
import com.example.app.Model.LoginComponents.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private UserDao userDao;

    @Mock
    private User mockUser;

    private LoginController loginController;

    @BeforeEach
    void setUp() {
        loginController = new LoginController(userDao);
    }

    // ==================== login() tests ====================

    @Test
    void login_shouldReturnEmpty_whenEmailIsNull() {
        Optional<User> result = loginController.login(null, "password123");
        assertTrue(result.isEmpty());
        verifyNoInteractions(userDao);
    }

    @Test
    void login_shouldReturnEmpty_whenEmailIsBlank() {
        Optional<User> result = loginController.login("   ", "password123");
        assertTrue(result.isEmpty());
        verifyNoInteractions(userDao);
    }

    @Test
    void login_shouldReturnEmpty_whenEmailIsEmpty() {
        Optional<User> result = loginController.login("", "password123");
        assertTrue(result.isEmpty());
        verifyNoInteractions(userDao);
    }

    @Test
    void login_shouldReturnEmpty_whenPasswordIsNull() {
        Optional<User> result = loginController.login("user@example.com", null);
        assertTrue(result.isEmpty());
        verifyNoInteractions(userDao);
    }

    @Test
    void login_shouldReturnEmpty_whenPasswordIsEmpty() {
        Optional<User> result = loginController.login("user@example.com", "");
        assertTrue(result.isEmpty());
        verifyNoInteractions(userDao);
    }

    @Test
    void login_shouldReturnUser_whenCredentialsAreValid() {
        User expected = mock(User.class);
        when(userDao.findByEmailAndPassword("user@example.com", "password123")).thenReturn(Optional.of(expected));

        Optional<User> result = loginController.login("user@example.com", "password123");

        assertTrue(result.isPresent());
        assertSame(expected, result.get());
        verify(userDao).findByEmailAndPassword("user@example.com", "password123");
    }

    @Test
    void login_shouldTrimEmail_beforeCallingDao() {
        when(userDao.findByEmailAndPassword("user@example.com", "password123")).thenReturn(Optional.of(mockUser));

        Optional<User> result = loginController.login("  user@example.com  ", "password123");

        assertTrue(result.isPresent());
        verify(userDao).findByEmailAndPassword("user@example.com", "password123");
        verify(userDao, never()).findByEmailAndPassword("  user@example.com  ", "password123");
    }

    @Test
    void login_shouldReturnEmpty_whenDaoReturnsEmpty() {
        when(userDao.findByEmailAndPassword("user@example.com", "wrongpass")).thenReturn(Optional.empty());

        Optional<User> result = loginController.login("user@example.com", "wrongpass");

        assertTrue(result.isEmpty());
        verify(userDao).findByEmailAndPassword("user@example.com", "wrongpass");
    }

    @Test
    void savePreferredLanguage_shouldPersistSelectedLanguage() {
        when(mockUser.getId()).thenReturn(42);
        when(userDao.updatePreferredLanguage(42, "ar")).thenReturn(true);

        boolean result = loginController.savePreferredLanguage(mockUser, "AR");

        assertTrue(result);
        verify(userDao).updatePreferredLanguage(42, "ar");
    }

    // ==================== getStartPageFor() tests ====================

    @Test
    void getStartPageFor_shouldThrow_whenUserIsNull() {
        Runnable onLogout = () -> {
        };

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> loginController.getStartPageFor(null, onLogout));
        assertEquals("User cannot be null", ex.getMessage());
    }

    private User mockUserWithRole(Role role) {
        User user = mock(User.class);
        when(user.getRole()).thenReturn(role);
        return user;
    }
}
