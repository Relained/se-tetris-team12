package org.example.model;

import org.example.game.logic.SettingData.ColorBlindMode;
import javafx.scene.paint.Color;

public class ColorModel {
    private static Color[] currentColorSet;
    
    public static void setColorSet(ColorBlindMode mode) {
        switch (mode) {
            case Default -> currentColorSet = defaultColorSet;
            case PROTANOPIA -> currentColorSet = protanopiaColorSet;
            case DEUTERANOPIA -> currentColorSet = deuteranopiaColorSet;
            case TRITANOPIA -> currentColorSet = tritanopiaColorSet;
        }
    }

    public static Color getColorFromIndex(int index) {
        if (index < 1 || index > currentColorSet.length) {
            throw new IllegalArgumentException("Invalid color index: " + index);
        }
        return currentColorSet[index - 1];
    }

    static final Color defaultColorSet[] = {
        Color.CYAN,
        Color.YELLOW,
        Color.PURPLE,
        Color.LIME,
        Color.RED,
        Color.BLUE,
        Color.ORANGE
    };

    static final Color protanopiaColorSet[] = {
        Color.CYAN,
        Color.YELLOW,
        Color.PURPLE,
        Color.LIME,
        Color.web("#FFA500"),    // Orange
        Color.BLUE,
        Color.PINK
    };

    static final Color deuteranopiaColorSet[] = {
        Color.CYAN,
        Color.YELLOW,
        Color.PURPLE,
        Color.LIME,
        Color.web("#FFA500"),    // Orange
        Color.BLUE,
        Color.PINK
    };

    static final Color tritanopiaColorSet[] = {
        Color.CYAN,
        Color.WHITE,
        Color.PURPLE,
        Color.LIME,
        Color.RED,
        Color.BLUE,
        Color.ORANGE
    };
}
