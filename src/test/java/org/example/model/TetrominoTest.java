package org.example.model;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tetromino Enum Unit Test
 */
class TetrominoTest {
    
    @Test
    void testAllTetrominoTypes() {
        Tetromino[] types = Tetromino.values();
        assertEquals(7, types.length);
    }
    
    @Test
    void testGetShapeReturns4x4Array() {
        for (Tetromino type : Tetromino.values()) {
            for (int rotation = 0; rotation < 4; rotation++) {
                int[][] shape = type.getShape(rotation);
                assertEquals(4, shape.length);
                for (int[] row : shape) {
                    assertEquals(4, row.length);
                }
            }
        }
    }
    
    @Test
    void testGetColorIndex() {
        assertEquals(1, Tetromino.I.getColorIndex());
        assertEquals(2, Tetromino.O.getColorIndex());
        assertEquals(3, Tetromino.T.getColorIndex());
        assertEquals(4, Tetromino.S.getColorIndex());
        assertEquals(5, Tetromino.Z.getColorIndex());
        assertEquals(6, Tetromino.J.getColorIndex());
        assertEquals(7, Tetromino.L.getColorIndex());
    }
    
    @Test
    void testGetAllRotations() {
        for (Tetromino type : Tetromino.values()) {
            int[][][] rotations = type.getAllRotations();
            assertNotNull(rotations);
            assertEquals(4, rotations.length);
        }
    }
    
    @Test
    void testIBlockShape() {
        int[][] shape0 = Tetromino.I.getShape(0);
        // I piece: {{0,0,0,0}, {1,1,1,1}, {0,0,0,0}, {0,0,0,0}}
        assertEquals(0, shape0[0][0]);
        assertEquals(1, shape0[1][0]);
        assertEquals(1, shape0[1][1]);
        assertEquals(1, shape0[1][2]);
        assertEquals(1, shape0[1][3]);
    }
    
    @Test
    void testOBlockShape() {
        int[][] shape0 = Tetromino.O.getShape(0);
        // O piece: {{0,1,1,0}, {0,1,1,0}, {0,0,0,0}, {0,0,0,0}}
        assertEquals(1, shape0[0][1]);
        assertEquals(1, shape0[0][2]);
        assertEquals(1, shape0[1][1]);
        assertEquals(1, shape0[1][2]);
    }
    
    @Test
    void testOBlockAllRotationsSameShape() {
        int[][] shape0 = Tetromino.O.getShape(0);
        int[][] shape1 = Tetromino.O.getShape(1);
        int[][] shape2 = Tetromino.O.getShape(2);
        int[][] shape3 = Tetromino.O.getShape(3);
        
        // O block looks the same in all rotations
        assertArrayEquals(shape0, shape1);
        assertArrayEquals(shape0, shape2);
        assertArrayEquals(shape0, shape3);
    }
    
    @Test
    void testGetBlockIndexMappingsReturns4x4Array() {
        for (Tetromino type : Tetromino.values()) {
            int[][] mappings = type.getBlockIndexMappings();
            assertNotNull(mappings);
            assertEquals(4, mappings.length, type + " should have 4 rotations");
            for (int rotation = 0; rotation < 4; rotation++) {
                assertEquals(4, mappings[rotation].length, 
                    type + " rotation " + rotation + " should map 4 blocks");
            }
        }
    }
    
    @Test
    void testGetBlockIndexMappingsRotation0IsIdentity() {
        for (Tetromino type : Tetromino.values()) {
            int[][] mappings = type.getBlockIndexMappings();
            // Rotation 0 should map to itself: [0,1,2,3]
            assertArrayEquals(new int[]{0, 1, 2, 3}, mappings[0],
                type + " rotation 0 should be identity mapping");
        }
    }
    
    @Test
    void testGetBlockIndexMappingsIBlock() {
        int[][] mappings = Tetromino.I.getBlockIndexMappings();
        
        // I block rotation mappings
        assertArrayEquals(new int[]{0, 1, 2, 3}, mappings[0]);
        assertArrayEquals(new int[]{0, 1, 2, 3}, mappings[1]);
        assertArrayEquals(new int[]{3, 2, 1, 0}, mappings[2]);
        assertArrayEquals(new int[]{3, 2, 1, 0}, mappings[3]);
    }
    
    @Test
    void testGetBlockIndexMappingsOBlock() {
        int[][] mappings = Tetromino.O.getBlockIndexMappings();
        
        // O block rotation mappings (rotates around center)
        assertArrayEquals(new int[]{0, 1, 2, 3}, mappings[0]);
        assertArrayEquals(new int[]{2, 0, 3, 1}, mappings[1]);
        assertArrayEquals(new int[]{3, 2, 1, 0}, mappings[2]);
        assertArrayEquals(new int[]{1, 3, 0, 2}, mappings[3]);
    }
    
