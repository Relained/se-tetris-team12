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
    
    public ScoreboardController(StateManager stateManager, ScoreboardView scoreboardView) {
        this.stateManager = stateManager;
        this.scoreboardView = scoreboardView;
        
        // 초기 데이터 로드는 View 생성 후 State에서 호출
    }
    
    /**
     * Back to Menu 버튼 클릭 시 처리
     */
    public void handleBackToMenu() {
        stateManager.setState("start");
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
