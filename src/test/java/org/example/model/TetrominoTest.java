package org.example.model;

import org.example.service.ColorManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TetrominoTest {

    @Test
    void testTetrominoI() {
        Tetromino tetromino = Tetromino.I;
        int[][] expectedShape = {{0,0,0,0}, {1,1,1,1}, {0,0,0,0}, {0,0,0,0}};

        assertArrayEquals(expectedShape, tetromino.getShape(0));
        assertEquals(ColorManager.getInstance().getColorFromIndex(1), tetromino.getColor());
    }

    @Test
    void testTetrominoO() {
        Tetromino tetromino = Tetromino.O;
        int[][] expectedShape = {{0,1,1,0}, {0,1,1,0}, {0,0,0,0}, {0,0,0,0}};

        assertArrayEquals(expectedShape, tetromino.getShape(0));
        assertEquals(ColorManager.getInstance().getColorFromIndex(2), tetromino.getColor());
    }

    @Test
    void testTetrominoT() {
        Tetromino tetromino = Tetromino.T;
        int[][] expectedShape = {{0,1,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}};

        assertArrayEquals(expectedShape, tetromino.getShape(0));
        assertEquals(ColorManager.getInstance().getColorFromIndex(3), tetromino.getColor());
    }

    @Test
    void testTetrominoS() {
        Tetromino tetromino = Tetromino.S;
        int[][] expectedShape = {{0,1,1,0}, {1,1,0,0}, {0,0,0,0}, {0,0,0,0}};

        assertArrayEquals(expectedShape, tetromino.getShape(0));
        assertEquals(ColorManager.getInstance().getColorFromIndex(4), tetromino.getColor());
    }

    @Test
    void testTetrominoZ() {
        Tetromino tetromino = Tetromino.Z;
        int[][] expectedShape = {{1,1,0,0}, {0,1,1,0}, {0,0,0,0}, {0,0,0,0}};

        assertArrayEquals(expectedShape, tetromino.getShape(0));
        assertEquals(ColorManager.getInstance().getColorFromIndex(5), tetromino.getColor());
    }

    @Test
    void testTetrominoJ() {
        Tetromino tetromino = Tetromino.J;
        int[][] expectedShape = {{1,0,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}};

        assertArrayEquals(expectedShape, tetromino.getShape(0));
        assertEquals(ColorManager.getInstance().getColorFromIndex(6), tetromino.getColor());
    }

    @Test
    void testTetrominoL() {
        Tetromino tetromino = Tetromino.L;
        int[][] expectedShape = {{0,0,1,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}};

        assertArrayEquals(expectedShape, tetromino.getShape(0));
        assertEquals(ColorManager.getInstance().getColorFromIndex(7), tetromino.getColor());
    }

    @Test
    void testGetAllRotations_I() {
        Tetromino tetromino = Tetromino.I;
        int[][][] rotations = tetromino.getAllRotations();

        assertEquals(4, rotations.length);

        int[][] rotation0 = {{0,0,0,0}, {1,1,1,1}, {0,0,0,0}, {0,0,0,0}};
        int[][] rotation1 = {{0,0,1,0}, {0,0,1,0}, {0,0,1,0}, {0,0,1,0}};
        int[][] rotation2 = {{0,0,0,0}, {0,0,0,0}, {1,1,1,1}, {0,0,0,0}};
        int[][] rotation3 = {{0,1,0,0}, {0,1,0,0}, {0,1,0,0}, {0,1,0,0}};

        assertArrayEquals(rotation0, rotations[0]);
        assertArrayEquals(rotation1, rotations[1]);
        assertArrayEquals(rotation2, rotations[2]);
        assertArrayEquals(rotation3, rotations[3]);
    }

    @Test
    void testGetAllRotations_O() {
        Tetromino tetromino = Tetromino.O;
        int[][][] rotations = tetromino.getAllRotations();

        assertEquals(4, rotations.length);

        int[][] expectedShape = {{0,1,1,0}, {0,1,1,0}, {0,0,0,0}, {0,0,0,0}};

        for (int i = 0; i < 4; i++) {
            assertArrayEquals(expectedShape, rotations[i]);
        }
    }

    @Test
    void testGetAllRotations_T() {
        Tetromino tetromino = Tetromino.T;
        int[][][] rotations = tetromino.getAllRotations();

        assertEquals(4, rotations.length);

        int[][] rotation0 = {{0,1,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}};
        int[][] rotation1 = {{0,1,0,0}, {0,1,1,0}, {0,1,0,0}, {0,0,0,0}};
        int[][] rotation2 = {{0,0,0,0}, {1,1,1,0}, {0,1,0,0}, {0,0,0,0}};
        int[][] rotation3 = {{0,1,0,0}, {1,1,0,0}, {0,1,0,0}, {0,0,0,0}};

        assertArrayEquals(rotation0, rotations[0]);
        assertArrayEquals(rotation1, rotations[1]);
        assertArrayEquals(rotation2, rotations[2]);
        assertArrayEquals(rotation3, rotations[3]);
    }

    @Test
    void testRotateClockwise() {
        Tetromino tetromino = Tetromino.L;
        int[][][] rotations = tetromino.getAllRotations();

        int[][] original = {{0,0,1,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}};
        int[][] rotated90 = {{0,1,0,0}, {0,1,0,0}, {0,1,1,0}, {0,0,0,0}};
        int[][] rotated180 = {{0,0,0,0}, {1,1,1,0}, {1,0,0,0}, {0,0,0,0}};
        int[][] rotated270 = {{1,1,0,0}, {0,1,0,0}, {0,1,0,0}, {0,0,0,0}};

        assertArrayEquals(original, rotations[0]);
        assertArrayEquals(rotated90, rotations[1]);
        assertArrayEquals(rotated180, rotations[2]);
        assertArrayEquals(rotated270, rotations[3]);
    }

    @Test
    void testAllTetrominoTypes() {
        Tetromino[] allTypes = Tetromino.values();
        assertEquals(7, allTypes.length);

        assertEquals(Tetromino.I, allTypes[0]);
        assertEquals(Tetromino.O, allTypes[1]);
        assertEquals(Tetromino.T, allTypes[2]);
        assertEquals(Tetromino.S, allTypes[3]);
        assertEquals(Tetromino.Z, allTypes[4]);
        assertEquals(Tetromino.J, allTypes[5]);
        assertEquals(Tetromino.L, allTypes[6]);
    }
}