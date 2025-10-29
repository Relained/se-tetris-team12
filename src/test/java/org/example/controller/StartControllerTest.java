package org.example.controller;

import javafx.application.Platform;
import org.example.service.StateManager;
import org.example.view.StartView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StartControllerTest {
    
    private StartController controller;
    private StateManager stateManager;
    private StartView startView;
    
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
        startView = mock(StartView.class);
        controller = new StartController(stateManager, startView);
    }
    
    @Test
    @DisplayName("Start Game 핸들러 - play 상태로 전환")
    void testHandleStartGame() {
        controller.handleStartGame();
        
        verify(stateManager).setState("play");
    }
    
    @Test
    @DisplayName("View Scoreboard 핸들러 - scoreboard 상태 추가 및 전환")
    void testHandleViewScoreboard() {
        controller.handleViewScoreboard();
        
        verify(stateManager).addState(eq("scoreboard"), any());
        verify(stateManager).setState("scoreboard");
    }
    
    @Test
    @DisplayName("Setting 핸들러 - setting 상태 스택")
    void testHandleSetting() {
        controller.handleSetting();
        
        verify(stateManager).stackState("setting");
    }
    
    @Test
    @DisplayName("컨트롤러 생성 시 null이 아닌지 확인")
    void testControllerNotNull() {
        assertNotNull(controller);
    }
}
