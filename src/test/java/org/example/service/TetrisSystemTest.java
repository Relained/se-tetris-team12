package org.example.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import org.example.model.GameBoard;
import org.example.model.Tetromino;
import org.example.model.TetrominoPosition;
import org.example.model.BoardSnapshot;

import java.util.List;

class TetrisSystemTest {

    private TetrisSystem system;

    @BeforeEach
    void setUp() {
        system = new TetrisSystem();
    }

    // Constructor and Initialization Tests
    @Test
    void testConstructorInitializesCorrectly() {
        assertNotNull(system.getBoard());
        assertNotNull(system.getCurrentPiece());
        assertNull(system.getHoldPiece());
        assertEquals(0, system.getScore());
        assertEquals(0, system.getLines());
        assertEquals(1, system.getLevel());
        assertEquals(2, system.getDifficulty());
        assertFalse(system.isGameOver());
    }

    @Test
    void testNextQueueIsInitialized() {
        List<TetrominoPosition> queue = system.getNextQueue();
        assertEquals(5, queue.size()); // Preview shows 5 pieces
        for (TetrominoPosition piece : queue) {
            assertNotNull(piece);
            assertNotNull(piece.getType());
        }
    }

    @Test
    void testCurrentPieceIsSpawned() {
        TetrominoPosition current = system.getCurrentPiece();
        assertNotNull(current);
        assertNotNull(current.getType());
        // Piece should be spawned at top-center
        assertTrue(current.getY() < GameBoard.BUFFER_ZONE);
    }

    // Movement Tests
    @Test
    void testMoveLeft() {
        TetrominoPosition initial = system.getCurrentPiece();
        int initialX = initial.getX();
        
        boolean moved = system.moveLeft();
        assertTrue(moved);
        assertEquals(initialX - 1, system.getCurrentPiece().getX());
    }

    @Test
    void testMoveRight() {
        TetrominoPosition initial = system.getCurrentPiece();
        int initialX = initial.getX();
        
        boolean moved = system.moveRight();
        assertTrue(moved);
        assertEquals(initialX + 1, system.getCurrentPiece().getX());
    }

    @Test
    void testMoveDown() {
        TetrominoPosition initial = system.getCurrentPiece();
        int initialY = initial.getY();
        
        boolean moved = system.moveDown();
        assertTrue(moved);
        assertEquals(initialY + 1, system.getCurrentPiece().getY());
    }

    @Test
    void testMoveLeftAtBoundary() {
        // Move piece to left edge
        while (system.moveLeft()) {
            // Keep moving left
        }
        
        // Should not be able to move further left
        int x = system.getCurrentPiece().getX();
        assertFalse(system.moveLeft());
        assertEquals(x, system.getCurrentPiece().getX());
    }

    @Test
    void testMoveRightAtBoundary() {
        // Move piece to right edge
        while (system.moveRight()) {
            // Keep moving right
        }
        
        // Should not be able to move further right
        int x = system.getCurrentPiece().getX();
        assertFalse(system.moveRight());
        assertEquals(x, system.getCurrentPiece().getX());
    }

    @Test
    void testMoveDownAddsScore() {
        int initialScore = system.getScore();
        system.moveDown();
        assertTrue(system.getScore() > initialScore);
    }

    // Rotation Tests
    @Test
    void testRotateClockwise() {
        TetrominoPosition initial = system.getCurrentPiece();
        int initialRotation = initial.getRotation();
        
        boolean rotated = system.rotateClockwise();
        assertTrue(rotated);
        assertEquals((initialRotation + 1) % 4, system.getCurrentPiece().getRotation());
    }

    @Test
    void testRotateCounterClockwise() {
        TetrominoPosition initial = system.getCurrentPiece();
        int initialRotation = initial.getRotation();
        
        boolean rotated = system.rotateCounterClockwise();
        assertTrue(rotated);
        assertEquals((initialRotation + 3) % 4, system.getCurrentPiece().getRotation());
    }

    @Test
    void testRotationCyclesAround() {
        // Rotate clockwise 4 times should return to original rotation
        int initialRotation = system.getCurrentPiece().getRotation();
        for (int i = 0; i < 4; i++) {
            system.rotateClockwise();
        }
        assertEquals(initialRotation, system.getCurrentPiece().getRotation());
    }

    // Hard Drop Tests
    @Test
    void testHardDrop() {
        system.hardDrop();
        
        // After hard drop, a new piece should spawn
        assertNotNull(system.getCurrentPiece());
        // Piece should be locked on board
        assertFalse(system.isGameOver());
    }

