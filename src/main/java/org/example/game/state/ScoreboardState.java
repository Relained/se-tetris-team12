package org.example.game.state;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import org.example.ui.NavigableButtonSystem;
import org.example.ui.components.ScoreboardUI;

public class ScoreboardState extends GameState {
    private ScoreboardUI scoreboardUI;

    public ScoreboardState(GameStateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void enter() {
        // Initialize or refresh scoreboard data
    }

    @Override
    public void exit() {
        // Cleanup if needed
    }

    @Override
    public void resume() {
        // Refresh scoreboard when returning to this state
        if (scoreboardUI != null) {
            scoreboardUI.refresh();
        }
    }

    @Override
    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        // Create scoreboard UI
        scoreboardUI = new ScoreboardUI();
        root.setCenter(scoreboardUI);

        // Create button panel
        HBox buttonPanel = createButtonPanel();
        root.setBottom(buttonPanel);

        scene = new Scene(root, 800, 700);
        setupKeyHandlers(scene);

        return scene;
    }

    private HBox createButtonPanel() {
        HBox buttonPanel = new HBox(20);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setStyle("-fx-padding: 20;");

        NavigableButtonSystem buttonSystem = new NavigableButtonSystem();
        
        var backButton = buttonSystem.createNavigableButton("Back to Menu", () -> {
            stateManager.setState("start");
        });
        
        var clearButton = buttonSystem.createNavigableButton("Clear Scores", () -> {
            scoreboardUI.clearScores();
        });

        buttonPanel.getChildren().addAll(backButton, clearButton);
        return buttonPanel;
    }

    private void setupKeyHandlers(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                case B:
                    stateManager.setState("start");
                    break;
                case C:
                    scoreboardUI.clearScores();
                    break;
                case F5:
                    scoreboardUI.refresh();
                    break;
            }
        });

        // Focus for keyboard input
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();
    }
}