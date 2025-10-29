package org.example.controller;

import javafx.application.Platform;
import org.example.service.KeySettingManager;
import org.example.service.SettingManager;
import org.example.service.StateManager;
import org.example.view.KeySettingView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KeySettingControllerTest {
    
    private KeySettingController controller;
    private StateManager stateManager;
    private KeySettingView keySettingView;
    private KeySettingManager keySettingManager;
    
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
        SettingManager settingManager = new SettingManager();
        stateManager.settingManager = settingManager;
        keySettingManager = KeySettingManager.getInstance();
        keySettingManager.setSettingManager(settingManager);
        keySettingView = mock(KeySettingView.class);
        controller = new KeySettingController(stateManager, keySettingView);
    }
    
    @Test
    @DisplayName("컨트롤러 생성 시 null이 아닌지 확인")
    void testControllerNotNull() {
        assertNotNull(controller);
    }
    
    @Test
    @DisplayName("초기 상태는 키 입력 대기 중이 아님")
    void testInitialState() {
        assertFalse(controller.isWaitingForKey());
        assertNull(controller.getWaitingForKeyAction());
    }
    
    @Test
    @DisplayName("Reset to Default 핸들러")
    void testHandleResetToDefault() {
        controller.handleResetToDefault();
        
        verify(keySettingView).updateAllKeyBindings();
    }
    
    @Test
    @DisplayName("Go Back 핸들러 - 이전 상태로 복귀")
    void testHandleGoBack() {
        controller.handleGoBack();
        
        verify(stateManager).popState();
    }
}