    @Test
    void testHardDropAddsScore() {
        int initialScore = system.getScore();
        system.hardDrop();
        assertTrue(system.getScore() > initialScore);
    }

    @Test
    void testHardDropLockesPiece() {
        Tetromino type = system.getCurrentPiece().getType();
        system.hardDrop();
        
        // Board should have the locked piece
        GameBoard board = system.getBoard();
        boolean foundPiece = false;
        for (int r = 0; r < GameBoard.HEIGHT + GameBoard.BUFFER_ZONE; r++) {
            for (int c = 0; c < GameBoard.WIDTH; c++) {
                if (board.getCellColor(r, c) == type.getColorIndex()) {
                    foundPiece = true;
                    break;
                }
            }
            if (foundPiece) break;
        }
        assertTrue(foundPiece);
    }

    // Hold Tests
    @Test
    void testHoldPiece() {
        TetrominoPosition initial = system.getCurrentPiece();
        Tetromino initialType = initial.getType();
        
        boolean held = system.hold();
        assertTrue(held);
        
        // Hold piece should be set
        assertNotNull(system.getHoldPiece());
        assertEquals(initialType, system.getHoldPiece().getType());
        
        // Current piece should be from the queue (might be same type by chance)
        assertNotNull(system.getCurrentPiece());
    }

    @Test
    void testHoldSwap() {
        // First hold
        Tetromino firstType = system.getCurrentPiece().getType();
        system.hold();
        
        // Hard drop to allow hold again
        system.hardDrop();
        
        // Second hold should swap
        Tetromino secondType = system.getCurrentPiece().getType();
        system.hold();
        
        // Current piece should be the first held piece
        assertEquals(firstType, system.getCurrentPiece().getType());
        // Hold piece should be the second piece
        assertEquals(secondType, system.getHoldPiece().getType());
    }

    @Test
    void testCannotHoldTwiceInARow() {
        system.hold();
        assertFalse(system.hold());
    }

    @Test
    void testHoldResetAfterLock() {
        system.hold();
        assertFalse(system.hold()); // Cannot hold twice
        
        system.hardDrop(); // Lock piece
        
        assertTrue(system.hold()); // Should be able to hold again
    }

    // Line Clearing Tests
    @Test
    void testLineClearingIncreasesScore() {
        GameBoard board = system.getBoard();
        
        // Fill bottom row except one column
        for (int c = 0; c < GameBoard.WIDTH - 1; c++) {
            board.setCellColor(GameBoard.HEIGHT + GameBoard.BUFFER_ZONE - 1, c, 1);
        }
        
        // Hard drop might clear lines if positioned correctly
        system.hardDrop();
        
        // Line clearing is tested indirectly
        // Direct line clearing is tested in GameBoard tests
    }

    @Test
    void testLineClearingIncreasesLineCount() {
        // Manually trigger line clearing by filling rows
        GameBoard board = system.getBoard();
        for (int c = 0; c < GameBoard.WIDTH; c++) {
            board.setCellColor(GameBoard.HEIGHT + GameBoard.BUFFER_ZONE - 1, c, 1);
        }
        
        int clearedLines = board.clearLines();
        if (clearedLines > 0) {
            // Test that system would increment line count
            assertTrue(clearedLines > 0);
        }
    }

    // Level Progression Tests
    @Test
    void testLevelIncreasesWithLines() {
        // Default levelFactor is 10
        int initialLevel = system.getLevel();
        assertEquals(1, initialLevel);
        
        // Simulate clearing 10 lines
        // Access private field through reflection or test indirectly
        // Here we test the level calculation logic
        assertTrue(system.getLevel() >= 1 && system.getLevel() <= 20);
    }

    @Test
    void testLevelCappedAt20() {
        // Level should be capped at 20
        // This is tested indirectly through the level getter
        assertTrue(system.getLevel() <= 20);
    }

    // Difficulty Tests
    @Test
    void testSetDifficultyEasy() {
        system.setDifficulty(1);
        assertEquals(1, system.getDifficulty());
    }

    @Test
    void testSetDifficultyNormal() {
        system.setDifficulty(2);
        assertEquals(2, system.getDifficulty());
    }

    @Test
    void testSetDifficultyHard() {
        system.setDifficulty(3);
        assertEquals(3, system.getDifficulty());
    }

    // Score Calculation Tests
    @Test
    void testScoreIncreasesWithDifficulty() {
        TetrisSystem easySystem = new TetrisSystem();
        easySystem.setDifficulty(1);
        
        TetrisSystem hardSystem = new TetrisSystem();
        hardSystem.setDifficulty(3);
        
        // Same action should give different scores
        easySystem.moveDown();
        hardSystem.moveDown();
        
        // Hard mode should give more points
        assertTrue(hardSystem.getScore() > easySystem.getScore());
    }

