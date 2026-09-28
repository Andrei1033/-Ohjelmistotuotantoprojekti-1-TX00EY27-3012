package com.example.app.Controller;

import com.example.app.Model.LoginComponents.Role;
import com.example.app.Model.LoginComponents.User;
import javafx.application.Platform;
import javafx.scene.Parent;
import org.junit.jupiter.api.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StudentControllerTest {

    @BeforeAll
    void startJavaFx() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException alreadyStarted) {
            latch.countDown(); // toolkit already up
        }
        assertTrue(latch.await(10, TimeUnit.SECONDS), "JavaFX failed to start");
    }

    @Test
    void getView_shouldReturnNonNullRoot() throws Exception {
        User user = new User(42, "a@b.c", "pw", "Test", Role.STUDENT);

        AtomicReference<Parent> ref = new AtomicReference<>();
        AtomicReference<Throwable> error = new AtomicReference<>();

        CountDownLatch done = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                ref.set(new StudentController(user, () -> {}).getView());
            } catch (Throwable t) {
                error.set(t);
            } finally {
                done.countDown();
            }
        });

        assertTrue(done.await(10, TimeUnit.SECONDS), "getView() timed out");
        if (error.get() != null) fail("getView threw: " + error.get(), error.get());
        assertNotNull(ref.get());
    }
}