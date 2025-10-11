package org.example.game.state;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.example.ui.NavigableButtonSystem;
import org.example.ui.components.ScoreboardUI;

public class ScoreboardState extends GameState {
    private ScoreboardUI scoreboardUI;
    private NavigableButtonSystem buttonSystem;
    private boolean showNewlyAddedHighlight;

    public ScoreboardState(GameStateManager stateManager) {
        this(stateManager, true); // 기본적으로 하이라이트 표시
    }
    
    public ScoreboardState(GameStateManager stateManager, boolean showNewlyAddedHighlight) {
        super(stateManager);
        this.showNewlyAddedHighlight = showNewlyAddedHighlight;
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
        // scoreboard refresh
        if (scoreboardUI != null) {
            scoreboardUI.refresh();
        }
    }

    @Override
    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        // Create scoreboard UI
        scoreboardUI = new ScoreboardUI(showNewlyAddedHighlight);
        root.setCenter(scoreboardUI);

        // Create button panel
        HBox buttonPanel = createButtonPanel();
        root.setBottom(buttonPanel);

        scene = new Scene(root, 800, 700);

        // Handle keyboard input
        scene.setOnKeyPressed(event -> buttonSystem.handleInput(event));

        // Focus for keyboard input
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();

        return scene;
    }

    private HBox createButtonPanel() {
        HBox buttonPanel = new HBox(20);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setStyle("-fx-padding: 20;");

        buttonSystem = new NavigableButtonSystem();
        
        buttonSystem.createNavigableButton("Back to Menu", () -> {
            stateManager.setState("start");
        });
        
        buttonSystem.createNavigableButton("Clear Scores", () -> {
            scoreboardUI.clearScores();
        });

        buttonPanel.getChildren().addAll(buttonSystem.getButtons());
        return buttonPanel;
    }
}