package org.example.game.state;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class GameOverState extends GameState {
    private Scene scene;
    private int finalScore;
    private int finalLines;
    private int finalLevel;

    public GameOverState(GameStateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void enter() {
        // Get final game stats from the previous play state
        GameState previousState = stateManager.getCurrentState();
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
    public void update(double deltaTime) {
        // Game over state doesn't need updates
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

        Button playAgainButton = new Button("Play Again (ENTER)");
        playAgainButton.setPrefSize(220, 50);
        playAgainButton.setStyle("-fx-font-size: 18px; -fx-background-color: #4a4a4a; -fx-text-fill: white;");
        playAgainButton.setOnAction(e -> {
            // Create new game and go to play state
            stateManager.setState("play");
        });

        Button mainMenuButton = new Button("Main Menu (ESC)");
        mainMenuButton.setPrefSize(220, 50);
        mainMenuButton.setStyle("-fx-font-size: 18px; -fx-background-color: #4a4a4a; -fx-text-fill: white;");
        mainMenuButton.setOnAction(e -> stateManager.setState("start"));

        Button exitButton = new Button("Exit Game (Q)");
        exitButton.setPrefSize(220, 50);
        exitButton.setStyle("-fx-font-size: 18px; -fx-background-color: #4a4a4a; -fx-text-fill: white;");
        exitButton.setOnAction(e -> System.exit(0));

        Text instructions = new Text("Press ENTER to play again\nPress ESC for main menu\nPress Q to exit");
        instructions.setFill(Color.LIGHTGRAY);
        instructions.setFont(Font.font("Arial", 14));

        root.getChildren().addAll(
                title,
                scoreText,
                linesText,
                levelText,
                playAgainButton,
                mainMenuButton,
                exitButton,
                instructions
        );

        scene = new Scene(root, 800, 600);

        // Handle keyboard input
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER, SPACE -> stateManager.setState("play");
                case ESCAPE -> stateManager.setState("start");
                case Q -> System.exit(0);
                default -> {}
            }
        });

        // Focus for keyboard input
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();

        return scene;
    }

    @Override
    public void handleInput() {
        // Input handled in scene key events
    }
}