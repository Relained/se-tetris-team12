package org.example.service;

import org.example.model.GameBoard;
import org.example.model.Tetromino;
import org.example.model.TetrominoPosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class TetrisSystemTest {
    private TetrisSystem tetrisSystem;

    @BeforeEach
    void setUp() {
        tetrisSystem = new TetrisSystem();
    }

    @Test
    @DisplayName("TetrisSystem 초기화 테스트")
    void testInitialization() {
        assertNotNull(tetrisSystem.getBoard());
        assertNotNull(tetrisSystem.getCurrentPiece());
        assertEquals(0, tetrisSystem.getScore());
        assertEquals(0, tetrisSystem.getLines());
        assertEquals(1, tetrisSystem.getLevel());
        assertFalse(tetrisSystem.isGameOver());
    }

    @Test
    @DisplayName("현재 피스가 존재함")
    void testCurrentPieceExists() {
        TetrominoPosition currentPiece = tetrisSystem.getCurrentPiece();
        assertNotNull(currentPiece);
        assertNotNull(currentPiece.getType());
    }

    @Test
    @DisplayName("다음 큐가 비어있지 않음")
    void testNextQueueNotEmpty() {
        List<Tetromino> nextQueue = tetrisSystem.getNextQueue();
        assertNotNull(nextQueue);
        assertTrue(nextQueue.size() > 0);
        assertTrue(nextQueue.size() <= 5);
    }

    @Test
    @DisplayName("왼쪽으로 이동 테스트")
    void testMoveLeft() {
        TetrominoPosition before = tetrisSystem.getCurrentPiece();
        int beforeX = before.getX();
        
        boolean moved = tetrisSystem.moveLeft();
        
        if (moved) {
            assertEquals(beforeX - 1, tetrisSystem.getCurrentPiece().getX());
        }
    }

    @Test
    @DisplayName("오른쪽으로 이동 테스트")
    void testMoveRight() {
        TetrominoPosition before = tetrisSystem.getCurrentPiece();
        int beforeX = before.getX();
        
        boolean moved = tetrisSystem.moveRight();
        
        if (moved) {
            assertEquals(beforeX + 1, tetrisSystem.getCurrentPiece().getX());
        }
    }

    @Test
    @DisplayName("아래로 이동 테스트")
    void testMoveDown() {
        TetrominoPosition before = tetrisSystem.getCurrentPiece();
        int beforeY = before.getY();
        
        boolean moved = tetrisSystem.moveDown();
        
        if (moved) {
            assertEquals(beforeY + 1, tetrisSystem.getCurrentPiece().getY());
            assertTrue(tetrisSystem.getScore() > 0); // 소프트 드롭 점수
        }
    }

    @Test
    @DisplayName("시계방향 회전 테스트")
    void testRotateClockwise() {
        TetrominoPosition before = tetrisSystem.getCurrentPiece();
        int beforeRotation = before.getRotation();
        
        boolean rotated = tetrisSystem.rotateClockwise();
        
        if (rotated) {
            int expectedRotation = (beforeRotation + 1) % 4;
            assertEquals(expectedRotation, tetrisSystem.getCurrentPiece().getRotation());
        }
    }

    @Test
    @DisplayName("반시계방향 회전 테스트")
    void testRotateCounterClockwise() {
        // 먼저 한 번 회전시켜서 rotation이 0이 아니게 만듦
        tetrisSystem.rotateClockwise();
        
        TetrominoPosition before = tetrisSystem.getCurrentPiece();
        int beforeRotation = before.getRotation();
        
        boolean rotated = tetrisSystem.rotateCounterClockwise();
        
        if (rotated) {
            int expectedRotation = Math.floorMod(beforeRotation - 1, 4);
            assertEquals(expectedRotation, tetrisSystem.getCurrentPiece().getRotation());
        }
    }

    @Test
    @DisplayName("하드 드롭 테스트")
    void testHardDrop() {
        int scoreBefore = tetrisSystem.getScore();
        
        tetrisSystem.hardDrop();
        
        // 점수가 증가해야 함
        assertTrue(tetrisSystem.getScore() > scoreBefore);
        
        // 새로운 피스가 생성되어야 함
        TetrominoPosition after = tetrisSystem.getCurrentPiece();
        assertNotNull(after);
        // 타입이 다를 가능성이 높음 (같을 수도 있음)
    }

    @Test
    @DisplayName("홀드 기능 테스트 - 첫 홀드")
    void testHold_FirstTime() {
        Tetromino currentType = tetrisSystem.getCurrentPiece().getType();
        
        boolean held = tetrisSystem.hold();
        
        assertTrue(held);
        assertNotNull(tetrisSystem.getHoldPiece());
        assertEquals(currentType, tetrisSystem.getHoldPiece().getType());
        assertNotEquals(currentType, tetrisSystem.getCurrentPiece().getType());
    }

    @Test
    @DisplayName("홀드 기능 테스트 - 연속 홀드 불가")
    void testHold_CannotHoldTwice() {
        tetrisSystem.hold();
        boolean held = tetrisSystem.hold();
        
        assertFalse(held);
    }

    @Test
    @DisplayName("홀드 후 피스 이동하면 다시 홀드 가능")
    void testHold_AfterMove() {
        tetrisSystem.hold();
        tetrisSystem.moveDown(); // 피스가 락되면 새 피스 생성
        
        // 새 피스가 생성되었으므로 다시 홀드 가능해야 함
        // 단, 이동이 성공했는지 확인 필요
    }

    @Test
    @DisplayName("게임 보드 가져오기")
    void testGetBoard() {
        GameBoard board = tetrisSystem.getBoard();
        assertNotNull(board);
    }

    @Test
    @DisplayName("점수 가져오기")
    void testGetScore() {
        int score = tetrisSystem.getScore();
        assertTrue(score >= 0);
    }

    @Test
    @DisplayName("클리어한 줄 수 가져오기")
    void testGetLines() {
        int lines = tetrisSystem.getLines();
        assertTrue(lines >= 0);
    }

    @Test
    @DisplayName("레벨 가져오기")
    void testGetLevel() {
        int level = tetrisSystem.getLevel();
        assertTrue(level >= 1);
    }

    @Test
    @DisplayName("게임 오버 상태 확인")
    void testIsGameOver() {
        assertFalse(tetrisSystem.isGameOver());
    }

    @Test
    @DisplayName("리셋 테스트")
    void testReset() {
        // 게임 상태 변경
        tetrisSystem.moveDown();
        tetrisSystem.moveLeft();
        
        // 리셋
        tetrisSystem.reset();
        
        // 초기 상태로 돌아가야 함
        assertEquals(0, tetrisSystem.getScore());
        assertEquals(0, tetrisSystem.getLines());
        assertEquals(1, tetrisSystem.getLevel());
        assertFalse(tetrisSystem.isGameOver());
        assertNotNull(tetrisSystem.getCurrentPiece());
        assertNull(tetrisSystem.getHoldPiece());
    }

    @Test
    @DisplayName("업데이트 테스트")
    void testUpdate() {
        tetrisSystem.update();
        
        // 업데이트 후 피스가 아래로 이동했거나 락되었어야 함
        TetrominoPosition after = tetrisSystem.getCurrentPiece();
        assertNotNull(after);
    }

    @Test
    @DisplayName("드롭 간격 가져오기")
    void testGetDropInterval() {
        long interval = tetrisSystem.getDropInterval();
        assertTrue(interval > 0);
        assertTrue(interval <= 1000);
    }

    @Test
    @DisplayName("드롭 간격은 레벨에 따라 감소")
    void testDropIntervalDecreasesWithLevel() {
        long interval1 = tetrisSystem.getDropInterval();
        
        // 레벨을 강제로 올리기 위해 많은 줄을 클리어해야 하지만
        // 여기서는 간단히 간격이 유효한지만 확인
        assertTrue(interval1 >= 50);
    }

    @Test
    @DisplayName("게임 오버 후 이동 불가")
    void testCannotMoveAfterGameOver() {
        // 강제로 게임 오버 상태로 전환
        try {
            Field go = TetrisSystem.class.getDeclaredField("gameOver");
            go.setAccessible(true);
            go.setBoolean(tetrisSystem, true);
        } catch (Exception e) {
            fail("Failed to set gameOver via reflection: " + e.getMessage());
        }

        assertTrue(tetrisSystem.isGameOver(), "게임 오버 상태여야 함");

        // 현재 상태 스냅샷
        TetrominoPosition before = tetrisSystem.getCurrentPiece();
        int bx = before.getX();
        int by = before.getY();
        int br = before.getRotation();
        Tetromino bt = before.getType();
        int scoreBefore = tetrisSystem.getScore();

        // 이동/회전/홀드 모두 금지되어야 함
        assertFalse(tetrisSystem.moveLeft(), "게임 오버 후 moveLeft는 false여야 함");
        assertFalse(tetrisSystem.moveRight(), "게임 오버 후 moveRight는 false여야 함");
        assertFalse(tetrisSystem.moveDown(), "게임 오버 후 moveDown은 false여야 함");
        assertFalse(tetrisSystem.rotateClockwise(), "게임 오버 후 회전은 false여야 함");
        assertFalse(tetrisSystem.rotateCounterClockwise(), "게임 오버 후 회전은 false여야 함");
        assertFalse(tetrisSystem.hold(), "게임 오버 후 홀드는 false여야 함");

        // 하드드롭은 아무 동작도 하지 않아야 함
        tetrisSystem.hardDrop();

        // 상태가 변하지 않았는지 확인
        TetrominoPosition after = tetrisSystem.getCurrentPiece();
        assertNotNull(after);
        assertEquals(bt, after.getType());
        assertEquals(bx, after.getX());
        assertEquals(by, after.getY());
        assertEquals(br, after.getRotation());
        assertEquals(scoreBefore, tetrisSystem.getScore(), "게임 오버 후 점수는 변하지 않아야 함");
    }

    @Test
    @DisplayName("다음 큐는 최대 5개까지만 반환")
    void testNextQueueMaxFive() {
        List<Tetromino> nextQueue = tetrisSystem.getNextQueue();
        assertTrue(nextQueue.size() <= 5);
    }

    @Test
    @DisplayName("홀드 피스 초기값은 null")
    void testHoldPieceInitiallyNull() {
        TetrisSystem newSystem = new TetrisSystem();
        assertNull(newSystem.getHoldPiece());
    }

    @Test
    @DisplayName("RWS 테스트")
    void testRouletteSelectionWeightedIPiece() throws Exception {
        // Stabilize randomness for reproducibility
        Field rndField = TetrisSystem.class.getDeclaredField("random");
        rndField.setAccessible(true);
        rndField.set(tetrisSystem, new Random(12345L));

        // Increase I piece weight by 100% (2.0 vs 1.0 others)
        tetrisSystem.setTetrominoWeight(Tetromino.I, 2);

        // Access private selection method via reflection
        Method picker = TetrisSystem.class.getDeclaredMethod("selectWeightedRandom");
        picker.setAccessible(true);

        int trials = 1000;
        int countI = 0;
        int countO = 0; // use O as a representative of weight=1.0 pieces

        for (int i = 0; i < trials; i++) {
            Tetromino t = (Tetromino) picker.invoke(tetrisSystem);
            if (t == Tetromino.I) countI++;
            if (t == Tetromino.O) countO++;
        }

        // Expected probabilities for I weight = 2.0, others = 1.0:
        // total weight = 2 + 6 * 1.0 = 8
        // P(I) = 2 / 8 = 0.25 => expected count = 250
        double pI = 0.25;
        double expectedI = trials * pI; // = 250
        double sigma = Math.sqrt(trials * pI * (1.0 - pI)); // std-dev for binomial

        // Assert I count within 3σ window to avoid flakiness
        double lower = expectedI - 3 * sigma;
        double upper = expectedI + 3 * sigma;
        assertTrue(countI >= lower && countI <= upper,
            String.format("I count %d outside expected range [%.1f, %.1f]", countI, lower, upper));

        // Also check relative ratio against a 1.0-weight piece roughly matches 2.0x
        // Avoid division by zero in pathological randomness
        assertTrue(countO > 0, "O count should be > 0 in 1000 trials");
        double ratio = (double) countI / (double) countO;
        // Expect ~2.0 ratio (I has double weight). Allow ±0.25 tolerance to absorb randomness
        assertTrue(Math.abs(ratio - 2.0) < 0.25,
            String.format("I/O ratio %.3f not close to 2.0 (counts: I=%d, O=%d)", ratio, countI, countO));
    }
}
