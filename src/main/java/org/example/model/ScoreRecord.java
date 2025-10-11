package org.example.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ScoreRecord implements Serializable, Comparable<ScoreRecord> {
    private static final long serialVersionUID = 1L; // 명시적 버전 설정
    
    private String playerName;
    private int score;
    private int lines;
    private int level;
    private LocalDateTime playDate;
    private transient boolean isNewlyAdded = false; // transient로 직렬화에서 제외

    public ScoreRecord(String playerName, int score, int lines, int level) {
        this.playerName = playerName;
        this.score = score;
        this.lines = lines;
        this.level = level;
        this.playDate = LocalDateTime.now();
        this.isNewlyAdded = true; // 생성 시에는 새로운 점수로 설정
    }

    // Getters
    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public int getLines() { return lines; }
    public int getLevel() { return level; }
    public LocalDateTime getPlayDate() { return playDate; }
    public boolean isNewlyAdded() { return isNewlyAdded; }
    
    // Setter for newly added flag
    public void setNewlyAdded(boolean newlyAdded) { this.isNewlyAdded = newlyAdded; }

    @Override
    public int compareTo(ScoreRecord other) {
        return Integer.compare(other.score, this.score); // 내림차순 정렬
    }
}