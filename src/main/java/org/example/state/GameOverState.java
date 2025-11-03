package org.example.state;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.example.controller.GameOverController;
import org.example.model.ScoreRecord;
import org.example.service.StateManager;
import org.example.view.GameOverView;

/**
 * 게임 오버 화면 State
 * MVC 패턴을 따라 View와 Controller를 사용하여 구성됩니다.
 */
public class GameOverState extends BaseState {
    
    private GameOverView gameOverView;
    private GameOverController controller;
    private int score;
    private int lines;
    private int level;

    public GameOverState(StateManager stateManager, ScoreRecord record) {
        super(stateManager);
        gameOverView = new GameOverView();
        controller = new GameOverController(stateManager, gameOverView, record);
        this.score = record.getScore();
        this.lines = record.getLines();
        this.level = record.getLevel();
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
            score,
            lines,
            level,
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
