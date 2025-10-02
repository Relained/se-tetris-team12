package org.example.game.state;

import org.example.model.ScoreRecord;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 점수 관리를 위한 서비스 클래스
 * 게임 상태와 함께 관리되는 점수 시스템
 */
public class ScoreManager {
        private static final String SAVE_FILE = System.getProperty("user.home") 
            + File.separator + "tetris_scores.dat";
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

    public boolean addScore(ScoreRecord record) {
        if (record == null) {
            return false;
        }
        
        scores.add(record);
        Collections.sort(scores);
        
        // 상위 점수만 유지
        if (scores.size() > MAX_SCORES) {
            scores = scores.subList(0, MAX_SCORES);
        }
        
        saveScores();
        return true;
    }

    public List<ScoreRecord> getTopScores() {
        return new ArrayList<>(scores);
    }

    public List<ScoreRecord> getTopScores(int count) {
        int limit = Math.min(count, scores.size());
        return new ArrayList<>(scores.subList(0, limit));
    }

    public void clearScores() {
        scores.clear();
        saveScores();
    }

    public boolean isHighScore(int score) {
        if (scores.size() < MAX_SCORES) {
            return true;
        }
        return score > scores.get(scores.size() - 1).getScore();
    }

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

    private void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(scores);
        } catch (IOException e) {
            System.err.println("Failed to save scores: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadScores() {
        File file = new File(SAVE_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(file))) {
                scores = (List<ScoreRecord>) ois.readObject();
                Collections.sort(scores);
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Failed to load scores: " + e.getMessage());
                scores = new ArrayList<>();
            }
        }
    }

    // 통계 기능은 현재 프로젝트에서 사용하지 않으므로 제거
    // 필요시 나중에 추가 가능
}