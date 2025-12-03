package org.example.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import javafx.scene.paint.Color;
import org.example.service.ColorManager;

class TetrominoPositionTest {

    private TetrominoPosition position;
    private ColorManager colorManager;

    @BeforeEach
    void setUp() {
        position = new TetrominoPosition(Tetromino.T, 5, 10, 0);
        colorManager = ColorManager.getInstance();
    }

    // Constructor and Getters Tests
    @Test
    void testConstructorInitializesCorrectly() {
        assertEquals(Tetromino.T, position.getType());
        assertEquals(5, position.getX());
        assertEquals(10, position.getY());
        assertEquals(0, position.getRotation());
        assertFalse(position.hasCustomShape());
        assertFalse(position.isRotationLocked());
        assertEquals(TetrominoPosition.SpecialKind.NONE, position.getSpecialKind());
    }

    @Test
    void testGetCurrentShapeUsesTetrominoShape() {
        int[][] shape = position.getCurrentShape();
        assertArrayEquals(Tetromino.T.getShape(0), shape);
    }

    @Test
    void testGetDisplayColorUsesTetrominoColor() {
        assertEquals(Tetromino.T.getColor(), position.getDisplayColor(colorManager));
    }

    // Rotation Tests
    @Test
    void testSetRotation() {
        position.setRotation(2);
        assertEquals(2, position.getRotation());
    }

    @Test
    void testRotationAffectsCurrentShape() {
        int[][] rotation0 = position.getCurrentShape();
        position.setRotation(1);
        int[][] rotation1 = position.getCurrentShape();
        assertFalse(arraysEqual(rotation0, rotation1));
    }

    @Test
    void testRotationWrapsAround() {
        position.setRotation(3);
        assertEquals(3, position.getRotation());
        int[][] shape3 = position.getCurrentShape();
        position.setRotation(0);
        int[][] shape0 = position.getCurrentShape();
        assertArrayEquals(Tetromino.T.getShape(3), shape3);
        assertArrayEquals(Tetromino.T.getShape(0), shape0);
    }

    // Position Tests
    @Test
    void testSetX() {
        position.setX(15);
        assertEquals(15, position.getX());
    }

    @Test
    void testSetY() {
        position.setY(20);
        assertEquals(20, position.getY());
    }

    @Test
    void testPositionCanBeSet() {
        position.setX(12);
        position.setY(18);
        assertEquals(12, position.getX());
        assertEquals(18, position.getY());
    }

    // Copy Tests
    @Test
    void testCopyCreatesIndependentInstance() {
        position.setRotation(2);
        position.setX(7);
        position.setY(12);
        
        TetrominoPosition copy = position.copy();
        
        assertEquals(position.getType(), copy.getType());
        assertEquals(position.getX(), copy.getX());
        assertEquals(position.getY(), copy.getY());
        assertEquals(position.getRotation(), copy.getRotation());
        
        // Modify original
        position.setX(99);
        assertEquals(7, copy.getX()); // Copy should be unchanged
    }

    @Test
    void testCopyPreservesSpecialKind() {
        TetrominoPosition weight = TetrominoPosition.createWeightPiece(5, 10);
        TetrominoPosition copy = weight.copy();
        assertEquals(TetrominoPosition.SpecialKind.WEIGHT, copy.getSpecialKind());
    }

    // Item System Tests
    @Test
    void testSetItemAtBlockIndexDefault() {
        position.setItemAtBlockIndex(0);
        assertTrue(position.hasItems());
        assertEquals(Integer.valueOf(0), position.getItemBlockIndex());
    }

    @Test
    void testSetItemAtBlockIndexWithType() {
        position.setItemAtBlockIndex(1, ItemBlock.COLUMN_CLEAR);
        assertTrue(position.hasItems());
        assertEquals(Integer.valueOf(1), position.getItemBlockIndex());
    }

    @Test
    void testGetItemAtForNormalBlock() {
        position.setItemAtBlockIndex(0, ItemBlock.LINE_CLEAR);
        
        // Find first block in shape and check if it has the item
        int[][] shape = position.getCurrentShape();
        boolean foundFirstBlock = false;
        for (int r = 0; r < shape.length && !foundFirstBlock; r++) {
            for (int c = 0; c < shape[r].length && !foundFirstBlock; c++) {
                if (shape[r][c] == 1) {
                    assertEquals(ItemBlock.LINE_CLEAR, position.getItemAt(r, c));
                    foundFirstBlock = true;
                }
            }
        }
        assertTrue(foundFirstBlock);
    }

