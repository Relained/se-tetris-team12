package org.example.controller;

import javafx.application.Platform;
import org.example.service.StateManager;
import org.example.view.ScoreboardView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScoreboardControllerTest {
    
    private ScoreboardController controller;
    private StateManager stateManager;
    private ScoreboardView scoreboardView;
    
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
        stateManager.settingManager = mock(org.example.service.SettingManager.class);
        scoreboardView = mock(ScoreboardView.class);
        controller = new ScoreboardController(stateManager, scoreboardView, false);
    }
    
    @Test
    @DisplayName("컨트롤러 생성 시 null이 아닌지 확인")
    void testControllerNotNull() {
        assertNotNull(controller);
    }
    
    @Test
    @DisplayName("게임 플레이 후 컨트롤러 생성")
    void testControllerAfterGamePlay() {
        ScoreboardController afterGame = new ScoreboardController(
            stateManager, scoreboardView, true);
        assertNotNull(afterGame);
    }
    
    @Test
    @DisplayName("Go Back 핸들러 - 일반 조회 시")
    void testHandleGoBackNormal() {
        controller.handleGoBack();
        
        verify(stateManager).popState();
    }
    
    @Test
    @DisplayName("Go Back 핸들러 - 게임 플레이 후")
    void testHandleGoBackAfterGame() {
        ScoreboardController afterGame = new ScoreboardController(
            stateManager, scoreboardView, true);
        
        afterGame.handleGoBack();
        
        verify(stateManager).setState("gameover");
    }
    
    @Test
    @DisplayName("Clear Scores 핸들러")
    void testHandleClearScores() {
        controller.handleClearScores();
        
        verify(stateManager.settingManager).resetScoreboard();
        verify(scoreboardView).refresh(any());
    }
    
    @Test
    @DisplayName("스코어보드 새로고침")
    void testRefreshScoreboard() {
        controller.refreshScoreboard();
        
        verify(scoreboardView).refresh(any());
    }
}
