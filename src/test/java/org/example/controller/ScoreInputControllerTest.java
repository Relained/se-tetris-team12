package org.example.controller;

import javafx.application.Platform;
import org.example.service.StateManager;
import org.example.view.ScoreInputView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScoreInputControllerTest {
    
    private ScoreInputController controller;
    private StateManager stateManager;
    private ScoreInputView scoreInputView;
    private int testScore = 1000;
    private int testLines = 10;
    private int testLevel = 5;
    
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
        scoreInputView = mock(ScoreInputView.class);
        controller = new ScoreInputController(stateManager, scoreInputView, testScore, testLines, testLevel);
    }
    
    @Test
    @DisplayName("컨트롤러 생성 시 null이 아닌지 확인")
    void testControllerNotNull() {
        assertNotNull(controller);
    }
    
    @Test
    @DisplayName("Rank 정보 가져오기")
    void testGetRank() {
        int rank = controller.getRank();
        assertTrue(rank > 0);
    }
    
    @Test
    @DisplayName("유효한 이름으로 Submit 처리")
    void testHandleSubmitWithValidName() {
        String playerName = "TestPlayer";
        when(scoreInputView.getPlayerName()).thenReturn(playerName);
        
        controller.handleSubmit();
        
        verify(scoreInputView).getPlayerName();
        verify(stateManager).addState(eq("scoreboardAfterSubmit"), any());
        verify(stateManager).setState("scoreboardAfterSubmit");
    }
    
    @Test
    @DisplayName("Skip 핸들러 - 점수 저장 건너뛰기")
    void testHandleSkip() {
        controller.handleSkip();
        
        verify(stateManager).addState(eq("scoreboardAfterSkip"), any());
        verify(stateManager).setState("scoreboardAfterSkip");
    }
}
