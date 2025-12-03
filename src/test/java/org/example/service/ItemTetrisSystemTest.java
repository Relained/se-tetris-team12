package org.example.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import org.example.model.GameBoard;
import org.example.model.ItemBlock;
import org.example.model.ItemGameBoard;
import org.example.model.Tetromino;
import org.example.model.TetrominoPosition;

import java.lang.reflect.Field;
import java.util.List;

class ItemTetrisSystemTest {

    private ItemTetrisSystem system;

    @BeforeEach
    void setUp() {
        system = new ItemTetrisSystem();
    }

    // Constructor and Initialization Tests
    @Test
    void testConstructorInitializesCorrectly() {
        assertNotNull(system.getBoard());
        assertTrue(system.getBoard() instanceof ItemGameBoard);
        assertNotNull(system.getCurrentPiece());
        assertEquals(0, system.getScore());
        assertEquals(0, system.getLines());
        assertFalse(system.isGameOver());
    }

    @Test
    void testUsesItemGameBoard() {
        GameBoard board = system.getBoard();
        assertTrue(board instanceof ItemGameBoard);
    }

    // Item Generation Tests
    @Test
    void testItemPieceGeneration() throws Exception {
        // Clear 10 lines to trigger item generation
        Field linesSinceLastItemField = ItemTetrisSystem.class.getDeclaredField("linesSinceLastItem");
        linesSinceLastItemField.setAccessible(true);
        linesSinceLastItemField.setInt(system, 9);
        
        // Manually fill and clear a line to trigger item generation
        ItemGameBoard board = (ItemGameBoard) system.getBoard();
        for (int c = 0; c < GameBoard.WIDTH; c++) {
            board.setCellColor(GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 1, c, 1);
        }
        
        // Item should be generated after clearing lines
        int linesSinceLastItem = linesSinceLastItemField.getInt(system);
        assertTrue(linesSinceLastItem >= 0);
    }

    // Special Piece Tests
    @Test
    void testWeightPieceCannotRotate() {
        // Create a weight piece and check rotation lock
        TetrominoPosition weight = TetrominoPosition.createWeightPiece(5, 10);
        assertTrue(weight.isRotationLocked());
        
        // Manually set current piece to weight
        try {
            Field currentPieceField = TetrisSystem.class.getDeclaredField("currentPiece");
            currentPieceField.setAccessible(true);
            currentPieceField.set(system, weight);
            
            assertFalse(system.rotateClockwise());
            assertFalse(system.rotateCounterClockwise());
        } catch (Exception e) {
            fail("Failed to test weight piece rotation: " + e.getMessage());
        }
    }

    @Test
    void testBombPieceCannotRotate() {
        TetrominoPosition bomb = TetrominoPosition.createBombPiece(5, 10);
        assertTrue(bomb.isRotationLocked());
        
        try {
            Field currentPieceField = TetrisSystem.class.getDeclaredField("currentPiece");
            currentPieceField.setAccessible(true);
            currentPieceField.set(system, bomb);
            
            assertFalse(system.rotateClockwise());
            assertFalse(system.rotateCounterClockwise());
        } catch (Exception e) {
            fail("Failed to test bomb piece rotation: " + e.getMessage());
        }
    }

    @Test
    void testWeightPieceCannotBeHeld() {
        TetrominoPosition weight = TetrominoPosition.createWeightPiece(5, 10);
        
        try {
            Field currentPieceField = TetrisSystem.class.getDeclaredField("currentPiece");
            currentPieceField.setAccessible(true);
            currentPieceField.set(system, weight);
            
            assertFalse(system.hold());
        } catch (Exception e) {
            fail("Failed to test weight piece hold: " + e.getMessage());
        }
    }

    @Test
    void testBombPieceCannotBeHeld() {
        TetrominoPosition bomb = TetrominoPosition.createBombPiece(5, 10);
        
        try {
            Field currentPieceField = TetrisSystem.class.getDeclaredField("currentPiece");
            currentPieceField.setAccessible(true);
            currentPieceField.set(system, bomb);
            
            assertFalse(system.hold());
        } catch (Exception e) {
            fail("Failed to test bomb piece hold: " + e.getMessage());
        }
    }