    @Test
    void testGetItemAtForNonItemBlock() {
        position.setItemAtBlockIndex(0, ItemBlock.LINE_CLEAR);
        
        // Find a block that's not the first one
        int[][] shape = position.getCurrentShape();
        int blockCount = 0;
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] == 1) {
                    if (blockCount > 0) {
                        assertEquals(ItemBlock.NONE, position.getItemAt(r, c));
                        return;
                    }
                    blockCount++;
                }
            }
        }
    }

    @Test
    void testGetItemAtForEmptyPosition() {
        int[][] shape = position.getCurrentShape();
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] == 0) {
                    assertEquals(ItemBlock.NONE, position.getItemAt(r, c));
                    return;
                }
            }
        }
    }

    @Test
    void testHasItems() {
        assertFalse(position.hasItems());
        position.setItemAtBlockIndex(0);
        assertTrue(position.hasItems());
    }

    @Test
    void testItemsPersistThroughCopy() {
        position.setItemAtBlockIndex(0, ItemBlock.BOMB);
        TetrominoPosition copy = position.copy();
        assertEquals(position.getItemBlockIndex(), copy.getItemBlockIndex());
        assertTrue(copy.hasItems());
    }

    // Special Piece Tests
    @Test
    void testCreateWeightPiece() {
        TetrominoPosition weight = TetrominoPosition.createWeightPiece(5, 10);
        
        assertEquals(TetrominoPosition.SpecialKind.WEIGHT, weight.getSpecialKind());
        assertEquals(5, weight.getX());
        assertEquals(10, weight.getY());
        assertTrue(weight.isRotationLocked());
        assertTrue(weight.hasCustomShape());
        
        // Weight piece should have 6 blocks
        int[][] shape = weight.getCurrentShape();
        int blockCount = 0;
        for (int[] row : shape) {
            for (int cell : row) {
                if (cell == 1) blockCount++;
            }
        }
        assertEquals(6, blockCount);
    }

    @Test
    void testCreateBombPiece() {
        TetrominoPosition bomb = TetrominoPosition.createBombPiece(3, 7);
        
        assertEquals(TetrominoPosition.SpecialKind.BOMB, bomb.getSpecialKind());
        assertEquals(3, bomb.getX());
        assertEquals(7, bomb.getY());
        assertTrue(bomb.isRotationLocked());
        assertTrue(bomb.hasCustomShape());
        
        // Bomb piece should have 4 blocks in a 2x2 pattern
        int[][] shape = bomb.getCurrentShape();
        int blockCount = 0;
        for (int[] row : shape) {
            for (int cell : row) {
                if (cell == 1) blockCount++;
            }
        }
        assertEquals(4, blockCount);
    }

    @Test
    void testWeightPieceHasCustomDisplayColor() {
        TetrominoPosition weight = TetrominoPosition.createWeightPiece(0, 0);
        Color displayColor = weight.getDisplayColor(colorManager);
        assertNotNull(displayColor);
        assertEquals(Color.GOLD, displayColor);
    }

    @Test
    void testBombPieceHasCustomDisplayColor() {
        TetrominoPosition bomb = TetrominoPosition.createBombPiece(0, 0);
        Color displayColor = bomb.getDisplayColor(colorManager);
        assertNotNull(displayColor);
        assertEquals(Color.ORANGERED, displayColor);
    }

    @Test
    void testWeightPieceRotationIsLocked() {
        TetrominoPosition weight = TetrominoPosition.createWeightPiece(0, 0);
        assertTrue(weight.isRotationLocked());
        
        // Even if we try to change rotation, it shouldn't change
        int[][] originalShape = weight.getCurrentShape();
        weight.setRotation(1);
        assertArrayEquals(originalShape, weight.getCurrentShape());
    }

    @Test
    void testBombPieceRotationIsLocked() {
        TetrominoPosition bomb = TetrominoPosition.createBombPiece(0, 0);
        assertTrue(bomb.isRotationLocked());
        
        int[][] originalShape = bomb.getCurrentShape();
        bomb.setRotation(2);
        assertArrayEquals(originalShape, bomb.getCurrentShape());
    }

    @Test
    void testSpecialPiecesUseOType() {
        TetrominoPosition weight = TetrominoPosition.createWeightPiece(0, 0);
        assertEquals(Tetromino.O, weight.getType());
        
        TetrominoPosition bomb = TetrominoPosition.createBombPiece(0, 0);
        assertEquals(Tetromino.O, bomb.getType());
    }

    @Test
    void testSpecialPiecesCopyCorrectly() {
        TetrominoPosition weight = TetrominoPosition.createWeightPiece(5, 10);
        TetrominoPosition weightCopy = weight.copy();
        
        assertEquals(TetrominoPosition.SpecialKind.WEIGHT, weightCopy.getSpecialKind());
        assertTrue(weightCopy.isRotationLocked());
        assertArrayEquals(weight.getCurrentShape(), weightCopy.getCurrentShape());
        
        TetrominoPosition bomb = TetrominoPosition.createBombPiece(3, 7);
        TetrominoPosition bombCopy = bomb.copy();
        
        assertEquals(TetrominoPosition.SpecialKind.BOMB, bombCopy.getSpecialKind());
        assertTrue(bombCopy.isRotationLocked());
        assertArrayEquals(bomb.getCurrentShape(), bombCopy.getCurrentShape());
    }

    @Test
    void testWeightPieceShapeIsCorrect() {
        TetrominoPosition weight = TetrominoPosition.createWeightPiece(0, 0);
        int[][] shape = weight.getCurrentShape();
        
        // Weight piece should be [[0,1,1,0], [1,1,1,1]]
        assertEquals(2, shape.length);
        assertEquals(4, shape[0].length);
        
        // First row: 0,1,1,0
        assertEquals(0, shape[0][0]);
        assertEquals(1, shape[0][1]);
        assertEquals(1, shape[0][2]);
        assertEquals(0, shape[0][3]);
        
        // Second row: 1,1,1,1
        assertEquals(1, shape[1][0]);
        assertEquals(1, shape[1][1]);
        assertEquals(1, shape[1][2]);
        assertEquals(1, shape[1][3]);
    }

    @Test
    void testBombPieceShapeIsCorrect() {
        TetrominoPosition bomb = TetrominoPosition.createBombPiece(0, 0);
        int[][] shape = bomb.getCurrentShape();
        
        // Bomb piece should be 2x2
        assertEquals(2, shape.length);
        assertEquals(2, shape[0].length);
        
        // All cells should be filled (4 blocks)
        for (int[] row : shape) {
            for (int cell : row) {
                assertEquals(1, cell);
            }
        }
    }

    @Test
    void testWeightPieceReturnsWeightItemForAllBlocks() {
        TetrominoPosition weight = TetrominoPosition.createWeightPiece(0, 0);
        int[][] shape = weight.getCurrentShape();
        
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] == 1) {
                    assertEquals(ItemBlock.WEIGHT, weight.getItemAt(r, c));
                }
            }
        }
    }

    @Test
    void testBombPieceReturnsBombItemForAllBlocks() {
        TetrominoPosition bomb = TetrominoPosition.createBombPiece(0, 0);
        int[][] shape = bomb.getCurrentShape();
        
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[r].length; c++) {
                if (shape[r][c] == 1) {
                    assertEquals(ItemBlock.BOMB, bomb.getItemAt(r, c));
                }
            }
        }
    }

    // SpecialKind Enum Tests
    @Test
    void testSpecialKindEnumValues() {
        TetrominoPosition.SpecialKind[] values = TetrominoPosition.SpecialKind.values();
        assertEquals(3, values.length);
        assertEquals(TetrominoPosition.SpecialKind.NONE, values[0]);
        assertEquals(TetrominoPosition.SpecialKind.WEIGHT, values[1]);
        assertEquals(TetrominoPosition.SpecialKind.BOMB, values[2]);
    }

    @Test
    void testSpecialKindValueOf() {
        assertEquals(TetrominoPosition.SpecialKind.NONE, 
                     TetrominoPosition.SpecialKind.valueOf("NONE"));
        assertEquals(TetrominoPosition.SpecialKind.WEIGHT, 
                     TetrominoPosition.SpecialKind.valueOf("WEIGHT"));
        assertEquals(TetrominoPosition.SpecialKind.BOMB, 
                     TetrominoPosition.SpecialKind.valueOf("BOMB"));
    }

    @Test
    void testDefaultSpecialKindIsNone() {
        assertEquals(TetrominoPosition.SpecialKind.NONE, position.getSpecialKind());
    }

    @Test
    void testSpecialKindToString() {
        assertEquals("NONE", TetrominoPosition.SpecialKind.NONE.toString());
        assertEquals("WEIGHT", TetrominoPosition.SpecialKind.WEIGHT.toString());
        assertEquals("BOMB", TetrominoPosition.SpecialKind.BOMB.toString());
    }

    // Helper Methods
    private boolean arraysEqual(int[][] a, int[][] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (a[i].length != b[i].length) return false;
            for (int j = 0; j < a[i].length; j++) {
                if (a[i][j] != b[i][j]) return false;
            }
        }
        return true;
    }
}