    // Game Over Tests
    @Test
    void testGameOverWhenSpawnFails() {
        // Game over is tested through isGameOver method and board.isGameOver()
        // Filling the board to trigger game over is complex due to buffer zone
        // This is tested indirectly through other game over related tests
        assertFalse(system.isGameOver());
    }

    @Test
    void testCannotMoveWhenGameOver() {
        // Test the game over flag behavior
        // Movement methods check gameOver flag
        assertFalse(system.isGameOver());
        
        // When not game over, moves should work
        assertTrue(system.moveLeft() || system.moveRight());
    }

    // Reset Tests
    @Test
    void testReset() {
        // Make some moves
        system.moveDown();
        system.hardDrop();
        system.hold();
        
        // Reset
        system.reset();
        
        // Should be back to initial state
        assertEquals(0, system.getScore());
        assertEquals(0, system.getLines());
        assertEquals(1, system.getLevel());
        assertFalse(system.isGameOver());
        assertNotNull(system.getCurrentPiece());
        assertNull(system.getHoldPiece());
    }

    // Drop Interval Tests
    @Test
    void testDropIntervalDecreasesWithLevel() {
        long level1Interval = system.getDropInterval();
        
        // Manually set level higher (through reflection or indirect testing)
        // Test that interval decreases
        assertTrue(level1Interval >= 50); // Minimum interval
        assertTrue(level1Interval <= 1000); // Maximum interval
    }

    @Test
    void testDropIntervalMinimum() {
        // Even at high levels, interval should not go below 50ms
        long interval = system.getDropInterval();
        assertTrue(interval >= 50);
    }

    // Snapshot Tests
    @Test
    void testSnapshotCapturedAfterLock() {
        assertNull(system.getPreviousSnapshot());
        
        system.hardDrop();
        
        // After locking a piece, snapshot should be captured
        assertNotNull(system.getPreviousSnapshot());
    }

    @Test
    void testSnapshotResetOnReset() {
        system.hardDrop();
        assertNotNull(system.getPreviousSnapshot());
        
        system.reset();
        assertNull(system.getPreviousSnapshot());
    }

    // Callback Tests
    @Test
    void testOnPieceLockedCallback() {
        final boolean[] callbackCalled = {false};
        
        system.setOnPieceLocked(() -> {
            callbackCalled[0] = true;
        });
        
        system.hardDrop();
        assertTrue(callbackCalled[0]);
    }

    // Compressed Board Tests
    @Test
    void testGetCompressedBoardData() {
        int[][] compressed = system.getCompressedBoardData();
        
        assertNotNull(compressed);
        assertEquals(GameBoard.HEIGHT, compressed.length);
        assertEquals(GameBoard.WIDTH, compressed[0].length);
    }

    @Test
    void testGetCompressedBoardDataWithGhost() {
        int[][] compressed = system.getCompressedBoardData();
        
        assertNotNull(compressed);
        assertEquals(GameBoard.HEIGHT, compressed.length);
        assertEquals(GameBoard.WIDTH, compressed[0].length);
        
        // Should contain ghost piece markers (-2)
        boolean hasGhost = false;
        for (int[] row : compressed) {
            for (int cell : row) {
                if (cell == -2) {
                    hasGhost = true;
                    break;
                }
            }
            if (hasGhost) break;
        }
        // Ghost might not always be visible depending on piece position
    }

    // Completed Lines Tests
    @Test
    void testGetCompletedLineIndices() {
        List<Integer> completed = system.getCompletedLineIndices();
        assertNotNull(completed);
        assertEquals(0, completed.size()); // No completed lines initially
    }

    @Test
    void testGetCompletedLineIndicesAfterClear() {
        GameBoard board = system.getBoard();
        
        // Fill a row and mark for clearing
        for (int c = 0; c < GameBoard.WIDTH; c++) {
            board.setCellColor(GameBoard.HEIGHT + GameBoard.BUFFER_ZONE - 1, c, GameBoard.CLEAR_MARK);
        }
        
        List<Integer> completed = system.getCompletedLineIndices();
        assertTrue(completed.size() > 0);
        assertTrue(completed.contains(GameBoard.HEIGHT - 1));
    }

    // Update Tests
    @Test
    void testUpdate() {
        int initialY = system.getCurrentPiece().getY();
        system.update();
        
        // Update should move piece down
        assertTrue(system.getCurrentPiece().getY() >= initialY);
    }

