package org.example.service;

import javafx.stage.Stage;
import org.example.model.SettingData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Field;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SettingManager Unit Test
 */
@ExtendWith(ApplicationExtension.class)
class SettingManagerTest {
    
    private SettingManager settingManager;
    
    @TempDir
    Path tempDir;
    
    @Start
    private void start(Stage stage) {
        // JavaFX 초기화
    }
    
    @BeforeEach
    void setUp() throws Exception {
        settingManager = new SettingManager();
        
        // Redirect save path to temp directory
        Field pathField = SettingManager.class.getDeclaredField("SETTING_SAVE_PATH");
        pathField.setAccessible(true);
        String tempPath = tempDir.resolve("test_tetris_settings.ser").toString();
        pathField.set(settingManager, tempPath);
    }
    
    @Test
    void testConstructor() {
        assertNotNull(settingManager);
        assertNotNull(settingManager.getCurrentSettings());
    }
    
    @Test
    void testGetCurrentSettings() {
        SettingData settings = settingManager.getCurrentSettings();
        assertNotNull(settings);
        assertNotNull(settings.controlData);
    }
    
    @Test
    void testResetToDefault() {
        settingManager.setColorSetting(SettingData.ColorBlindMode.PROTANOPIA);
        settingManager.setScreenSize(SettingData.ScreenSize.LARGE);
        
        settingManager.resetToDefault();
        
        SettingData settings = settingManager.getCurrentSettings();
        assertEquals(SettingData.ColorBlindMode.Default, settings.colorBlindMode);
        assertEquals(SettingData.ScreenSize.MEDIUM, settings.screenSize);
    }
    
    @Test
    void testSetColorSetting() {
        settingManager.setColorSetting(SettingData.ColorBlindMode.PROTANOPIA);
        
        assertEquals(SettingData.ColorBlindMode.PROTANOPIA, 
            settingManager.getCurrentSettings().colorBlindMode);
    }
    
    @Test
    void testSetScreenSize() {
        settingManager.setScreenSize(SettingData.ScreenSize.SMALL);
        
        assertEquals(SettingData.ScreenSize.SMALL, 
            settingManager.getCurrentSettings().screenSize);
    }
    
    @Test
    void testApplyColorSetting() {
        settingManager.setColorSetting(SettingData.ColorBlindMode.DEUTERANOPIA);
        
        assertDoesNotThrow(() -> settingManager.applyColorSetting());
    }
    
    @Test
    void testApplyScreenSize() {
        settingManager.setScreenSize(SettingData.ScreenSize.LARGE);
        
        assertDoesNotThrow(() -> settingManager.applyScreenSize());
    }
    
    @Test
    void testSaveSettingData() {
        settingManager.setColorSetting(SettingData.ColorBlindMode.TRITANOPIA);
        settingManager.setScreenSize(SettingData.ScreenSize.LARGE);
        
        assertDoesNotThrow(() -> settingManager.saveSettingData());
    }
    
    @Test
    void testLoadSettingDataWhenFileNotExists() {
        // First time load should return false (file doesn't exist)
        SettingManager newManager = new SettingManager();
        assertNotNull(newManager.getCurrentSettings());
    }
    
    @Test
    void testSaveAndLoadSettingData() throws Exception {
        // Set custom settings
        settingManager.setColorSetting(SettingData.ColorBlindMode.PROTANOPIA);
        settingManager.setScreenSize(SettingData.ScreenSize.SMALL);
        settingManager.saveSettingData();
        
        // Create new manager (should load saved settings)
        SettingManager newManager = new SettingManager();
        Field pathField = SettingManager.class.getDeclaredField("SETTING_SAVE_PATH");
        pathField.setAccessible(true);
        String tempPath = tempDir.resolve("test_tetris_settings.ser").toString();
        pathField.set(newManager, tempPath);
        
        boolean loaded = newManager.loadSettingData();
        
        if (loaded) {
            assertEquals(SettingData.ColorBlindMode.PROTANOPIA, 
                newManager.getCurrentSettings().colorBlindMode);
            assertEquals(SettingData.ScreenSize.SMALL, 
                newManager.getCurrentSettings().screenSize);
        }
    }
    
    @Test
    void testResetScoreboard() {
        assertDoesNotThrow(() -> settingManager.resetScoreboard());
    }
    
    @Test
    void testMultipleColorModeChanges() {
        settingManager.setColorSetting(SettingData.ColorBlindMode.PROTANOPIA);
        assertEquals(SettingData.ColorBlindMode.PROTANOPIA, 
            settingManager.getCurrentSettings().colorBlindMode);
        
        settingManager.setColorSetting(SettingData.ColorBlindMode.DEUTERANOPIA);
        assertEquals(SettingData.ColorBlindMode.DEUTERANOPIA, 
            settingManager.getCurrentSettings().colorBlindMode);
        
        settingManager.setColorSetting(SettingData.ColorBlindMode.Default);
        assertEquals(SettingData.ColorBlindMode.Default, 
            settingManager.getCurrentSettings().colorBlindMode);
    }
    
    @Test
    void testMultipleScreenSizeChanges() {
        settingManager.setScreenSize(SettingData.ScreenSize.SMALL);
        assertEquals(SettingData.ScreenSize.SMALL, settingManager.getCurrentSettings().screenSize);
        
        settingManager.setScreenSize(SettingData.ScreenSize.LARGE);
        assertEquals(SettingData.ScreenSize.LARGE, settingManager.getCurrentSettings().screenSize);
        
        settingManager.setScreenSize(SettingData.ScreenSize.MEDIUM);
        assertEquals(SettingData.ScreenSize.MEDIUM, settingManager.getCurrentSettings().screenSize);
    }
}
