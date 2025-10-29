package org.example.model;

import javafx.scene.paint.Color;

/**
 * 색상 블라인드 모드별 색상 세트를 정의하는 클래스
 * 순수 데이터만 포함
 */
public class ColorSet {
    
    // Default 색상 세트 (일반)
    public static final Color[] DEFAULT = {
        Color.CYAN,          // I (밝은 청록)
        Color.YELLOW,        // O (노랑)
        Color.PURPLE,        // T (보라)
        Color.LIME,          // S (연두)
        Color.RED,           // Z (빨강)
        Color.BLUE,          // J (파랑)
        Color.ORANGE         // L (주황)
    };

    // Protanopia (적색약) - 빨강-초록 구별 어려움
    // 빨강이 어둡게 보이고 초록과 구별 안 됨
    public static final Color[] PROTANOPIA = {
        Color.web("#0072B2"),    // I - 파랑 (Blue)
        Color.web("#F0E442"),    // O - 노랑 (Yellow)
        Color.web("#CC79A7"),    // T - 자홍 (Magenta)
        Color.web("#56B4E9"),    // S - 하늘색 (Sky Blue)
        Color.web("#E69F00"),    // Z - 주황 (Orange) - 빨강 대체
        Color.web("#009E73"),    // J - 청록 (Teal)
        Color.web("#D55E00")     // L - 진한 주황 (Vermillion)
    };

    // Deuteranopia (녹색약) - 빨강-초록 구별 어려움 (적색약과 유사)
    // 초록이 어둡게 보이고 빨강과 구별 안 됨
    public static final Color[] DEUTERANOPIA = {
        Color.web("#0072B2"),    // I - 파랑 (Blue)
        Color.web("#F0E442"),    // O - 노랑 (Yellow)
        Color.web("#CC79A7"),    // T - 자홍 (Magenta)
        Color.web("#56B4E9"),    // S - 하늘색 (Sky Blue)
        Color.web("#E69F00"),    // Z - 주황 (Orange)
        Color.web("#009E73"),    // J - 청록 (Teal)
        Color.web("#D55E00")     // L - 진한 주황 (Vermillion)
    };

    // Tritanopia (청색약) - 파랑-노랑 구별 어려움
    // 파랑이 초록처럼 보이고 노랑이 핑크처럼 보임
    public static final Color[] TRITANOPIA = {
        Color.web("#D55E00"),    // I - 진한 주황 (Vermillion)
        Color.web("#CC79A7"),    // O - 자홍 (Magenta)
        Color.web("#E69F00"),    // T - 주황 (Orange)
        Color.web("#009E73"),    // S - 청록 (Teal)
        Color.web("#F0E442"),    // Z - 노랑 (Yellow)
        Color.web("#0072B2"),    // J - 파랑 (Blue)
        Color.web("#56B4E9")     // L - 하늘색 (Sky Blue)
    };    private ColorSet() {
        // Utility class - prevent instantiation
    }
}
