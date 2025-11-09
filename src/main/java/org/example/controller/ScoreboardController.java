package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

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

    // 게임 플레이 후 조회용 생성자
    public ScoreboardController(ScoreRecord record) {
        this.scoreboardView = new ScoreboardView(record.isNewAndEligible());
        this.isAfterGamePlay = true;
        this.record = record;
    }

    @Override
    protected Scene createScene() {
        BorderPane root = scoreboardView.createView(
            () -> handleGoBack(),
            () -> handleClearScores()
        );

        refreshScoreboard();

        scene = new Scene(root, 1000, 700);
        scene.setFill(org.example.service.ColorManager.getInstance().getBackgroundColor());
        scene.setOnKeyPressed(event -> handleKeyInput(event));
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();
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
    public void handleKeyInput(KeyEvent event) {
        scoreboardView.getButtonSystem().handleInput(event);
    }
}
