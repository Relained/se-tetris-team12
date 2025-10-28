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
                
                // 점수 제출 후 Scoreboard를 보여주고, 그 다음 GameOver 화면으로
                org.example.state.ScoreboardState scoreboardState = 
                    new org.example.state.ScoreboardState(stateManager, true, finalScore, finalLines, finalLevel, true);
                stateManager.addState("scoreboardAfterSubmit", scoreboardState);
                stateManager.setState("scoreboardAfterSubmit");
            }
        }
    }
    
    /**
     * Skip 버튼 클릭 시 처리
     */
    public void handleSkip() {
        // 점수 제출을 건너뛰고 Scoreboard를 보여주고, 그 다음 GameOver 화면으로
        org.example.state.ScoreboardState scoreboardState = 
            new org.example.state.ScoreboardState(stateManager, false, finalScore, finalLines, finalLevel, false);
        stateManager.addState("scoreboardAfterSkip", scoreboardState);
        stateManager.setState("scoreboardAfterSkip");
    }
    
    /**
     * 키보드 입력 처리
     */
    public void handleKeyInput(KeyEvent event) {
        KeyCode code = event.getCode();
        
        if (code == KeyCode.ENTER) {
            // Enter: 이름이 비어있지 않으면 Submit
            if (!scoreInputView.getPlayerName().isEmpty()) {
                handleSubmit();
            }
        } else if (code == KeyCode.ESCAPE) {
            // ESC: Skip
            handleSkip();
        }
    }
}
