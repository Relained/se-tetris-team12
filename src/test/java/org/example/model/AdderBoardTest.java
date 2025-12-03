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
