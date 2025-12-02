package org.example.service;

import javafx.scene.paint.Color;
import org.example.model.ColorSet;
import org.example.model.SettingData.ColorBlindMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ColorManager 클래스의 Unit Test
 */
class ColorManagerTest {
    
    private ColorManager colorManager;
    
    @BeforeEach
    void setUp() throws Exception {
        // 싱글톤 인스턴스 초기화
        colorManager = ColorManager.getInstance();
        
        // 테스트 전 기본 모드로 리셋
        colorManager.setColorMode(ColorBlindMode.Default);
    }
    
    @Test
    void testGetInstance() {
        ColorManager instance1 = ColorManager.getInstance();
        ColorManager instance2 = ColorManager.getInstance();
        
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2, "getInstance should return the same singleton instance");
    }
    
    @Test
    void testSetColorModeToDefault() {
        colorManager.setColorMode(ColorBlindMode.Default);
        
        assertEquals(ColorBlindMode.Default, colorManager.getCurrentMode());
        assertArrayEquals(ColorSet.DEFAULT, colorManager.getCurrentColorSet());
    }
    
    @Test
    void testSetColorModeToProtanopia() {
        colorManager.setColorMode(ColorBlindMode.PROTANOPIA);
        
        assertEquals(ColorBlindMode.PROTANOPIA, colorManager.getCurrentMode());
        assertArrayEquals(ColorSet.PROTANOPIA, colorManager.getCurrentColorSet());
    }
    
    @Test
    void testSetColorModeToDeuteranopia() {
        colorManager.setColorMode(ColorBlindMode.DEUTERANOPIA);
        
        assertEquals(ColorBlindMode.DEUTERANOPIA, colorManager.getCurrentMode());
        assertArrayEquals(ColorSet.DEUTERANOPIA, colorManager.getCurrentColorSet());
    }
    
    @Test
    void testSetColorModeToTritanopia() {
        colorManager.setColorMode(ColorBlindMode.TRITANOPIA);
        
        assertEquals(ColorBlindMode.TRITANOPIA, colorManager.getCurrentMode());
        assertArrayEquals(ColorSet.TRITANOPIA, colorManager.getCurrentColorSet());
    }
    
    @Test
    void testGetCurrentModeAfterSwitch() {
        colorManager.setColorMode(ColorBlindMode.PROTANOPIA);
        assertEquals(ColorBlindMode.PROTANOPIA, colorManager.getCurrentMode());
        
        colorManager.setColorMode(ColorBlindMode.Default);
        assertEquals(ColorBlindMode.Default, colorManager.getCurrentMode());
    }
    
    @Test
    void testGetColorFromIndexValid() {
        colorManager.setColorMode(ColorBlindMode.Default);
        
        // Index 1부터 7까지 유효
        for (int i = 1; i <= 7; i++) {
            Color color = colorManager.getColorFromIndex(i);
            assertNotNull(color);
            assertEquals(ColorSet.DEFAULT[i - 1], color);
        }
    }
    
    @Test
    void testGetColorFromIndexForGray() {
        Color grayColor = colorManager.getColorFromIndex(8);
        
        assertNotNull(grayColor);
        assertEquals(Color.GRAY, grayColor);
    }
    
    @Test
    void testGetColorFromIndexInvalidLow() {
        assertThrows(IllegalArgumentException.class, () -> {
            colorManager.getColorFromIndex(0);
        });
    }
    
    @Test
    void testGetColorFromIndexInvalidHigh() {
        assertThrows(IllegalArgumentException.class, () -> {
            colorManager.getColorFromIndex(9);
        });
    }
    
    @Test
    void testGetColorFromIndexNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            colorManager.getColorFromIndex(-1);
        });
    }
    
    @Test
    void testGetCurrentColorSetIsClone() {
        Color[] colorSet1 = colorManager.getCurrentColorSet();
        Color[] colorSet2 = colorManager.getCurrentColorSet();
        
        assertNotSame(colorSet1, colorSet2, "getCurrentColorSet should return a clone");
        assertArrayEquals(colorSet1, colorSet2);
    }
    
    @Test
    void testGetBackgroundColor() {
        Color backgroundColor = colorManager.getBackgroundColor();
        
        assertNotNull(backgroundColor);
        assertEquals(Color.BLACK, backgroundColor);
    }
    
    @Test
    void testGetPrimaryTextColor() {
        Color primaryTextColor = colorManager.getPrimaryTextColor();
        
        assertNotNull(primaryTextColor);
        assertEquals(Color.WHITE, primaryTextColor);
    }
    
    @Test
    void testGetSecondaryTextColor() {
        Color secondaryTextColor = colorManager.getSecondaryTextColor();
        
        assertNotNull(secondaryTextColor);
        assertEquals(Color.LIGHTGRAY, secondaryTextColor);
    }
    
    @Test
    void testGetGameBackgroundColor() {
        Color gameBackgroundColor = colorManager.getGameBackgroundColor();
        
        assertNotNull(gameBackgroundColor);
        assertEquals(Color.DARKSLATEGRAY, gameBackgroundColor);
    }
    
    @Test
    void testColorModeChangesColorSet() {
        colorManager.setColorMode(ColorBlindMode.Default);
        Color defaultColor1 = colorManager.getColorFromIndex(1);
        
        colorManager.setColorMode(ColorBlindMode.PROTANOPIA);
        Color protanopiaColor1 = colorManager.getColorFromIndex(1);
        
        // 색상이 다를 수 있음 (모드에 따라)
        assertNotNull(defaultColor1);
        assertNotNull(protanopiaColor1);
    }
    
    @Test
    void testAllColorBlindModes() {
        for (ColorBlindMode mode : ColorBlindMode.values()) {
            assertDoesNotThrow(() -> colorManager.setColorMode(mode));
            assertEquals(mode, colorManager.getCurrentMode());
        }
    }
    
    @Test
    void testCurrentColorSetLength() {
        Color[] colorSet = colorManager.getCurrentColorSet();
        
        assertEquals(7, colorSet.length, "Color set should have 7 colors for tetromino types");
    }
    
    @Test
    void testFixedColorsDoNotChange() {
        Color background1 = colorManager.getBackgroundColor();
        colorManager.setColorMode(ColorBlindMode.PROTANOPIA);
        Color background2 = colorManager.getBackgroundColor();
        
        assertEquals(background1, background2, "Background color should not change with mode");
        
        Color primaryText1 = colorManager.getPrimaryTextColor();
        colorManager.setColorMode(ColorBlindMode.DEUTERANOPIA);
        Color primaryText2 = colorManager.getPrimaryTextColor();
        
        assertEquals(primaryText1, primaryText2, "Primary text color should not change with mode");
    }
}
