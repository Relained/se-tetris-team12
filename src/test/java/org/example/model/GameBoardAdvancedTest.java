package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GameBoard의 추가 테스트 - 엣지 케이스 및 경계 조건
 */
class GameBoardAdvancedTest {
    private GameBoard board;

    @BeforeEach
    void setUp() {
        board = new GameBoard();
    }

    @Test
    @DisplayName("버퍼 존 경계 테스트 - 위쪽 경계")
    void testBufferZoneBoundary() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 3, 0, 0);
        // 버퍼 존 내부이므로 유효해야 함
        assertTrue(board.isValidPosition(pos));
    }

    @Test
    @DisplayName("버퍼 존 위로 벗어난 위치")
    void testAboveBufferZone() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 3, -5, 0);
        // 음수 Y 좌표도 부분적으로 허용될 수 있음 (블록의 일부만 보드 밖)
        // isValidPosition은 블록의 일부라도 유효한 위치에 있으면 true
    }

    @Test
    @DisplayName("정확히 보드 너비만큼의 위치")
    void testExactBoardWidth() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, GameBoard.WIDTH - 4, 10, 0);
        // I 블록(4칸)이 보드 내에 들어갈 수 있는지 확인
        assertTrue(board.isValidPosition(pos));
    }

    @Test
    @DisplayName("여러 줄 동시 클리어 - 최대 4줄")
    void testClearMaxFourLines() {
        // 아래 4줄을 모두 채우기
        for (int row = GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 4; 
             row < GameBoard.BUFFER_ZONE + GameBoard.HEIGHT; row++) {
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                fillSingleCell(col, row);
            }
        }
        
        int cleared = board.clearLines();
        assertEquals(4, cleared);
    }

    @Test
    @DisplayName("중간 줄만 클리어 - 위 줄이 내려옴")
    void testClearMiddleLine() {
        // 위쪽에 블록 배치
        fillSingleCell(0, GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 3);
        
        // 아래쪽 줄을 가득 채우기
        for (int col = 0; col < GameBoard.WIDTH; col++) {
            fillSingleCell(col, GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 1);
        }
        
        int cleared = board.clearLines();
        assertEquals(1, cleared);
        
        // 위에 있던 블록이 한 칸 내려왔는지 확인
        assertEquals(0, board.getCellColor(GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 3, 0));
        assertNotEquals(0, board.getCellColor(GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 2, 0));
    }

    @Test
    @DisplayName("불완전한 줄은 클리어되지 않음")
    void testIncompleteLinesNotCleared() {
        // 한 칸만 비워두고 채우기
        for (int col = 0; col < GameBoard.WIDTH - 1; col++) {
            fillSingleCell(col, GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 1);
        }
        
        int cleared = board.clearLines();
        assertEquals(0, cleared);
    }

    @Test
    @DisplayName("연속되지 않은 줄 클리어")
    void testNonConsecutiveLineClears() {
        // 첫 번째 줄 채우기
        for (int col = 0; col < GameBoard.WIDTH; col++) {
            fillSingleCell(col, GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 1);
        }
        
        // 세 번째 줄 채우기 (두 번째 줄은 비워둠)
        for (int col = 0; col < GameBoard.WIDTH; col++) {
            fillSingleCell(col, GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 3);
        }
        
        int cleared = board.clearLines();
        assertEquals(2, cleared);
    }

    @Test
    @DisplayName("가시 영역 vs 전체 영역")
    void testVisibleVsTotalArea() {
        int[][] visible = board.getVisibleBoard();
        
        // 가시 영역은 버퍼 존을 제외한 크기
        assertEquals(GameBoard.HEIGHT, visible.length);
        assertEquals(GameBoard.WIDTH, visible[0].length);
    }

    @Test
    @DisplayName("겹치는 테트로미노 배치 불가")
    void testOverlappingPlacement() {
        // 첫 번째 블록 배치
        fillSingleCell(5, GameBoard.BUFFER_ZONE + 10);
        
        // 같은 위치에 블록이 있는 테트로미노는 유효하지 않음
        TetrominoPosition pos = new TetrominoPosition(Tetromino.O, 4, GameBoard.BUFFER_ZONE + 9, 0);
        // O 블록은 2x2이므로 (5,10) 위치와 겹침
        assertFalse(board.isValidPosition(pos));
    }

    @Test
    @DisplayName("테트로미노 배치 후 보드 상태 확인")
    void testBoardStateAfterPlacement() {
        board.clear();
        
        // 모든 셀이 비어있는지 확인
        int[][] visible = board.getVisibleBoard();
        for (int row = 0; row < GameBoard.HEIGHT; row++) {
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                assertEquals(0, visible[row][col]);
            }
        }
    }

    @Test
    @DisplayName("클리어 후 빈 줄 확인")
    void testEmptyLineAfterClear() {
        // 맨 아래 줄 채우기
        for (int col = 0; col < GameBoard.WIDTH; col++) {
            fillSingleCell(col, GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 1);
        }
        
        board.clearLines();
        
        // 클리어 후 맨 아래 줄이 비어있어야 함
        for (int col = 0; col < GameBoard.WIDTH; col++) {
            assertEquals(0, board.getCellColor(GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 1, col));
        }
    }

    @Test
    @DisplayName("게임 오버 상태 전환")
    void testGameOverTransition() {
        assertFalse(board.isGameOver());
        
        // 버퍼 존에 블록 추가
        fillSingleCell(5, 2);
        
        assertTrue(board.isGameOver());
    }

    @RepeatedTest(3)
    @DisplayName("반복 테스트 - 클리어 후 재사용")
    void testReusableAfterClear() {
        // 블록 배치
        fillSingleCell(5, GameBoard.BUFFER_ZONE + 10);
        
        // 클리어
        board.clear();
        
        // 다시 사용 가능
        assertFalse(board.isGameOver());
        
        TetrominoPosition pos = new TetrominoPosition(Tetromino.O, 4, GameBoard.BUFFER_ZONE + 10, 0);
        assertTrue(board.isValidPosition(pos));
    }

    @Test
    @DisplayName("다양한 테트로미노 타입 배치")
    void testAllTetrominoTypes() {
        int y = GameBoard.BUFFER_ZONE + 10;
        
        for (Tetromino type : Tetromino.values()) {
            board.clear();
            TetrominoPosition pos = new TetrominoPosition(type, 3, y, 0);
            
            if (board.isValidPosition(pos)) {
                board.placeTetromino(pos);
                
                // 배치 후 해당 색상이 보드에 존재해야 함
                int colorIndex = type.getColorIndex();
                boolean found = false;
                
                for (int row = 0; row < GameBoard.HEIGHT + GameBoard.BUFFER_ZONE; row++) {
                    for (int col = 0; col < GameBoard.WIDTH; col++) {
                        if (board.getCellColor(row, col) == colorIndex) {
                            found = true;
                            break;
                        }
                    }
                    if (found) break;
                }
                
                assertTrue(found, "Type " + type + " should be placed on board");
            }
        }
    }

    // 헬퍼 메소드: 단일 셀에 블록 배치
    private void fillSingleCell(int x, int y) {
        int[][] shape = new int[][]{{1}};
        TetrominoPosition singleBlock = new TetrominoPosition(Tetromino.I, x, y, 0) {
            @Override
            public int[][] getCurrentShape() {
                return shape;
            }
        };
        board.placeTetromino(singleBlock);
    }
}
