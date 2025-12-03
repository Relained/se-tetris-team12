package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GameBoard Unit Test
 */
class GameBoardTest {
    
    private GameBoard board;
    
    @BeforeEach
    void setUp() {
        board = new GameBoard();
    }
    
    @Test
    void testBoardConstants() {
        assertEquals(10, GameBoard.WIDTH);
        assertEquals(20, GameBoard.HEIGHT);
        assertEquals(4, GameBoard.BUFFER_ZONE);
        assertEquals(-1, GameBoard.CLEAR_MARK);
    }
    
    @Test
    void testConstructor() {
        assertNotNull(board);
    }
    
    @Test
    void testInitialBoardIsEmpty() {
        for (int row = 0; row < GameBoard.HEIGHT + GameBoard.BUFFER_ZONE; row++) {
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                assertEquals(0, board.getCellColor(row, col));
            }
        }
    }
    
    @Test
    void testIsValidPositionOnEmptyBoard() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 3, 0, 0);
        assertTrue(board.isValidPosition(pos));
    }
    
    @Test
    void testIsValidPositionOutOfBoundsLeft() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, -1, 0, 0);
        assertFalse(board.isValidPosition(pos));
    }
    
    @Test
    void testIsValidPositionOutOfBoundsRight() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 8, 0, 0); // I piece at x=8 is out of bounds
        assertFalse(board.isValidPosition(pos));
    }
    
    @Test
    void testIsValidPositionOutOfBoundsBottom() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 3, GameBoard.HEIGHT + GameBoard.BUFFER_ZONE, 0);
        assertFalse(board.isValidPosition(pos));
    }
    
    @Test
    void testPlaceTetromino() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.O, 4, 5, 0);
        board.placeTetromino(pos);
        
        // O piece shape: [[0,1,1,0], [0,1,1,0], [0,0,0,0], [0,0,0,0]]
        // At position (4,5), blocks are at: (5,5), (5,6), (6,5), (6,6)
        assertEquals(Tetromino.O.getColorIndex(), board.getCellColor(5, 5));
        assertEquals(Tetromino.O.getColorIndex(), board.getCellColor(5, 6));
        assertEquals(Tetromino.O.getColorIndex(), board.getCellColor(6, 5));
        assertEquals(Tetromino.O.getColorIndex(), board.getCellColor(6, 6));
    }
    
    @Test
    void testIsValidPositionWithBlockedCell() {
        // Place a piece
        board.setCellColor(10, 5, 1);
        
        // Try to place another piece at the same location
        TetrominoPosition pos = new TetrominoPosition(Tetromino.O, 4, 9, 0);
        assertFalse(board.isValidPosition(pos));
    }
    
    @Test
    void testClearLines() {
        // Fill a complete line
        int lineIndex = GameBoard.HEIGHT + GameBoard.BUFFER_ZONE - 1;
        for (int col = 0; col < GameBoard.WIDTH; col++) {
            board.setCellColor(lineIndex, col, 1);
        }
        
        int cleared = board.clearLines();
        assertEquals(1, cleared);
    }
    
    @Test
    void testClearMultipleLines() {
        // Fill two complete lines
        for (int line = 0; line < 2; line++) {
            int lineIndex = GameBoard.HEIGHT + GameBoard.BUFFER_ZONE - 1 - line;
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                board.setCellColor(lineIndex, col, 1);
            }
        }
        
        int cleared = board.clearLines();
        assertEquals(2, cleared);
    }
    
    @Test
    void testClearNoLines() {
        // Partially filled line
        for (int col = 0; col < GameBoard.WIDTH - 1; col++) {
            board.setCellColor(GameBoard.HEIGHT + GameBoard.BUFFER_ZONE - 1, col, 1);
        }
        
        int cleared = board.clearLines();
        assertEquals(0, cleared);
    }
    
    @Test
    void testGetCellColor() {
        board.setCellColor(10, 5, 3);
        assertEquals(3, board.getCellColor(10, 5));
    }
    
    @Test
    void testGetCellColorOutOfBounds() {
        assertEquals(0, board.getCellColor(-1, 5));
        assertEquals(0, board.getCellColor(100, 5));
        assertEquals(0, board.getCellColor(10, -1));
        assertEquals(0, board.getCellColor(10, 100));
    }
    
    @Test
    void testSetCellColor() {
        board.setCellColor(10, 5, 7);
        assertEquals(7, board.getCellColor(10, 5));
    }
    
    @Test
    void testSetCellColorOutOfBounds() {
        // Should not throw exception
        assertDoesNotThrow(() -> board.setCellColor(-1, 5, 1));
        assertDoesNotThrow(() -> board.setCellColor(100, 5, 1));
        assertDoesNotThrow(() -> board.setCellColor(10, -1, 1));
        assertDoesNotThrow(() -> board.setCellColor(10, 100, 1));
    }
    
    @Test
    void testIsGameOverEmptyBoard() {
        assertFalse(board.isGameOver());
    }
    
    @Test
    void testIsGameOverWithBlockInBufferZone() {
        board.setCellColor(0, 5, 1); // Block in buffer zone
        assertTrue(board.isGameOver());
    }
    
    @Test
    void testIsGameOverWithBlockInVisibleArea() {
        board.setCellColor(GameBoard.BUFFER_ZONE, 5, 1); // Block in visible area
        assertFalse(board.isGameOver());
    }
    
    @Test
    void testClear() {
        // Fill some cells
        board.setCellColor(10, 5, 1);
        board.setCellColor(15, 7, 2);
        
        board.clear();
        
        // All cells should be 0
        for (int row = 0; row < GameBoard.HEIGHT + GameBoard.BUFFER_ZONE; row++) {
            for (int col = 0; col < GameBoard.WIDTH; col++) {
                assertEquals(0, board.getCellColor(row, col));
            }
        }
    }
    
    @Test
    void testGetCompressedBoard() {
        board.setCellColor(GameBoard.BUFFER_ZONE + 5, 3, 5);
        
        int[][] compressed = board.getCompressedBoard();
        
        assertEquals(GameBoard.HEIGHT, compressed.length);
        assertEquals(GameBoard.WIDTH, compressed[0].length);
        assertEquals(5, compressed[5][3]);
    }
    
    @Test
    void testGetVisibleBoard() {
        board.setCellColor(GameBoard.BUFFER_ZONE + 5, 3, 5);
        
        int[][] visible = board.getVisibleBoard();
        
        assertEquals(GameBoard.HEIGHT, visible.length);
        assertEquals(GameBoard.WIDTH, visible[0].length);
        assertEquals(5, visible[5][3]);
    }
    
    @Test
    void testGetItemAtReturnsNone() {
        // GameBoard default implementation always returns NONE
        assertEquals(ItemBlock.NONE, board.getItemAt(10, 5));
    }
    
    @Test
    void testPlayClearLineEffect() {
        board.playClearLineEffect(2, 5, 7, 10);
        
        // Should not throw exception
        assertNotNull(board);
    }
    
    @Test
    void testProcessPendingClearsIfDue() throws InterruptedException {
        // Fill a complete line
        int lineIndex = GameBoard.HEIGHT + GameBoard.BUFFER_ZONE - 1;
        for (int col = 0; col < GameBoard.WIDTH; col++) {
            board.setCellColor(lineIndex, col, 1);
        }
        
        board.clearLines();
        
        // Wait for pending clear
        Thread.sleep(600);
        
        board.processPendingClearsIfDue();
        
        // Line should be cleared (moved up)
        boolean lineCleared = true;
        for (int col = 0; col < GameBoard.WIDTH; col++) {
            if (board.getCellColor(lineIndex, col) != 0) {
                lineCleared = false;
                break;
            }
        }
        assertTrue(lineCleared);
    }
}
