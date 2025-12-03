package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ScoreRecord 클래스의 Unit Test
 */
class ScoreRecordTest {
    
    private ScoreRecord record;
    
    @BeforeEach
    void setUp() {
        record = new ScoreRecord(10000, 50, 5, 1, GameMode.NORMAL, true);
    }
    
    @Test
    void testConstructor() {
        assertNotNull(record);
        assertEquals(10000, record.getScore());
        assertEquals(50, record.getLines());
        assertEquals(5, record.getLevel());
        assertEquals(1, record.getDifficulty());
        assertEquals(GameMode.NORMAL, record.getGameMode());
        assertTrue(record.isNewAndEligible());
        assertEquals("", record.getPlayerName());
        assertNotNull(record.getPlayDate());
    }
    
    @Test
    void testSetPlayerName() {
        record.setPlayerName("ABC");
        assertEquals("ABC", record.getPlayerName());
        
        record.setPlayerName("XYZ");
        assertEquals("XYZ", record.getPlayerName());
    }
    
    @Test
    void testSetNewAndEligible() {
        assertTrue(record.isNewAndEligible());
        
        record.setNewAndEligible(false);
        assertFalse(record.isNewAndEligible());
        
        record.setNewAndEligible(true);
        assertTrue(record.isNewAndEligible());
    }
    
    @Test
    void testCompareTo() {
        ScoreRecord higher = new ScoreRecord(15000, 60, 6, 1, GameMode.NORMAL, false);
        ScoreRecord lower = new ScoreRecord(5000, 30, 3, 1, GameMode.NORMAL, false);
        ScoreRecord equal = new ScoreRecord(10000, 50, 5, 1, GameMode.NORMAL, false);
        
        assertTrue(record.compareTo(higher) > 0); // 10000 < 15000
        assertTrue(record.compareTo(lower) < 0);  // 10000 > 5000
        assertEquals(0, record.compareTo(equal)); // 10000 == 10000
    }
    
    @Test
    void testPlayDateIsSet() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        ScoreRecord newRecord = new ScoreRecord(1000, 10, 1, 1, GameMode.NORMAL, false);
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);
        
        assertTrue(newRecord.getPlayDate().isAfter(before));
        assertTrue(newRecord.getPlayDate().isBefore(after));
    }
    
    @Test
    void testDifferentGameModes() {
        ScoreRecord normalRecord = new ScoreRecord(1000, 10, 1, 1, GameMode.NORMAL, false);
        ScoreRecord itemRecord = new ScoreRecord(1000, 10, 1, 1, GameMode.ITEM, false);
        
        assertEquals(GameMode.NORMAL, normalRecord.getGameMode());
        assertEquals(GameMode.ITEM, itemRecord.getGameMode());
    }
    
    @Test
    void testDifferentDifficulties() {
        ScoreRecord easy = new ScoreRecord(1000, 10, 1, 0, GameMode.NORMAL, false);
        ScoreRecord medium = new ScoreRecord(1000, 10, 1, 1, GameMode.NORMAL, false);
        ScoreRecord hard = new ScoreRecord(1000, 10, 1, 2, GameMode.NORMAL, false);
        
        assertEquals(0, easy.getDifficulty());
        assertEquals(1, medium.getDifficulty());
        assertEquals(2, hard.getDifficulty());
    }
    
    @Test
    void testEmptyPlayerNameByDefault() {
        ScoreRecord newRecord = new ScoreRecord(1000, 10, 1, 1, GameMode.NORMAL, false);
        assertEquals("", newRecord.getPlayerName());
    }
    
    @Test
    void testSortingOrder() {
        ScoreRecord score1 = new ScoreRecord(5000, 25, 3, 1, GameMode.NORMAL, false);
        ScoreRecord score2 = new ScoreRecord(10000, 50, 5, 1, GameMode.NORMAL, false);
        ScoreRecord score3 = new ScoreRecord(15000, 75, 7, 1, GameMode.NORMAL, false);
        
        java.util.List<ScoreRecord> scores = new java.util.ArrayList<>();
        scores.add(score1);
        scores.add(score2);
        scores.add(score3);
        
        java.util.Collections.sort(scores);
        
        assertEquals(15000, scores.get(0).getScore());
        assertEquals(10000, scores.get(1).getScore());
        assertEquals(5000, scores.get(2).getScore());
    }
    
    @Test
    void testHighScoreValues() {
        ScoreRecord highScore = new ScoreRecord(999999, 9999, 99, 2, GameMode.ITEM, true);
        
        assertEquals(999999, highScore.getScore());
        assertEquals(9999, highScore.getLines());
        assertEquals(99, highScore.getLevel());
    }
    
    @Test
    void testZeroValues() {
        ScoreRecord zeroScore = new ScoreRecord(0, 0, 0, 0, GameMode.NORMAL, false);
        
        assertEquals(0, zeroScore.getScore());
        assertEquals(0, zeroScore.getLines());
        assertEquals(0, zeroScore.getLevel());
        assertEquals(0, zeroScore.getDifficulty());
    }
}