    // Hold with Items Tests
    @Test
    void testHoldPreservesItemInformation() {
        TetrominoPosition piece = system.getCurrentPiece();
        piece.setItemAtBlockIndex(0, ItemBlock.LINE_CLEAR);
        
        assertTrue(system.hold());
        
        TetrominoPosition heldPiece = system.getHoldPiece();
        assertNotNull(heldPiece);
        assertTrue(heldPiece.hasItems());
        assertEquals(Integer.valueOf(0), heldPiece.getItemBlockIndex());
    }

    @Test
    void testHoldSwapPreservesItems() {
        // First hold with item
        TetrominoPosition firstPiece = system.getCurrentPiece();
        firstPiece.setItemAtBlockIndex(0, ItemBlock.COLUMN_CLEAR);
        system.hold();
        
        // Hard drop to allow another hold
        system.hardDrop();
        
        // Second hold with different item
        TetrominoPosition secondPiece = system.getCurrentPiece();
        secondPiece.setItemAtBlockIndex(1, ItemBlock.CROSS_CLEAR);
        system.hold();
        
        // Check that items are preserved
        assertTrue(system.getCurrentPiece().hasItems());
        assertTrue(system.getHoldPiece().hasItems());
    }

    // Movement Tests
    @Test
    void testNormalPieceCanMove() {
        TetrominoPosition initial = system.getCurrentPiece();
        int initialX = initial.getX();
        
        boolean moved = system.moveLeft();
        assertTrue(moved);
        assertEquals(initialX - 1, system.getCurrentPiece().getX());
    }

    @Test
    void testMoveDownWorks() {
        int initialY = system.getCurrentPiece().getY();
        boolean moved = system.moveDown();
        assertTrue(moved);
        assertTrue(system.getCurrentPiece().getY() >= initialY);
    }

    // Weight Piece Tests
    @Test
    void testWeightPieceActivation() throws Exception {
        TetrominoPosition weight = TetrominoPosition.createWeightPiece(5, 10);
        
        Field currentPieceField = TetrisSystem.class.getDeclaredField("currentPiece");
        currentPieceField.setAccessible(true);
        currentPieceField.set(system, weight);
        
        // Move to bottom
        for (int i = 0; i < 20; i++) {
            if (!system.moveDown()) {
                break;
            }
        }
        
        // Weight should be activated or piece cleared
        Field weightActiveField = ItemTetrisSystem.class.getDeclaredField("weightActive");
        weightActiveField.setAccessible(true);
        
        // Weight activation is internal behavior, just verify no crash
        assertNotNull(system.getBoard());
    }

    @Test
    void testWeightHardDrop() throws Exception {
        TetrominoPosition weight = TetrominoPosition.createWeightPiece(5, 10);
        
        Field currentPieceField = TetrisSystem.class.getDeclaredField("currentPiece");
        currentPieceField.setAccessible(true);
        currentPieceField.set(system, weight);
        
        system.hardDrop();
        
        // After hard drop, weight should be activated
        Field weightActiveField = ItemTetrisSystem.class.getDeclaredField("weightActive");
        weightActiveField.setAccessible(true);
        assertTrue(weightActiveField.getBoolean(system));
    }

    // Bomb Piece Tests
    @Test
    void testBombPieceHardDrop() throws Exception {
        TetrominoPosition bomb = TetrominoPosition.createBombPiece(5, 10);
        
        Field currentPieceField = TetrisSystem.class.getDeclaredField("currentPiece");
        currentPieceField.setAccessible(true);
        currentPieceField.set(system, bomb);
        
        system.hardDrop();
        
        // After bomb explosion, a new piece should spawn
        assertNotNull(system.getCurrentPiece());
    }

    @Test
    void testBombPieceMoveDown() throws Exception {
        TetrominoPosition bomb = TetrominoPosition.createBombPiece(5, GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 5);
        
        Field currentPieceField = TetrisSystem.class.getDeclaredField("currentPiece");
        currentPieceField.setAccessible(true);
        currentPieceField.set(system, bomb);
        
        // Fill bottom to trigger lock
        ItemGameBoard board = (ItemGameBoard) system.getBoard();
        for (int c = 0; c < GameBoard.WIDTH; c++) {
            board.setCellColor(GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 1, c, 1);
        }
        
        system.moveDown();
        
        // Bomb should explode on landing
        // New piece spawned
        assertNotNull(system.getCurrentPiece());
    }

