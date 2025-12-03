package org.example.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GameMode Unit Test
 */
class GameModeTest {
    
    @Test
    void testNormalMode() {
        assertEquals("Normal", GameMode.NORMAL.toString());
    }
    
    @Test
    void testItemMode() {
        assertEquals("Item", GameMode.ITEM.toString());
    }
    
    @Test
    void testTimeAttackMode() {
        assertEquals("Time Attack", GameMode.TIME_ATTACK.toString());
    }
    
    @Test
    void testAllModesExist() {
        GameMode[] modes = GameMode.values();
        assertEquals(3, modes.length);
    }
    
    @Test
    void testValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            GameMode.valueOf("INVALID");
        });
    }
    
    @Test
    void testEnumInequality() {
        assertNotEquals(GameMode.NORMAL, GameMode.ITEM);
        assertNotEquals(GameMode.ITEM, GameMode.TIME_ATTACK);
        assertNotEquals(GameMode.NORMAL, GameMode.TIME_ATTACK);
    }
    
    @Test
    void testToStringUniqueness() {
        assertNotEquals(GameMode.NORMAL.toString(), GameMode.ITEM.toString());
        assertNotEquals(GameMode.ITEM.toString(), GameMode.TIME_ATTACK.toString());
        assertNotEquals(GameMode.NORMAL.toString(), GameMode.TIME_ATTACK.toString());
    }
}
