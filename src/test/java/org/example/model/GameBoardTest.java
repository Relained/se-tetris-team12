package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {
    private GameBoard board;

    @BeforeEach
    void setUp() {
        board = new GameBoard();
    }

    @Test
    @DisplayName("새 게임 보드는 모든 셀이 비어있어야 함")
    void testNewBoardIsEmpty() {
        int[][] visibleBoard = board.getVisibleBoard();
        for (int row = 0; row < GameBoard.HEIGHT; row++) {
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                assertEquals(0, visibleBoard[row][col]);
            }
        }
    }

    @Test
    @DisplayName("유효한 위치 확인 - 보드 중앙")
    void testIsValidPosition_Center() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 3, 10, 0);
        assertTrue(board.isValidPosition(pos));
    }

    @Test
    @DisplayName("유효하지 않은 위치 - 왼쪽 경계 밖")
    void testIsValidPosition_LeftOutOfBounds() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, -1, 10, 0);
        assertFalse(board.isValidPosition(pos));
    }

    @Test
    @DisplayName("유효하지 않은 위치 - 오른쪽 경계 밖")
    void testIsValidPosition_RightOutOfBounds() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, GameBoard.WIDTH, 10, 0);
        assertFalse(board.isValidPosition(pos));
    }

    @Test
    @DisplayName("유효하지 않은 위치 - 아래 경계 밖")
    void testIsValidPosition_BottomOutOfBounds() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 3, GameBoard.HEIGHT + GameBoard.BUFFER_ZONE, 0);
        assertFalse(board.isValidPosition(pos));
    }

    @Test
    @DisplayName("테트로미노 배치 테스트")
    void testPlaceTetromino() {
        // O 블록은 4x4 배열에서 (0,1), (0,2), (1,1), (1,2) 위치에 블록이 있음
        TetrominoPosition pos = new TetrominoPosition(Tetromino.O, 4, GameBoard.BUFFER_ZONE + 18, 0);
        board.placeTetromino(pos);
        
        int colorIndex = Tetromino.O.getColorIndex();
        // O 블록의 실제 모양: {0,1,1,0}, {0,1,1,0}
        // x=4에서 시작하므로 x+1=5, x+2=6 위치에 블록
        assertEquals(colorIndex, board.getCellColor(GameBoard.BUFFER_ZONE + 18, 5));
        assertEquals(colorIndex, board.getCellColor(GameBoard.BUFFER_ZONE + 18, 6));
        assertEquals(colorIndex, board.getCellColor(GameBoard.BUFFER_ZONE + 19, 5));
        assertEquals(colorIndex, board.getCellColor(GameBoard.BUFFER_ZONE + 19, 6));
    }

    @Test
    @DisplayName("한 줄 클리어 테스트")
    void testClearOneLine() {
        // 바닥 줄을 거의 채우기 (한 칸만 남김)
        for (int col = 0; col < GameBoard.WIDTH; col++) {
            TetrominoPosition pos = new TetrominoPosition(Tetromino.I, col, GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 1, 0);
            int[][] shape = new int[][]{{1}};
            TetrominoPosition singleBlock = new TetrominoPosition(Tetromino.I, col, GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 1, 0) {
                @Override
                public int[][] getCurrentShape() {
                    return shape;
                }
            };
            board.placeTetromino(singleBlock);
        }
        
        int linesCleared = board.clearLines();
        assertEquals(1, linesCleared);
    }

    @Test
    @DisplayName("여러 줄 클리어 테스트")
    void testClearMultipleLines() {
        // 아래 두 줄을 완전히 채우기
        for (int row = GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 2; row < GameBoard.BUFFER_ZONE + GameBoard.HEIGHT; row++) {
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                int[][] shape = new int[][]{{1}};
                TetrominoPosition singleBlock = new TetrominoPosition(Tetromino.I, col, row, 0) {
                    @Override
                    public int[][] getCurrentShape() {
                        return shape;
                    }
                };
                board.placeTetromino(singleBlock);
            }
        }
        
        int linesCleared = board.clearLines();
        assertEquals(2, linesCleared);
    }

    @Test
    @DisplayName("셀 색상 가져오기 - 유효한 위치")
    void testGetCellColor_ValidPosition() {
        assertEquals(0, board.getCellColor(10, 5));
    }

    @Test
    @DisplayName("셀 색상 가져오기 - 유효하지 않은 위치")
    void testGetCellColor_InvalidPosition() {
        assertEquals(0, board.getCellColor(-1, 5));
        assertEquals(0, board.getCellColor(10, -1));
        assertEquals(0, board.getCellColor(GameBoard.HEIGHT + GameBoard.BUFFER_ZONE + 1, 5));
        assertEquals(0, board.getCellColor(10, GameBoard.WIDTH + 1));
    }

    @Test
    @DisplayName("게임 오버 확인 - 버퍼 존에 블록 없음")
    void testIsGameOver_NoBlocksInBuffer() {
        assertFalse(board.isGameOver());
    }

    @Test
    @DisplayName("게임 오버 확인 - 버퍼 존에 블록 있음")
    void testIsGameOver_BlocksInBuffer() {
        // 버퍼 존에 블록 배치
        int[][] shape = new int[][]{{1}};
        TetrominoPosition blockInBuffer = new TetrominoPosition(Tetromino.I, 5, 1, 0) {
            @Override
            public int[][] getCurrentShape() {
                return shape;
            }
        };
        board.placeTetromino(blockInBuffer);
        
        assertTrue(board.isGameOver());
    }

    @Test
    @DisplayName("보드 클리어 테스트")
    void testClear() {
        // 보드에 블록 배치
        TetrominoPosition pos = new TetrominoPosition(Tetromino.O, 4, 18, 0);
        board.placeTetromino(pos);
        
        // 클리어
        board.clear();
        
        // 모든 셀이 비어있는지 확인
        int[][] visibleBoard = board.getVisibleBoard();
        for (int row = 0; row < GameBoard.HEIGHT; row++) {
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                assertEquals(0, visibleBoard[row][col]);
            }
        }
    }

    @Test
    @DisplayName("가시 영역 보드 가져오기")
    void testGetVisibleBoard() {
        int[][] visibleBoard = board.getVisibleBoard();
        assertEquals(GameBoard.HEIGHT, visibleBoard.length);
        assertEquals(GameBoard.WIDTH, visibleBoard[0].length);
    }

    @Test
    @DisplayName("겹치는 위치는 유효하지 않음")
    void testIsValidPosition_Overlap() {
        // 먼저 블록 배치
        TetrominoPosition pos1 = new TetrominoPosition(Tetromino.O, 4, 18, 0);
        board.placeTetromino(pos1);
        
        // 같은 위치에 다른 블록 배치 시도
        TetrominoPosition pos2 = new TetrominoPosition(Tetromino.O, 4, 18, 0);
        assertFalse(board.isValidPosition(pos2));
    }
}
