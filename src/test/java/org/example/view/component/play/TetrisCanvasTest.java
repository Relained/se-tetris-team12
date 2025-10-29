package org.example.view.component.play;
import org.testfx.util.WaitForAsyncUtils;

import javafx.application.Platform;
import org.example.model.GameBoard;
import org.example.model.Tetromino;
import org.example.model.TetrominoPosition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.testfx.framework.junit5.ApplicationTest;


import static org.junit.jupiter.api.Assertions.*;

class TetrisCanvasTest extends ApplicationTest {
    
    @Test
    @DisplayName("TetrisCanvas 생성 시 기본 크기가 설정됨")
    void testTetrisCanvasCreation() {
        Platform.runLater(() -> {
            TetrisCanvas canvas = new TetrisCanvas();
            
            assertEquals(GameBoard.WIDTH * 30, canvas.getWidth(), 0.01);
            assertEquals(GameBoard.HEIGHT * 30, canvas.getHeight(), 0.01);
            assertNotNull(canvas.getGraphicsContext2D());
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("setCanvasHeight로 높이 변경 시 너비도 비례하여 변경됨")
    void testSetCanvasHeight() {
        Platform.runLater(() -> {
            TetrisCanvas canvas = new TetrisCanvas();
            
            double newHeight = 600.0;
            canvas.setCanvasHeight(newHeight);
            
            assertEquals(newHeight, canvas.getHeight(), 0.01);
            double expectedCellSize = newHeight / GameBoard.HEIGHT;
            double expectedWidth = GameBoard.WIDTH * expectedCellSize;
            assertEquals(expectedWidth, canvas.getWidth(), 0.01);
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("setCanvasSize로 크기 조정 시 비율 유지됨 - 너비 기준")
    void testSetCanvasSizeWidthBased() {
        Platform.runLater(() -> {
            TetrisCanvas canvas = new TetrisCanvas();
            
            canvas.setCanvasSize(200, 800);
            assertEquals(200, canvas.getWidth(), 0.01);
            assertEquals(400, canvas.getHeight(), 0.01);
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("setCanvasSize로 크기 조정 시 비율 유지됨 - 높이 기준")
    void testSetCanvasSizeHeightBased() {
        Platform.runLater(() -> {
            TetrisCanvas canvas = new TetrisCanvas();
            
            canvas.setCanvasSize(400, 400);
            assertEquals(200, canvas.getWidth(), 0.01);
            assertEquals(400, canvas.getHeight(), 0.01);
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("updateBoard 호출 시 보드 정보가 업데이트됨")
    void testUpdateBoard() {
        Platform.runLater(() -> {
            TetrisCanvas canvas = new TetrisCanvas();
            GameBoard board = new GameBoard();
            TetrominoPosition currentPiece = new TetrominoPosition(Tetromino.I, 3, 0, 0);
            TetrominoPosition ghostPiece = new TetrominoPosition(Tetromino.I, 3, 18, 0);
            
            assertDoesNotThrow(() -> canvas.updateBoard(board, currentPiece, ghostPiece));
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("null 보드로 updateBoard 호출해도 예외 발생하지 않음")
    void testUpdateBoardWithNull() {
        Platform.runLater(() -> {
            TetrisCanvas canvas = new TetrisCanvas();
            
            assertDoesNotThrow(() -> canvas.updateBoard(null, null, null));
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("그래픽 컨텍스트를 사용한 그리기 작업이 정상 동작함")
    void testDrawingWithPieces() {
        Platform.runLater(() -> {
            TetrisCanvas canvas = new TetrisCanvas();
            GameBoard board = new GameBoard();
            
            // 보드에 직접 블록 배치는 불가능하므로 빈 보드로 테스트
            TetrominoPosition piece = new TetrominoPosition(Tetromino.T, 3, 0, 0);
            
            assertDoesNotThrow(() -> canvas.updateBoard(board, piece, null));
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("고스트 피스를 포함한 그리기 작업이 정상 동작함")
    void testDrawingWithGhostPiece() {
        Platform.runLater(() -> {
            TetrisCanvas canvas = new TetrisCanvas();
            GameBoard board = new GameBoard();
            TetrominoPosition currentPiece = new TetrominoPosition(Tetromino.L, 3, 0, 0);
            TetrominoPosition ghostPiece = new TetrominoPosition(Tetromino.L, 3, 15, 0);
            
            assertDoesNotThrow(() -> canvas.updateBoard(board, currentPiece, ghostPiece));
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    @DisplayName("다양한 테트로미노 타입으로 그리기 작업 테스트")
    void testDrawingWithVariousPieces() {
        Platform.runLater(() -> {
            TetrisCanvas canvas = new TetrisCanvas();
            GameBoard board = new GameBoard();
            
            for (Tetromino type : Tetromino.values()) {
                TetrominoPosition piece = new TetrominoPosition(type, 3, 5, 0);
                assertDoesNotThrow(() -> canvas.updateBoard(board, piece, null));
            }
            
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
}
