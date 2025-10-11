package org.example.state;

import org.example.service.StateManager;
import org.example.view.component.NavigableButtonSystem;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class GameOverState extends State {
    private int finalScore;
    private int finalLines;
    private int finalLevel;

    public GameOverState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void enter() {
        // Get final game stats from the previous play state
        State previousState = stateManager.getCurrentState();
        if (previousState instanceof PlayState playState && playState.getGameLogic() != null) {
            finalScore = playState.getGameLogic().getScore();
            finalLines = playState.getGameLogic().getLines();
            finalLevel = playState.getGameLogic().getLevel();
        }
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
        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.DARKRED, null, null)));

        Text title = new Text("GAME OVER");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", 42));

        Text scoreText = new Text("Final Score: " + finalScore);
        scoreText.setFill(Color.LIGHTGRAY);
        scoreText.setFont(Font.font("Arial", 20));

        Text linesText = new Text("Lines Cleared: " + finalLines);
        linesText.setFill(Color.LIGHTGRAY);
        linesText.setFont(Font.font("Arial", 20));

        Text levelText = new Text("Level Reached: " + finalLevel);
        levelText.setFill(Color.LIGHTGRAY);
        levelText.setFont(Font.font("Arial", 20));

        NavigableButtonSystem buttonSystem = new NavigableButtonSystem();

        var playAgainButton = buttonSystem.createNavigableButton("Play Again", () -> stateManager.setState("play"));
        var mainMenuButton = buttonSystem.createNavigableButton("Main Menu", () -> stateManager.setState("start"));
        var exitButton = buttonSystem.createNavigableButton("Exit Game", () -> System.exit(0));

        root.getChildren().addAll(
                title,
                scoreText,
                linesText,
                levelText,
                playAgainButton,
                mainMenuButton,
                exitButton
        );

        scene = new Scene(root, 1000, 700);
        // Scene 레벨에서 배경색 설정하여 플리커링 방지
        scene.setFill(Color.DARKRED);

        // Handle keyboard input
        scene.setOnKeyPressed(event -> buttonSystem.handleInput(event));

        // Focus for keyboard input
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();

        return scene;
    }
}