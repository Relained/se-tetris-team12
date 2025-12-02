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
    void testValueOf() {
        assertEquals(GameMode.NORMAL, GameMode.valueOf("NORMAL"));
        assertEquals(GameMode.ITEM, GameMode.valueOf("ITEM"));
        assertEquals(GameMode.TIME_ATTACK, GameMode.valueOf("TIME_ATTACK"));
    }
    
    @Test
    void testValueOfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            GameMode.valueOf("INVALID");
        });
    }
    
    @Test
    void testEnumEquality() {
        GameMode mode1 = GameMode.NORMAL;
        GameMode mode2 = GameMode.NORMAL;
        assertSame(mode1, mode2);
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