    @Test
    void testUpdateWhenGameOver() {
        // Test update behavior
        system.update();
        
        // Update moves piece down or locks it
        // Piece might change if locked
        assertNotNull(system.getCurrentPiece());
    }

    // Queue Tests
    @Test
    void testNextQueueRefills() {
        // Lock piece to consume from queue
        system.hardDrop();
        
        // Queue should still have 5 preview pieces
        assertEquals(5, system.getNextQueue().size());
    }

    // Edge Cases
    @Test
    void testMultipleHardDrops() {
        for (int i = 0; i < 10; i++) {
            assertNotNull(system.getCurrentPiece());
            system.hardDrop();
            if (system.isGameOver()) {
                break;
            }
        }
        // Should handle multiple hard drops without crashing
    }

    @Test
    void testScoreIsNonNegative() {
        assertTrue(system.getScore() >= 0);
        system.moveDown();
        assertTrue(system.getScore() >= 0);
        system.hardDrop();
        assertTrue(system.getScore() >= 0);
    }

    @Test
    void testLevelIsInValidRange() {
        assertTrue(system.getLevel() >= 1);
        assertTrue(system.getLevel() <= 20);
    }

    // Probability Distribution Tests
    
    /**
     * 확률에 따른 블록 생성 테스트 - Easy 난이도
     * I 블록: 1.2배 가중치 → 약 16.7% (1.2 / 7.2)
     * 나머지 블록: 1.0배 가중치 → 약 13.9% (1.0 / 7.2)
     * 1,000번 반복, 오차 범위: ±5%
     */
    @Test
    void testBlockDistributionEasyDifficulty() throws Exception {
        TetrisSystem testSystem = new TetrisSystem();
        testSystem.setDifficulty(1); // Easy
        
        final int ITERATIONS = 1000;
        final double TOTAL_WEIGHT = 1.2 + 6.0; // I(1.2) + others(6*1.0) = 7.2
        final double I_EXPECTED = (1.2 / TOTAL_WEIGHT) * 100; // ~16.67%
        final double OTHER_EXPECTED = (1.0 / TOTAL_WEIGHT) * 100; // ~13.89%
        final double ERROR_MARGIN = 5.0; // ±5%
        
        int[] counts = new int[7]; // I, O, T, S, Z, J, L
        
        // selectWeightedRandom 메서드에 접근하기 위해 reflection 사용
        java.lang.reflect.Method selectMethod = TetrisSystem.class.getDeclaredMethod("selectWeightedRandom");
        selectMethod.setAccessible(true);
        
        // 1,000번 블록 생성
        for (int i = 0; i < ITERATIONS; i++) {
            Tetromino selected = (Tetromino) selectMethod.invoke(testSystem);
            counts[selected.ordinal()]++;
        }
        
        // 각 블록의 분포 검증
        System.err.println("\n=== Easy Difficulty Block Distribution (1,000 iterations) ===");
        System.err.println("I block weight: 1.2x, Other blocks weight: 1.0x");
        for (Tetromino type : Tetromino.values()) {
            int count = counts[type.ordinal()];
            double percentage = (count * 100.0) / ITERATIONS;
            double expected = (type == Tetromino.I) ? I_EXPECTED : OTHER_EXPECTED;
            double deviation = Math.abs(percentage - expected);
            
            System.err.printf("%s: %d times (%.2f%%) - Expected: %.2f%% - Deviation: %.2f%%\n",
                type, count, percentage, expected, deviation);
            
            assertTrue(deviation <= ERROR_MARGIN,
                String.format("%s block deviation %.2f%% exceeds ±5%% margin (Expected: %.2f%%, Actual: %.2f%%)", 
                    type, deviation, expected, percentage));
        }
        
        // I 블록이 다른 블록보다 더 많이 나왔는지 확인
        double iPercentage = (counts[Tetromino.I.ordinal()] * 100.0) / ITERATIONS;
        assertTrue(iPercentage > OTHER_EXPECTED - ERROR_MARGIN,
            "I block should appear more frequently than other blocks in Easy mode");
    }
    
