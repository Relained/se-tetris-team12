package org.example.model;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class TetrominoTest {

    @Test
    @DisplayName("모든 테트로미노 타입이 존재하는지 확인")
    void testAllTetrominoTypes() {
        Tetromino[] types = Tetromino.values();
        assertEquals(7, types.length);
        
        assertEquals(Tetromino.I, types[0]);
        assertEquals(Tetromino.O, types[1]);
        assertEquals(Tetromino.T, types[2]);
        assertEquals(Tetromino.S, types[3]);
        assertEquals(Tetromino.Z, types[4]);
        assertEquals(Tetromino.J, types[5]);
        assertEquals(Tetromino.L, types[6]);
    }

    @Test
    @DisplayName("I 블록의 모양 테스트")
    void testITetrominoShape() {
        int[][] shape = Tetromino.I.getShape(0);
        assertNotNull(shape);
        assertEquals(4, shape.length);
        assertEquals(4, shape[0].length);
        
        // I 블록의 첫 번째 회전 상태 확인
        int[][] expected = {
            {0, 0, 0, 0},
            {1, 1, 1, 1},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
        };
        
        assertArrayEquals(expected, shape);
    }

    @Test
    @DisplayName("O 블록의 모양 테스트")
    void testOTetrominoShape() {
        int[][] shape = Tetromino.O.getShape(0);
        assertNotNull(shape);
        
        // O 블록은 모든 회전에서 같은 모양
        int[][] expected = {
            {0, 1, 1, 0},
            {0, 1, 1, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
        };
        
        assertArrayEquals(expected, shape);
    }

    @Test
    @DisplayName("T 블록의 모양 테스트")
    void testTTetrominoShape() {
        int[][] shape = Tetromino.T.getShape(0);
        assertNotNull(shape);
        
        int[][] expected = {
            {0, 1, 0, 0},
            {1, 1, 1, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
        };
        
        assertArrayEquals(expected, shape);
    }

    @Test
    @DisplayName("각 테트로미노가 4개의 회전 상태를 가지는지 확인")
    void testRotationCount() {
        for (Tetromino type : Tetromino.values()) {
            int[][][] rotations = type.getAllRotations();
            assertEquals(4, rotations.length);
        }
    }

    @Test
    @DisplayName("회전 인덱스로 모양 가져오기")
    void testGetShapeByIndex() {
        for (Tetromino type : Tetromino.values()) {
            for (int i = 0; i < 4; i++) {
                int[][] shape = type.getShape(i);
                assertNotNull(shape);
                assertEquals(4, shape.length);
                assertEquals(4, shape[0].length);
            }
        }
    }

    @Test
    @DisplayName("색상 인덱스 테스트")
    void testColorIndex() {
        assertEquals(1, Tetromino.I.getColorIndex());
        assertEquals(2, Tetromino.O.getColorIndex());
        assertEquals(3, Tetromino.T.getColorIndex());
        assertEquals(4, Tetromino.S.getColorIndex());
        assertEquals(5, Tetromino.Z.getColorIndex());
        assertEquals(6, Tetromino.J.getColorIndex());
        assertEquals(7, Tetromino.L.getColorIndex());
    }

    @Test
    @DisplayName("색상 가져오기 테스트")
    void testGetColor() {
        for (Tetromino type : Tetromino.values()) {
            Color color = type.getColor();
            assertNotNull(color);
        }
    }

    @Test
    @DisplayName("모든 회전 가져오기 테스트")
    void testGetAllRotations() {
        for (Tetromino type : Tetromino.values()) {
            int[][][] rotations = type.getAllRotations();
            assertNotNull(rotations);
            assertEquals(4, rotations.length);
            
            for (int[][] rotation : rotations) {
                assertEquals(4, rotation.length);
                assertEquals(4, rotation[0].length);
            }
        }
    }

    @Test
    @DisplayName("각 블록의 모양에 블록이 4개 있는지 확인")
    void testEachShapeHasFourBlocks() {
        for (Tetromino type : Tetromino.values()) {
            int[][] shape = type.getShape(0);
            int blockCount = 0;
            
            for (int[] row : shape) {
                for (int cell : row) {
                    if (cell == 1) {
                        blockCount++;
                    }
                }
            }
            
            assertEquals(4, blockCount, type + " should have exactly 4 blocks");
        }
    }

    @Test
    @DisplayName("O 블록은 모든 회전에서 같은 모양")
    void testOBlockRotationInvariance() {
        int[][] shape0 = Tetromino.O.getShape(0);
        
        for (int i = 1; i < 4; i++) {
            int[][] shapeI = Tetromino.O.getShape(i);
            assertArrayEquals(shape0, shapeI, 
                "O block should have same shape for all rotations");
        }
    }
}
