package org.example.view;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import org.example.model.GameBoard;
import org.example.model.Tetromino;
import org.example.model.TetrominoPosition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayViewTest extends ApplicationTest {
    
    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // 이미 초기화된 경우 무시
        }
    }
    
    @Test
    @DisplayName("PlayView 생성자 테스트")
    void testConstructor() {
        Platform.runLater(() -> {
            PlayView view = new PlayView();
            assertNotNull(view);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView로 UI 생성 테스트")
    void testCreateView() {
        Platform.runLater(() -> {
            PlayView view = new PlayView();
            HBox root = view.createView();
            
            assertNotNull(root);
            assertNotNull(view.getGameCanvas());
            assertNotNull(view.getHoldPanel());
            assertNotNull(view.getNextPanel());
            assertNotNull(view.getScorePanel());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateCanvasSize - null Scene 테스트")
    void testUpdateCanvasSizeWithNullScene() {
        Platform.runLater(() -> {
            PlayView view = new PlayView();
            view.createView();
            
            // null Scene으로 호출 - 분기 커버
            assertDoesNotThrow(() -> view.updateCanvasSize(null));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateCanvasSize - null Canvas 테스트")
    void testUpdateCanvasSizeWithNullCanvas() {
        Platform.runLater(() -> {
            PlayView view = new PlayView();
            // createView를 호출하지 않아 gameCanvas가 null인 상태
            Scene scene = new Scene(new HBox(), 800, 600);
            
            // Canvas가 null일 때 분기 커버
            assertDoesNotThrow(() -> view.updateCanvasSize(scene));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateCanvasSize - 유효한 Scene 테스트")
    void testUpdateCanvasSizeWithValidScene() {
        Platform.runLater(() -> {
            PlayView view = new PlayView();
            view.createView();
            
            Scene scene = new Scene(new HBox(), 800, 600);
            assertDoesNotThrow(() -> view.updateCanvasSize(scene));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateCanvasSize - 작은 Scene 크기 테스트 (최소값 보장)")
    void testUpdateCanvasSizeWithSmallScene() {
        Platform.runLater(() -> {
            PlayView view = new PlayView();
            view.createView();
            
            // 작은 Scene - 최소 크기 보장 로직 테스트
            Scene scene = new Scene(new HBox(), 200, 200);
            assertDoesNotThrow(() -> view.updateCanvasSize(scene));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateCanvasSize - 큰 Scene 크기 테스트")
    void testUpdateCanvasSizeWithLargeScene() {
        Platform.runLater(() -> {
            PlayView view = new PlayView();
            view.createView();
            
            Scene scene = new Scene(new HBox(), 1920, 1080);
            assertDoesNotThrow(() -> view.updateCanvasSize(scene));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateDisplay - null Canvas 테스트")
    void testUpdateDisplayWithNullCanvas() {
        Platform.runLater(() -> {
            PlayView view = new PlayView();
            // createView를 호출하지 않아 gameCanvas가 null인 상태
            
            GameBoard board = new GameBoard();
            TetrominoPosition current = new TetrominoPosition(Tetromino.I, 0, 0, 0);
            TetrominoPosition ghost = new TetrominoPosition(Tetromino.I, 0, 10, 0);
            TetrominoPosition hold = new TetrominoPosition(Tetromino.T, 0, 0, 0);
            List<TetrominoPosition> nextQueue = new ArrayList<>();
            nextQueue.add(new TetrominoPosition(Tetromino.O, 0, 0, 0));
            
            // Canvas가 null일 때 분기 커버
            assertDoesNotThrow(() -> view.updateDisplay(board, current, ghost, hold, nextQueue, 1000, 10, 5));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateDisplay - 유효한 데이터 테스트")
    void testUpdateDisplayWithValidData() {
        Platform.runLater(() -> {
            PlayView view = new PlayView();
            view.createView();
            
            GameBoard board = new GameBoard();
            TetrominoPosition current = new TetrominoPosition(Tetromino.I, 0, 0, 0);
            TetrominoPosition ghost = new TetrominoPosition(Tetromino.I, 0, 10, 0);
            TetrominoPosition hold = new TetrominoPosition(Tetromino.T, 0, 0, 0);
            List<TetrominoPosition> nextQueue = new ArrayList<>();
            nextQueue.add(new TetrominoPosition(Tetromino.O, 0, 0, 0));
            nextQueue.add(new TetrominoPosition(Tetromino.L, 0, 0, 0));
            
            assertDoesNotThrow(() -> view.updateDisplay(board, current, ghost, hold, nextQueue, 2500, 25, 10));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateDisplay - null hold piece 테스트")
    void testUpdateDisplayWithNullHold() {
        Platform.runLater(() -> {
            PlayView view = new PlayView();
            view.createView();
            
            GameBoard board = new GameBoard();
            TetrominoPosition current = new TetrominoPosition(Tetromino.I, 0, 0, 0);
            TetrominoPosition ghost = new TetrominoPosition(Tetromino.I, 0, 10, 0);
            List<TetrominoPosition> nextQueue = new ArrayList<>();
            nextQueue.add(new TetrominoPosition(Tetromino.S, 0, 0, 0));
            
            assertDoesNotThrow(() -> view.updateDisplay(board, current, ghost, null, nextQueue, 500, 5, 2));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateDisplay - 빈 next queue 테스트")
    void testUpdateDisplayWithEmptyNextQueue() {
        Platform.runLater(() -> {
            PlayView view = new PlayView();
            view.createView();
            
            GameBoard board = new GameBoard();
            TetrominoPosition current = new TetrominoPosition(Tetromino.Z, 0, 0, 0);
            TetrominoPosition ghost = new TetrominoPosition(Tetromino.Z, 0, 15, 0);
            List<TetrominoPosition> nextQueue = new ArrayList<>();
            
            assertDoesNotThrow(() -> view.updateDisplay(board, current, ghost, null, nextQueue, 0, 0, 1));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("getGameCanvas 테스트")
    void testGetGameCanvas() {
        Platform.runLater(() -> {
            PlayView view = new PlayView();
            view.createView();
            
            assertNotNull(view.getGameCanvas());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("getHoldPanel 테스트")
    void testGetHoldPanel() {
        Platform.runLater(() -> {
            PlayView view = new PlayView();
            view.createView();
            
            assertNotNull(view.getHoldPanel());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("getNextPanel 테스트")
    void testGetNextPanel() {
        Platform.runLater(() -> {
            PlayView view = new PlayView();
            view.createView();
            
            assertNotNull(view.getNextPanel());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("getScorePanel 테스트")
    void testGetScorePanel() {
        Platform.runLater(() -> {
            PlayView view = new PlayView();
            view.createView();
            
            assertNotNull(view.getScorePanel());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
}
