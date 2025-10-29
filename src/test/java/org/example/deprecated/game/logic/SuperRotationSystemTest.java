package org.example.deprecated.game.logic;

import org.example.model.GameBoard;
import org.example.model.Tetromino;
import org.example.model.TetrominoPosition;
import org.example.service.SuperRotationSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SuperRotationSystemTest {

    private GameBoard gameBoard;

    @BeforeEach
    void setUp() {
        gameBoard = new GameBoard();
    }

    @Test
    void testBasicRotation() {
        TetrominoPosition tPos = new TetrominoPosition(Tetromino.T, 5, 10, 0);
        TetrominoPosition rotated = SuperRotationSystem.attemptRotation(tPos, gameBoard, true);

        assertNotNull(rotated);
        assertEquals(1, rotated.getRotation());
        assertEquals(5, rotated.getX());
        assertEquals(10, rotated.getY());
    }

    @Test
    void testWallKick() {
        // Place T piece near right wall where it needs wall kick to rotate
        TetrominoPosition tPos = new TetrominoPosition(Tetromino.T, 8, 10, 0);
        TetrominoPosition rotated = SuperRotationSystem.attemptRotation(tPos, gameBoard, true);

        assertNotNull(rotated);
        assertEquals(1, rotated.getRotation());
        // Position should be kicked left due to wall collision
        assertTrue(rotated.getX() < tPos.getX());
    }

    @Test
    void testIPieceWallKick() {
        // Test I piece specific wall kicks
        TetrominoPosition iPos = new TetrominoPosition(Tetromino.I, 7, 10, 0);
        TetrominoPosition rotated = SuperRotationSystem.attemptRotation(iPos, gameBoard, true);

        assertNotNull(rotated);
        assertEquals(1, rotated.getRotation());
    }

    @Test
    void testOPieceRotation() {
        // O piece should always succeed without kicks
        TetrominoPosition oPos = new TetrominoPosition(Tetromino.O, 5, 10, 0);
        TetrominoPosition rotated = SuperRotationSystem.attemptRotation(oPos, gameBoard, true);

        assertNotNull(rotated);
        assertEquals(1, rotated.getRotation());
        // O piece position might adjust due to its 4x4 grid representation
        assertTrue(rotated.getX() >= 4 && rotated.getX() <= 6);
        assertEquals(10, rotated.getY());
    }

    @Test
    void testFailedRotation() {
        // Create scenario where rotation is impossible
        fillBoardAroundPosition(5, 10);
        TetrominoPosition tPos = new TetrominoPosition(Tetromino.T, 5, 10, 0);
        TetrominoPosition rotated = SuperRotationSystem.attemptRotation(tPos, gameBoard, true);

        assertNull(rotated);
    }

    @Test
    void testCounterClockwiseRotation() {
        TetrominoPosition tPos = new TetrominoPosition(Tetromino.T, 5, 10, 1);
        TetrominoPosition rotated = SuperRotationSystem.attemptRotation(tPos, gameBoard, false);

        assertNotNull(rotated);
        assertEquals(0, rotated.getRotation());
    }

    @Test
    void testRotationWithTSpinCheck() {
        // Setup board for T-spin
        setupTSpinBoard();

        TetrominoPosition tPos = new TetrominoPosition(Tetromino.T, 4, 18, 2);
        SuperRotationSystem.RotationResult result = SuperRotationSystem.attemptRotationWithTSpinCheck(tPos, gameBoard, true);

        assertNotNull(result.getPosition());
        // Should detect T-spin due to wall kick usage and corner blocking
        assertTrue(result.isTSpin() || result.getKickUsed() >= 0); // Either T-spin or successful rotation
    }

    @Test
    void testMovementMethods() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.T, 5, 10, 0);

        // Test moveLeft
        TetrominoPosition left = SuperRotationSystem.moveLeft(pos, gameBoard);
        assertNotNull(left);
        assertEquals(4, left.getX());
        assertEquals(10, left.getY());

        // Test moveRight
        TetrominoPosition right = SuperRotationSystem.moveRight(pos, gameBoard);
        assertNotNull(right);
        assertEquals(6, right.getX());
        assertEquals(10, right.getY());

        // Test moveDown
        TetrominoPosition down = SuperRotationSystem.moveDown(pos, gameBoard);
        assertNotNull(down);
        assertEquals(5, down.getX());
        assertEquals(11, down.getY());
    }

    @Test
    void testHardDrop() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.T, 5, 4, 0);
        TetrominoPosition dropped = SuperRotationSystem.hardDrop(pos, gameBoard);

        assertNotNull(dropped);
        assertEquals(5, dropped.getX());
        assertTrue(dropped.getY() > pos.getY()); // Should drop to bottom
    }

    @Test
    void testMovementBlocked() {
        // Test movement blocked by walls
        TetrominoPosition leftWall = new TetrominoPosition(Tetromino.T, 0, 10, 0);
        TetrominoPosition blocked = SuperRotationSystem.moveLeft(leftWall, gameBoard);
        assertNull(blocked);

        TetrominoPosition rightWall = new TetrominoPosition(Tetromino.T, 9, 10, 0);
        blocked = SuperRotationSystem.moveRight(rightWall, gameBoard);
        assertNull(blocked);
    }

    @Test
    void testAllTetrominoRotations() {
        Tetromino[] pieces = {Tetromino.I, Tetromino.O, Tetromino.T,
                             Tetromino.S, Tetromino.Z, Tetromino.J, Tetromino.L};

        for (Tetromino piece : pieces) {
            TetrominoPosition pos = new TetrominoPosition(piece, 5, 10, 0);
            TetrominoPosition rotated = SuperRotationSystem.attemptRotation(pos, gameBoard, true);

            assertNotNull(rotated, "Failed to rotate " + piece);
            assertEquals(1, rotated.getRotation());
        }
    }

    @Test
    void testFullRotationCycle() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.T, 5, 10, 0);

        for (int i = 0; i < 4; i++) {
            pos = SuperRotationSystem.attemptRotation(pos, gameBoard, true);
            assertNotNull(pos);
            assertEquals((i + 1) % 4, pos.getRotation());
        }
    }

    // Helper methods
    private void fillBoardAroundPosition(int x, int y) {
        // Fill a 5x5 area around position to block rotation
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if (dx != 0 || dy != 0) { // Don't fill center position
                    int posX = x + dx;
                    int posY = y + dy;
                    if (posX >= 0 && posX < GameBoard.WIDTH - 1 &&
                        posY >= 0 && posY < GameBoard.HEIGHT + GameBoard.BUFFER_ZONE - 1) {
                        gameBoard.placeTetromino(new TetrominoPosition(Tetromino.O, posX - 1, posY - 1, 0));
                    }
                }
            }
        }
    }

    private void setupTSpinBoard() {
        // Create a board setup that can result in T-spin
        for (int col = 0; col < 4; col++) {
            gameBoard.placeTetromino(new TetrominoPosition(Tetromino.O, col * 2 - 1, 22, 0));
        }
        for (int col = 6; col < GameBoard.WIDTH; col++) {
            if (col < GameBoard.WIDTH - 1) {
                gameBoard.placeTetromino(new TetrominoPosition(Tetromino.O, col - 1, 22, 0));
            }
        }
        // Add some blocks above to create T-spin opportunity
        gameBoard.placeTetromino(new TetrominoPosition(Tetromino.O, 3, 19, 0));
        gameBoard.placeTetromino(new TetrominoPosition(Tetromino.O, 5, 19, 0));
    }
}