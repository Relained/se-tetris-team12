package org.example.service;

import org.example.model.SettingData;
import org.example.model.SettingData.ColorBlindMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SettingManagerTest {
    private SettingManager settingManager;

    @BeforeEach
    void setUp() {
        settingManager = new SettingManager();
    }

    @Test
    @DisplayName("현재 설정 가져오기")
    void testGetCurrentSettings() {
        SettingData settings = settingManager.getCurrentSettings();
        assertNotNull(settings);
    }

    @Test
    @DisplayName("기본값으로 리셋")
    void testResetToDefault() {
        settingManager.setColorSetting(ColorBlindMode.PROTANOPIA);
        settingManager.resetToDefault();
        
        SettingData settings = settingManager.getCurrentSettings();
        assertEquals(ColorBlindMode.Default, settings.colorBlindMode);
    }

    @Test
    @DisplayName("색상 설정 변경")
    void testSetColorSetting() {
        settingManager.setColorSetting(ColorBlindMode.DEUTERANOPIA);
        
        SettingData settings = settingManager.getCurrentSettings();
        assertEquals(ColorBlindMode.DEUTERANOPIA, settings.colorBlindMode);
    }

    @Test
    @DisplayName("색상 설정 적용")
    void testApplyColorSetting() {
        settingManager.setColorSetting(ColorBlindMode.TRITANOPIA);
        settingManager.applyColorSetting();
        
        ColorManager colorManager = ColorManager.getInstance();
        assertEquals(ColorBlindMode.TRITANOPIA, colorManager.getCurrentMode());
    }

    @Test
    @DisplayName("설정 저장 테스트")
    void testSaveSettingData() {
        settingManager.setColorSetting(ColorBlindMode.PROTANOPIA);
        
        // 예외가 발생하지 않아야 함
        assertDoesNotThrow(() -> settingManager.saveSettingData());
    }

    @Test
    @DisplayName("설정 로드 테스트")
    void testLoadSettingData() {
        // 먼저 설정 저장
        settingManager.setColorSetting(ColorBlindMode.DEUTERANOPIA);
        settingManager.saveSettingData();
        
        // 새로운 SettingManager 생성 (저장된 설정을 로드)
        SettingManager newManager = new SettingManager();
        
        // 로드된 설정이 저장된 설정과 같은지 확인
        assertEquals(ColorBlindMode.DEUTERANOPIA, newManager.getCurrentSettings().colorBlindMode);
    }

    @Test
    @DisplayName("설정 파일이 없을 때 기본 설정 사용")
    void testLoadSettingData_FileNotExists() {
        // setting.ser 파일을 삭제 (존재한다면)
        File settingFile = new File("setting.ser");
        if (settingFile.exists()) {
            settingFile.delete();
        }
        
        // 새로운 SettingManager 생성
        SettingManager newManager = new SettingManager();
        
        // 기본 설정이어야 함
        assertNotNull(newManager.getCurrentSettings());
    }

    @Test
    @DisplayName("모든 색상 블라인드 모드 설정 가능")
    void testSetAllColorBlindModes() {
        for (ColorBlindMode mode : ColorBlindMode.values()) {
            settingManager.setColorSetting(mode);
            assertEquals(mode, settingManager.getCurrentSettings().colorBlindMode);
        }
    }

    @Test
    @DisplayName("설정 저장 및 로드 일관성")
    void testSaveAndLoadConsistency() {
        // 여러 설정 변경
        settingManager.setColorSetting(ColorBlindMode.TRITANOPIA);
        settingManager.saveSettingData();
        
        // 로드
        boolean loaded = settingManager.loadSettingData();
        
        assertTrue(loaded);
        assertEquals(ColorBlindMode.TRITANOPIA, settingManager.getCurrentSettings().colorBlindMode);
    }
}
