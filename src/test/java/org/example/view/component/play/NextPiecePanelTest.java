package org.example.view.component.play;
import org.testfx.util.WaitForAsyncUtils;

import javafx.application.Platform;
import org.example.model.Tetromino;
import org.example.model.TetrominoPosition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class NextPiecePanelTest extends ApplicationTest {
    
    @Test
    @DisplayName("NextPiecePanel 생성 시 초기화됨")
    void testNextPiecePanelCreation() {
        Platform.runLater(() -> {
            NextPiecePanel panel = new NextPiecePanel();
            
            assertNotNull(panel);
            assertFalse(panel.getChildren().isEmpty());
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("다음 피스 업데이트 시 그리기 작업 수행")
    void testUpdateNextPieces() {
        Platform.runLater(() -> {
            NextPiecePanel panel = new NextPiecePanel();
            List<TetrominoPosition> nextPieces = Arrays.asList(
                Tetromino.I, Tetromino.O, Tetromino.T, Tetromino.L, Tetromino.J
            ).stream().map(t -> new TetrominoPosition(t, 0, 0, 0)).collect(Collectors.toList());
            
            assertDoesNotThrow(() -> panel.updateNextPieces(nextPieces));
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("적은 수의 다음 피스로 업데이트")
    void testUpdateWithFewerPieces() {
        Platform.runLater(() -> {
            NextPiecePanel panel = new NextPiecePanel();
            List<TetrominoPosition> nextPieces = Arrays.asList(Tetromino.T, Tetromino.Z)
                .stream().map(t -> new TetrominoPosition(t, 0, 0, 0)).collect(Collectors.toList());
            
            assertDoesNotThrow(() -> panel.updateNextPieces(nextPieces));
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("많은 수의 다음 피스로 업데이트 (5개 초과)")
    void testUpdateWithMorePieces() {
        Platform.runLater(() -> {
            NextPiecePanel panel = new NextPiecePanel();
            List<TetrominoPosition> nextPieces = Arrays.asList(
                Tetromino.I, Tetromino.O, Tetromino.T, 
                Tetromino.L, Tetromino.J, Tetromino.S, Tetromino.Z
            ).stream().map(t -> new TetrominoPosition(t, 0, 0, 0)).collect(Collectors.toList());
            
            assertDoesNotThrow(() -> panel.updateNextPieces(nextPieces));
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("반복적인 다음 피스 업데이트 테스트")
    void testMultipleNextPiecesUpdates() {
        Platform.runLater(() -> {
            NextPiecePanel panel = new NextPiecePanel();
            
            List<TetrominoPosition> pieces1 = Arrays.asList(Tetromino.I, Tetromino.O, Tetromino.T)
                .stream().map(t -> new TetrominoPosition(t, 0, 0, 0)).collect(Collectors.toList());
            List<TetrominoPosition> pieces2 = Arrays.asList(Tetromino.L, Tetromino.J)
                .stream().map(t -> new TetrominoPosition(t, 0, 0, 0)).collect(Collectors.toList());
            
            assertDoesNotThrow(() -> {
                panel.updateNextPieces(pieces1);
                panel.updateNextPieces(pieces2);
            });
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("모든 테트로미노 타입으로 업데이트 테스트")
    void testUpdateWithAllTetrominoTypes() {
        Platform.runLater(() -> {
            NextPiecePanel panel = new NextPiecePanel();
            List<TetrominoPosition> allTypes = Arrays.asList(Tetromino.values())
                .stream().map(t -> new TetrominoPosition(t, 0, 0, 0)).collect(Collectors.toList());
            
            assertDoesNotThrow(() -> panel.updateNextPieces(allTypes));
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
}
