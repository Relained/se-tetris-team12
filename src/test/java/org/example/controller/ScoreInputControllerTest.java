package org.example.controller;

import javafx.application.Platform;
import org.example.model.ScoreRecord;
import org.example.service.StateManager;
import org.example.state.ScoreboardState;
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
    private ScoreboardState scoreboardState;
    private ScoreRecord testRecord;
    
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
        scoreboardState = mock(ScoreboardState.class);
        testRecord = new ScoreRecord(1000, 10, 5, 1);
        
        // Mock getCurrentState to return scoreboardState
        when(stateManager.getCurrentState()).thenReturn(scoreboardState);
        
        controller = new ScoreInputController(stateManager, scoreInputView, testRecord);
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
        verify(scoreboardState).setScoreBoardScene(true);
    }
    
    @Test
    @DisplayName("Skip 핸들러 - 점수 저장 건너뛰기")
    void testHandleSkip() {
        controller.handleSkip();
        
        verify(scoreboardState).setScoreBoardScene(false);
    }
}
