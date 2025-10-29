package org.example.controller;

import javafx.scene.input.KeyEvent;
import org.example.service.ScoreManager;
import org.example.service.StateManager;
import org.example.view.ScoreboardView;

/**
 * Scoreboard 화면의 입력을 처리하는 Controller
 */
public class ScoreboardController {
    
    private StateManager stateManager;
    private ScoreboardView scoreboardView;
    private boolean isAfterGamePlay;
    private int gameScore;
    private int gameLines;
    private int gameLevel;
    private boolean scoreWasSubmitted;
    
    // 일반 조회용 생성자
    public ScoreboardController(StateManager stateManager, ScoreboardView scoreboardView) {
        this.stateManager = stateManager;
        this.scoreboardView = scoreboardView;
        this.isAfterGamePlay = false;
    }
    
    // 게임 플레이 후용 생성자
    public ScoreboardController(StateManager stateManager, ScoreboardView scoreboardView,
                               int score, int lines, int level, boolean scoreSubmitted) {
        this.stateManager = stateManager;
        this.scoreboardView = scoreboardView;
        this.isAfterGamePlay = true;
        this.gameScore = score;
        this.gameLines = lines;
        this.gameLevel = level;
        this.scoreWasSubmitted = scoreSubmitted;
    }
    
    /**
     * Back to Menu / Continue 버튼 클릭 시 처리
     */
    public void handleBackToMenu() {
        if (isAfterGamePlay) {
            // 게임 플레이 후라면 GameOver 화면으로
            org.example.state.GameOverState gameOverState = 
                new org.example.state.GameOverState(stateManager, gameScore, gameLines, gameLevel, scoreWasSubmitted);
            stateManager.addState("gameOver", gameOverState);
            stateManager.setState("gameOver");
        } else {
            // 일반 조회라면 시작 화면으로
            stateManager.setState("start");
        }
    }
    
    /**
     * Clear Scores 버튼 클릭 시 처리
     */
    public void handleClearScores() {
        stateManager.settingManager.resetScoreboard();
        refreshScoreboard();
    }
    
    /**
     * 스코어보드 데이터를 새로고침합니다.
     */
    public void refreshScoreboard() {
        var topScores = ScoreManager.getInstance().getTopScores();
        scoreboardView.refresh(topScores);
    }
    
    /**
     * 키보드 입력 처리
     */
    public void handleKeyInput(KeyEvent event) {
        scoreboardView.getButtonSystem().handleInput(event);
    }
}
