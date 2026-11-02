package com.example.app.View;

import com.example.app.Localisation.i18n;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.framework.junit5.ApplicationTest;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import static org.junit.jupiter.api.Assertions.*;

abstract class ViewTestSupport extends ApplicationTest {
    private i18n.Lang originalLanguage;
    @Override public void start(Stage stage) {}
    @BeforeEach void prepareLanguage() {
        interact(() -> { originalLanguage = i18n.getCurrentLang(); i18n.setLanguage(i18n.FI); });
    }
    @AfterEach void cleanUpWindowsAndLanguage() {
        interact(() -> {
            new ArrayList<>(Window.getWindows()).forEach(Window::hide);
            i18n.setLanguage(originalLanguage);
        });
    }
    static <T extends Node> List<T> nodes(Node root, Class<T> type) {
        List<T> result = new ArrayList<>();
        if (type.isInstance(root)) result.add(type.cast(root));
        if (root instanceof ScrollPane scroll && scroll.getContent() != null)
            result.addAll(nodes(scroll.getContent(), type));
        if (root instanceof Parent parent)
            for (Node child : parent.getChildrenUnmodifiable()) result.addAll(nodes(child, type));
        return result.stream().distinct().toList();
    }
    static Button button(Node root, String text) {
        return nodes(root, Button.class).stream().filter(b -> text.equals(b.getText()))
                .findFirst().orElseThrow(() -> new AssertionError("Button missing: " + text));
    }
    static void hasLabel(Node root, String text) {
        assertTrue(nodes(root, Label.class).stream().anyMatch(l -> text.equals(l.getText())), "Label missing: " + text);
    }
    @SuppressWarnings("unchecked")
    static ComboBox<i18n.Lang> languageBox(Node root) {
        return (ComboBox<i18n.Lang>) nodes(root, ComboBox.class).getFirst();
    }
    // Run assertions inside the nested event loop of showAndWait().
    static void inModal(Runnable open, Consumer<Stage> action) {
        List<Window> existing = new ArrayList<>(Window.getWindows());
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Platform.runLater(() -> {
            Stage dialog = null;
            try {
                dialog = (Stage) Window.getWindows().stream().filter(w -> !existing.contains(w)).findFirst().orElseThrow();
                action.accept(dialog);
            } catch (Throwable error) { failure.set(error); }
            finally { if (dialog != null) dialog.close(); }
        });
        open.run();
        if (failure.get() != null) throw new AssertionError("Dialog assertion failed", failure.get());
    }
}
