package org.example.service;

import javafx.scene.paint.Color;
import org.example.model.ColorSet;
import org.example.model.SettingData.ColorBlindMode;

/**
 * 색상 관리를 담당하는 싱글톤 매니저 클래스
 * 현재 색상 블라인드 모드에 따라 적절한 색상을 제공
 */
public class ColorManager {
    private static ColorManager instance;
    private Color[] currentColorSet;
    private ColorBlindMode currentMode;
    
    private ColorManager() {
        // 기본 색상 세트로 초기화
        setColorMode(ColorBlindMode.Default);
    }
    
    /**
     * ColorManager 싱글톤 인스턴스 반환
     */
    public static ColorManager getInstance() {
        if (instance == null) {
            instance = new ColorManager();
        }
        return instance;
    }
    
    /**
     * 색상 블라인드 모드 설정
     */
    public void setColorMode(ColorBlindMode mode) {
        this.currentMode = mode;
        switch (mode) {
            case Default -> currentColorSet = ColorSet.DEFAULT;
            case PROTANOPIA -> currentColorSet = ColorSet.PROTANOPIA;
            case DEUTERANOPIA -> currentColorSet = ColorSet.DEUTERANOPIA;
            case TRITANOPIA -> currentColorSet = ColorSet.TRITANOPIA;
        }
    }
    
    /**
     * 현재 색상 블라인드 모드 반환
     */
    public ColorBlindMode getCurrentMode() {
        return currentMode;
    }
    
    /**
     * 인덱스로 색상 가져오기 (1-based index)
     * @param index 1부터 7까지의 테트로미노 타입 인덱스
     * @return 해당 인덱스의 색상
     */
    public Color getColorFromIndex(int index) {
        if (index < 1 || index > currentColorSet.length) {
            throw new IllegalArgumentException("Invalid color index: " + index);
        }
        return currentColorSet[index - 1];
    }
    
    /**
     * 현재 색상 세트 전체 반환
     */
    public Color[] getCurrentColorSet() {
        return currentColorSet.clone();
    }
    
    /**
     * 배경 색상 반환 (고정)
     */
    public Color getBackgroundColor() {
        return Color.BLACK;
    }
    
    /**
     * 텍스트 주 색상 반환 (고정)
     */
    public Color getPrimaryTextColor() {
        return Color.WHITE;
    }
    
    /**
     * 텍스트 보조 색상 반환 (고정)
     */
    public Color getSecondaryTextColor() {
        return Color.LIGHTGRAY;
    }
    
    /**
     * 게임 배경 색상 반환 (고정)
     */
    public Color getGameBackgroundColor() {
        return Color.DARKSLATEGRAY;
    }
}
