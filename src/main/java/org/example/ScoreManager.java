package org.example;

import org.example.ScoreRecord;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service class for score management
 * Score system managed with game state
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
        
        // Mark all existing scores as not newly added
        for (ScoreRecord existingRecord : scores) {
            existingRecord.setNewlyAdded(false);
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

    /**
     * 상위 10개 점수에 저장 가능 여부 확인, 10개 미만이면 무조건 저장 가능
     * @param score Score to check
     * @return true if saveable, false otherwise
     */
    public boolean isScoreEligibleForSaving(int score) {
        // If less than 10 scores saved, always saveable
        if (scores.size() < MAX_SCORES) {
            return true;
        }
        
        // If 10 scores saved, check if higher than lowest score
        ScoreRecord lowestScore = scores.get(scores.size() - 1);
        return score > lowestScore.getScore();
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
                
                // Mark all loaded scores as not newly added
                for (ScoreRecord record : scores) {
                    record.setNewlyAdded(false);
                }
            }
             
            // 버전 불일치로 인한 InvalidClassException

            /* catch (InvalidClassException e) {
                // Version mismatch - delete old file and start fresh
                System.out.println("Score file version mismatch. Creating new scoreboard.");
                if (file.delete()) {
                    System.out.println("Old score file deleted successfully.");
                }
                scores = new ArrayList<>();
                saveScores(); // Create new compatible file
            } 
            */
            catch (IOException | ClassNotFoundException e) {
                System.err.println("Failed to load scores: " + e.getMessage());
                scores = new ArrayList<>();
            }
        }
    }
}