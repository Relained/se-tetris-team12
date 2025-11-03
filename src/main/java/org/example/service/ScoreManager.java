package org.example.service;

import org.example.model.ScoreRecord;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 점수 관리를 담당하는 Service 클래스
 * 게임 점수를 저장, 로드, 정렬하는 기능을 제공합니다.
 */
public class ScoreManager {
    private static final String SCORE_SAVE_PATH = System.getProperty("user.home") 
            + File.separator + "tetris_scores.ser";
    private static final int MAX_SCORES = 10;
    
    private List<ScoreRecord> scores;
    private static ScoreManager instance;

    private ScoreManager() {
        scores = new ArrayList<>();
        loadScores();
    }

    public static ScoreManager getInstance() {
        if (instance == null) {
            instance = new ScoreManager();
        }
        return instance;
    }

    /**
     * 점수를 추가하고 상위 10개만 유지합니다.
     * @param record 추가할 점수 기록
     * @return 추가 성공 여부
     */
    public boolean addScore(ScoreRecord record) {
        if (record == null) {
            return false;
        }
        
        // Mark all existing scores as not newly added
        for (ScoreRecord existingRecord : scores) {
            existingRecord.setNewAndEligible(false);
        }
        
        scores.add(record);
        Collections.sort(scores);
        
        // Keep only top scores
        if (scores.size() > MAX_SCORES) {
            scores = new ArrayList<>(scores.subList(0, MAX_SCORES));
        }
        
        saveScores();
        return true;
    }

    /**
     * 상위 점수 목록을 반환합니다.
     * @return 전체 상위 점수 목록
     */
    public List<ScoreRecord> getTopScores() {
        return new ArrayList<>(scores);
    }

    /**
     * 지정된 개수만큼 상위 점수를 반환합니다.
     * @param count 반환할 점수 개수
     * @return 상위 점수 목록
     */
    public List<ScoreRecord> getTopScores(int count) {
        int limit = Math.min(count, scores.size());
        return new ArrayList<>(scores.subList(0, limit));
    }

    /**
     * 모든 점수를 삭제합니다.
     */
    public void clearScores() {
        scores.clear();
        saveScores();
    }

    /**
     * 주어진 점수가 상위 점수인지 확인합니다.
     * @param score 확인할 점수
     * @return 상위 점수 여부
     */
    public boolean isHighScore(int score) {
        if (scores.size() < MAX_SCORES) {
            return true;
        }
        return score > scores.get(scores.size() - 1).getScore();
    }

    /**
     * 상위 10개 점수에 저장 가능 여부를 확인합니다.
     * 10개 미만이면 무조건 저장 가능합니다.
     * @param score 확인할 점수
     * @return 저장 가능 여부
     */
    public boolean isScoreEligibleForSaving(int score) {
        // 10개 미만의 점수가 저장되어 있으면 저장 가능
        if (scores.size() < MAX_SCORES) {
            return true;
        }
        
        // 10개의 점수가 저장되어 있다면, 최저 점수보다 높은지 확인
        ScoreRecord lowestScore = scores.get(scores.size() - 1);
        return score > lowestScore.getScore();
    }

    /**
     * 주어진 점수의 순위를 반환합니다.
     * @param score 확인할 점수
     * @return 순위 (1부터 시작), 순위권 밖이면 -1
     */
    public int getScoreRank(int score) {
        for (int i = 0; i < scores.size(); i++) {
            if (score > scores.get(i).getScore()) {
                return i + 1;
            }
        }
        
        if (scores.size() < MAX_SCORES) {
            return scores.size() + 1;
        }
        
        return -1;
    }

    /**
     * 점수를 파일에 저장합니다.
     */
    private void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SCORE_SAVE_PATH))) {
            oos.writeObject(scores);
        } catch (IOException e) { }
    }

    /**
     * 파일에서 점수를 로드합니다.
     */
    @SuppressWarnings("unchecked")
    private void loadScores() {
        File file = new File(SCORE_SAVE_PATH);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(file))) {
                scores = (List<ScoreRecord>) ois.readObject();
                Collections.sort(scores);
                
                // 로드된 점수들은 모두 newlyAdded를 false로 설정 (highlight 방지)
                for (ScoreRecord record : scores) {
                    record.setNewAndEligible(false);
                }
            } catch (IOException | ClassNotFoundException e) {
                // 로드 실패 시 빈 리스트로 초기화 (파일 손상 등)
                scores = new ArrayList<>();
            }
        }
    }
}
