package org.example.controller;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.example.model.ScoreRecord;
import org.example.service.ScoreManager;
import org.example.service.StateManager;
import org.example.view.ScoreInputView;

/**
 * ScoreInput 화면의 입력을 처리하는 Controller
 */
public class ScoreInputController {
    
    private StateManager stateManager;
    private ScoreInputView scoreInputView;
    private int finalScore;
    private int finalLines;
    private int finalLevel;
    private int rank;
    private boolean scoreSubmitted = false;
    
    public ScoreInputController(StateManager stateManager, ScoreInputView scoreInputView,
                               int score, int lines, int level) {
        this.stateManager = stateManager;
        this.scoreInputView = scoreInputView;
        this.finalScore = score;
        this.finalLines = lines;
        this.finalLevel = level;
        this.rank = ScoreManager.getInstance().getScoreRank(score);
    }
    
    /**
     * Rank 정보를 반환합니다.
     * @return 달성한 순위
     */
    public int getRank() {
        return rank;
    }
    
    /**
     * Submit 버튼 클릭 시 처리
     */
    public void handleSubmit() {
        if (!scoreSubmitted) {
            String playerName = scoreInputView.getPlayerName();
            if (!playerName.isEmpty()) {
                ScoreRecord record = new ScoreRecord(playerName, finalScore, finalLines, finalLevel);
                ScoreManager.getInstance().addScore(record);
                scoreSubmitted = true;
                
                // Go to Game Over screen after submitting score with score info and submission status
                org.example.state.GameOverState gameOverState = 
                    new org.example.state.GameOverState(stateManager, finalScore, finalLines, finalLevel, true);
                stateManager.addState("gameOver", gameOverState);
                stateManager.setState("gameOver");
            }
        }
    }
    
    /**
     * Skip 버튼 클릭 시 처리
     */
    public void handleSkip() {
        // Skip and go to Game Over screen with score info (no submission)
        org.example.state.GameOverState gameOverState = 
            new org.example.state.GameOverState(stateManager, finalScore, finalLines, finalLevel, false);
        stateManager.addState("gameOver", gameOverState);
        stateManager.setState("gameOver");
    }
    
    /**
     * 키보드 입력 처리
     */
    public void handleKeyInput(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            handleSkip();
        } else {
            scoreInputView.getButtonSystem().handleInput(event);
        }
    }
}
