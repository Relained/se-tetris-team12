package org.example.service;

import org.example.model.GameBoard;
import org.example.model.Tetromino;
import org.example.model.TetrominoPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SuperRotationSystem Unit Test
 */
class SuperRotationSystemTest {
    
    private GameBoard board;
    
    @BeforeEach
    void setUp() {
        board = new GameBoard();
    }
    
    @Test
    void testMoveLeft() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 5, 10, 0);
        TetrominoPosition newPos = SuperRotationSystem.moveLeft(pos, board);
        
        assertNotNull(newPos);
        assertEquals(4, newPos.getX());
        assertEquals(10, newPos.getY());
    }
    
    @Test
    void testMoveLeftBlocked() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 0, 10, 0);
        TetrominoPosition newPos = SuperRotationSystem.moveLeft(pos, board);
        
        assertNull(newPos); // Cannot move left from x=0
    }
    
    @Test
    void testMoveRight() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 3, 10, 0);
        TetrominoPosition newPos = SuperRotationSystem.moveRight(pos, board);
        
        assertNotNull(newPos);
        assertEquals(4, newPos.getX());
        assertEquals(10, newPos.getY());
    }
    
    @Test
    void testMoveRightBlocked() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 7, 10, 0);
        TetrominoPosition newPos = SuperRotationSystem.moveRight(pos, board);
        
        assertNull(newPos); // I piece width exceeds board
    }
    
    @Test
    void testMoveDown() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.T, 5, 10, 0);
        TetrominoPosition newPos = SuperRotationSystem.moveDown(pos, board);
        
        assertNotNull(newPos);
        assertEquals(5, newPos.getX());
        assertEquals(11, newPos.getY());
    }
    
    @Test
    void testMoveDownBlocked() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.T, 5, GameBoard.HEIGHT + GameBoard.BUFFER_ZONE - 1, 0);
        TetrominoPosition newPos = SuperRotationSystem.moveDown(pos, board);
        
        assertNull(newPos); // Cannot move down from bottom
    }
    
    @Test
    void testHardDrop() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 3, 0, 0);
        TetrominoPosition dropped = SuperRotationSystem.hardDrop(pos, board);
        
        assertNotNull(dropped);
        assertTrue(dropped.getY() > pos.getY());
    }
    
    @Test
    void testAttemptRotationClockwise() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.T, 5, 10, 0);
        TetrominoPosition rotated = SuperRotationSystem.attemptRotation(pos, board, true);
        
        assertNotNull(rotated);
        assertEquals(1, rotated.getRotation());
    }
    
    @Test
    void testAttemptRotationCounterClockwise() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.T, 5, 10, 1);
        TetrominoPosition rotated = SuperRotationSystem.attemptRotation(pos, board, false);
        
        assertNotNull(rotated);
        assertEquals(0, rotated.getRotation());
    }
    
    @Test
    void testAttemptRotationOPiece() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.O, 5, 10, 0);
        TetrominoPosition rotated = SuperRotationSystem.attemptRotation(pos, board, true);
        
        assertNotNull(rotated);
        // O piece should rotate (for item tracking) even though shape looks the same
    }
    
    @Test
    void testAttemptRotationWithTSpinCheck() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.T, 5, 10, 0);
        SuperRotationSystem.RotationResult result = SuperRotationSystem.attemptRotationWithTSpinCheck(pos, board, true);
        
        assertNotNull(result);
        assertNotNull(result.getPosition());
        assertEquals(1, result.getPosition().getRotation());
    }
    
    @Test
    void testRotationResultGetters() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.T, 5, 10, 1);
        SuperRotationSystem.RotationResult result = new SuperRotationSystem.RotationResult(pos, true, 2);
        
        assertEquals(pos, result.getPosition());
        assertTrue(result.isTSpin());
        assertEquals(2, result.getKickUsed());
    }
    
    @Test
    void testRotationResultWithNullPosition() {
        SuperRotationSystem.RotationResult result = new SuperRotationSystem.RotationResult(null, false, -1);
        
        assertNull(result.getPosition());
        assertFalse(result.isTSpin());
        assertEquals(-1, result.getKickUsed());
    }
    
    @Test
    void testIPieceRotation() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 4, 10, 0);
        TetrominoPosition rotated = SuperRotationSystem.attemptRotation(pos, board, true);
        
        assertNotNull(rotated);
        assertEquals(1, rotated.getRotation());
    }
    
    @Test
    void testMoveLeftPreservesRotation() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.T, 5, 10, 2);
        TetrominoPosition newPos = SuperRotationSystem.moveLeft(pos, board);
        
        assertNotNull(newPos);
        assertEquals(2, newPos.getRotation());
    }
    
    @Test
    void testMoveRightPreservesRotation() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.T, 5, 10, 3);
        TetrominoPosition newPos = SuperRotationSystem.moveRight(pos, board);
        
        assertNotNull(newPos);
        assertEquals(3, newPos.getRotation());
    }
    
    @Test
    void testMoveDownPreservesRotation() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.T, 5, 10, 1);
        TetrominoPosition newPos = SuperRotationSystem.moveDown(pos, board);
        
        assertNotNull(newPos);
        assertEquals(1, newPos.getRotation());
    }
    
    @Test
    void testRotationWrapsAround() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.T, 5, 10, 3);
        TetrominoPosition rotated = SuperRotationSystem.attemptRotation(pos, board, true);
        
        assertNotNull(rotated);
        assertEquals(0, rotated.getRotation()); // 3 + 1 wraps to 0
    }
    
    @Test
    void testCounterClockwiseRotationWrapsAround() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.T, 5, 10, 0);
        TetrominoPosition rotated = SuperRotationSystem.attemptRotation(pos, board, false);
        
        assertNotNull(rotated);
        assertEquals(3, rotated.getRotation()); // 0 - 1 wraps to 3
    }
}
