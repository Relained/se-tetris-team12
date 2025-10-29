package org.example.controller;

import javafx.application.Platform;
import org.example.service.StateManager;
import org.example.view.PlayView;
import org.example.service.TetrisSystem;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayControllerTest {
    
    private PlayController controller;
    private StateManager stateManager;
    private PlayView playView;
    private TetrisSystem tetrisSystem;
    
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
        playView = mock(PlayView.class);
        tetrisSystem = mock(TetrisSystem.class);
        controller = new PlayController(stateManager, playView, tetrisSystem);
    }
    
    @Test
    @DisplayName("Pause 핸들러 - pause 상태 스택")
    void testHandlePause() {
        controller.handlePause();
        
        verify(stateManager).stackState("pause");
    }
    
    @Test
    @DisplayName("게임 로직 반환")
    void testGetGameLogic() {
        TetrisSystem result = controller.getGameLogic();
        
        assertSame(tetrisSystem, result);
    }
    
    @Test
    @DisplayName("lastDropTime 리셋")
    void testResetLastDropTime() {
        assertDoesNotThrow(() -> controller.resetLastDropTime());
    }
    
    @Test
    @DisplayName("컨트롤러 생성 시 null이 아닌지 확인")
    void testControllerNotNull() {
        assertNotNull(controller);
    }
    
    @Test
    @DisplayName("업데이트 호출 - TetrisSystem이 null일 때")
    void testUpdateWithNullSystem() {
        controller = new PlayController(stateManager, playView, null);
        
        assertDoesNotThrow(() -> controller.update(0.016));
    }
}
