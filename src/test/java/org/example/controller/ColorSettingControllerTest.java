package org.example.controller;

import javafx.application.Platform;
import org.example.model.SettingData.ColorBlindMode;
import org.example.service.SettingManager;
import org.example.service.StateManager;
import org.example.view.ColorSettingView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ColorSettingControllerTest {
    
    private ColorSettingController controller;
    private StateManager stateManager;
    private ColorSettingView colorSettingView;
    private SettingManager settingManager;
    
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
        settingManager = new SettingManager();
        stateManager.settingManager = settingManager;
        colorSettingView = mock(ColorSettingView.class);
        controller = new ColorSettingController(stateManager, colorSettingView);
    }
    
    @Test
    @DisplayName("컨트롤러 생성 시 null이 아닌지 확인")
    void testControllerNotNull() {
        assertNotNull(controller);
    }
    
    @Test
    @DisplayName("Default 모드 선택")
    void testHandleDefault() {
        controller.handleDefault();
        
        verify(colorSettingView).updateCurrentMode(ColorBlindMode.Default);
        assertEquals(ColorBlindMode.Default, controller.getSelectedMode());
    }
    
    @Test
    @DisplayName("Protanopia 모드 선택")
    void testHandleProtanopia() {
        controller.handleProtanopia();
        
        verify(colorSettingView).updateCurrentMode(ColorBlindMode.PROTANOPIA);
        assertEquals(ColorBlindMode.PROTANOPIA, controller.getSelectedMode());
    }
    
    @Test
    @DisplayName("Deuteranopia 모드 선택")
    void testHandleDeuteranopia() {
        controller.handleDeuteranopia();
        
        verify(colorSettingView).updateCurrentMode(ColorBlindMode.DEUTERANOPIA);
        assertEquals(ColorBlindMode.DEUTERANOPIA, controller.getSelectedMode());
    }
    
    @Test
    @DisplayName("Tritanopia 모드 선택")
    void testHandleTritanopia() {
        controller.handleTritanopia();
        
        verify(colorSettingView).updateCurrentMode(ColorBlindMode.TRITANOPIA);
        assertEquals(ColorBlindMode.TRITANOPIA, controller.getSelectedMode());
    }
    
    @Test
    @DisplayName("Go Back 핸들러 - 이전 상태로 복귀")
    void testHandleGoBack() {
        controller.handleGoBack();
        
        verify(stateManager).popState();
    }
    
    @Test
    @DisplayName("선택된 모드 가져오기")
    void testGetSelectedMode() {
        assertNotNull(controller.getSelectedMode());
    }
}