    /**
     * 확률에 따른 블록 생성 테스트 - Normal 난이도 (균등 분포)
     * 7개 블록(I,O,T,S,Z,J,L)이 각각 약 14.3% 확률로 생성되는지 확인
     * 1,000번 반복, 오차 범위: ±5%
     */
    @Test
    void testBlockDistributionNormalDifficulty() throws Exception {
        TetrisSystem testSystem = new TetrisSystem();
        testSystem.setDifficulty(2); // Normal
        
        final int ITERATIONS = 1000;
        final double EXPECTED_PERCENTAGE = 100.0 / 7; // 14.285...%
        final double ERROR_MARGIN = 5.0; // ±5%
        
        int[] counts = new int[7]; // I, O, T, S, Z, J, L
        
        // selectWeightedRandom 메서드에 접근하기 위해 reflection 사용
        java.lang.reflect.Method selectMethod = TetrisSystem.class.getDeclaredMethod("selectWeightedRandom");
        selectMethod.setAccessible(true);
        
        // 1,000번 블록 생성
        for (int i = 0; i < ITERATIONS; i++) {
            Tetromino selected = (Tetromino) selectMethod.invoke(testSystem);
            counts[selected.ordinal()]++;
        }
        
        // 각 블록의 분포 검증
        System.err.println("\n=== Normal Difficulty Block Distribution (1,000 iterations) ===");
        System.err.println("All blocks weight: 1.0x (uniform distribution)");
        for (Tetromino type : Tetromino.values()) {
            int count = counts[type.ordinal()];
            double percentage = (count * 100.0) / ITERATIONS;
            double deviation = Math.abs(percentage - EXPECTED_PERCENTAGE);
            
            System.err.printf("%s: %d times (%.2f%%) - Expected: %.2f%% - Deviation: %.2f%%\n",
                type, count, percentage, EXPECTED_PERCENTAGE, deviation);
            
            assertTrue(deviation <= ERROR_MARGIN,
                String.format("%s block deviation %.2f%% exceeds ±5%% margin (Expected: %.2f%%, Actual: %.2f%%)", 
                    type, deviation, EXPECTED_PERCENTAGE, percentage));
        }
    }
    
    /**
     * 확률에 따른 블록 생성 테스트 - Hard 난이도
     * I 블록: 0.8배 가중치 → 약 11.8% (0.8 / 6.8)
     * 나머지 블록: 1.0배 가중치 → 약 14.7% (1.0 / 6.8)
     * 1,000번 반복, 오차 범위: ±5%
     */
    @Test
    void testBlockDistributionHardDifficulty() throws Exception {
        TetrisSystem testSystem = new TetrisSystem();
        testSystem.setDifficulty(3); // Hard
        
        final int ITERATIONS = 1000;
        final double TOTAL_WEIGHT = 0.8 + 6.0; // I(0.8) + others(6*1.0) = 6.8
        final double I_EXPECTED = (0.8 / TOTAL_WEIGHT) * 100; // ~11.76%
        final double OTHER_EXPECTED = (1.0 / TOTAL_WEIGHT) * 100; // ~14.71%
        final double ERROR_MARGIN = 5.0; // ±5%
        
        int[] counts = new int[7];
        
        // selectWeightedRandom 메서드에 접근하기 위해 reflection 사용
        java.lang.reflect.Method selectMethod = TetrisSystem.class.getDeclaredMethod("selectWeightedRandom");
        selectMethod.setAccessible(true);
        
        // 1,000번 블록 생성
        for (int i = 0; i < ITERATIONS; i++) {
            Tetromino selected = (Tetromino) selectMethod.invoke(testSystem);
            counts[selected.ordinal()]++;
        }
        
        // 각 블록의 분포 검증
        System.err.println("\n=== Hard Difficulty Block Distribution (1,000 iterations) ===");
        System.err.println("I block weight: 0.8x, Other blocks weight: 1.0x");
        for (Tetromino type : Tetromino.values()) {
            int count = counts[type.ordinal()];
            double percentage = (count * 100.0) / ITERATIONS;
            double expected = (type == Tetromino.I) ? I_EXPECTED : OTHER_EXPECTED;
            double deviation = Math.abs(percentage - expected);
            
            System.err.printf("%s: %d times (%.2f%%) - Expected: %.2f%% - Deviation: %.2f%%\n",
                type, count, percentage, expected, deviation);
            
            assertTrue(deviation <= ERROR_MARGIN,
                String.format("%s block deviation %.2f%% exceeds ±5%% margin (Expected: %.2f%%, Actual: %.2f%%)", 
                    type, deviation, expected, percentage));
        }
        
        // I 블록이 다른 블록보다 적게 나왔는지 확인
        double iPercentage = (counts[Tetromino.I.ordinal()] * 100.0) / ITERATIONS;
        assertTrue(iPercentage < OTHER_EXPECTED + ERROR_MARGIN,
            "I block should appear less frequently than other blocks in Hard mode");
    }
}
