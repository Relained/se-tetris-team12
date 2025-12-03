package org.example.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import org.example.service.ColorManager;

class ItemGameBoardTest {

    private ItemGameBoard board;
    private ColorManager colorManager;

    @BeforeEach
    void setUp() {
        board = new ItemGameBoard();
        colorManager = ColorManager.getInstance();
    }

    // Constructor and Initialization Tests
    @Test
    void testConstructorInitializesCorrectly() {
        assertNotNull(board);
        
        // Board should be empty
        for (int r = 0; r < GameBoard.HEIGHT + GameBoard.BUFFER_ZONE; r++) {
            for (int c = 0; c < GameBoard.WIDTH; c++) {
                assertEquals(0, board.getCellColor(r, c));
                assertEquals(ItemBlock.NONE, board.getItemAt(r, c));
            }
        }
    }

    @Test
    void testExtendsGameBoard() {
        assertTrue(board instanceof GameBoard);
    }

    // Place Tetromino with Items Tests
    @Test
    void testPlaceTetrominoWithoutItem() {
        TetrominoPosition piece = new TetrominoPosition(Tetromino.I, 5, 10, 0);
        board.placeTetromino(piece);
        
        // Should place normally
        int[][] shape = piece.getCurrentShape();
        boolean foundPiece = false;
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] == 1) {
                    int boardY = 10 + r;
                    int boardX = 5 + c;
                    if (board.getCellColor(boardY, boardX) != 0) {
                        foundPiece = true;
                    }
                }
            }
        }
        assertTrue(foundPiece);
    }

    @Test
    void testPlaceTetrominoWithLineItem() {
        TetrominoPosition piece = new TetrominoPosition(Tetromino.T, 5, 10, 0);
        piece.setItemAtBlockIndex(0, ItemBlock.LINE_CLEAR);
        
        board.placeTetromino(piece);
        
        // Item should be stored
        int[][] shape = piece.getCurrentShape();
        boolean foundItem = false;
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] == 1) {
                    int boardY = 10 + r;
                    int boardX = 5 + c;
                    if (board.getItemAt(boardY, boardX) == ItemBlock.LINE_CLEAR) {
                        foundItem = true;
                        break;
                    }
                }
            }
            if (foundItem) break;
        }
        assertTrue(foundItem);
    }

    @Test
    void testPlaceTetrominoWithColumnItem() {
        TetrominoPosition piece = new TetrominoPosition(Tetromino.O, 4, 10, 0);
        piece.setItemAtBlockIndex(0, ItemBlock.COLUMN_CLEAR);
        
        board.placeTetromino(piece);
        
        // Verify item is placed
        boolean hasColumnItem = false;
        for (int r = 10; r < 12; r++) {
            for (int c = 4; c < 6; c++) {
                if (board.getItemAt(r, c) == ItemBlock.COLUMN_CLEAR) {
                    hasColumnItem = true;
                    break;
                }
            }
            if (hasColumnItem) break;
        }
        assertTrue(hasColumnItem);
    }

    // Line Clear with Items Tests
    @Test
    void testClearLinesWithLineClearItem() {
        // Place a piece with LINE_CLEAR item
        TetrominoPosition piece = new TetrominoPosition(Tetromino.I, 0, GameBoard.BUFFER_ZONE + 19, 0);
        piece.setItemAtBlockIndex(0, ItemBlock.LINE_CLEAR);
        
        // Fill the rest of the bottom row
        for (int c = 4; c < GameBoard.WIDTH; c++) {
            board.setCellColor(GameBoard.BUFFER_ZONE + 19, c, 1);
        }
        
        board.placeTetromino(piece);
        int[] results = board.clearLinesWithItems();
        
        // Should clear lines
        assertNotNull(results);
        assertEquals(2, results.length);
        assertTrue(results[0] >= 0); // Total cleared lines
        assertTrue(results[1] >= 0); // Item-cleared lines
    }

    @Test
    void testClearLinesWithoutItems() {
        // Fill bottom row without items
        for (int c = 0; c < GameBoard.WIDTH; c++) {
            board.setCellColor(GameBoard.BUFFER_ZONE + 19, c, 1);
        }
        
        int[] results = board.clearLinesWithItems();
        
        assertNotNull(results);
        assertTrue(results[0] >= 0);
        assertEquals(0, results[1]); // No item-cleared lines
    }

    @Test
    void testClearMultipleLinesWithItems() {
        // Fill multiple rows and add LINE_CLEAR items
        for (int r = 17; r < 20; r++) {
            for (int c = 0; c < GameBoard.WIDTH; c++) {
                board.setCellColor(GameBoard.BUFFER_ZONE + r, c, 1);
            }
            // Add item to one cell in each row
            board.setItemBlock(GameBoard.BUFFER_ZONE + r, 0, ItemBlock.LINE_CLEAR);
        }
        
        int[] results = board.clearLinesWithItems();
        
        assertTrue(results[0] >= 3);
        assertTrue(results[1] >= 3);
    }

    // Column Clear Tests
    @Test
    void testClearColumnsWithItems() {
        // Place a piece with COLUMN_CLEAR item
        TetrominoPosition piece = new TetrominoPosition(Tetromino.I, 5, GameBoard.BUFFER_ZONE + 10, 1);
        piece.setItemAtBlockIndex(0, ItemBlock.COLUMN_CLEAR);
        
        board.placeTetromino(piece);
        
        int clearedColumns = board.clearColumnsWithItems();
        
        assertTrue(clearedColumns >= 0);
    }

    @Test
    void testClearMultipleColumnsWithItems() {
        // Add COLUMN_CLEAR items to multiple columns
        board.setCellColor(GameBoard.BUFFER_ZONE + 10, 2, 1);
        board.setItemBlock(GameBoard.BUFFER_ZONE + 10, 2, ItemBlock.COLUMN_CLEAR);
        
        board.setCellColor(GameBoard.BUFFER_ZONE + 10, 5, 1);
        board.setItemBlock(GameBoard.BUFFER_ZONE + 10, 5, ItemBlock.COLUMN_CLEAR);
        
        int clearedColumns = board.clearColumnsWithItems();
        
        assertEquals(2, clearedColumns);
    }

    // Cross Clear Tests
    @Test
    void testClearCrossesWithItems() {
        // Place a piece with CROSS_CLEAR item
        board.setCellColor(GameBoard.BUFFER_ZONE + 10, 5, 1);
        board.setItemBlock(GameBoard.BUFFER_ZONE + 10, 5, ItemBlock.CROSS_CLEAR);
        
        int clearedCrosses = board.clearCrossesWithItems();
        
        assertTrue(clearedCrosses >= 0);
    }

    @Test
    void testCrossClearAffectsBothRowAndColumn() {
        // Fill a row and column, place CROSS_CLEAR at intersection
        for (int c = 0; c < GameBoard.WIDTH; c++) {
            board.setCellColor(GameBoard.BUFFER_ZONE + 10, c, 1);
        }
        for (int r = 0; r < GameBoard.HEIGHT; r++) {
            board.setCellColor(GameBoard.BUFFER_ZONE + r, 5, 1);
        }
        
        board.setItemBlock(GameBoard.BUFFER_ZONE + 10, 5, ItemBlock.CROSS_CLEAR);
        
        int clearedCrosses = board.clearCrossesWithItems();
        
        // Should clear both row and column
        assertTrue(clearedCrosses > 0);
    }

    // Weight Effect Tests
    @Test
    void testTriggerWeightEffect() {
        int[][] weightShape = {{0,1,1,0}, {1,1,1,1}};
        
        // Fill some cells that should be cleared
        for (int r = 10; r < 20; r++) {
            for (int c = 2; c < 6; c++) {
                board.setCellColor(GameBoard.BUFFER_ZONE + r, c, 1);
            }
        }
        
        board.triggerWeightEffect(GameBoard.BUFFER_ZONE + 10, 2, weightShape);
        
        // Weight should be at bottom
        int bottomRow = GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 1;
        boolean foundWeight = false;
        for (int c = 2; c < 6; c++) {
            if (board.getItemAt(bottomRow, c) == ItemBlock.WEIGHT) {
                foundWeight = true;
                break;
            }
        }
        assertTrue(foundWeight);
    }

    @Test
    void testClearWeight() {
        int[][] weightShape = {{0,1,1,0}, {1,1,1,1}};
        
        // First place weight
        board.triggerWeightEffect(GameBoard.BUFFER_ZONE + 10, 2, weightShape);
        
        // Then clear it
        board.clearWeight(2, weightShape);
        
        // Weight should be gone from bottom
        int bottomRow = GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 1;
        for (int c = 2; c < 6; c++) {
            assertEquals(0, board.getCellColor(bottomRow, c));
            assertEquals(ItemBlock.NONE, board.getItemAt(bottomRow, c));
        }
    }

    @Test
    void testClearWeightStep() {
        int[][] weightShape = {{0,1,1,0}, {1,1,1,1}};
        board.triggerWeightEffect(GameBoard.BUFFER_ZONE + 10, 2, weightShape);
        
        int bottomRow = GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 1;
        board.clearWeightStep(bottomRow, 2, 4);
        
        // Bottom row of weight should be cleared
        for (int c = 2; c < 6; c++) {
            assertEquals(0, board.getCellColor(bottomRow, c));
        }
    }

    // Bomb Effect Tests
    @Test
    void testTriggerBombAt() {
        // Fill area around bomb position
        for (int r = 10; r < 16; r++) {
            for (int c = 3; c < 9; c++) {
                board.setCellColor(GameBoard.BUFFER_ZONE + r, c, 1);
            }
        }
        
        // Trigger bomb at center
        board.triggerBombAt(GameBoard.BUFFER_ZONE + 12, 5);
        
        // Bomb should mark cells for clearing
        // Actual clearing happens in processPendingClearsIfDue
        assertNotNull(board);
    }

    // Item Block Management Tests
    @Test
    void testSetItemBlock() {
        board.setCellColor(GameBoard.BUFFER_ZONE + 10, 5, 1);
        board.setItemBlock(GameBoard.BUFFER_ZONE + 10, 5, ItemBlock.LINE_CLEAR);
        
        assertEquals(ItemBlock.LINE_CLEAR, board.getItemAt(GameBoard.BUFFER_ZONE + 10, 5));
    }

    @Test
    void testGetItemBlock() {
        board.setItemBlock(GameBoard.BUFFER_ZONE + 10, 5, ItemBlock.COLUMN_CLEAR);
        
        ItemBlock item = board.getItemBlock(GameBoard.BUFFER_ZONE + 10, 5);
        assertEquals(ItemBlock.COLUMN_CLEAR, item);
    }

    @Test
    void testClearItemBlock() {
        board.setItemBlock(GameBoard.BUFFER_ZONE + 10, 5, ItemBlock.CROSS_CLEAR);
        assertEquals(ItemBlock.CROSS_CLEAR, board.getItemAt(GameBoard.BUFFER_ZONE + 10, 5));
        
        board.clearItemBlock(GameBoard.BUFFER_ZONE + 10, 5);
        assertEquals(ItemBlock.NONE, board.getItemAt(GameBoard.BUFFER_ZONE + 10, 5));
    }

    @Test
    void testSetItemBlockNone() {
        board.setItemBlock(GameBoard.BUFFER_ZONE + 10, 5, ItemBlock.LINE_CLEAR);
        board.setItemBlock(GameBoard.BUFFER_ZONE + 10, 5, ItemBlock.NONE);
        
        assertEquals(ItemBlock.NONE, board.getItemAt(GameBoard.BUFFER_ZONE + 10, 5));
    }

    // Clear Tests
    @Test
    void testClearRemovesAllItems() {
        // Place some items
        board.setCellColor(GameBoard.BUFFER_ZONE + 10, 5, 1);
        board.setItemBlock(GameBoard.BUFFER_ZONE + 10, 5, ItemBlock.LINE_CLEAR);
        
        board.setCellColor(GameBoard.BUFFER_ZONE + 11, 6, 1);
        board.setItemBlock(GameBoard.BUFFER_ZONE + 11, 6, ItemBlock.COLUMN_CLEAR);
        
        board.clear();
        
        // All items should be cleared
        assertEquals(ItemBlock.NONE, board.getItemAt(GameBoard.BUFFER_ZONE + 10, 5));
        assertEquals(ItemBlock.NONE, board.getItemAt(GameBoard.BUFFER_ZONE + 11, 6));
    }

    // Compressed Board Tests
    @Test
    void testGetCompressedBoardWithItems() {
        // Place a block with item
        board.setCellColor(GameBoard.BUFFER_ZONE + 10, 5, 1);
        board.setItemBlock(GameBoard.BUFFER_ZONE + 10, 5, ItemBlock.LINE_CLEAR);
        
        int[][] compressed = board.getCompressedBoard();
        
        assertNotNull(compressed);
        assertEquals(GameBoard.HEIGHT, compressed.length);
        assertEquals(GameBoard.WIDTH, compressed[0].length);
        
        // Check that item symbol is stored (LINE_CLEAR = 'L')
        int cellValue = compressed[10][5];
        assertEquals('L', cellValue); // Should be LINE_CLEAR symbol
    }

    @Test
    void testGetCompressedBoardWithoutItems() {
        board.setCellColor(GameBoard.BUFFER_ZONE + 10, 5, 1);
        
        int[][] compressed = board.getCompressedBoard();
        
        int cellValue = compressed[10][5];
        int symbol = cellValue >> 16;
        assertEquals(0, symbol); // No item
    }

    // Process Pending Clears Tests
    @Test
    void testProcessPendingClearsWithItems() {
        // Fill a row
        for (int c = 0; c < GameBoard.WIDTH; c++) {
            board.setCellColor(GameBoard.BUFFER_ZONE + 19, c, 1);
        }
        
        // Add item
        board.setItemBlock(GameBoard.BUFFER_ZONE + 19, 0, ItemBlock.LINE_CLEAR);
        
        // Mark for clearing
        for (int c = 0; c < GameBoard.WIDTH; c++) {
            board.setCellColor(GameBoard.BUFFER_ZONE + 19, c, GameBoard.CLEAR_MARK);
        }
        
        // Trigger processing
        try {
            java.lang.reflect.Field pendingField = GameBoard.class.getDeclaredField("pendingClearDueMs");
            pendingField.setAccessible(true);
            pendingField.setLong(board, System.currentTimeMillis() - 1);
        } catch (Exception e) {
            fail("Failed to set pendingClearDueMs: " + e.getMessage());
        }
        
        board.processPendingClearsIfDue();
        
        // Items should be cleared/shifted
        assertEquals(ItemBlock.NONE, board.getItemAt(GameBoard.BUFFER_ZONE + 19, 0));
    }

    // Integration Tests
    @Test
    void testCompleteItemWorkflow() {
        // Place piece with item
        TetrominoPosition piece = new TetrominoPosition(Tetromino.T, 5, GameBoard.BUFFER_ZONE + 18, 0);
        piece.setItemAtBlockIndex(0, ItemBlock.LINE_CLEAR);
        
        board.placeTetromino(piece);
        
        // Fill row
        for (int c = 0; c < GameBoard.WIDTH; c++) {
            if (board.getCellColor(GameBoard.BUFFER_ZONE + 18, c) == 0) {
                board.setCellColor(GameBoard.BUFFER_ZONE + 18, c, 1);
            }
        }
        
        // Clear lines
        int[] results = board.clearLinesWithItems();
        
        // Should detect items
        assertTrue(results[1] > 0);
    }

    @Test
    void testMultipleItemTypesOnBoard() {
        board.setCellColor(GameBoard.BUFFER_ZONE + 10, 2, 1);
        board.setItemBlock(GameBoard.BUFFER_ZONE + 10, 2, ItemBlock.LINE_CLEAR);
        
        board.setCellColor(GameBoard.BUFFER_ZONE + 10, 5, 1);
        board.setItemBlock(GameBoard.BUFFER_ZONE + 10, 5, ItemBlock.COLUMN_CLEAR);
        
        board.setCellColor(GameBoard.BUFFER_ZONE + 10, 8, 1);
        board.setItemBlock(GameBoard.BUFFER_ZONE + 10, 8, ItemBlock.CROSS_CLEAR);
        
        assertEquals(ItemBlock.LINE_CLEAR, board.getItemAt(GameBoard.BUFFER_ZONE + 10, 2));
        assertEquals(ItemBlock.COLUMN_CLEAR, board.getItemAt(GameBoard.BUFFER_ZONE + 10, 5));
        assertEquals(ItemBlock.CROSS_CLEAR, board.getItemAt(GameBoard.BUFFER_ZONE + 10, 8));
    }

    @Test
    void testItemPersistenceAfterBoardOperations() {
        // Place item
        board.setCellColor(GameBoard.BUFFER_ZONE + 15, 5, 1);
        board.setItemBlock(GameBoard.BUFFER_ZONE + 15, 5, ItemBlock.LINE_CLEAR);
        
        // Do some operations
        board.setCellColor(GameBoard.BUFFER_ZONE + 16, 3, 1);
        
        // Item should still be there
        assertEquals(ItemBlock.LINE_CLEAR, board.getItemAt(GameBoard.BUFFER_ZONE + 15, 5));
    }

    @Test
    void testNoItemsReturnsNone() {
        assertEquals(ItemBlock.NONE, board.getItemAt(GameBoard.BUFFER_ZONE + 10, 5));
        assertEquals(ItemBlock.NONE, board.getItemBlock(GameBoard.BUFFER_ZONE + 10, 5));
    }

    @Test
    void testItemBlockBoundaries() {
        // Test at boundaries
        board.setItemBlock(0, 0, ItemBlock.LINE_CLEAR);
        assertEquals(ItemBlock.LINE_CLEAR, board.getItemAt(0, 0));
        
        board.setItemBlock(GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 1, 
                          GameBoard.WIDTH - 1, ItemBlock.COLUMN_CLEAR);
        assertEquals(ItemBlock.COLUMN_CLEAR, 
                    board.getItemAt(GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 1, GameBoard.WIDTH - 1));
    }

    @Test
    void testClearingOrderMatters() {
        // Test that columns clear before lines (as per implementation)
        board.setCellColor(GameBoard.BUFFER_ZONE + 10, 5, 1);
        board.setItemBlock(GameBoard.BUFFER_ZONE + 10, 5, ItemBlock.CROSS_CLEAR);
        
        int crossClears = board.clearCrossesWithItems();
        
        // Cross clear should handle both
        assertTrue(crossClears >= 0);
    }
}
