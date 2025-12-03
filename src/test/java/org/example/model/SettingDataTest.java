package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SettingData Unit Test
 */
class SettingDataTest {
    
    private SettingData settingData;
    
    @BeforeEach
    void setUp() {
        settingData = new SettingData();
    }
    
    @Test
    void testDefaultScreenSize() {
        assertEquals(SettingData.ScreenSize.MEDIUM, settingData.screenSize);
    }
    
    @Test
    void testDefaultColorBlindMode() {
        assertEquals(SettingData.ColorBlindMode.Default, settingData.colorBlindMode);
    }
    
    @Test
    void testDefaultControlData() {
        assertNotNull(settingData.controlData);
    }
    
    @Test
    void testSetScreenSize() {
        settingData.screenSize = SettingData.ScreenSize.LARGE;
        assertEquals(SettingData.ScreenSize.LARGE, settingData.screenSize);
    }
    
    @Test
    void testSetColorBlindMode() {
        settingData.colorBlindMode = SettingData.ColorBlindMode.PROTANOPIA;
        assertEquals(SettingData.ColorBlindMode.PROTANOPIA, settingData.colorBlindMode);
    }
    
    @Test
    void testSetControlData() {
        KeyData newKeyData = new KeyData();
        settingData.controlData = newKeyData;
        assertSame(newKeyData, settingData.controlData);
    }
    
    @Test
    void testScreenSizeEnum() {
        SettingData.ScreenSize[] sizes = SettingData.ScreenSize.values();
        assertEquals(3, sizes.length);
        assertEquals(SettingData.ScreenSize.SMALL, sizes[0]);
        assertEquals(SettingData.ScreenSize.MEDIUM, sizes[1]);
        assertEquals(SettingData.ScreenSize.LARGE, sizes[2]);
    }
    
    @Test
    void testColorBlindModeEnum() {
        SettingData.ColorBlindMode[] modes = SettingData.ColorBlindMode.values();
        assertEquals(4, modes.length);
        assertEquals(SettingData.ColorBlindMode.Default, modes[0]);
        assertEquals(SettingData.ColorBlindMode.PROTANOPIA, modes[1]);
        assertEquals(SettingData.ColorBlindMode.DEUTERANOPIA, modes[2]);
        assertEquals(SettingData.ColorBlindMode.TRITANOPIA, modes[3]);
    }
    
    @Test
    void testSerializable() {
        assertTrue(settingData instanceof java.io.Serializable);
    }
    
    @Test
    void testMultipleInstances() {
        SettingData data1 = new SettingData();
        SettingData data2 = new SettingData();
        
        data1.screenSize = SettingData.ScreenSize.SMALL;
        data2.screenSize = SettingData.ScreenSize.LARGE;
        
        assertNotEquals(data1.screenSize, data2.screenSize);
    }
    
    @Test
    void testAllScreenSizes() {
        settingData.screenSize = SettingData.ScreenSize.SMALL;
        assertEquals(SettingData.ScreenSize.SMALL, settingData.screenSize);
        
        settingData.screenSize = SettingData.ScreenSize.MEDIUM;
        assertEquals(SettingData.ScreenSize.MEDIUM, settingData.screenSize);
        
        settingData.screenSize = SettingData.ScreenSize.LARGE;
        assertEquals(SettingData.ScreenSize.LARGE, settingData.screenSize);
    }
    
    @Test
    void testAllColorBlindModes() {
        settingData.colorBlindMode = SettingData.ColorBlindMode.Default;
        assertEquals(SettingData.ColorBlindMode.Default, settingData.colorBlindMode);
        
        settingData.colorBlindMode = SettingData.ColorBlindMode.PROTANOPIA;
        assertEquals(SettingData.ColorBlindMode.PROTANOPIA, settingData.colorBlindMode);
        
        settingData.colorBlindMode = SettingData.ColorBlindMode.DEUTERANOPIA;
        assertEquals(SettingData.ColorBlindMode.DEUTERANOPIA, settingData.colorBlindMode);
        
        settingData.colorBlindMode = SettingData.ColorBlindMode.TRITANOPIA;
        assertEquals(SettingData.ColorBlindMode.TRITANOPIA, settingData.colorBlindMode);
    }
}
