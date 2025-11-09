package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import org.example.model.ScoreRecord;
import org.example.service.ScoreManager;
import org.example.view.ScoreInputView;

/**
 * ScoreInput 화면의 입력을 처리하는 Controller
 */
public class ScoreInputController extends BaseController {

    private ScoreInputView scoreInputView;
    private ScoreRecord record;
    private int rank;

    public ScoreInputController(ScoreRecord record) {
        this.scoreInputView = new ScoreInputView();
        this.record = record;
        this.rank = ScoreManager.getInstance().getScoreRank(record.getScore());
    }

    @Override
    protected Scene createScene() {
        VBox root = scoreInputView.createView(
            rank,
            record.getScore(),
            record.getLines(),
            record.getLevel(),
            () -> handleSubmit(),
            () -> handleSkip()
        );

        scene = new Scene(root, 1000, 700);
        scene.setFill(org.example.service.ColorManager.getInstance().getBackgroundColor());
        scene.setOnKeyPressed(event -> handleKeyInput(event));
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();
        return scene;
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
            setState(new ScoreboardController(record));
        }
    }

    /**
     * Skip 버튼 클릭 시 처리
     */
    public void handleSkip() {
        record.setNewAndEligible(false); //이 값을 scoreWasSubmitted 용도로 재활용
        setState(new ScoreboardController(record));
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
