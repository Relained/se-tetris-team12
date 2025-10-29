package org.example.controller;

import javafx.application.Platform;
import org.example.service.StateManager;
import org.example.view.PauseView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PauseControllerTest {
    
    private PauseController controller;
    private StateManager stateManager;
    private PauseView pauseView;
    
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
        pauseView = mock(PauseView.class);
        controller = new PauseController(stateManager, pauseView);
    }
    
    @Test
    @DisplayName("Resume 핸들러 - 이전 상태로 복귀")
    void testHandleResume() {
        controller.handleResume();
        
        verify(stateManager).popState();
    }
    
    @Test
    @DisplayName("Restart 핸들러 - play 상태로 전환")
    void testHandleRestart() {
        controller.handleRestart();
        
        verify(stateManager).setState("play");
    }
    
    @Test
    @DisplayName("Settings 핸들러 - setting 상태 스택")
    void testHandleSettings() {
        controller.handleSettings();
        
        verify(stateManager).stackState("setting");
    }
    
    @Test
    @DisplayName("Main Menu 핸들러 - start 상태로 전환")
    void testHandleMainMenu() {
        controller.handleMainMenu();
        
        verify(stateManager).setState("start");
    }
    
    @Test
    @DisplayName("컨트롤러 생성 시 null이 아닌지 확인")
    void testControllerNotNull() {
        assertNotNull(controller);
    }
}
