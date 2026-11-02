package com.example.app.Localisation;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public final class FontSupport {

    private FontSupport() {}

    public static Font font(double size, FontWeight weight) {
        return Font.font(pickFamily(), weight, size);
    }

    public static String pickFamily() {
        String lang = i18n.getCurrentLang().code();

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