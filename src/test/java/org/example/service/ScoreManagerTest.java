package org.example.service;

import org.example.model.GameMode;
import org.example.model.ScoreRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ScoreManager Unit Test
 */
class ScoreManagerTest {
    
    private ScoreManager scoreManager;
    
    @BeforeEach
    void setUp() throws Exception {
        scoreManager = ScoreManager.getInstance();
        
        // scores 리스??초기??
        Field scoresField = ScoreManager.class.getDeclaredField("scores");
        scoresField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<ScoreRecord> scores = (List<ScoreRecord>) scoresField.get(scoreManager);
        scores.clear();
    }
    
    @Test
    void testGetInstance() {
        ScoreManager instance1 = ScoreManager.getInstance();
        ScoreManager instance2 = ScoreManager.getInstance();
        
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2);
    }
    
    @Test
    void testAddScore() {
        ScoreRecord record = new ScoreRecord(10000, 50, 5, 1, GameMode.NORMAL, true);
        record.setPlayerName("AAA");
        
        assertTrue(scoreManager.addScore(record));
        
        List<ScoreRecord> topScores = scoreManager.getTopScores();
        assertEquals(1, topScores.size());
        assertEquals(10000, topScores.get(0).getScore());
    }
    
    @Test
    void testAddNullScore() {
        assertFalse(scoreManager.addScore(null));
        assertEquals(0, scoreManager.getTopScores().size());
    }
    
    @Test
    void testAddMultipleScores() {
        ScoreRecord record1 = new ScoreRecord(10000, 50, 5, 1, GameMode.NORMAL, true);
        ScoreRecord record2 = new ScoreRecord(5000, 25, 3, 1, GameMode.NORMAL, false);
        ScoreRecord record3 = new ScoreRecord(15000, 75, 7, 1, GameMode.NORMAL, false);
        
        scoreManager.addScore(record1);
        scoreManager.addScore(record2);
        scoreManager.addScore(record3);
        
        List<ScoreRecord> topScores = scoreManager.getTopScores();
        assertEquals(3, topScores.size());
        assertEquals(15000, topScores.get(0).getScore());
        assertEquals(10000, topScores.get(1).getScore());
        assertEquals(5000, topScores.get(2).getScore());
    }
    
    @Test
    void testMaxScoresLimit() {
        // Add 11 scores
        for (int i = 0; i < 11; i++) {
            ScoreRecord record = new ScoreRecord((11 - i) * 1000, i * 5, i, 1, GameMode.NORMAL, false);
            scoreManager.addScore(record);
        }
        
        List<ScoreRecord> topScores = scoreManager.getTopScores();
        assertEquals(10, topScores.size());
        assertEquals(11000, topScores.get(0).getScore());
        assertEquals(2000, topScores.get(9).getScore());
    }
    
    @Test
    void testGetTopScoresWithLimit() {
        for (int i = 0; i < 5; i++) {
            ScoreRecord record = new ScoreRecord((5 - i) * 1000, i * 5, i, 1, GameMode.NORMAL, false);
            scoreManager.addScore(record);
        }
        
        List<ScoreRecord> top3 = scoreManager.getTopScores(3);
        assertEquals(3, top3.size());
        assertEquals(5000, top3.get(0).getScore());
        assertEquals(4000, top3.get(1).getScore());
        assertEquals(3000, top3.get(2).getScore());
    }
    
    @Test
    void testGetTopScoresWithLimitExceedingAvailable() {
        ScoreRecord record1 = new ScoreRecord(10000, 50, 5, 1, GameMode.NORMAL, false);
        ScoreRecord record2 = new ScoreRecord(5000, 25, 3, 1, GameMode.NORMAL, false);
        
        scoreManager.addScore(record1);
        scoreManager.addScore(record2);
        
        List<ScoreRecord> top5 = scoreManager.getTopScores(5);
        assertEquals(2, top5.size());
    }
    
    @Test
    void testClearScores() {
        ScoreRecord record = new ScoreRecord(10000, 50, 5, 1, GameMode.NORMAL, false);
        scoreManager.addScore(record);
        
        assertEquals(1, scoreManager.getTopScores().size());
        
        scoreManager.clearScores();
        
        assertEquals(0, scoreManager.getTopScores().size());
    }
    
    @Test
    void testIsHighScoreWhenEmpty() {
        assertTrue(scoreManager.isHighScore(100));
        assertTrue(scoreManager.isHighScore(0));
    }
    
    @Test
    void testIsHighScoreWhenNotFull() {
        for (int i = 0; i < 5; i++) {
            ScoreRecord record = new ScoreRecord((5 - i) * 1000, i * 5, i, 1, GameMode.NORMAL, false);
            scoreManager.addScore(record);
        }
        
        assertTrue(scoreManager.isHighScore(10000));
        assertTrue(scoreManager.isHighScore(500));
    }
    
    @Test
    void testIsHighScoreWhenFull() {
        for (int i = 0; i < 10; i++) {
            ScoreRecord record = new ScoreRecord((10 - i) * 1000, i * 5, i, 1, GameMode.NORMAL, false);
            scoreManager.addScore(record);
        }
        
        assertTrue(scoreManager.isHighScore(11000));
        assertTrue(scoreManager.isHighScore(1001));
        assertFalse(scoreManager.isHighScore(1000));
        assertFalse(scoreManager.isHighScore(500));
    }
    
    @Test
    void testIsScoreEligibleForSavingWhenEmpty() {
        assertTrue(scoreManager.isScoreEligibleForSaving(100));
        assertTrue(scoreManager.isScoreEligibleForSaving(0));
    }
    
    @Test
    void testIsScoreEligibleForSavingWhenNotFull() {
        for (int i = 0; i < 5; i++) {
            ScoreRecord record = new ScoreRecord((5 - i) * 1000, i * 5, i, 1, GameMode.NORMAL, false);
            scoreManager.addScore(record);
        }
        
        assertTrue(scoreManager.isScoreEligibleForSaving(10000));
        assertTrue(scoreManager.isScoreEligibleForSaving(100));
    }
    
    @Test
    void testIsScoreEligibleForSavingWhenFull() {
        for (int i = 0; i < 10; i++) {
            ScoreRecord record = new ScoreRecord((10 - i) * 1000, i * 5, i, 1, GameMode.NORMAL, false);
            scoreManager.addScore(record);
        }
        
        assertTrue(scoreManager.isScoreEligibleForSaving(11000));
        assertTrue(scoreManager.isScoreEligibleForSaving(1001));
        assertFalse(scoreManager.isScoreEligibleForSaving(1000));
        assertFalse(scoreManager.isScoreEligibleForSaving(500));
    }
    
    @Test
    void testGetScoreRank() {
        for (int i = 0; i < 5; i++) {
            ScoreRecord record = new ScoreRecord((5 - i) * 1000, i * 5, i, 1, GameMode.NORMAL, false);
            scoreManager.addScore(record);
        }
        
        assertEquals(1, scoreManager.getScoreRank(6000));
        assertEquals(2, scoreManager.getScoreRank(4500));
        assertEquals(6, scoreManager.getScoreRank(500));
    }
    
    @Test
    void testGetScoreRankOutsideTop10() {
        for (int i = 0; i < 10; i++) {
            ScoreRecord record = new ScoreRecord((10 - i) * 1000, i * 5, i, 1, GameMode.NORMAL, false);
            scoreManager.addScore(record);
        }
        
        assertEquals(-1, scoreManager.getScoreRank(500));
    }
    
    @Test
    void testNewlyAddedFlagReset() {
        ScoreRecord record1 = new ScoreRecord(10000, 50, 5, 1, GameMode.NORMAL, true);
        ScoreRecord record2 = new ScoreRecord(5000, 25, 3, 1, GameMode.NORMAL, true);
        
        scoreManager.addScore(record1);
        assertTrue(record1.isNewAndEligible()); // Newly added record stays true
        
        scoreManager.addScore(record2);
        assertFalse(record1.isNewAndEligible()); // Previous record becomes false
        assertTrue(record2.isNewAndEligible()); // Newly added record stays true
    }
    
    @Test
    void testTopScoresReturnsCopy() {
        ScoreRecord record = new ScoreRecord(10000, 50, 5, 1, GameMode.NORMAL, false);
        scoreManager.addScore(record);
        
        List<ScoreRecord> scores1 = scoreManager.getTopScores();
        List<ScoreRecord> scores2 = scoreManager.getTopScores();
        
        assertNotSame(scores1, scores2);
    }
}