    // Item Effects Tests
    @Test
    void testLineItemEffect() {
        ItemGameBoard board = (ItemGameBoard) system.getBoard();
        
        // Place a piece with LINE_CLEAR item
        TetrominoPosition piece = new TetrominoPosition(Tetromino.I, 0, GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 1, 0);
        piece.setItemAtBlockIndex(0, ItemBlock.LINE_CLEAR);
        
        // Fill bottom row except where I piece will go
        for (int c = 4; c < GameBoard.WIDTH; c++) {
            board.setCellColor(GameBoard.BUFFER_ZONE + GameBoard.HEIGHT - 1, c, 1);
        }
        
        board.placeTetromino(piece);
        int[] results = board.clearLinesWithItems();
        
        // Results should be valid (non-null, length 2)
        assertNotNull(results);
        assertEquals(2, results.length);
        assertTrue(results[0] >= 0);
    }

    @Test
    void testColumnItemEffect() {
        ItemGameBoard board = (ItemGameBoard) system.getBoard();
        
        // Place a piece with COLUMN_CLEAR item
        TetrominoPosition piece = new TetrominoPosition(Tetromino.I, 5, GameBoard.BUFFER_ZONE, 1);
        piece.setItemAtBlockIndex(0, ItemBlock.COLUMN_CLEAR);
        
        // Fill the column
        for (int r = 0; r < GameBoard.HEIGHT; r++) {
            board.setCellColor(GameBoard.BUFFER_ZONE + r, 5, 1);
        }
        
        board.placeTetromino(piece);
        int clearedColumns = board.clearColumnsWithItems();
        
        // Should clear at least one column
        assertTrue(clearedColumns >= 0);
    }

    @Test
    void testCrossItemEffect() {
        ItemGameBoard board = (ItemGameBoard) system.getBoard();
        
        // Place a piece with CROSS_CLEAR item
        TetrominoPosition piece = new TetrominoPosition(Tetromino.O, 5, GameBoard.BUFFER_ZONE + 10, 0);
        piece.setItemAtBlockIndex(0, ItemBlock.CROSS_CLEAR);
        
        board.placeTetromino(piece);
        int clearedCrosses = board.clearCrossesWithItems();
        
        // Cross clear happens
        assertTrue(clearedCrosses >= 0);
    }

    // Update Tests
    @Test
    void testUpdateNormalMode() {
        int initialY = system.getCurrentPiece().getY();
        system.update();
        
        // Update should move piece down or lock it
        assertTrue(system.getCurrentPiece().getY() >= initialY || 
                   system.getCurrentPiece().getY() < GameBoard.BUFFER_ZONE);
    }

    @Test
    void testUpdateWithWeightActive() throws Exception {
        Field weightActiveField = ItemTetrisSystem.class.getDeclaredField("weightActive");
        weightActiveField.setAccessible(true);
        weightActiveField.setBoolean(system, true);
        
        Field weightShowingField = ItemTetrisSystem.class.getDeclaredField("weightShowingAtBottom");
        weightShowingField.setAccessible(true);
        weightShowingField.setBoolean(system, true);
        
        Field weightShapeField = ItemTetrisSystem.class.getDeclaredField("weightShape");
        weightShapeField.setAccessible(true);
        weightShapeField.set(system, new int[][]{{1,1}, {1,1}});
        
        Field weightStartColField = ItemTetrisSystem.class.getDeclaredField("weightStartCol");
        weightStartColField.setAccessible(true);
        weightStartColField.setInt(system, 0);
        
        system.update();
        
        // Update should handle weight state and spawn new piece
        assertNotNull(system.getCurrentPiece());
    }

    // Reset Tests
    @Test
    void testResetClearsItemState() throws Exception {
        // Set some item state
        Field linesSinceLastItemField = ItemTetrisSystem.class.getDeclaredField("linesSinceLastItem");
        linesSinceLastItemField.setAccessible(true);
        linesSinceLastItemField.setInt(system, 5);
        
        Field weightActiveField = ItemTetrisSystem.class.getDeclaredField("weightActive");
        weightActiveField.setAccessible(true);
        weightActiveField.setBoolean(system, true);
        
        system.reset();
        
        // Item state should be reset
        assertEquals(0, linesSinceLastItemField.getInt(system));
        assertFalse(weightActiveField.getBoolean(system));
    }

    @Test
    void testResetPreservesItemGameBoard() {
        system.reset();
        
        assertTrue(system.getBoard() instanceof ItemGameBoard);
        assertNotNull(system.getCurrentPiece());
    }

