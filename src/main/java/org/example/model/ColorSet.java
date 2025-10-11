package org.example.model;

import javafx.scene.paint.Color;

/**
 * 색상 블라인드 모드별 색상 세트를 정의하는 클래스
 * 순수 데이터만 포함
 */
public class ColorSet {
    
    // Default 색상 세트
    public static final Color[] DEFAULT = {
        Color.CYAN,
        Color.YELLOW,
        Color.PURPLE,
        Color.LIME,
        Color.RED,
        Color.BLUE,
        Color.ORANGE
    };

    // Protanopia (적색약) 색상 세트
    public static final Color[] PROTANOPIA = {
        Color.CYAN,
        Color.YELLOW,
        Color.PURPLE,
        Color.LIME,
        Color.web("#FFA500"),    // Orange
        Color.BLUE,
        Color.PINK
    };

    // Deuteranopia (녹색약) 색상 세트
    public static final Color[] DEUTERANOPIA = {
        Color.CYAN,
        Color.YELLOW,
        Color.PURPLE,
        Color.LIME,
        Color.web("#FFA500"),    // Orange
        Color.BLUE,
        Color.PINK
    };

    // Tritanopia (청색약) 색상 세트
    public static final Color[] TRITANOPIA = {
        Color.CYAN,
        Color.WHITE,
        Color.PURPLE,
        Color.LIME,
        Color.RED,
        Color.BLUE,
        Color.ORANGE
    };
    
    private ColorSet() {
        // Utility class - prevent instantiation
    }
}
