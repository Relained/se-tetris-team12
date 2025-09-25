package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TetrominoPositionTest {

    private TetrominoPosition position;

    @BeforeEach
    void setUp() {
        position = new TetrominoPosition(Tetromino.T, 5, 10, 0);
    }

    @Test
    void testConstructor() {
        assertEquals(Tetromino.T, position.getType());
        assertEquals(5, position.getX());
        assertEquals(10, position.getY());
        assertEquals(0, position.getRotation());
    }

    @Test
    void testSettersAndGetters() {
        position.setX(7);
        position.setY(15);
        position.setRotation(2);

        assertEquals(7, position.getX());
        assertEquals(15, position.getY());
        assertEquals(2, position.getRotation());
        assertEquals(Tetromino.T, position.getType());
    }

    @Test
    void testGetCurrentShape_Rotation0() {
        position.setRotation(0);
        int[][] expectedShape = {{0,1,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}};
        assertArrayEquals(expectedShape, position.getCurrentShape());
    }

    @Test
    void testGetCurrentShape_Rotation1() {
        position.setRotation(1);
        int[][] expectedShape = {{0,1,0,0}, {0,1,1,0}, {0,1,0,0}, {0,0,0,0}};
        assertArrayEquals(expectedShape, position.getCurrentShape());
    }

    @Test
    void testGetCurrentShape_Rotation2() {
        position.setRotation(2);
        int[][] expectedShape = {{0,0,0,0}, {1,1,1,0}, {0,1,0,0}, {0,0,0,0}};
        assertArrayEquals(expectedShape, position.getCurrentShape());
    }

    @Test
    void testGetCurrentShape_Rotation3() {
        position.setRotation(3);
        int[][] expectedShape = {{0,1,0,0}, {1,1,0,0}, {0,1,0,0}, {0,0,0,0}};
        assertArrayEquals(expectedShape, position.getCurrentShape());
    }

    @Test
    void testGetCurrentShape_RotationModulo() {
        position.setRotation(4);
        int[][] expectedShape0 = {{0,1,0,0}, {1,1,1,0}, {0,0,0,0}, {0,0,0,0}};
        assertArrayEquals(expectedShape0, position.getCurrentShape());

        position.setRotation(5);
        int[][] expectedShape1 = {{0,1,0,0}, {0,1,1,0}, {0,1,0,0}, {0,0,0,0}};
        assertArrayEquals(expectedShape1, position.getCurrentShape());
    }

    @Test
    void testCopy() {
        TetrominoPosition copy = position.copy();

        assertEquals(position.getType(), copy.getType());
        assertEquals(position.getX(), copy.getX());
        assertEquals(position.getY(), copy.getY());
        assertEquals(position.getRotation(), copy.getRotation());

        assertNotSame(position, copy);
    }

    @Test
    void testCopy_IndependentModification() {
        TetrominoPosition copy = position.copy();

        copy.setX(20);
        copy.setY(25);
        copy.setRotation(3);

        assertEquals(5, position.getX());
        assertEquals(10, position.getY());
        assertEquals(0, position.getRotation());

        assertEquals(20, copy.getX());
        assertEquals(25, copy.getY());
        assertEquals(3, copy.getRotation());
    }

    @Test
    void testDifferentTetrominos() {
        TetrominoPosition iPosition = new TetrominoPosition(Tetromino.I, 0, 0, 0);
        int[][] iShape = {{0,0,0,0}, {1,1,1,1}, {0,0,0,0}, {0,0,0,0}};
        assertArrayEquals(iShape, iPosition.getCurrentShape());

        TetrominoPosition oPosition = new TetrominoPosition(Tetromino.O, 0, 0, 0);
        int[][] oShape = {{0,1,1,0}, {0,1,1,0}, {0,0,0,0}, {0,0,0,0}};
        assertArrayEquals(oShape, oPosition.getCurrentShape());
    }

    @Test
    void testNegativeRotation() {
        position.setRotation(-1);
        int[][] expectedShape = {{0,1,0,0}, {1,1,0,0}, {0,1,0,0}, {0,0,0,0}};
        assertArrayEquals(expectedShape, position.getCurrentShape());

        position.setRotation(-5);
        int[][] expectedShape1 = {{0,1,0,0}, {1,1,0,0}, {0,1,0,0}, {0,0,0,0}};
        assertArrayEquals(expectedShape1, position.getCurrentShape());
    }
}