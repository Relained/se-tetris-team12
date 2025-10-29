package org.example.view.component.play;
import org.testfx.util.WaitForAsyncUtils;

import javafx.application.Platform;
import org.example.model.Tetromino;
import org.example.model.TetrominoPosition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.testfx.framework.junit5.ApplicationTest;


import static org.junit.jupiter.api.Assertions.*;

class HoldPanelTest extends ApplicationTest {
    
    @Test
    @DisplayName("HoldPanel 생성 시 초기화됨")
    void testHoldPanelCreation() {
        Platform.runLater(() -> {
            HoldPanel panel = new HoldPanel();
            
            assertNotNull(panel);
            assertFalse(panel.getChildren().isEmpty());
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("홀드 피스 업데이트 시 그리기 작업 수행")
    void testUpdateHoldPiece() {
        Platform.runLater(() -> {
            HoldPanel panel = new HoldPanel();
            TetrominoPosition holdPiece = new TetrominoPosition(Tetromino.T, 0, 0, 0);
            
            assertDoesNotThrow(() -> panel.updateHoldPiece(holdPiece));
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("null 홀드 피스로 업데이트 시 예외 없이 처리")
    void testUpdateHoldPieceWithNull() {
        Platform.runLater(() -> {
            HoldPanel panel = new HoldPanel();
            
            assertDoesNotThrow(() -> panel.updateHoldPiece(null));
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("다양한 테트로미노 타입으로 홀드 피스 업데이트")
    void testUpdateHoldPieceWithVariousTypes() {
        Platform.runLater(() -> {
            HoldPanel panel = new HoldPanel();
            
            for (Tetromino type : Tetromino.values()) {
                TetrominoPosition piece = new TetrominoPosition(type, 0, 0, 0);
                assertDoesNotThrow(() -> panel.updateHoldPiece(piece));
            }
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("반복적인 홀드 피스 업데이트 테스트")
    void testMultipleHoldPieceUpdates() {
        Platform.runLater(() -> {
            HoldPanel panel = new HoldPanel();
            
            TetrominoPosition piece1 = new TetrominoPosition(Tetromino.I, 0, 0, 0);
            TetrominoPosition piece2 = new TetrominoPosition(Tetromino.O, 0, 0, 0);
            
            assertDoesNotThrow(() -> {
                panel.updateHoldPiece(piece1);
                panel.updateHoldPiece(null);
                panel.updateHoldPiece(piece2);
            });
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
}
