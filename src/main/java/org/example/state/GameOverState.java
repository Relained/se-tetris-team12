package org.example.state;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.example.controller.GameOverController;
import org.example.service.ScoreManager;
import org.example.service.StateManager;
import org.example.view.GameOverView;

/**
 * 게임 오버 화면 State
 * MVC 패턴을 따라 View와 Controller를 사용하여 구성됩니다.
 */
public class GameOverState extends BaseState {
    
    private GameOverView gameOverView;
    private GameOverController controller;
    private int finalScore;
    private int finalLines;
    private int finalLevel;
    private boolean scoreWasSubmitted = false; // 점수가 제출되었는지 추적

    public GameOverState(StateManager stateManager) {
        super(stateManager);
    }
    
    // Constructor with score information
    public GameOverState(StateManager stateManager, int score, int lines, int level) {
        super(stateManager);
        this.finalScore = score;
        this.finalLines = lines;
        this.finalLevel = level;
    }
    
    // Constructor with score information and submission status
    public GameOverState(StateManager stateManager, int score, int lines, int level, boolean scoreSubmitted) {
        super(stateManager);
        this.finalScore = score;
        this.finalLines = lines;
        this.finalLevel = level;
        this.scoreWasSubmitted = scoreSubmitted;
    }

    @Override
    public void enter() {
        // Get final game stats from the previous play state only if not already set
        if (finalScore == 0 && finalLines == 0 && finalLevel == 0) {
            BaseState previousState = stateManager.getCurrentState();
            if (previousState instanceof PlayState playState && playState.getGameLogic() != null) {
                finalScore = playState.getGameLogic().getScore();
                finalLines = playState.getGameLogic().getLines();
                finalLevel = playState.getGameLogic().getLevel();
            }
        }
        
        // State 진입 시 View와 Controller 초기화
        gameOverView = new GameOverView();
        controller = new GameOverController(stateManager, gameOverView, scoreWasSubmitted);
    }

    @Override
    public void exit() {
        // Cleanup if needed
    }

    @Override
    public void resume() {
        // Not applicable for game over state
    }

    @Override
    public Scene createScene() {
        // View로부터 UI 구성 요소를 받아옴
        // Controller의 핸들러를 콜백으로 전달
        VBox root = gameOverView.createView(
            finalScore,
            finalLines,
            finalLevel,
            () -> controller.handlePlayAgain(),       // Play Again 버튼
            () -> controller.handleViewScoreboard(),  // View Scoreboard 버튼
            () -> controller.handleMainMenu(),        // Main Menu 버튼
            () -> controller.handleExit()             // Exit Game 버튼
        );

        scene = new Scene(root, 1000, 700);
        // Scene 레벨에서 배경색 설정하여 플리커링 방지
        scene.setFill(Color.DARKRED);

        // 키보드 입력은 Controller를 통해 처리
        scene.setOnKeyPressed(event -> controller.handleKeyInput(event));

        // Focus for keyboard input
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();

        return scene;
    }
}
