package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AdderBoard unit tests
 * Tests the line storage and transfer mechanism for multiplayer
 */
class AdderBoardTest {

    private AdderBoard adderBoard;

    @BeforeEach
    void setUp() {
        adderBoard = new AdderBoard();
    }

    @Test
    @DisplayName("AdderBoard initializes with zero lines")
    void testInitialState() {
        assertEquals(0, adderBoard.getLineCount(), "Initial line count should be 0");
    }

    @Test
    @DisplayName("AdderBoard adds single line correctly")
    void testAddSingleLine() {
        int[][] newLines = new int[1][GameBoard.WIDTH];
        for (int i = 0; i < GameBoard.WIDTH; i++) {
            newLines[0][i] = 8; // Color index 8
        }

        adderBoard.addLines(newLines);

        assertEquals(1, adderBoard.getLineCount(), "Should have 1 line after adding");
    }

    @Test
    @DisplayName("AdderBoard adds multiple lines correctly")
    void testAddMultipleLines() {
        int[][] newLines = new int[3][GameBoard.WIDTH];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                newLines[row][col] = 8;
            }
        }

        adderBoard.addLines(newLines);

        assertEquals(3, adderBoard.getLineCount(), "Should have 3 lines after adding");
    }

    @Test
    @DisplayName("AdderBoard limits to maximum 10 lines")
    void testMaximumLineLimit() {
        // Add 15 lines, should keep only 10
        int[][] newLines = new int[15][GameBoard.WIDTH];
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                newLines[row][col] = 8;
            }
        }

        adderBoard.addLines(newLines);

        assertEquals(10, adderBoard.getLineCount(), "Should limit to 10 lines maximum");
    }
    
    @Test
    @DisplayName("이미 10줄이 차 있으면 새 줄 추가 무시")
    void testIgnoreNewLinesWhenFull() {
        // 먼저 10줄을 채움
        int[][] initialLines = new int[10][GameBoard.WIDTH];
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                initialLines[row][col] = 1; // 색상 1로 표시
            }
        }
        adderBoard.addLines(initialLines);
        System.err.println("초기 줄 개수: " + adderBoard.getLineCount());
        assertEquals(10, adderBoard.getLineCount(), "초기에 10줄이어야 함");
        
        // 3줄 더 추가 시도 - 이미 최대치이므로 무시되어야 함
        int[][] newLines = new int[3][GameBoard.WIDTH];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                newLines[row][col] = 2; // 색상 2로 표시
            }
        }
        adderBoard.addLines(newLines);
        System.err.println("3줄 추가 시도 후 줄 개수: " + adderBoard.getLineCount());
        
        // 여전히 10줄이어야 함
        assertEquals(10, adderBoard.getLineCount(), 
            "가득 찬 상태에서 새 줄은 무시되어야 하므로 여전히 10줄이어야 함");
        
        // 원래 줄들이 보존되었는지 확인 (색상 1)
        int[][] resultLines = adderBoard.getLines();
        System.err.println("첫 번째 줄의 첫 번째 값: " + resultLines[0][0]);
        assertEquals(1, resultLines[0][0], 
            "첫 번째 줄은 새 색상(2)이 아닌 원래 색상(1)이어야 함");
    }
    
    @Test
    @DisplayName("줄 추가 시 오버플로우가 발생하면 초과분을 잘라냄")
    void testTrimExcessLinesOnOverflow() {
        // 초기에 8줄 추가
        int[][] initialLines = new int[8][GameBoard.WIDTH];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                initialLines[row][col] = row + 1; // 각 줄을 행 번호로 표시
            }
        }
        adderBoard.addLines(initialLines);
        System.err.println("초기 줄 개수: " + adderBoard.getLineCount());
        assertEquals(8, adderBoard.getLineCount(), "초기에 8줄이어야 함");
        
        // 4줄 더 추가 (8 + 4 = 12, 10줄로 잘려야 함)
        int[][] newLines = new int[4][GameBoard.WIDTH];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                newLines[row][col] = 10 + row; // 10, 11, 12, 13으로 표시
            }
        }
        adderBoard.addLines(newLines);
        System.err.println("4줄 추가 후 줄 개수: " + adderBoard.getLineCount());
        
        // 정확히 10줄이어야 함 (12에서 잘림)
        assertEquals(10, adderBoard.getLineCount(), 
            "8줄에 4줄 추가 후 10줄이어야 함 (12에서 잘림)");
        
        // 코드에 따르면 끝(최신)의 초과 줄이 제거됨
        // 따라서 lines[0]~lines[9]에는:
        // 원래: 1, 2, 3, 4, 5, 6, 7, 8 (인덱스 0-7)
        // 새로 (잘림): 10, 11 (인덱스 8-9) - 12, 13은 잘려나감
        int[][] resultLines = adderBoard.getLines();
        
        // 모든 줄의 값 출력
        System.err.println("=== 결과 줄 값 ===");
        for (int i = 0; i < 10; i++) {
            System.err.println("resultLines[" + i + "][0] = " + resultLines[i][0]);
        }
        System.err.println("==================");
        
        // 처음 8줄이 원래 값인지 확인
        for (int i = 0; i < 8; i++) {
            assertEquals(i + 1, resultLines[i][0], 
                i + "번 줄은 원래 값 " + (i + 1) + "이어야 함");
        }
        
        // 8, 9번 줄이 새로 추가된 줄(10, 11)인지 확인
        assertEquals(10, resultLines[8][0], "8번 줄은 첫 번째 새 줄(10)이어야 함");
        assertEquals(11, resultLines[9][0], "9번 줄은 두 번째 새 줄(11)이어야 함");
        
        // 12, 13은 잘려나갔으므로 resultLines[8], [9]에 없어야 함
        assertNotEquals(12, resultLines[8][0], "8번 줄은 12가 아니어야 함");
        assertNotEquals(13, resultLines[8][0], "8번 줄은 13이 아니어야 함");
        assertNotEquals(12, resultLines[9][0], "9번 줄은 12가 아니어야 함");
        assertNotEquals(13, resultLines[9][0], "9번 줄은 13이 아니어야 함");
    }

    @Test
    @DisplayName("AdderBoard ignores null lines")
    void testAddNullLines() {
        adderBoard.addLines(null);

        assertEquals(0, adderBoard.getLineCount(), "Should remain 0 after adding null");
    }

    @Test
    @DisplayName("AdderBoard ignores empty array")
    void testAddEmptyArray() {
        int[][] emptyLines = new int[0][GameBoard.WIDTH];

        adderBoard.addLines(emptyLines);

        assertEquals(0, adderBoard.getLineCount(), "Should remain 0 after adding empty array");
    }

    @Test
    @DisplayName("AdderBoard clears lines after clear()")
    void testClear() {
        int[][] newLines = new int[3][GameBoard.WIDTH];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                newLines[row][col] = 8;
            }
        }

        adderBoard.addLines(newLines);
        assertEquals(3, adderBoard.getLineCount(), "Should have 3 lines before clear");

        adderBoard.clear();
        assertEquals(0, adderBoard.getLineCount(), "Should have 0 lines after clear");
    }

    @Test
    @DisplayName("AdderBoard applies lines to GameBoard")
    void testApplyToBoard() {
        GameBoard gameBoard = new GameBoard();

        // Add 2 lines to adderBoard
        int[][] newLines = new int[2][GameBoard.WIDTH];
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                newLines[row][col] = 8;
            }
        }

        adderBoard.addLines(newLines);
        int appliedCount = adderBoard.applyToBoard(gameBoard);

        assertEquals(2, appliedCount, "Should apply 2 lines");
        assertEquals(0, adderBoard.getLineCount(), "AdderBoard should be cleared after apply");
    }

    @Test
    @DisplayName("AdderBoard returns 0 when applying empty board")
    void testApplyEmptyBoard() {
        GameBoard gameBoard = new GameBoard();

        int appliedCount = adderBoard.applyToBoard(gameBoard);

        assertEquals(0, appliedCount, "Should apply 0 lines when empty");
    }

    @Test
    @DisplayName("AdderBoard returns max lines constant")
    void testGetMaxLines() {
        assertEquals(10, adderBoard.getMaxLines(), "Max lines should be 10");
    }

    @Test
    @DisplayName("AdderBoard returns lines as 10x10 array")
    void testGetLines() {
        int[][] newLines = new int[3][GameBoard.WIDTH];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                newLines[row][col] = 8;
            }
        }

        adderBoard.addLines(newLines);
        int[][] result = adderBoard.getLines();

        assertNotNull(result, "Result should not be null");
        assertEquals(10, result.length, "Should return 10 rows");
        assertEquals(GameBoard.WIDTH, result[0].length, "Each row should have WIDTH columns");
    }
}