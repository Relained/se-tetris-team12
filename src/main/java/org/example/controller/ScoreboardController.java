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
    
    // 일반 조회용 생성자
    public ScoreboardController(StateManager stateManager, ScoreboardView scoreboardView, boolean isAfterGamePlay) {
        this.stateManager = stateManager;
        this.scoreboardView = scoreboardView;
        this.isAfterGamePlay = isAfterGamePlay;
    }
    
    /**
     * Go Back 버튼 클릭 시 처리
     */
    public void handleGoBack() {
        if (isAfterGamePlay) stateManager.setState("gameover");
        else stateManager.popState();
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
