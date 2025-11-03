package org.example.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 점수 기록을 저장하는 Model 클래스
 */
public class ScoreRecord implements Serializable, Comparable<ScoreRecord> {
    private static final long serialVersionUID = 2L; // 필드 추가로 버전 증가
    
    private String playerName;
    private int score;
    private int lines;
    private int level;
    private int difficulty;
    private GameMode gameMode; // 게임 모드 추가
    private LocalDateTime playDate;
    private transient boolean isNewAndEligible = false; // transient로 직렬화에서 제외

    public ScoreRecord(int score, int lines, int level, int difficulty, GameMode gameMode, boolean isNewAndEligible) {
        this.playerName = "";
        this.score = score;
        this.lines = lines;
        this.level = level;
        this.difficulty = difficulty;
        this.isNewAndEligible = isNewAndEligible; //new 여도 not Eligible 이면 의미가 없음. 따라서 생성 때 Eligible 여부도 합쳐서 저장
        this.gameMode = gameMode != null ? gameMode : GameMode.NORMAL; // null 방지
        this.playDate = LocalDateTime.now();
    }

    // Getters
    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public int getLines() { return lines; }
    public int getLevel() { return level; }
    public int getDifficulty() { return difficulty; }
    public GameMode getGameMode() { return gameMode; }
    public LocalDateTime getPlayDate() { return playDate; }
    public boolean isNewAndEligible() { return isNewAndEligible; }
    
    // Setter for newly added flag
    public void setNewAndEligible(boolean newlyAdded) { this.isNewAndEligible = newlyAdded; }

    // Setter for player name (to fill after game over)
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    @Override
    public int compareTo(ScoreRecord other) {
        return Integer.compare(other.score, this.score); // 내림차순 정렬
    }
}
