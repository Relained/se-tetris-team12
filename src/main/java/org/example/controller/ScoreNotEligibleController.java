package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import org.example.model.ScoreRecord;
import org.example.view.ScoreNotEligibleView;

/**
 * 점수가 상위 10개에 들지 못한 경우를 표시하는 Controller
 * 게임 오버 화면으로 이동할 수 있습니다.
 */
public class ScoreNotEligibleController extends BaseController {

    private final ScoreRecord record;
    private ScoreNotEligibleView scoreNotEligibleView;

    public ScoreNotEligibleController(ScoreRecord record) {
        this.record = record;
        this.scoreNotEligibleView = new ScoreNotEligibleView();
    }
    
    @Override
    protected void resume() {
        // 화면으로 돌아올 때 현재 스케일 재적용
        if (scoreNotEligibleView.getButtonSystem() != null) {
            var displayManager = org.example.service.DisplayManager.getInstance();
            var currentSize = displayManager.getCurrentSize();
            scoreNotEligibleView.updateScale(currentSize);
        }
    }

    @Override
    public Scene createScene() {
        var root = scoreNotEligibleView.createView(
            record.getScore(),
            this::handleContinue
        );
        
        createDefaultScene(root);
        return scene;
    }
    
    /**
     * Continue 버튼 클릭 시 처리
     */
    private void handleContinue() {
        setState(new GameOverController(record));
    }

    @Override
    protected void handleKeyInput(KeyEvent event) {
        // NavigableButtonSystem을 통해 버튼 내비게이션 처리
        scoreNotEligibleView.getButtonSystem().handleInput(event);
        
        // ESC 키는 Continue와 동일하게 처리
        if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
            handleContinue();
        }
    }
}
