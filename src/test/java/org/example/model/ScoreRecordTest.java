package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ScoreRecordTest {
    
    private ScoreRecord scoreRecord;
    
    @BeforeEach
    void setUp() {
        scoreRecord = new ScoreRecord(1000, 10, 10, 5);
    }
    
    @Test
    @DisplayName("ScoreRecord 생성 시 필드 값 확인")
    void testScoreRecordCreation() {
        assertEquals(1000, scoreRecord.getScore());
        assertEquals(10, scoreRecord.getLines());
        assertEquals(10, scoreRecord.getLevel());
        assertEquals(5, scoreRecord.getDifficulty());
        assertEquals("", scoreRecord.getPlayerName()); // 초기값은 빈 문자열
        assertNotNull(scoreRecord.getPlayDate());
        assertTrue(scoreRecord.isNewlyAdded());
    }
    
    @Test
    @DisplayName("점수 기록 날짜가 현재 시간 근처인지 확인")
    void testPlayDateIsNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime playDate = scoreRecord.getPlayDate();
        
        // 1초 이내 차이 확인
        assertTrue(playDate.isAfter(now.minusSeconds(1)));
        assertTrue(playDate.isBefore(now.plusSeconds(1)));
    }
    
    @Test
    @DisplayName("compareTo - 점수가 높은 것이 우선순위")
    void testCompareToHigherScore() {
        ScoreRecord higherScore = new ScoreRecord(2000, 20, 10, 5);
        
        assertTrue(scoreRecord.compareTo(higherScore) > 0);
        assertTrue(higherScore.compareTo(scoreRecord) < 0);
    }
    
    @Test
    @DisplayName("compareTo - 같은 점수")
    void testCompareToEqualScore() {
        ScoreRecord equalScore = new ScoreRecord(1000, 10, 5, 3);
        
        assertEquals(0, scoreRecord.compareTo(equalScore));
    }
    
    @Test
    @DisplayName("setNewlyAdded - 플래그 변경 확인")
    void testSetNewlyAdded() {
        assertTrue(scoreRecord.isNewlyAdded());
        
        scoreRecord.setNewlyAdded(false);
        assertFalse(scoreRecord.isNewlyAdded());
        
        scoreRecord.setNewlyAdded(true);
        assertTrue(scoreRecord.isNewlyAdded());
    }
    
    @Test
    @DisplayName("setPlayerName - 플레이어 이름 설정")
    void testSetPlayerName() {
        ScoreRecord record = new ScoreRecord(500, 5, 3, 1);
        assertEquals("", record.getPlayerName());
        
        record.setPlayerName("NewPlayer");
        assertEquals("NewPlayer", record.getPlayerName());
    }
    
    @Test
    @DisplayName("여러 ScoreRecord 정렬 테스트")
    void testSortingMultipleRecords() {
        ScoreRecord record1 = new ScoreRecord(500, 5, 3, 1);
        ScoreRecord record2 = new ScoreRecord(1500, 15, 7, 3);
        ScoreRecord record3 = new ScoreRecord(1000, 10, 5, 2);
        
        // record2 > record3 > record1 순서여야 함
        assertTrue(record2.compareTo(record3) < 0);
        assertTrue(record3.compareTo(record1) < 0);
        assertTrue(record2.compareTo(record1) < 0);
    }
    
    @Test
    @DisplayName("0점 기록 생성 테스트")
    void testZeroScore() {
        ScoreRecord zeroScore = new ScoreRecord(0, 0, 1, 0);
        
        assertEquals(0, zeroScore.getScore());
        assertEquals(0, zeroScore.getLines());
        assertEquals(1, zeroScore.getLevel());
    }
    
    @Test
    @DisplayName("높은 점수 기록 생성 테스트")
    void testHighScore() {
        ScoreRecord highScore = new ScoreRecord(999999, 999, 99, 99);
        
        assertEquals(999999, highScore.getScore());
        assertEquals(999, highScore.getLines());
        assertEquals(99, highScore.getLevel());
    }
    
    @Test
    @DisplayName("플레이어 이름 설정 테스트")
    void testSetPlayerName() {
        assertEquals("", scoreRecord.getPlayerName()); // 초기값은 빈 문자열
        
        scoreRecord.setPlayerName("Player1");
        assertEquals("Player1", scoreRecord.getPlayerName());
        
        scoreRecord.setPlayerName("TestUser");
        assertEquals("TestUser", scoreRecord.getPlayerName());
    }
}
