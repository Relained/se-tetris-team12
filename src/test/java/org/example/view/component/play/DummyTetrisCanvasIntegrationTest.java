package org.example.view.component.play;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.example.model.GameBoard;
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
class DummyTetrisCanvasIntegrationTest {

    private Stage testStage;
    private static final int WEIGHT_MARK = 200;
    private static final int BOMB_MARK = 201;
    private static final int GHOST_MARK = -2;

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
        final DummyTetrisCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new DummyTetrisCanvas();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
        assertEquals(GameBoard.WIDTH * 30, holder[0].getWidth());
        assertEquals(GameBoard.HEIGHT * 30, holder[0].getHeight());
    }

    @Test
    void testUpdateBoard_NullBoard() throws Exception {
        final DummyTetrisCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new DummyTetrisCanvas();
            holder[0].updateBoard(null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_EmptyBoard() throws Exception {
        final DummyTetrisCanvas[] holder = {null};
        final int[][] board = new int[GameBoard.HEIGHT][GameBoard.WIDTH];
        
        Platform.runLater(() -> {
            holder[0] = new DummyTetrisCanvas();
            holder[0].updateBoard(board);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_WithBlocks() throws Exception {
        final DummyTetrisCanvas[] holder = {null};
        final int[][] board = new int[GameBoard.HEIGHT][GameBoard.WIDTH];
        
        Platform.runLater(() -> {
            holder[0] = new DummyTetrisCanvas();
            
            // 몇 개의 블록 추가
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                board[GameBoard.HEIGHT - 1][col] = 1; // 맨 아래 줄 채우기
            }
            
            holder[0].updateBoard(board);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_WithClearMark() throws Exception {
        final DummyTetrisCanvas[] holder = {null};
        final int[][] board = new int[GameBoard.HEIGHT][GameBoard.WIDTH];
        
        Platform.runLater(() -> {
            holder[0] = new DummyTetrisCanvas();
            
            // CLEAR_MARK 설정
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                board[GameBoard.HEIGHT - 1][col] = GameBoard.CLEAR_MARK;
            }
            
            holder[0].updateBoard(board);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_WithGhostMark() throws Exception {
        final DummyTetrisCanvas[] holder = {null};
        final int[][] board = new int[GameBoard.HEIGHT][GameBoard.WIDTH];
        
        Platform.runLater(() -> {
            holder[0] = new DummyTetrisCanvas();
            
            // GHOST_MARK 설정
            for (int col = 0; col < 4; col++) {
                board[10][col] = GHOST_MARK;
            }
            
            holder[0].updateBoard(board);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_WithWeightMark() throws Exception {
        final DummyTetrisCanvas[] holder = {null};
        final int[][] board = new int[GameBoard.HEIGHT][GameBoard.WIDTH];
        
        Platform.runLater(() -> {
            holder[0] = new DummyTetrisCanvas();
            
            // WEIGHT_MARK 설정
            for (int col = 0; col < 3; col++) {
                board[10][col] = WEIGHT_MARK;
            }
            
            holder[0].updateBoard(board);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_WithBombMark() throws Exception {
        final DummyTetrisCanvas[] holder = {null};
        final int[][] board = new int[GameBoard.HEIGHT][GameBoard.WIDTH];
        
        Platform.runLater(() -> {
            holder[0] = new DummyTetrisCanvas();
            
            // BOMB_MARK 설정
            for (int col = 0; col < 3; col++) {
                board[10][col] = BOMB_MARK;
            }
            
            holder[0].updateBoard(board);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_WithItemBlocks() throws Exception {
        final DummyTetrisCanvas[] holder = {null};
        final int[][] board = new int[GameBoard.HEIGHT][GameBoard.WIDTH];
        
        Platform.runLater(() -> {
            holder[0] = new DummyTetrisCanvas();
            
            // 아이템 블록 인코딩: (symbol << 16) | colorIdx
            char symbolL = 'L';
            char symbolC = 'C';
            int colorIdx = 1;
            
            board[10][0] = (symbolL << 16) | colorIdx; // LINE_CLEAR
            board[10][1] = (symbolC << 16) | colorIdx; // COLUMN_CLEAR
            
            holder[0].updateBoard(board);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_AllColorIndices() throws Exception {
        final DummyTetrisCanvas[] holder = {null};
        final int[][] board = new int[GameBoard.HEIGHT][GameBoard.WIDTH];
        
        Platform.runLater(() -> {
            holder[0] = new DummyTetrisCanvas();
            
            // 모든 색상 인덱스 테스트 (1-7)
            for (int i = 1; i <= 7 && i <= GameBoard.WIDTH; i++) {
                board[10][i-1] = i;
            }
            
            holder[0].updateBoard(board);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_MultipleUpdates() throws Exception {
        final DummyTetrisCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new DummyTetrisCanvas();
            
            int[][] board1 = new int[GameBoard.HEIGHT][GameBoard.WIDTH];
            board1[10][0] = 1;
            holder[0].updateBoard(board1);
            
            int[][] board2 = new int[GameBoard.HEIGHT][GameBoard.WIDTH];
            board2[10][0] = WEIGHT_MARK;
            holder[0].updateBoard(board2);
            
            int[][] board3 = new int[GameBoard.HEIGHT][GameBoard.WIDTH];
            board3[10][0] = GHOST_MARK;
            holder[0].updateBoard(board3);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_ComplexScenario() throws Exception {
        final DummyTetrisCanvas[] holder = {null};
        final int[][] board = new int[GameBoard.HEIGHT][GameBoard.WIDTH];
        
        Platform.runLater(() -> {
            holder[0] = new DummyTetrisCanvas();
            
            // 복잡한 시나리오: 다양한 마크 혼합
            // 맨 아래 줄: 일반 블록
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                board[GameBoard.HEIGHT - 1][col] = col % 7 + 1;
            }
            
            // 중간 줄: 아이템 블록
            board[10][0] = ('L' << 16) | 1;
            board[10][1] = ('C' << 16) | 2;
            board[10][2] = ('X' << 16) | 3;
            
            // 특수 마크
            board[5][0] = WEIGHT_MARK;
            board[5][1] = BOMB_MARK;
            board[5][2] = GHOST_MARK;
            board[5][3] = GameBoard.CLEAR_MARK;
            
            holder[0].updateBoard(board);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testCanvasSize() throws Exception {
        final DummyTetrisCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new DummyTetrisCanvas();
            holder[0].setCanvasSize(200, 400);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(200, holder[0].getWidth());
        assertEquals(400, holder[0].getHeight());
    }

    @Test
    void testUpdateBoard_WithInvalidColorIndex() throws Exception {
        final DummyTetrisCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new DummyTetrisCanvas();
            int[][] board = new int[GameBoard.HEIGHT][GameBoard.WIDTH];
            
            // 7보다 큰 색상 인덱스는 else if 조건에 맞지 않아 건너뜀
            // 하지만 정상 범위 값들과 섞어서 테스트
            board[0][0] = 1;  // 정상
            board[0][1] = 7;  // 정상 최대값
            board[0][2] = 8;  // 건너뜀 (1~7 범위 밖)
            board[1][0] = 100; // 건너뜀
            
            holder[0].updateBoard(board);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_WithEdgeCaseValues() throws Exception {
        final DummyTetrisCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new DummyTetrisCanvas();
            int[][] board = new int[GameBoard.HEIGHT][GameBoard.WIDTH];
            
            // 경계값 테스트: 0, 1, 7 경계
            board[0][0] = 0;  // 빈 셀 (continue)
            board[0][1] = 1;  // 최소 정상 색상
            board[0][2] = 7;  // 최대 정상 색상
            board[1][0] = 8;  // 범위 초과 (건너뜀)
            
            holder[0].updateBoard(board);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }

    @Test
    void testUpdateBoard_WithMixedInvalidValues() throws Exception {
        final DummyTetrisCanvas[] holder = {null};
        
        Platform.runLater(() -> {
            holder[0] = new DummyTetrisCanvas();
            int[][] board = new int[GameBoard.HEIGHT][GameBoard.WIDTH];
            
            // 다양한 값 타입 혼합 (유효하지 않은 값 제외)
            board[0][0] = 0;  // 빈 셀
            board[0][1] = 1;  // 정상 색상
            board[0][2] = 7;  // 최대 정상 색상
            board[1][0] = GHOST_MARK; // GHOST
            board[1][1] = WEIGHT_MARK; // WEIGHT
            board[1][2] = BOMB_MARK;   // BOMB
            board[2][0] = GameBoard.CLEAR_MARK; // CLEAR_MARK
            // 아이템 인코딩 (상위 16비트: symbol, 하위 8비트: colorIdx)
            board[2][1] = ('L' << 16) | 3; // LINE_CLEAR item with color 3
            
            holder[0].updateBoard(board);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(holder[0]);
    }
}
