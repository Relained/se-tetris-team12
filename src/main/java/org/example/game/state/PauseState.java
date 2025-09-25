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

public class PauseState extends GameState {
    private Scene scene;

    public PauseState(GameStateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void enter() {
        // Setup complete when scene is created
    }

    @Override
    public void exit() {
        // Cleanup if needed
    }

    @Override
    public void update(double deltaTime) {
        // Pause state doesn't need updates
    }

    @Override
    public Scene createScene() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        Text title = new Text("PAUSED");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", 36));

        Button resumeButton = new Button("Resume (P)");
        resumeButton.setPrefSize(200, 50);
        resumeButton.setStyle("-fx-font-size: 18px; -fx-background-color: #4a4a4a; -fx-text-fill: white;");
        resumeButton.setOnAction(e -> stateManager.setState("play"));

        Button restartButton = new Button("Restart (R)");
        restartButton.setPrefSize(200, 50);
        restartButton.setStyle("-fx-font-size: 18px; -fx-background-color: #4a4a4a; -fx-text-fill: white;");
        restartButton.setOnAction(e -> {
            // Reset the game and go to play state
            PlayState playState = (PlayState) stateManager.getCurrentState();
            if (playState != null && playState.getGameLogic() != null) {
                playState.getGameLogic().reset();
            }
            stateManager.setState("play");
        });

        Button mainMenuButton = new Button("Main Menu (ESC)");
        mainMenuButton.setPrefSize(200, 50);
        mainMenuButton.setStyle("-fx-font-size: 18px; -fx-background-color: #4a4a4a; -fx-text-fill: white;");
        mainMenuButton.setOnAction(e -> stateManager.setState("start"));

        Text instructions = new Text("Press P to resume\nPress R to restart\nPress ESC for main menu");
        instructions.setFill(Color.LIGHTGRAY);
        instructions.setFont(Font.font("Arial", 14));

        root.getChildren().addAll(title, resumeButton, restartButton, mainMenuButton, instructions);

        scene = new Scene(root, 800, 600);

        // Handle keyboard input
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case P -> stateManager.setState("play");
                case R -> {
                    // Get current play state and reset game
                    GameState currentPlayState = stateManager.getCurrentState();
                    if (currentPlayState instanceof PlayState playState) {
                        if (playState.getGameLogic() != null) {
                            playState.getGameLogic().reset();
                        }
                    }
                    stateManager.setState("play");
                }
                case ESCAPE -> stateManager.setState("start");
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