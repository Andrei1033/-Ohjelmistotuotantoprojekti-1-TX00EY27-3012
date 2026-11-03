package com.example.app.Localisation;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public final class FontSupport {

    private FontSupport() {}

    /** Let controls, table cells and dialog buttons inherit the locale font. */
    public static void applyTo(javafx.scene.Parent root) {
        root.setStyle(root.getStyle() + "; -fx-font-family: '" + pickFamily() + "';");
    }

    public static Font font(double size, FontWeight weight) {
        return Font.font(pickFamily(), weight, size);
    }

    public static String pickFamily() {
        return pickFamily(i18n.getCurrentLang());
    }

    public static void configureLanguageSelector(javafx.scene.control.ComboBox<i18n.Lang> selector) {
        selector.setCellFactory(list -> languageCell());
        selector.setButtonCell(languageCell());
    }

    private static javafx.scene.control.ListCell<i18n.Lang> languageCell() {
        return new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(i18n.Lang language, boolean empty) {
                super.updateItem(language, empty);
                setText(empty || language == null ? "" : language.label());
                setGraphic(null);
                setStyle(empty || language == null ? ""
                        : "-fx-font-family: '" + pickFamily(language) + "';");
            }
        };
    }

    private static String pickFamily(i18n.Lang language) {
        String lang = language.code();

        return switch (lang) {
            case "ja" -> firstAvailable(
                    "Noto Sans CJK JP",
                    "Noto Sans JP",
                    "Noto Sans CJK",
                    "Yu Gothic UI",
                    "Meiryo",
                    "System"
            );

            case "ar" -> firstAvailable(
                    "Noto Naskh Arabic",
                    "Segoe UI",
                    "Tahoma",
                    "System"
            );

            case "ru" -> firstAvailable(
                    "Noto Sans",
                    "Segoe UI",
                    "Arial",
                    "System"
            );

            default -> "System";
        };
    }

    private static String firstAvailable(String... families) {
        List<String> installed = Font.getFamilies();

        for (String family : families) {
            if (installed.contains(family)) {
                System.out.println("Selected font: " + family);
                return family;
            }
        }

        System.out.println("WARNING: No matching font found!");
        return "System";
    }

    public static boolean isRtl() {
        return "ar".equals(i18n.getCurrentLang().code());
    }
}