    @Test
    void testGetBlockIndexMappingsTBlock() {
        int[][] mappings = Tetromino.T.getBlockIndexMappings();
        
        // T block rotation mappings
        assertArrayEquals(new int[]{0, 1, 2, 3}, mappings[0]);
        assertArrayEquals(new int[]{1, 2, 0, 3}, mappings[1]);
        assertArrayEquals(new int[]{3, 2, 1, 0}, mappings[2]);
        assertArrayEquals(new int[]{3, 0, 2, 1}, mappings[3]);
    }
    
    @Test
    void testGetBlockIndexMappingsSBlock() {
        int[][] mappings = Tetromino.S.getBlockIndexMappings();
        
        assertArrayEquals(new int[]{0, 1, 2, 3}, mappings[0]);
        assertArrayEquals(new int[]{2, 3, 0, 1}, mappings[1]);
        assertArrayEquals(new int[]{3, 2, 1, 0}, mappings[2]);
        assertArrayEquals(new int[]{1, 0, 3, 2}, mappings[3]);
    }
    
    @Test
    void testGetBlockIndexMappingsZBlock() {
        int[][] mappings = Tetromino.Z.getBlockIndexMappings();
        
        assertArrayEquals(new int[]{0, 1, 2, 3}, mappings[0]);
        assertArrayEquals(new int[]{0, 2, 1, 3}, mappings[1]);
        assertArrayEquals(new int[]{3, 2, 1, 0}, mappings[2]);
        assertArrayEquals(new int[]{3, 1, 2, 0}, mappings[3]);
    }
    
    @Test
    void testGetBlockIndexMappingsJBlock() {
        int[][] mappings = Tetromino.J.getBlockIndexMappings();
        
        assertArrayEquals(new int[]{0, 1, 2, 3}, mappings[0]);
        assertArrayEquals(new int[]{1, 0, 2, 3}, mappings[1]);
        assertArrayEquals(new int[]{3, 2, 1, 0}, mappings[2]);
        assertArrayEquals(new int[]{3, 2, 0, 1}, mappings[3]);
    }
    
    @Test
    void testGetBlockIndexMappingsLBlock() {
        int[][] mappings = Tetromino.L.getBlockIndexMappings();
        
        assertArrayEquals(new int[]{0, 1, 2, 3}, mappings[0]);
        assertArrayEquals(new int[]{1, 2, 3, 0}, mappings[1]);
        assertArrayEquals(new int[]{3, 2, 1, 0}, mappings[2]);
        assertArrayEquals(new int[]{0, 3, 2, 1}, mappings[3]);
    }
    
    @Test
    void testGetBlockIndexMappingsAllValuesInRange() {
        for (Tetromino type : Tetromino.values()) {
            int[][] mappings = type.getBlockIndexMappings();
            for (int rotation = 0; rotation < 4; rotation++) {
                for (int block = 0; block < 4; block++) {
                    int mappedIndex = mappings[rotation][block];
                    assertTrue(mappedIndex >= 0 && mappedIndex <= 3,
                        type + " rotation " + rotation + " block " + block + 
                        " maps to invalid index " + mappedIndex);
                }
            }
        }
    }
    
    @Test
    void testGetBlockIndexMappingsIsPermutation() {
        for (Tetromino type : Tetromino.values()) {
            int[][] mappings = type.getBlockIndexMappings();
            for (int rotation = 0; rotation < 4; rotation++) {
                boolean[] seen = new boolean[4];
                for (int block = 0; block < 4; block++) {
                    int mappedIndex = mappings[rotation][block];
                    assertFalse(seen[mappedIndex], 
                        type + " rotation " + rotation + " has duplicate mapping for index " + mappedIndex);
                    seen[mappedIndex] = true;
                }
                // All indices 0-3 should be present
                for (int i = 0; i < 4; i++) {
                    assertTrue(seen[i], 
                        type + " rotation " + rotation + " missing mapping for index " + i);
                }
            }
        }
    }
    
    @Test
    void testEachShapeHasFourBlocks() {
        for (Tetromino type : Tetromino.values()) {
            for (int rotation = 0; rotation < 4; rotation++) {
                int[][] shape = type.getShape(rotation);
                int blockCount = 0;
                for (int row = 0; row < 4; row++) {
                    for (int col = 0; col < 4; col++) {
                        if (shape[row][col] == 1) {
                            blockCount++;
                        }
                    }
                }
                assertEquals(4, blockCount, 
                    type + " rotation " + rotation + " should have exactly 4 blocks");
            }
        }
    }
    
    @Test
    void testColorIndexMatchesOrdinal() {
        for (Tetromino type : Tetromino.values()) {
            assertEquals(type.ordinal() + 1, type.getColorIndex());
        }
    }
}
