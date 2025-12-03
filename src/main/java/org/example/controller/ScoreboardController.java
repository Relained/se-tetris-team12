package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

import org.example.model.ScoreRecord;
import org.example.service.ScoreManager;
import org.example.view.ScoreboardView;

/**
 * Scoreboard 화면의 입력을 처리하는 Controller
 */
public class ScoreboardController extends BaseController {
    
    private ScoreboardView scoreboardView;
    private boolean isAfterGamePlay;
    private ScoreRecord record;
    
    // 일반 조회용 생성자
    public ScoreboardController() {
        this.scoreboardView = new ScoreboardView();
        this.isAfterGamePlay = false;
    }

    public ScoreboardController(boolean isAfterGamePlay, ScoreRecord record) {
        this.scoreboardView = new ScoreboardView(isAfterGamePlay, record.isNewAndEligible());
        this.isAfterGamePlay = isAfterGamePlay;
        this.record = record;
    }

    @Override
    protected Scene createScene() {
        var root = scoreboardView.createView(
            this::handleGoBack,
            this::handleClearScores
        );
        refreshScoreboard();
        createDefaultScene(root);
        return scene;
    }
    
    /**
     * Go Back 버튼 클릭 시 처리
     */
    public void handleGoBack() {
        if (isAfterGamePlay) {
            setState(new GameOverController(record));
        } else {
            popState();
        }
    }
    
    /**
     * Clear Scores 버튼 클릭 시 처리
     */
    public void handleClearScores() {
        settingManager.resetScoreboard();
        refreshScoreboard();
    }
    
    /**
     * 스코어보드 데이터를 새로고침합니다.
     */
    public void refreshScoreboard() {
        var topScores = ScoreManager.getInstance().getTopScores();
        scoreboardView.updateScoreboard(topScores);
    }
    
    /**
     * 키보드 입력 처리
     */
    @Override
    public void handleKeyInput(KeyEvent event) {
        scoreboardView.getButtonSystem().handleInput(event);
    }
}
