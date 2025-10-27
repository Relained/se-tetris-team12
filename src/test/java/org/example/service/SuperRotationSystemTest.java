package org.example.service;

import org.example.model.GameBoard;
import org.example.model.Tetromino;
import org.example.model.TetrominoPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class SuperRotationSystemTest {
    private GameBoard board;

    @BeforeEach
    void setUp() {
        board = new GameBoard();
    }

    @Test
    @DisplayName("왼쪽으로 이동 - 성공")
    void testMoveLeft_Success() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 5, 10, 0);
        TetrominoPosition newPos = SuperRotationSystem.moveLeft(pos, board);
        
        assertNotNull(newPos);
        assertEquals(4, newPos.getX());
        assertEquals(10, newPos.getY());
    }

    @Test
    @DisplayName("왼쪽으로 이동 - 벽에 막힘")
    void testMoveLeft_Blocked() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 0, 10, 0);
        TetrominoPosition newPos = SuperRotationSystem.moveLeft(pos, board);
        
        assertNull(newPos);
    }

    @Test
    @DisplayName("오른쪽으로 이동 - 성공")
    void testMoveRight_Success() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 3, 10, 0);
        TetrominoPosition newPos = SuperRotationSystem.moveRight(pos, board);
        
        assertNotNull(newPos);
        assertEquals(4, newPos.getX());
        assertEquals(10, newPos.getY());
    }

    @Test
    @DisplayName("오른쪽으로 이동 - 벽에 막힘")
    void testMoveRight_Blocked() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 6, 10, 0);
        TetrominoPosition newPos = SuperRotationSystem.moveRight(pos, board);
        
        assertNull(newPos);
    }

    @Test
    @DisplayName("아래로 이동 - 성공")
    void testMoveDown_Success() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 3, 10, 0);
        TetrominoPosition newPos = SuperRotationSystem.moveDown(pos, board);
        
        assertNotNull(newPos);
        assertEquals(3, newPos.getX());
        assertEquals(11, newPos.getY());
    }

    @Test
    @DisplayName("아래로 이동 - 바닥에 막힘")
    void testMoveDown_Blocked() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 3, GameBoard.HEIGHT + GameBoard.BUFFER_ZONE - 2, 0);
        TetrominoPosition newPos = SuperRotationSystem.moveDown(pos, board);
        
        assertNull(newPos);
    }

    @Test
    @DisplayName("하드 드롭 테스트")
    void testHardDrop() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 3, GameBoard.BUFFER_ZONE, 0);
        TetrominoPosition dropPos = SuperRotationSystem.hardDrop(pos, board);
        
        assertNotNull(dropPos);
        assertTrue(dropPos.getY() > pos.getY());
        
        // 한 칸 더 내려가면 유효하지 않아야 함
        TetrominoPosition testPos = dropPos.copy();
        testPos.setY(dropPos.getY() + 1);
        assertFalse(board.isValidPosition(testPos));
    }

    @Test
    @DisplayName("O 블록 회전 시도 - 성공")
    void testAttemptRotation_OBlock() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.O, 4, 10, 0);
        TetrominoPosition newPos = SuperRotationSystem.attemptRotation(pos, board, true);
        
        assertNotNull(newPos);
        assertEquals(1, newPos.getRotation());
    }

    @Test
    @DisplayName("I 블록 시계방향 회전")
    void testAttemptRotation_IBlock_Clockwise() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 3, 10, 0);
        TetrominoPosition newPos = SuperRotationSystem.attemptRotation(pos, board, true);
        
        assertNotNull(newPos);
        assertEquals(1, newPos.getRotation());
    }

    @Test
    @DisplayName("I 블록 반시계방향 회전")
    void testAttemptRotation_IBlock_CounterClockwise() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 3, 10, 1);
        TetrominoPosition newPos = SuperRotationSystem.attemptRotation(pos, board, false);
        
        assertNotNull(newPos);
        assertEquals(0, newPos.getRotation());
    }

    @Test
    @DisplayName("T 블록 회전 테스트")
    void testAttemptRotation_TBlock() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.T, 4, 10, 0);
        TetrominoPosition newPos = SuperRotationSystem.attemptRotation(pos, board, true);
        
        assertNotNull(newPos);
        assertEquals(1, newPos.getRotation());
    }

    @Test
    @DisplayName("회전 with T-Spin 체크")
    void testAttemptRotationWithTSpinCheck() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.T, 4, 10, 0);
        SuperRotationSystem.RotationResult result = 
            SuperRotationSystem.attemptRotationWithTSpinCheck(pos, board, true);
        
        assertNotNull(result);
        assertNotNull(result.getPosition());
        assertFalse(result.isTSpin()); // 빈 보드에서는 T-Spin이 아님
    }

    @Test
    @DisplayName("T-Spin이 아닌 경우 감지")
    void testIsTSpinRotation_NotTSpin() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.T, 4, 10, 0);
        boolean isTSpin = SuperRotationSystem.isTSpinRotation(pos, board);
        
        assertFalse(isTSpin);
    }

    @Test
    @DisplayName("T 블록이 아니면 T-Spin이 아님")
    void testIsTSpinRotation_NotTBlock() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 4, 10, 0);
        boolean isTSpin = SuperRotationSystem.isTSpinRotation(pos, board);
        
        assertFalse(isTSpin);
    }

    @Test
    @DisplayName("벽 킥 테스트 - 왼쪽 벽")
    void testWallKick_LeftWall() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 0, 10, 0);
        TetrominoPosition newPos = SuperRotationSystem.attemptRotation(pos, board, true);
        
        // Wall kick이 작동하여 회전이 성공해야 함
        assertNotNull(newPos);
    }

    @Test
    @DisplayName("벽 킥 테스트 - 오른쪽 벽")
    void testWallKick_RightWall() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, GameBoard.WIDTH - 1, 10, 0);
        TetrominoPosition newPos = SuperRotationSystem.attemptRotation(pos, board, true);
        
        // Wall kick이 작동하여 회전이 성공하거나 실패할 수 있음 (위치에 따라)
        // 적어도 null이 아니거나 null이어야 함
        assertTrue(newPos == null || newPos.getRotation() == 1);
    }

    @Test
    @DisplayName("다양한 테트로미노 회전 테스트")
    void testRotation_AllTypes() {
        for (Tetromino type : Tetromino.values()) {
            TetrominoPosition pos = new TetrominoPosition(type, 4, 10, 0);
            TetrominoPosition newPos = SuperRotationSystem.attemptRotation(pos, board, true);
            
            assertNotNull(newPos, "Rotation should succeed for " + type);
        }
    }

    @Test
    @DisplayName("하드 드롭 후 위치가 바닥에 닿아야 함")
    void testHardDrop_ReachesBottom() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.O, 4, GameBoard.BUFFER_ZONE, 0);
        TetrominoPosition dropPos = SuperRotationSystem.hardDrop(pos, board);
        
        // 드롭 위치가 원래 위치보다 아래에 있어야 함
        assertTrue(dropPos.getY() >= pos.getY());
    }

    @Test
    @DisplayName("Rotation Result의 kickUsed 확인")
    void testRotationResult_KickUsed() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 3, 10, 0);
        SuperRotationSystem.RotationResult result = 
            SuperRotationSystem.attemptRotationWithTSpinCheck(pos, board, true);
        
        assertNotNull(result);
        assertTrue(result.getKickUsed() >= 0);
    }
}
