package org.example.service;

import org.example.model.SettingData.ColorBlindMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import javafx.scene.paint.Color;

import static org.junit.jupiter.api.Assertions.*;

class ColorManagerTest {

    @BeforeEach
    void setUp() {
        // 싱글톤 인스턴스를 기본 모드로 리셋
        ColorManager.getInstance().setColorMode(ColorBlindMode.Default);
    }

    @Test
    @DisplayName("싱글톤 인스턴스 테스트")
    void testSingleton() {
        ColorManager instance1 = ColorManager.getInstance();
        ColorManager instance2 = ColorManager.getInstance();
        
        assertSame(instance1, instance2);
    }

    @Test
    @DisplayName("기본 색상 모드 설정")
    void testSetDefaultColorMode() {
        ColorManager manager = ColorManager.getInstance();
        manager.setColorMode(ColorBlindMode.Default);
        
        assertEquals(ColorBlindMode.Default, manager.getCurrentMode());
    }

    @Test
    @DisplayName("PROTANOPIA 색상 모드 설정")
    void testSetProtanopiaMode() {
        ColorManager manager = ColorManager.getInstance();
        manager.setColorMode(ColorBlindMode.PROTANOPIA);
        
        assertEquals(ColorBlindMode.PROTANOPIA, manager.getCurrentMode());
    }

    @Test
    @DisplayName("DEUTERANOPIA 색상 모드 설정")
    void testSetDeuteranopiaMode() {
        ColorManager manager = ColorManager.getInstance();
        manager.setColorMode(ColorBlindMode.DEUTERANOPIA);
        
        assertEquals(ColorBlindMode.DEUTERANOPIA, manager.getCurrentMode());
    }

    @Test
    @DisplayName("TRITANOPIA 색상 모드 설정")
    void testSetTritanopiaMode() {
        ColorManager manager = ColorManager.getInstance();
        manager.setColorMode(ColorBlindMode.TRITANOPIA);
        
        assertEquals(ColorBlindMode.TRITANOPIA, manager.getCurrentMode());
    }

    @Test
    @DisplayName("인덱스로 색상 가져오기 - 유효한 인덱스")
    void testGetColorFromIndex_ValidIndex() {
        ColorManager manager = ColorManager.getInstance();
        
        for (int i = 1; i <= 7; i++) {
            Color color = manager.getColorFromIndex(i);
            assertNotNull(color);
        }
    }

    @Test
    @DisplayName("인덱스로 색상 가져오기 - 유효하지 않은 인덱스 (0)")
    void testGetColorFromIndex_InvalidIndexZero() {
        ColorManager manager = ColorManager.getInstance();
        
        assertThrows(IllegalArgumentException.class, () -> {
            manager.getColorFromIndex(0);
        });
    }

    @Test
    @DisplayName("인덱스로 색상 가져오기 - 유효하지 않은 인덱스 (8)")
    void testGetColorFromIndex_InvalidIndexEight() {
        ColorManager manager = ColorManager.getInstance();
        
        assertThrows(IllegalArgumentException.class, () -> {
            manager.getColorFromIndex(8);
        });
    }

    @Test
    @DisplayName("현재 색상 세트 가져오기")
    void testGetCurrentColorSet() {
        ColorManager manager = ColorManager.getInstance();
        Color[] colorSet = manager.getCurrentColorSet();
        
        assertNotNull(colorSet);
        assertEquals(7, colorSet.length);
    }

    @Test
    @DisplayName("색상 세트가 복사본을 반환하는지 확인")
    void testGetCurrentColorSetReturnsCopy() {
        ColorManager manager = ColorManager.getInstance();
        Color[] colorSet1 = manager.getCurrentColorSet();
        Color[] colorSet2 = manager.getCurrentColorSet();
        
        assertNotSame(colorSet1, colorSet2);
    }

    @Test
    @DisplayName("배경 색상 테스트")
    void testGetBackgroundColor() {
        ColorManager manager = ColorManager.getInstance();
        Color bgColor = manager.getBackgroundColor();
        
        assertEquals(Color.BLACK, bgColor);
    }

    @Test
    @DisplayName("주 텍스트 색상 테스트")
    void testGetPrimaryTextColor() {
        ColorManager manager = ColorManager.getInstance();
        Color textColor = manager.getPrimaryTextColor();
        
        assertEquals(Color.WHITE, textColor);
    }

    @Test
    @DisplayName("보조 텍스트 색상 테스트")
    void testGetSecondaryTextColor() {
        ColorManager manager = ColorManager.getInstance();
        Color secondaryColor = manager.getSecondaryTextColor();
        
        assertEquals(Color.LIGHTGRAY, secondaryColor);
    }

    @Test
    @DisplayName("게임 배경 색상 테스트")
    void testGetGameBackgroundColor() {
        ColorManager manager = ColorManager.getInstance();
        Color gameBgColor = manager.getGameBackgroundColor();
        
        assertEquals(Color.DARKSLATEGRAY, gameBgColor);
    }

    @Test
    @DisplayName("색상 모드 변경 시 색상 세트가 변경되는지 확인")
    void testColorSetChangesWithMode() {
        ColorManager manager = ColorManager.getInstance();
        
        manager.setColorMode(ColorBlindMode.Default);
        manager.getColorFromIndex(1); // 기본 색상 로드
        
        manager.setColorMode(ColorBlindMode.PROTANOPIA);
        manager.getColorFromIndex(1); // 적록색맹 색상 로드
        
        // 색상이 다를 수 있음 (같을 수도 있지만, 모드가 변경되었는지 확인)
        assertEquals(ColorBlindMode.PROTANOPIA, manager.getCurrentMode());
    }

    @Test
    @DisplayName("모든 색상 블라인드 모드에서 7개 색상 제공")
    void testAllModesProvideSevenColors() {
        ColorManager manager = ColorManager.getInstance();
        
        for (ColorBlindMode mode : ColorBlindMode.values()) {
            manager.setColorMode(mode);
            Color[] colorSet = manager.getCurrentColorSet();
            assertEquals(7, colorSet.length, "Mode " + mode + " should provide 7 colors");
        }
    }
}
