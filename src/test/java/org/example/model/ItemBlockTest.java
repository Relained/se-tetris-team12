package org.example.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ItemBlock Enum Unit Test
 */
class ItemBlockTest {
    
    @Test
    void testAllItemBlockTypesExist() {
        ItemBlock[] types = ItemBlock.values();
        assertEquals(6, types.length);
    }
    
    @Test
    void testLineCllearSymbol() {
        assertEquals('L', ItemBlock.LINE_CLEAR.getSymbol());
    }
    
    @Test
    void testColumnClearSymbol() {
        assertEquals('I', ItemBlock.COLUMN_CLEAR.getSymbol());
    }
    
    @Test
    void testCrossClearSymbol() {
        assertEquals('X', ItemBlock.CROSS_CLEAR.getSymbol());
    }
    
    @Test
    void testWeightSymbol() {
        assertEquals('W', ItemBlock.WEIGHT.getSymbol());
    }
    
    @Test
    void testBombSymbol() {
        assertEquals('B', ItemBlock.BOMB.getSymbol());
    }
    
    @Test
    void testNoneSymbol() {
        assertEquals(' ', ItemBlock.NONE.getSymbol());
    }
    
    @Test
    void testLineClearIsItem() {
        assertTrue(ItemBlock.LINE_CLEAR.isItem());
    }
    
    @Test
    void testColumnClearIsItem() {
        assertTrue(ItemBlock.COLUMN_CLEAR.isItem());
    }
    
    @Test
    void testCrossClearIsItem() {
        assertTrue(ItemBlock.CROSS_CLEAR.isItem());
    }
    
    @Test
    void testWeightIsItem() {
        assertTrue(ItemBlock.WEIGHT.isItem());
    }
    
    @Test
    void testBombIsItem() {
        assertTrue(ItemBlock.BOMB.isItem());
    }
    
    @Test
    void testNoneIsNotItem() {
        assertFalse(ItemBlock.NONE.isItem());
    }
    
    @Test
    void testItemGenerationConstant() {
        assertEquals(10, ItemBlock.LINES_FOR_ITEM_GENERATION);
    }
    
    @Test
    void testValueOf() {
        assertEquals(ItemBlock.LINE_CLEAR, ItemBlock.valueOf("LINE_CLEAR"));
        assertEquals(ItemBlock.COLUMN_CLEAR, ItemBlock.valueOf("COLUMN_CLEAR"));
        assertEquals(ItemBlock.CROSS_CLEAR, ItemBlock.valueOf("CROSS_CLEAR"));
        assertEquals(ItemBlock.WEIGHT, ItemBlock.valueOf("WEIGHT"));
        assertEquals(ItemBlock.BOMB, ItemBlock.valueOf("BOMB"));
        assertEquals(ItemBlock.NONE, ItemBlock.valueOf("NONE"));
    }
    
    @Test
    void testValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> ItemBlock.valueOf("INVALID"));
    }
    
    @Test
    void testAllSymbolsAreUnique() {
        ItemBlock[] types = ItemBlock.values();
        for (int i = 0; i < types.length; i++) {
            for (int j = i + 1; j < types.length; j++) {
                assertNotEquals(types[i].getSymbol(), types[j].getSymbol(),
                    types[i] + " and " + types[j] + " should have different symbols");
            }
        }
    }
}
