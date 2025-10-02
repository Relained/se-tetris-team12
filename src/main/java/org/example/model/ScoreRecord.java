package org.example.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ScoreRecord implements Serializable, Comparable<ScoreRecord> {
    private String playerName;
    private int score;
    private int lines;
    private int level;
    private LocalDateTime playDate;

    public ScoreRecord(String playerName, int score, int lines, int level) {
        this.playerName = playerName;
        this.score = score;
        this.lines = lines;
        this.level = level;
        this.playDate = LocalDateTime.now();
    }

    // Getters
    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public int getLines() { return lines; }
    public int getLevel() { return level; }
    public LocalDateTime getPlayDate() { return playDate; }

    @Override
    public int compareTo(ScoreRecord other) {
        return Integer.compare(other.score, this.score); // 내림차순 정렬
    }
}