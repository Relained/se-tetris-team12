package org.example.view.component.play;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.model.*;
import org.example.service.ColorManager;
import org.example.view.BaseView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class TetrisCanvasIntegrationTest {

    private Stage testStage;

    @Start
    void start(Stage stage) {
        this.testStage = stage;
    }

    @BeforeEach
    void setUp() throws Exception {
        FxToolkit.registerPrimaryStage();
        
        Platform.runLater(() -> {
            BaseView.Initialize(ColorManager.getInstance());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testCanvasCreation() throws Exception {
        final TetrisCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
        assertEquals(GameBoard.WIDTH * 30, holder[0].getWidth());
        assertEquals(GameBoard.HEIGHT * 30, holder[0].getHeight());
    }

    @Test
    void testSetCanvasHeight() throws Exception {
        final TetrisCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            holder[0].setCanvasHeight(400);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(400, holder[0].getHeight());
    }

    @Test
    void testSetCanvasSize_MaintainsRatio() throws Exception {
        final TetrisCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            holder[0].setCanvasSize(200, 500);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // 가로:세로 = 1:2 비율 유지
        assertEquals(200, holder[0].getWidth());
        assertEquals(400, holder[0].getHeight());
    }

    @Test
    void testSetCanvasSize_HeightLimited() throws Exception {
        final TetrisCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            holder[0].setCanvasSize(300, 400);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // 높이 제한으로 너비 조정
        assertEquals(200, holder[0].getWidth());
        assertEquals(400, holder[0].getHeight());
    }

    @Test
    void testUpdateBoard_NullBoard() throws Exception {
        final TetrisCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            holder[0].updateBoard(null, null, null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_EmptyBoard() throws Exception {
        final TetrisCanvas[] holder = {null};
        final GameBoard[] boardHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            boardHolder[0] = new GameBoard();
            holder[0].updateBoard(boardHolder[0], null, null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_WithCurrentPiece() throws Exception {
        final TetrisCanvas[] holder = {null};
        final GameBoard[] boardHolder = {null};
        final TetrominoPosition[] pieceHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            boardHolder[0] = new GameBoard();
            pieceHolder[0] = new TetrominoPosition(Tetromino.I, 5, 2, 0);
            holder[0].updateBoard(boardHolder[0], pieceHolder[0], null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_WithGhostPiece() throws Exception {
        final TetrisCanvas[] holder = {null};
        final GameBoard[] boardHolder = {null};
        final TetrominoPosition[] pieceHolder = {null};
        final TetrominoPosition[] ghostHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            boardHolder[0] = new GameBoard();
            pieceHolder[0] = new TetrominoPosition(Tetromino.T, 5, 2, 0);
            ghostHolder[0] = new TetrominoPosition(Tetromino.T, 5, 18, 0);
            holder[0].updateBoard(boardHolder[0], pieceHolder[0], ghostHolder[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_WithPlacedBlocks() throws Exception {
        final TetrisCanvas[] holder = {null};
        final GameBoard[] boardHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            boardHolder[0] = new GameBoard();
            
            // 블록 배치
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                boardHolder[0].setCellColor(GameBoard.HEIGHT - 1 + GameBoard.BUFFER_ZONE, col, 1);
            }
            
            holder[0].updateBoard(boardHolder[0], null, null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_WithItemBlocks() throws Exception {
        final TetrisCanvas[] holder = {null};
        final ItemGameBoard[] boardHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            boardHolder[0] = new ItemGameBoard();
            
            // 아이템 블록 배치 - ItemGameBoard는 placeTetromino를 통해 아이템 추가
            TetrominoPosition itemPiece = new TetrominoPosition(Tetromino.O, 0, GameBoard.HEIGHT - 1, 0);
            itemPiece.setItemAtBlockIndex(0, ItemBlock.LINE_CLEAR);
            boardHolder[0].placeTetromino(itemPiece);
            
            holder[0].updateBoard(boardHolder[0], null, null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_WithClearMark() throws Exception {
        final TetrisCanvas[] holder = {null};
        final GameBoard[] boardHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            boardHolder[0] = new GameBoard();
            
            // CLEAR_MARK 설정
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                boardHolder[0].setCellColor(GameBoard.HEIGHT - 1 + GameBoard.BUFFER_ZONE, col, GameBoard.CLEAR_MARK);
            }
            
            holder[0].updateBoard(boardHolder[0], null, null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_WithItemPiece() throws Exception {
        final TetrisCanvas[] holder = {null};
        final GameBoard[] boardHolder = {null};
        final TetrominoPosition[] pieceHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            boardHolder[0] = new GameBoard();
            pieceHolder[0] = new TetrominoPosition(Tetromino.O, 5, 2, 0);
            
            // 아이템 추가 - 첫 번째 블록에 LINE_CLEAR 아이템 부착
            pieceHolder[0].setItemAtBlockIndex(0, ItemBlock.LINE_CLEAR);
            
            holder[0].updateBoard(boardHolder[0], pieceHolder[0], null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_WithWeightPiece() throws Exception {
        final TetrisCanvas[] holder = {null};
        final GameBoard[] boardHolder = {null};
        final TetrominoPosition[] pieceHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            boardHolder[0] = new GameBoard();
            pieceHolder[0] = TetrominoPosition.createWeightPiece(5, 2);
            holder[0].updateBoard(boardHolder[0], pieceHolder[0], null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_WithBombPiece() throws Exception {
        final TetrisCanvas[] holder = {null};
        final GameBoard[] boardHolder = {null};
        final TetrominoPosition[] pieceHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            boardHolder[0] = new GameBoard();
            pieceHolder[0] = TetrominoPosition.createBombPiece(5, 2);
            holder[0].updateBoard(boardHolder[0], pieceHolder[0], null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_AllPieceTypes() throws Exception {
        final TetrisCanvas[] holder = {null};
        final GameBoard[] boardHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            boardHolder[0] = new GameBoard();
            
            // 모든 테트로미노 타입을 보드에 배치
            Tetromino[] types = Tetromino.values();
            for (int i = 0; i < types.length; i++) {
                TetrominoPosition piece = new TetrominoPosition(types[i], 0, GameBoard.BUFFER_ZONE, 0);
                int[][] shape = piece.getCurrentShape();
                
                for (int row = 0; row < shape.length; row++) {
                    for (int col = 0; col < shape[row].length; col++) {
                        if (shape[row][col] == 1) {
                            boardHolder[0].setCellColor(row + GameBoard.BUFFER_ZONE, col + i, types[i].getColorIndex());
                        }
                    }
                }
            }
            
            holder[0].updateBoard(boardHolder[0], null, null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testCellSizeCalculation() throws Exception {
        final TetrisCanvas[] holder = {null};
        final double[] cellSizeHolder = {0};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            holder[0].setCanvasSize(100, 200);
            // cellSize는 protected라 직접 확인은 못하지만 크기로 검증
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // cellSize = 100 / GameBoard.WIDTH
        double expectedCellSize = 100.0 / GameBoard.WIDTH;
        double expectedHeight = expectedCellSize * GameBoard.HEIGHT;
        
        assertEquals(100, holder[0].getWidth());
        assertEquals(expectedHeight, holder[0].getHeight(), 0.01);
    }

    @Test
    void testMultipleUpdates() throws Exception {
        final TetrisCanvas[] holder = {null};
        final GameBoard[] boardHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            boardHolder[0] = new GameBoard();
            
            // 여러 번 업데이트
            holder[0].updateBoard(boardHolder[0], null, null);
            
            TetrominoPosition piece1 = new TetrominoPosition(Tetromino.I, 5, 2, 0);
            holder[0].updateBoard(boardHolder[0], piece1, null);
            
            TetrominoPosition piece2 = new TetrominoPosition(Tetromino.T, 5, 5, 0);
            holder[0].updateBoard(boardHolder[0], piece2, null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testCanvasSizeAfterMultipleAdjustments() throws Exception {
        final TetrisCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            holder[0].setCanvasSize(200, 500);
            holder[0].setCanvasSize(300, 400);
            holder[0].setCanvasSize(150, 300);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(150, holder[0].getWidth());
        assertEquals(300, holder[0].getHeight());
    }

    @Test
    void testUpdateBoard_ComplexScenario() throws Exception {
        final TetrisCanvas[] holder = {null};
        final ItemGameBoard[] boardHolder = {null};
        final TetrominoPosition[] currentHolder = {null};
        final TetrominoPosition[] ghostHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            boardHolder[0] = new ItemGameBoard();
            
            // 복잡한 시나리오: 배치된 블록 + 아이템 + 현재 피스 + 고스트
            // 배치된 블록 - ItemGameBoard는 placeTetromino를 통해 아이템 추가
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                boardHolder[0].setCellColor(GameBoard.HEIGHT - 1 + GameBoard.BUFFER_ZONE, col, 1);
            }
            // 일부 블록에 아이템 추가
            TetrominoPosition itemPiece = new TetrominoPosition(Tetromino.O, 0, GameBoard.HEIGHT - 1, 0);
            itemPiece.setItemAtBlockIndex(0, ItemBlock.LINE_CLEAR);
            boardHolder[0].placeTetromino(itemPiece);
            
            // 현재 피스 (WEIGHT 특수 타입)
            currentHolder[0] = TetrominoPosition.createWeightPiece(5, 5);
            
            // 고스트 피스
            ghostHolder[0] = new TetrominoPosition(Tetromino.T, 5, 18, 0);
            
            holder[0].updateBoard(boardHolder[0], currentHolder[0], ghostHolder[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_PieceOutOfBoundsLeft() throws Exception {
        final TetrisCanvas[] holder = {null};
        final GameBoard[] boardHolder = {null};
        final TetrominoPosition[] pieceHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            boardHolder[0] = new GameBoard();
            // I 블록을 왼쪽 경계 밖으로 배치 (x < 0)
            pieceHolder[0] = new TetrominoPosition(Tetromino.I, -2, GameBoard.BUFFER_ZONE + 5, 0);
            holder[0].updateBoard(boardHolder[0], pieceHolder[0], null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_PieceOutOfBoundsRight() throws Exception {
        final TetrisCanvas[] holder = {null};
        final GameBoard[] boardHolder = {null};
        final TetrominoPosition[] pieceHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            boardHolder[0] = new GameBoard();
            // T 블록을 오른쪽 경계 밖으로 배치 (x >= WIDTH)
            pieceHolder[0] = new TetrominoPosition(Tetromino.T, GameBoard.WIDTH + 1, GameBoard.BUFFER_ZONE + 5, 0);
            holder[0].updateBoard(boardHolder[0], pieceHolder[0], null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_PieceOutOfBoundsTop() throws Exception {
        final TetrisCanvas[] holder = {null};
        final GameBoard[] boardHolder = {null};
        final TetrominoPosition[] pieceHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            boardHolder[0] = new GameBoard();
            // O 블록을 위쪽 경계 밖으로 배치 (y < 0, 버퍼존 위)
            pieceHolder[0] = new TetrominoPosition(Tetromino.O, 5, -3, 0);
            holder[0].updateBoard(boardHolder[0], pieceHolder[0], null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_WithBombPiece_VisibleArea() throws Exception {
        final TetrisCanvas[] holder = {null};
        final GameBoard[] boardHolder = {null};
        final TetrominoPosition[] pieceHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            boardHolder[0] = new GameBoard();
            // BOMB 블록을 화면 내 확실한 위치에 배치 (y >= 0 보장)
            pieceHolder[0] = TetrominoPosition.createBombPiece(5, GameBoard.BUFFER_ZONE + 5);
            holder[0].updateBoard(boardHolder[0], pieceHolder[0], null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_PiecePartiallyOutOfBounds() throws Exception {
        final TetrisCanvas[] holder = {null};
        final GameBoard[] boardHolder = {null};
        final TetrominoPosition[] pieceHolder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new TetrisCanvas();
            boardHolder[0] = new GameBoard();
            // I 블록(가로)을 일부만 화면에 보이도록 배치
            pieceHolder[0] = new TetrominoPosition(Tetromino.I, GameBoard.WIDTH - 2, GameBoard.BUFFER_ZONE + 3, 1);
            holder[0].updateBoard(boardHolder[0], pieceHolder[0], null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }
}
