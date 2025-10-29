package org.example.controller;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.example.model.ScoreRecord;
import org.example.service.ScoreManager;
import org.example.service.StateManager;
import org.example.state.ScoreboardState;
import org.example.view.ScoreInputView;

/**
 * ScoreInput 화면의 입력을 처리하는 Controller
 */
public class ScoreInputController {

    private StateManager stateManager;
    private ScoreInputView scoreInputView;
    private ScoreRecord record;
    private int rank;

    public ScoreInputController(StateManager stateManager, ScoreInputView scoreInputView,
            ScoreRecord record) {
        this.stateManager = stateManager;
        this.scoreInputView = scoreInputView;
        this.record = record;
        this.rank = ScoreManager.getInstance().getScoreRank(record.getScore());
    }

    /**
     * Rank 정보를 반환합니다.
     * 
     * @return 달성한 순위
     */
    public int getRank() {
        return rank;
    }

    /**
     * Submit 버튼 클릭 시 처리
     */
    public void handleSubmit() {
        String playerName = scoreInputView.getPlayerName();
        if (!playerName.isEmpty()) {
            record.setPlayerName(playerName);
            ScoreManager.getInstance().addScore(record);
            
            ScoreboardState scoreboardState = (ScoreboardState)stateManager.getCurrentState();
            scoreboardState.setScoreBoardScene(true);
        }
    }

    /**
     * Skip 버튼 클릭 시 처리
     */
    public void handleSkip() {
        ScoreboardState scoreboardState = (ScoreboardState)stateManager.getCurrentState();
        scoreboardState.setScoreBoardScene(false);
    }

    /**
     * 키보드 입력 처리
     */
    public void handleKeyInput(KeyEvent event) {
        KeyCode code = event.getCode();

        if (code == KeyCode.ENTER) {
            handleSubmit();
        } else if (code == KeyCode.ESCAPE) {
            handleSkip();
        }
    }
}
