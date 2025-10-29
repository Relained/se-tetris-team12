package org.example.service;

import org.example.model.ScoreRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScoreManagerTest {
    
    private ScoreManager scoreManager;
    private static final String SAVE_FILE = System.getProperty("user.home") 
            + File.separator + "tetris_scores.dat";
    
    @BeforeEach
    void setUp() {
        scoreManager = ScoreManager.getInstance();
        scoreManager.clearScores();
    }
    
    @AfterEach
    void tearDown() {
        scoreManager.clearScores();
        File file = new File(SAVE_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
    
    @Test
    @DisplayName("싱글톤 인스턴스 확인")
    void testSingleton() {
        ScoreManager instance1 = ScoreManager.getInstance();
        ScoreManager instance2 = ScoreManager.getInstance();
        
        assertSame(instance1, instance2);
    }
    
    @Test
    @DisplayName("점수 추가 - 성공")
    void testAddScore() {
        ScoreRecord record = new ScoreRecord(1000, 10, 5, 1);
        record.setPlayerName("Player1");
        boolean result = scoreManager.addScore(record);
        
        assertTrue(result);
        assertEquals(1, scoreManager.getTopScores().size());
    }
    
    @Test
    @DisplayName("점수 추가 - null 실패")
    void testAddScoreNull() {
        boolean result = scoreManager.addScore(null);
        
        assertFalse(result);
        assertEquals(0, scoreManager.getTopScores().size());
    }
    
    @Test
    @DisplayName("점수 정렬 확인")
    void testScoreSorting() {
        ScoreRecord recordA = new ScoreRecord(500, 5, 3, 1);
        recordA.setPlayerName("A");
        scoreManager.addScore(recordA);
        
        ScoreRecord recordB = new ScoreRecord(1500, 15, 7, 1);
        recordB.setPlayerName("B");
        scoreManager.addScore(recordB);
        
        ScoreRecord recordC = new ScoreRecord(1000, 10, 5, 1);
        recordC.setPlayerName("C");
        scoreManager.addScore(recordC);
        
        List<ScoreRecord> scores = scoreManager.getTopScores();
        
        assertEquals(3, scores.size());
        assertEquals(1500, scores.get(0).getScore());
        assertEquals(1000, scores.get(1).getScore());
        assertEquals(500, scores.get(2).getScore());
    }
    
    @Test
    @DisplayName("최대 10개 점수만 유지")
    void testMaxScoresLimit() {
        for (int i = 0; i < 15; i++) {
            ScoreRecord record = new ScoreRecord((i + 1) * 100, i, i, 1);
            record.setPlayerName("Player" + i);
            scoreManager.addScore(record);
        }
        
        List<ScoreRecord> scores = scoreManager.getTopScores();
        
        assertEquals(10, scores.size());
        assertEquals(1500, scores.get(0).getScore()); // 가장 높은 점수
    }
    
    @Test
    @DisplayName("상위 N개 점수 가져오기")
    void testGetTopScoresWithLimit() {
        for (int i = 0; i < 5; i++) {
            ScoreRecord record = new ScoreRecord((i + 1) * 100, i, i, 1);
            record.setPlayerName("Player" + i);
            scoreManager.addScore(record);
        }
        
        List<ScoreRecord> top3 = scoreManager.getTopScores(3);
        
        assertEquals(3, top3.size());
        assertEquals(500, top3.get(0).getScore());
    }
    
    @Test
    @DisplayName("모든 점수 삭제")
    void testClearScores() {
        ScoreRecord record1 = new ScoreRecord(1000, 10, 5, 1);
        record1.setPlayerName("Player1");
        scoreManager.addScore(record1);
        
        ScoreRecord record2 = new ScoreRecord(2000, 20, 10, 1);
        record2.setPlayerName("Player2");
        scoreManager.addScore(record2);
        
        scoreManager.clearScores();
        
        assertEquals(0, scoreManager.getTopScores().size());
    }
    
    @Test
    @DisplayName("상위 점수 여부 확인 - 10개 미만일 때")
    void testIsHighScoreLessThan10() {
        for (int i = 0; i < 5; i++) {
            ScoreRecord record = new ScoreRecord((i + 1) * 100, i, i, 1);
            record.setPlayerName("Player" + i);
            scoreManager.addScore(record);
        }
        
        assertTrue(scoreManager.isHighScore(50)); // 어떤 점수든 상위 점수
    }
    
    @Test
    @DisplayName("상위 점수 여부 확인 - 10개일 때")
    void testIsHighScore10Records() {
        for (int i = 0; i < 10; i++) {
            ScoreRecord record = new ScoreRecord((i + 1) * 100, i, i, 1);
            record.setPlayerName("Player" + i);
            scoreManager.addScore(record);
        }
        
        assertTrue(scoreManager.isHighScore(1100)); // 최고 점수보다 높음
        assertFalse(scoreManager.isHighScore(50)); // 최저 점수보다 낮음
    }
    
    @Test
    @DisplayName("점수 저장 가능 여부 - 10개 미만")
    void testIsScoreEligibleForSavingLessThan10() {
        for (int i = 0; i < 5; i++) {
            ScoreRecord record = new ScoreRecord((i + 1) * 100, i, i, 1);
            record.setPlayerName("Player" + i);
            scoreManager.addScore(record);
        }
        
        assertTrue(scoreManager.isScoreEligibleForSaving(1));
    }
    
    @Test
    @DisplayName("점수 저장 가능 여부 - 10개일 때")
    void testIsScoreEligibleForSaving10Records() {
        for (int i = 0; i < 10; i++) {
            ScoreRecord record = new ScoreRecord((i + 1) * 100, i, i, 1);
            record.setPlayerName("Player" + i);
            scoreManager.addScore(record);
        }
        
        assertTrue(scoreManager.isScoreEligibleForSaving(1100));
        assertFalse(scoreManager.isScoreEligibleForSaving(50));
    }
    
    @Test
    @DisplayName("점수 순위 확인")
    void testGetScoreRank() {
        for (int i = 0; i < 5; i++) {
            ScoreRecord record = new ScoreRecord((i + 1) * 100, i, i, 1);
            record.setPlayerName("Player" + i);
            scoreManager.addScore(record);
        }
        
        assertEquals(1, scoreManager.getScoreRank(600)); // 1위
        assertEquals(3, scoreManager.getScoreRank(400)); // 3위
        assertEquals(6, scoreManager.getScoreRank(50)); // 6위
    }
    
    @Test
    @DisplayName("점수 순위 - 순위권 밖")
    void testGetScoreRankOutOfRange() {
        for (int i = 0; i < 10; i++) {
            ScoreRecord record = new ScoreRecord((i + 1) * 100, i, i, 1);
            record.setPlayerName("Player" + i);
            scoreManager.addScore(record);
        }
        
        assertEquals(-1, scoreManager.getScoreRank(50)); // 순위권 밖
    }
    
    @Test
    @DisplayName("새로 추가된 점수 플래그 확인")
    void testNewlyAddedFlag() {
        ScoreRecord record1 = new ScoreRecord(1000, 10, 5, 1);
        record1.setPlayerName("Player1");
        scoreManager.addScore(record1);
        
        ScoreRecord record2 = new ScoreRecord(2000, 20, 10, 1);
        record2.setPlayerName("Player2");
        scoreManager.addScore(record2);
        
        List<ScoreRecord> scores = scoreManager.getTopScores();
        
        // record2가 새로 추가되었으므로 true
        assertTrue(scores.get(0).isNewlyAdded());
        // record1은 이전 기록이므로 false
        assertFalse(scores.get(1).isNewlyAdded());
    }
    
    @Test
    @DisplayName("빈 상태에서 상위 점수 가져오기")
    void testGetTopScoresEmpty() {
        List<ScoreRecord> scores = scoreManager.getTopScores();
        
        assertNotNull(scores);
        assertEquals(0, scores.size());
    }
}
