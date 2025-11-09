package org.example.state;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import org.example.controller.ScoreboardController;
import org.example.model.ScoreRecord;
import org.example.service.StateManager;
import org.example.view.ScoreboardView;

/**
 * 스코어보드 조회 화면을 담당하는 State
 * 게임 플레이 후 또는 메뉴에서 스코어보드를 볼 때 사용됩니다.
 */
public class ScoreboardState extends BaseState {
    
    private ScoreboardView scoreboardView;
    private ScoreboardController scoreboardController;

    /**
     * 게임 플레이 후 스코어보드를 보는 생성자
     * 
     * @param stateManager StateManager 인스턴스
     * @param record 게임 플레이 기록
     */
    public ScoreboardState(StateManager stateManager, ScoreRecord record) {
        super(stateManager);
        scoreboardView = new ScoreboardView(record.isNewAndEligible(), true);
        scoreboardController = new ScoreboardController(stateManager, scoreboardView, record);
    }

    /**
     * 메뉴에서 스코어보드를 보는 생성자
     * 
     * @param stateManager StateManager 인스턴스
     */
    public ScoreboardState(StateManager stateManager) {
        super(stateManager);
        scoreboardView = new ScoreboardView(false, false);
        scoreboardController = new ScoreboardController(stateManager, scoreboardView);
    }

    @Override
    public void exit() {
        // Cleanup if needed
    }

    @Override
    public void resume() {
        if (scoreboardController != null) {
            scoreboardController.refreshScoreboard();
        }
    }

    @Override
    public Scene createScene() {        
        BorderPane root = scoreboardView.createView(
            () -> scoreboardController.handleGoBack(),
            () -> scoreboardController.handleClearScores()
        );
        
        // View 생성 후 데이터 로드
        scoreboardController.refreshScoreboard();

        scene = new Scene(root, 800, 700);
        scene.setOnKeyPressed(event -> scoreboardController.handleKeyInput(event));

        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();

        return scene;
    }
}
