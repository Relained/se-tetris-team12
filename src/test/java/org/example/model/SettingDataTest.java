package org.example.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class SettingDataTest {

    @Test
    @DisplayName("SettingData 기본값 테스트")
    void testDefaultValues() {
        SettingData settings = new SettingData();
        
        assertEquals(SettingData.ScreenSize.MEDIUM, settings.screenSize);
        assertEquals(SettingData.ColorBlindMode.Default, settings.colorBlindMode);
        assertNotNull(settings.controlData);
    }

    @Test
    @DisplayName("ScreenSize enum 값들 확인")
    void testScreenSizeValues() {
        SettingData.ScreenSize[] sizes = SettingData.ScreenSize.values();
        
        assertEquals(3, sizes.length);
        assertEquals(SettingData.ScreenSize.SMALL, sizes[0]);
        assertEquals(SettingData.ScreenSize.MEDIUM, sizes[1]);
        assertEquals(SettingData.ScreenSize.LARGE, sizes[2]);
    }

    @Test
    @DisplayName("ColorBlindMode enum 값들 확인")
    void testColorBlindModeValues() {
        SettingData.ColorBlindMode[] modes = SettingData.ColorBlindMode.values();
        
        assertEquals(4, modes.length);
        assertEquals(SettingData.ColorBlindMode.Default, modes[0]);
        assertEquals(SettingData.ColorBlindMode.PROTANOPIA, modes[1]);
        assertEquals(SettingData.ColorBlindMode.DEUTERANOPIA, modes[2]);
        assertEquals(SettingData.ColorBlindMode.TRITANOPIA, modes[3]);
    }

    @Test
    @DisplayName("screenSize 변경 테스트")
    void testSetScreenSize() {
        SettingData settings = new SettingData();
        
        settings.screenSize = SettingData.ScreenSize.LARGE;
        assertEquals(SettingData.ScreenSize.LARGE, settings.screenSize);
        
        settings.screenSize = SettingData.ScreenSize.SMALL;
        assertEquals(SettingData.ScreenSize.SMALL, settings.screenSize);
    }

    @Test
    @DisplayName("colorBlindMode 변경 테스트")
    void testSetColorBlindMode() {
        SettingData settings = new SettingData();
        
        settings.colorBlindMode = SettingData.ColorBlindMode.PROTANOPIA;
        assertEquals(SettingData.ColorBlindMode.PROTANOPIA, settings.colorBlindMode);
        
        settings.colorBlindMode = SettingData.ColorBlindMode.DEUTERANOPIA;
        assertEquals(SettingData.ColorBlindMode.DEUTERANOPIA, settings.colorBlindMode);
        
        settings.colorBlindMode = SettingData.ColorBlindMode.TRITANOPIA;
        assertEquals(SettingData.ColorBlindMode.TRITANOPIA, settings.colorBlindMode);
    }

    @Test
    @DisplayName("ControlData가 null이 아님")
    void testControlDataNotNull() {
        SettingData settings = new SettingData();
        assertNotNull(settings.controlData);
    }

    @Test
    @DisplayName("Serializable 인터페이스 구현 확인")
    void testSerializable() {
        SettingData settings = new SettingData();
        assertTrue(settings instanceof java.io.Serializable);
    }
}
