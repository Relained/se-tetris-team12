package org.example.controller;

import javafx.application.Platform;
import org.example.model.SettingData.ScreenSize;
import org.example.service.SettingManager;
import org.example.service.StateManager;
import org.example.view.DisplaySettingView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DisplaySettingControllerTest {
    
    private DisplaySettingController controller;
    private StateManager stateManager;
    private DisplaySettingView displaySettingView;
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
        displaySettingView = mock(DisplaySettingView.class);
        controller = new DisplaySettingController(stateManager, displaySettingView);
    }
    
    @Test
    @DisplayName("컨트롤러 생성 시 null이 아닌지 확인")
    void testControllerNotNull() {
        assertNotNull(controller);
    }
    
    @Test
    @DisplayName("Small 사이즈 선택")
    void testHandleSmall() {
        controller.handleSmall();
        
        verify(displaySettingView).updateCurrentSize(ScreenSize.SMALL);
        assertEquals(ScreenSize.SMALL, controller.getSelectedSize());
    }
    
    @Test
    @DisplayName("Medium 사이즈 선택")
    void testHandleMedium() {
        controller.handleMedium();
        
        verify(displaySettingView).updateCurrentSize(ScreenSize.MEDIUM);
        assertEquals(ScreenSize.MEDIUM, controller.getSelectedSize());
    }
    
    @Test
    @DisplayName("Large 사이즈 선택")
    void testHandleLarge() {
        controller.handleLarge();
        
        verify(displaySettingView).updateCurrentSize(ScreenSize.LARGE);
        assertEquals(ScreenSize.LARGE, controller.getSelectedSize());
    }
    
    @Test
    @DisplayName("Go Back 핸들러 - 이전 상태로 복귀")
    void testHandleGoBack() {
        controller.handleGoBack();
        
        verify(stateManager).popState();
    }
    
    @Test
    @DisplayName("선택된 사이즈 가져오기")
    void testGetSelectedSize() {
        assertNotNull(controller.getSelectedSize());
    }
}
