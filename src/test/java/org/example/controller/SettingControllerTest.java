package org.example.controller;

import javafx.application.Platform;
import org.example.service.SettingManager;
import org.example.service.StateManager;
import org.example.view.SettingView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SettingControllerTest {
    
    private SettingController controller;
    private StateManager stateManager;
    private SettingView settingView;
    
    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // 이미 초기화된 경우 무시
        }
    }
    
    @BeforeEach
    void setUp() {
        stateManager = mock(StateManager.class);
        stateManager.settingManager = mock(SettingManager.class);
        settingView = mock(SettingView.class);
        controller = new SettingController(stateManager, settingView);
    }
    
    @Test
    @DisplayName("Screen Size 핸들러 - display_setting 상태 스택")
    void testHandleScreenSize() {
        controller.handleScreenSize();
        
        verify(stateManager).stackState("display_setting");
    }
    
    @Test
    @DisplayName("Controls 핸들러 - key_setting 상태 스택")
    void testHandleControls() {
        controller.handleControls();
        
        verify(stateManager).stackState("key_setting");
    }
    
    @Test
    @DisplayName("Color Blind Setting 핸들러 - color_setting 상태 스택")
    void testHandleColorBlindSetting() {
        controller.handleColorBlindSetting();
        
        verify(stateManager).stackState("color_setting");
    }
    
    @Test
    @DisplayName("Reset Score Board 핸들러")
    void testHandleResetScoreBoard() {
        controller.handleResetScoreBoard();
        
        verify(stateManager.settingManager).resetScoreboard();
    }
    
    @Test
    @DisplayName("Reset All Setting 핸들러")
    void testHandleResetAllSetting() {
        controller.handleResetAllSetting();
        
        verify(stateManager.settingManager).resetToDefault();
        verify(stateManager.settingManager).applyColorSetting();
    }
    
    @Test
    @DisplayName("Go Back 핸들러 - 설정 저장 및 이전 상태로 복귀")
    void testHandleGoBack() {
        controller.handleGoBack();
        
        verify(stateManager.settingManager).applyColorSetting();
        verify(stateManager.settingManager).saveSettingData();
        verify(stateManager).popState();
    }
    
    @Test
    @DisplayName("컨트롤러 생성 시 null이 아닌지 확인")
    void testControllerNotNull() {
        assertNotNull(controller);
    }
}