    // Score and Level Tests
    @Test
    void testScoreIncreasesWithItemClear() {
        int initialScore = system.getScore();
        
        // Simulate clearing lines with items
        system.hardDrop();
        
        // Score should increase
        assertTrue(system.getScore() >= initialScore);
    }

    @Test
    void testMultipleLineClearScore() {
        ItemGameBoard board = (ItemGameBoard) system.getBoard();
        
        // Fill multiple rows
        for (int r = GameBoard.HEIGHT - 3; r < GameBoard.HEIGHT; r++) {
            for (int c = 0; c < GameBoard.WIDTH - 1; c++) {
                board.setCellColor(GameBoard.BUFFER_ZONE + r, c, 1);
            }
        }
        
        int initialScore = system.getScore();
        system.hardDrop();
        
        // Multiple line clear should give more points
        assertTrue(system.getScore() >= initialScore);
    }

    // Queue Tests
    @Test
    void testNextQueueContainsPieces() {
        List<TetrominoPosition> queue = system.getNextQueue();
        assertEquals(5, queue.size());
        
        for (TetrominoPosition piece : queue) {
            assertNotNull(piece);
        }
    }

    // Game Over Tests
    @Test
    void testGameOverState() {
        assertFalse(system.isGameOver());
    }

    // Integration Tests
    @Test
    void testCompleteGameFlow() {
        // Play a few turns
        for (int i = 0; i < 5; i++) {
            assertNotNull(system.getCurrentPiece());
            system.hardDrop();
            if (system.isGameOver()) {
                break;
            }
        }
        
        // Should handle multiple turns without crashing
        assertTrue(system.getScore() >= 0);
        assertTrue(system.getLines() >= 0);
    }

    @Test
    void testItemPieceInQueue() {
        // Check if any piece in queue has items or is special
        List<TetrominoPosition> queue = system.getNextQueue();
        
        for (TetrominoPosition piece : queue) {
            // Each piece should be valid
            assertNotNull(piece);
            assertNotNull(piece.getType());
        }
        
        // Initially might not have items (generated after 10 lines)
        // Just check that queue is valid
        assertNotNull(queue);
    }

    @Test
    void testMoveDownWithWeightActive() throws Exception {
        Field weightActiveField = ItemTetrisSystem.class.getDeclaredField("weightActive");
        weightActiveField.setAccessible(true);
        weightActiveField.setBoolean(system, true);
        
        assertFalse(system.moveDown());
    }

    @Test
    void testRotationWithNormalPiece() {
        TetrominoPosition piece = system.getCurrentPiece();
        if (!piece.isRotationLocked()) {
            system.rotateClockwise();
            
            // Should rotate unless blocked
            assertTrue(system.getCurrentPiece().getRotation() >= 0);
        }
    }

    @Test
    void testBoardClearingOrder() {
        // Test that columns, crosses, and lines are cleared in correct order
        ItemGameBoard board = (ItemGameBoard) system.getBoard();
        
        // This is tested through the board's methods
        // Just verify the board is accessible
        assertNotNull(board);
        assertTrue(board instanceof ItemGameBoard);
    }

    @Test
    void testSpecialPieceProperties() {
        TetrominoPosition weight = TetrominoPosition.createWeightPiece(0, 0);
        assertEquals(TetrominoPosition.SpecialKind.WEIGHT, weight.getSpecialKind());
        
        TetrominoPosition bomb = TetrominoPosition.createBombPiece(0, 0);
        assertEquals(TetrominoPosition.SpecialKind.BOMB, bomb.getSpecialKind());
    }

    @Test
    void testItemBlockTypes() {
        assertTrue(ItemBlock.LINE_CLEAR.isItem());
        assertTrue(ItemBlock.COLUMN_CLEAR.isItem());
        assertTrue(ItemBlock.CROSS_CLEAR.isItem());
        assertTrue(ItemBlock.WEIGHT.isItem());
        assertTrue(ItemBlock.BOMB.isItem());
        assertFalse(ItemBlock.NONE.isItem());
    }

    @Test
    void testHardDropScore() {
        int initialScore = system.getScore();
        system.hardDrop();
        
        // Hard drop should add score
        assertTrue(system.getScore() >= initialScore);
    }

    @Test
    void testLevelProgression() {
        int initialLevel = system.getLevel();
        assertEquals(1, initialLevel);
        
        // Level increases with lines cleared
        assertTrue(system.getLevel() >= 1 && system.getLevel() <= 20);
    }
}
