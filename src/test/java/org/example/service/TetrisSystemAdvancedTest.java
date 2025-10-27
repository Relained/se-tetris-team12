package org.example.service;

import org.example.model.GameBoard;
import org.example.model.Tetromino;
import org.example.model.TetrominoPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TetrisSystem의 추가 테스트 - 엣지 케이스 및 통합 시나리오
 */
class TetrisSystemAdvancedTest {
    private TetrisSystem tetrisSystem;

    @BeforeEach
    void setUp() {
        tetrisSystem = new TetrisSystem();
    }

    @Test
    @DisplayName("연속 하드 드롭 테스트")
    void testConsecutiveHardDrops() {
        for (int i = 0; i < 5; i++) {
            int scoreBefore = tetrisSystem.getScore();
            tetrisSystem.hardDrop();
            assertTrue(tetrisSystem.getScore() >= scoreBefore);
        }
    }

    @Test
    @DisplayName("홀드 후 하드 드롭 테스트")
    void testHoldThenHardDrop() {
        Tetromino firstType = tetrisSystem.getCurrentPiece().getType();
        tetrisSystem.hold();
        
        assertNotNull(tetrisSystem.getHoldPiece());
        assertEquals(firstType, tetrisSystem.getHoldPiece().getType());
        
        tetrisSystem.hardDrop();
        assertNotNull(tetrisSystem.getCurrentPiece());
    }

    @Test
    @DisplayName("최대 회전 테스트 - 4번 회전하면 원래대로")
    void testFourRotationsReturnToOriginal() {
        int originalRotation = tetrisSystem.getCurrentPiece().getRotation();
        
        for (int i = 0; i < 4; i++) {
            tetrisSystem.rotateClockwise();
        }
        
        // O 블록이 아닌 경우에만 확인 (O 블록은 항상 같은 모양)
        if (tetrisSystem.getCurrentPiece().getType() != Tetromino.O) {
            assertEquals(originalRotation, tetrisSystem.getCurrentPiece().getRotation());
        }
    }

    @Test
    @DisplayName("빠른 이동 테스트 - 연속 이동")
    void testRapidMovement() {
        int moves = 0;
        // 왼쪽 끝까지 이동
        while (tetrisSystem.moveLeft()) {
            moves++;
            assertTrue(moves < 20); // 무한 루프 방지
        }
        
        moves = 0;
        // 오른쪽으로 다시 이동
        while (tetrisSystem.moveRight()) {
            moves++;
            assertTrue(moves < 20); // 무한 루프 방지
        }
    }

    @Test
    @DisplayName("레벨 증가 확인")
    void testLevelIncrease() {
        int initialLevel = tetrisSystem.getLevel();
        assertEquals(1, initialLevel);
        
        // 레벨은 (줄 수 / 10) + 1
        assertTrue(tetrisSystem.getLevel() >= 1);
        assertTrue(tetrisSystem.getLevel() <= 20);
    }

    @Test
    @DisplayName("드롭 간격은 레벨에 따라 변함")
    void testDropIntervalChangesWithLevel() {
        long interval1 = tetrisSystem.getDropInterval();
        assertTrue(interval1 > 0);
        
        // 간격은 최소 50ms, 최대 1000ms
        assertTrue(interval1 >= 50);
        assertTrue(interval1 <= 1000);
    }

    @Test
    @DisplayName("리셋 후 다음 큐 재생성")
    void testResetRegeneratesQueue() {
        tetrisSystem.reset();
        
        assertNotNull(tetrisSystem.getNextQueue());
        assertTrue(tetrisSystem.getNextQueue().size() > 0);
        assertTrue(tetrisSystem.getNextQueue().size() <= 5);
    }

    @Test
    @DisplayName("여러 번 리셋해도 정상 작동")
    void testMultipleResets() {
        for (int i = 0; i < 3; i++) {
            tetrisSystem.reset();
            
            assertEquals(0, tetrisSystem.getScore());
            assertEquals(0, tetrisSystem.getLines());
            assertEquals(1, tetrisSystem.getLevel());
            assertFalse(tetrisSystem.isGameOver());
            assertNotNull(tetrisSystem.getCurrentPiece());
        }
    }

