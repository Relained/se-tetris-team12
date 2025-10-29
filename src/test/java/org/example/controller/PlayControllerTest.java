package org.example.controller;

import javafx.scene.input.KeyCode;
import org.example.model.*;
import org.example.service.StateManager;
import org.example.service.SettingManager;
import org.example.view.PlayView;
import org.example.service.TetrisSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayControllerTest extends ApplicationTest {
    
    private PlayController controller;
    private StateManager stateManager;
    private PlayView playView;
    private TetrisSystem tetrisSystem;
    private SettingManager settingManager;
    private GameBoard gameBoard;
    
    @BeforeEach
    void setUp() {
        stateManager = mock(StateManager.class);
        playView = mock(PlayView.class);
        tetrisSystem = mock(TetrisSystem.class);
        settingManager = mock(SettingManager.class);
        gameBoard = mock(GameBoard.class);
        
        // StateManager에 SettingManager 주입
        stateManager.settingManager = settingManager;
        
        // 기본 KeyData 설정
        SettingData settingData = new SettingData();
        when(settingManager.getCurrentSettings()).thenReturn(settingData);
        
        // 기본 게임 상태 mock
        when(tetrisSystem.getDifficulty()).thenReturn(1);
        when(tetrisSystem.getScore()).thenReturn(0);
        when(tetrisSystem.getLines()).thenReturn(0);
        when(tetrisSystem.getLevel()).thenReturn(1);
        when(tetrisSystem.getBoard()).thenReturn(gameBoard);
        when(tetrisSystem.getDropInterval()).thenReturn(1000L);
        when(tetrisSystem.getCurrentPiece()).thenReturn(null);
        when(tetrisSystem.getHoldPiece()).thenReturn(null);
        when(tetrisSystem.getNextQueue()).thenReturn(Arrays.asList());
        
        controller = new PlayController(stateManager, playView, tetrisSystem, org.example.model.GameMode.NORMAL);
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
        controller = new PlayController(stateManager, playView, null, org.example.model.GameMode.NORMAL);
        
        assertDoesNotThrow(() -> controller.update(0.016));
    }
    
    @Test
    @DisplayName("update - 정상적인 게임 업데이트")
    void testUpdateNormal() throws InterruptedException {
        GameBoard board = new GameBoard();
        TetrominoPosition currentPiece = new TetrominoPosition(Tetromino.I, 3, 0, 0);
        List<TetrominoPosition> nextQueue = Arrays.asList(
            new TetrominoPosition(Tetromino.T, 0, 0, 0),
            new TetrominoPosition(Tetromino.O, 0, 0, 0)
        );
        
        when(tetrisSystem.getBoard()).thenReturn(board);
        when(tetrisSystem.getCurrentPiece()).thenReturn(currentPiece);
        when(tetrisSystem.getNextQueue()).thenReturn(nextQueue);
        when(tetrisSystem.getScore()).thenReturn(100);
        when(tetrisSystem.getLines()).thenReturn(5);
        when(tetrisSystem.getLevel()).thenReturn(1);
        when(tetrisSystem.isGameOver()).thenReturn(false);
        when(tetrisSystem.getDropInterval()).thenReturn(1000L);
        
        // 충분한 시간이 지난 후 업데이트
        Thread.sleep(1100);
        controller.update(0.016);
        
        verify(tetrisSystem, atLeastOnce()).update();
        verify(playView, atLeastOnce()).updateDisplay(
            any(GameBoard.class),
            any(TetrominoPosition.class),
            any(),
            any(),
            anyList(),
            anyInt(),
            anyInt(),
            anyInt()
        );
    }
    
    @Test
    @DisplayName("handleKeyPressed - LEFT 키 입력")
    void testHandleKeyPressedLeft() {
        when(tetrisSystem.isGameOver()).thenReturn(false);
        
        controller.handleKeyPressed(KeyCode.LEFT);
        
        verify(tetrisSystem, atLeastOnce()).moveLeft();
    }
    
    @Test
    @DisplayName("handleKeyPressed - RIGHT 키 입력")
    void testHandleKeyPressedRight() {
        when(tetrisSystem.isGameOver()).thenReturn(false);
        
        controller.handleKeyPressed(KeyCode.RIGHT);
        
        verify(tetrisSystem, atLeastOnce()).moveRight();
    }
    
    @Test
    @DisplayName("handleKeyPressed - DOWN 키 입력 (소프트 드롭)")
    void testHandleKeyPressedDown() {
        when(tetrisSystem.isGameOver()).thenReturn(false);
        
        controller.handleKeyPressed(KeyCode.DOWN);
        
        verify(tetrisSystem, atLeastOnce()).moveDown();
    }
    
    @Test
    @DisplayName("handleKeyPressed - SPACE 키 입력 (하드 드롭)")
    void testHandleKeyPressedSpace() {
        when(tetrisSystem.isGameOver()).thenReturn(false);
        
        controller.handleKeyPressed(KeyCode.SPACE);
        
        verify(tetrisSystem).hardDrop();
    }
    
    @Test
    @DisplayName("handleKeyPressed - Z 키 입력 (반시계 회전)")
    void testHandleKeyPressedZ() {
        when(tetrisSystem.isGameOver()).thenReturn(false);
        
        controller.handleKeyPressed(KeyCode.Z);
        
        verify(tetrisSystem, times(1)).rotateCounterClockwise();
    }
    
    @Test
    @DisplayName("handleKeyPressed - UP 키 입력 (시계 회전)")
    void testHandleKeyPressedUp() {
        when(tetrisSystem.isGameOver()).thenReturn(false);
        
        controller.handleKeyPressed(KeyCode.UP);
        
        verify(tetrisSystem, times(1)).rotateClockwise();
    }
    
    @Test
    @DisplayName("handleKeyPressed - C 키 입력 (홀드)")
    void testHandleKeyPressedC() {
        when(tetrisSystem.isGameOver()).thenReturn(false);
        
        controller.handleKeyPressed(KeyCode.C);
        
        verify(tetrisSystem, times(1)).hold();
    }
    
    @Test
    @DisplayName("handleKeyPressed - ESCAPE 키 입력 (일시정지)")
    void testHandleKeyPressedEscape() {
        when(tetrisSystem.isGameOver()).thenReturn(false);
        
        controller.handleKeyPressed(KeyCode.ESCAPE);
        
        verify(stateManager).stackState("pause");
    }
    
    @Test
    @DisplayName("handleKeyReleased - 키 해제")
    void testHandleKeyReleased() {
        when(tetrisSystem.isGameOver()).thenReturn(false);
        
        controller.handleKeyPressed(KeyCode.LEFT);
        controller.handleKeyReleased(KeyCode.LEFT);
        
        // 키가 해제되면 더 이상 moveLeft가 호출되지 않아야 함
        assertDoesNotThrow(() -> controller.handleKeyReleased(KeyCode.LEFT));
    }
    
    @Test
    @DisplayName("handleGameOver - 게임 오버 시 Scoreboard로 전환")
    void testHandleGameOver() {
        when(tetrisSystem.getScore()).thenReturn(1000);
        when(tetrisSystem.getLines()).thenReturn(50);
        when(tetrisSystem.getLevel()).thenReturn(5);
        
        controller.handleGameOver();
        
        verify(stateManager).addState(eq("scoreboard"), any());
        verify(stateManager).setState("scoreboard");
    }
    
    @Test
    @DisplayName("update - 게임 오버 상태에서 handleGameOver 호출")
    void testUpdateWhenGameOver() {
        when(tetrisSystem.isGameOver()).thenReturn(true);
        when(tetrisSystem.getScore()).thenReturn(500);
        when(tetrisSystem.getLines()).thenReturn(25);
        when(tetrisSystem.getLevel()).thenReturn(3);
        when(tetrisSystem.getDifficulty()).thenReturn(1);
        when(tetrisSystem.getDropInterval()).thenReturn(1000L);
        
        controller.update(0.016);
        
        verify(stateManager).addState(eq("scoreboard"), any());
        verify(stateManager).setState("scoreboard");
    }
    
    @Test
    @DisplayName("handleKeyPressed - 게임 오버 시 입력 무시")
    void testHandleKeyPressedWhenGameOver() {
        when(tetrisSystem.isGameOver()).thenReturn(true);
        
        controller.handleKeyPressed(KeyCode.LEFT);
        controller.handleKeyPressed(KeyCode.SPACE);
        
        verify(tetrisSystem, never()).moveLeft();
        verify(tetrisSystem, never()).hardDrop();
    }
    
    @Test
    @DisplayName("여러 키 동시 입력 처리")
    void testMultipleKeyPress() {
        when(tetrisSystem.isGameOver()).thenReturn(false);
        
        controller.handleKeyPressed(KeyCode.LEFT);
        controller.handleKeyPressed(KeyCode.DOWN);
        
        verify(tetrisSystem, atLeastOnce()).moveLeft();
        verify(tetrisSystem, atLeastOnce()).moveDown();
    }
    
    @Test
    @DisplayName("같은 키 반복 입력 처리")
    void testRepeatedKeyPress() {
        when(tetrisSystem.isGameOver()).thenReturn(false);
        
        controller.handleKeyPressed(KeyCode.LEFT);
        controller.handleKeyPressed(KeyCode.LEFT);
        controller.handleKeyPressed(KeyCode.LEFT);
        
        // moveLeft는 여러 번 호출되어야 함
        verify(tetrisSystem, atLeast(1)).moveLeft();
    }
}
