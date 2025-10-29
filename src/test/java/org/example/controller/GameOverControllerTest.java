package org.example.controller;

import javafx.application.Platform;
import org.example.service.StateManager;
import org.example.view.GameOverView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameOverControllerTest {
    
    private GameOverController controller;
    private StateManager stateManager;
    private GameOverView gameOverView;
    
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
        gameOverView = mock(GameOverView.class);
        controller = new GameOverController(stateManager, gameOverView);
    }
    
    @Test
    @DisplayName("컨트롤러 생성 시 null이 아닌지 확인")
    void testControllerNotNull() {
        assertNotNull(controller);
    }
    
    @Test
    @DisplayName("점수 제출 여부와 함께 컨트롤러 생성")
    void testControllerWithScoreSubmitted() {
        GameOverController controllerWithScore = new GameOverController(stateManager, gameOverView);
        assertNotNull(controllerWithScore);
    }
    
    @Test
    @DisplayName("Play Again 핸들러 - play 상태로 전환")
    void testHandlePlayAgain() {
        controller.handlePlayAgain();
        
        verify(stateManager).setState("play");
    }
    
    @Test
    @DisplayName("View Scoreboard 핸들러 - scoreboard 상태 추가 및 전환")
    void testHandleViewScoreboard() {
        controller.handleViewScoreboard();
        
        verify(stateManager).stackState("scoreboard");
    }
    
    @Test
    @DisplayName("Main Menu 핸들러 - start 상태로 전환")
    void testHandleMainMenu() {
        controller.handleMainMenu();
        
        verify(stateManager).setState("start");
    }
}