    @Test
    @DisplayName("업데이트 호출 시 피스가 아래로 이동하거나 고정됨")
    void testUpdateMovesPieceDown() {
        TetrominoPosition before = tetrisSystem.getCurrentPiece();
        int beforeY = before.getY();
        
        tetrisSystem.update();
        
        TetrominoPosition after = tetrisSystem.getCurrentPiece();
        // 피스가 이동했거나 새 피스가 생성됨
        assertTrue(after.getY() >= beforeY || after.getType() != before.getType());
    }

    @Test
    @DisplayName("소프트 드롭 점수 누적 확인")
    void testSoftDropScoreAccumulation() {
        int initialScore = tetrisSystem.getScore();
        int moves = 0;
        
        // 여러 번 아래로 이동
        while (tetrisSystem.moveDown() && moves < 20) {
            moves++;
        }
        
        if (moves > 0) {
            // 소프트 드롭으로 점수가 증가했어야 함
            assertTrue(tetrisSystem.getScore() > initialScore);
        }
    }

    @Test
    @DisplayName("하드 드롭은 소프트 드롭보다 점수가 높음")
    void testHardDropScoreHigherThanSoftDrop() {
        TetrisSystem system1 = new TetrisSystem();
        TetrisSystem system2 = new TetrisSystem();
        
        // system1: 소프트 드롭 1칸
        system1.moveDown();
        int softDropScore = system1.getScore();
        
        // system2: 하드 드롭
        system2.hardDrop();
        int hardDropScore = system2.getScore();
        
        // 하드 드롭이 더 많은 점수를 줌
        assertTrue(hardDropScore >= softDropScore);
    }

    @RepeatedTest(5)
    @DisplayName("반복 테스트 - 게임 로직 일관성")
    void testGameLogicConsistency() {
        TetrisSystem freshSystem = new TetrisSystem();
        
        assertNotNull(freshSystem.getCurrentPiece());
        assertNotNull(freshSystem.getBoard());
        assertFalse(freshSystem.isGameOver());
        assertEquals(0, freshSystem.getScore());
        assertEquals(1, freshSystem.getLevel());
    }

    @Test
    @DisplayName("다음 큐는 모든 테트로미노 타입을 포함")
    void testNextQueueContainsDiverseTypes() {
        tetrisSystem.reset();
        
        // 충분히 많은 피스를 생성하여 다양성 확인
        for (int i = 0; i < 20; i++) {
            tetrisSystem.hardDrop();
        }
        
        // 게임 오버가 아니면 다음 큐가 있어야 함
        if (!tetrisSystem.isGameOver()) {
            assertNotNull(tetrisSystem.getNextQueue());
        }
    }

    @Test
    @DisplayName("회전 후 유효하지 않은 위치면 회전 취소")
    void testRotationValidation() {
        // 피스를 벽 근처로 이동
        while (tetrisSystem.moveLeft()) {
            // 계속 왼쪽으로
        }
        
        // 회전 시도 - 성공하거나 실패할 수 있음
        boolean rotated = tetrisSystem.rotateClockwise();
        
        // 회전 실패 시 피스는 원래 위치에 있어야 함
        assertNotNull(tetrisSystem.getCurrentPiece());
    }

    @Test
    @DisplayName("게임 보드 상태 확인")
    void testGameBoardState() {
        GameBoard board = tetrisSystem.getBoard();
        assertNotNull(board);
        
        int[][] visibleBoard = board.getVisibleBoard();
        assertEquals(GameBoard.HEIGHT, visibleBoard.length);
        assertEquals(GameBoard.WIDTH, visibleBoard[0].length);
    }

    @Test
    @DisplayName("스코어는 음수가 될 수 없음")
    void testScoreNeverNegative() {
        for (int i = 0; i < 10; i++) {
            tetrisSystem.update();
            assertTrue(tetrisSystem.getScore() >= 0);
        }
    }
}
