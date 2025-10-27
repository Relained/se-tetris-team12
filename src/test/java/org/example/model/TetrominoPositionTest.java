package org.example.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class TetrominoPositionTest {

    @Test
    @DisplayName("TetrominoPosition 생성 테스트")
    void testConstructor() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 5, 10, 0);
        
        assertEquals(Tetromino.I, pos.getType());
        assertEquals(5, pos.getX());
        assertEquals(10, pos.getY());
        assertEquals(0, pos.getRotation());
    }

    @Test
    @DisplayName("X 좌표 설정 테스트")
    void testSetX() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.T, 0, 0, 0);
        pos.setX(7);
        assertEquals(7, pos.getX());
    }

    @Test
    @DisplayName("Y 좌표 설정 테스트")
    void testSetY() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.T, 0, 0, 0);
        pos.setY(15);
        assertEquals(15, pos.getY());
    }

    @Test
    @DisplayName("회전 설정 테스트 - 정상 범위")
    void testSetRotation_NormalRange() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.T, 0, 0, 0);
        
        pos.setRotation(1);
        assertEquals(1, pos.getRotation());
        
        pos.setRotation(2);
        assertEquals(2, pos.getRotation());
        
        pos.setRotation(3);
        assertEquals(3, pos.getRotation());
    }

    @Test
    @DisplayName("회전 설정 테스트 - 음수 값")
    void testSetRotation_NegativeValue() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.T, 0, 0, 0);
        
        pos.setRotation(-1);
        assertEquals(3, pos.getRotation()); // -1 % 4 = 3
        
        pos.setRotation(-2);
        assertEquals(2, pos.getRotation());
    }

    @Test
    @DisplayName("회전 설정 테스트 - 4 이상의 값")
    void testSetRotation_OverFour() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.T, 0, 0, 0);
        
        pos.setRotation(4);
        assertEquals(0, pos.getRotation()); // 4 % 4 = 0
        
        pos.setRotation(5);
        assertEquals(1, pos.getRotation());
    }

    @Test
    @DisplayName("현재 모양 가져오기 테스트")
    void testGetCurrentShape() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.O, 0, 0, 0);
        int[][] shape = pos.getCurrentShape();
        
        assertNotNull(shape);
        assertEquals(4, shape.length);
        assertEquals(4, shape[0].length);
    }

    @Test
    @DisplayName("복사 테스트")
    void testCopy() {
        TetrominoPosition original = new TetrominoPosition(Tetromino.L, 3, 7, 2);
        TetrominoPosition copy = original.copy();
        
        // 복사본이 원본과 같은 값을 가지는지 확인
        assertEquals(original.getType(), copy.getType());
        assertEquals(original.getX(), copy.getX());
        assertEquals(original.getY(), copy.getY());
        assertEquals(original.getRotation(), copy.getRotation());
        
        // 복사본이 별도의 객체인지 확인
        assertNotSame(original, copy);
        
        // 복사본 수정이 원본에 영향을 주지 않는지 확인
        copy.setX(10);
        assertNotEquals(original.getX(), copy.getX());
    }

    @Test
    @DisplayName("다양한 테트로미노 타입 테스트")
    void testDifferentTetrominoTypes() {
        for (Tetromino type : Tetromino.values()) {
            TetrominoPosition pos = new TetrominoPosition(type, 0, 0, 0);
            assertEquals(type, pos.getType());
            assertNotNull(pos.getCurrentShape());
        }
    }

    @Test
    @DisplayName("회전 후 모양 변경 확인")
    void testShapeChangesWithRotation() {
        TetrominoPosition pos = new TetrominoPosition(Tetromino.I, 0, 0, 0);
        
        int[][] shape0 = pos.getCurrentShape();
        pos.setRotation(1);
        int[][] shape1 = pos.getCurrentShape();
        
        // 회전 0과 회전 1의 모양이 다른지 확인 (I 블록은 회전하면 모양이 바뀜)
        boolean isDifferent = false;
        outer:
        for (int i = 0; i < shape0.length; i++) {
            for (int j = 0; j < shape0[i].length; j++) {
                if (shape0[i][j] != shape1[i][j]) {
                    isDifferent = true;
                    break outer;
                }
            }
        }
        assertTrue(isDifferent);
    }
}
