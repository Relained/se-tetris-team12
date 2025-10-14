package org.example;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.example.ScoreRecord;
import org.example.view.component.NavigableButtonSystem;
import org.example.ScoreInputDialog;
import org.example.ScoreboardUI;
import org.example.state.BaseState;
import org.example.service.StateManager;

/**
 * Unified state for handling both score input and scoreboard display
 */
public class ScoreState extends BaseState {
    
    // State modes
    public enum Mode {
        INPUT,           // Score input mode
        NOT_ELIGIBLE,    // Score not eligible for top 10
        SCOREBOARD       // Scoreboard viewing mode
    }
    
    private Mode currentMode;
    
    // Score input related fields
    private int finalScore;
    private int finalLines;
    private int finalLevel;
    private ScoreInputDialog scoreInputDialog;
    private boolean scoreSubmitted = false;
    
    // Scoreboard related fields
    private ScoreboardUI scoreboardUI;
    private boolean showNewlyAddedHighlight;
    
    // Common fields
    private NavigableButtonSystem buttonSystem;

    // Constructor for score input mode
    public ScoreState(StateManager stateManager, int score, int lines, int level) {
        super(stateManager);
        this.finalScore = score;
        this.finalLines = lines;
        this.finalLevel = level;
        this.showNewlyAddedHighlight = true;
        
        // Determine initial mode based on score eligibility
        if (ScoreManager.getInstance().isScoreEligibleForSaving(finalScore)) {
            this.currentMode = Mode.INPUT;
        } else {
            this.currentMode = Mode.NOT_ELIGIBLE;
        }
    }

    // Constructor for scoreboard viewing mode
    public ScoreState(StateManager stateManager, boolean showNewlyAddedHighlight) {
        super(stateManager);
        this.currentMode = Mode.SCOREBOARD;
        this.showNewlyAddedHighlight = showNewlyAddedHighlight;
    }

    // Constructor for scoreboard viewing mode (default highlight)
    public ScoreState(StateManager stateManager) {
        this(stateManager, true);
    }

    @Override
    public void enter() {
        scoreSubmitted = false;
    }

    @Override
    public void exit() {
        // Cleanup if needed
    }

    @Override
    public void resume() {
        if (currentMode == Mode.SCOREBOARD && scoreboardUI != null) {
            scoreboardUI.refresh();
        }
    }

    @Override
    public Scene createScene() {
        switch (currentMode) {
            case INPUT:
                return createScoreInputScene();
            case NOT_ELIGIBLE:
                return createNotEligibleScene();
            case SCOREBOARD:
                return createScoreboardScene();
            default:
                throw new IllegalStateException("Unknown mode: " + currentMode);
        }
    }

    private Scene createScoreInputScene() {
        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.8), null, null)));

        scoreInputDialog = new ScoreInputDialog(finalScore, finalLines, finalLevel);
        
        scoreInputDialog.setOnSubmit(() -> {
            if (!scoreSubmitted) {
                String playerName = scoreInputDialog.getPlayerName();
                if (!playerName.isEmpty()) {
                    ScoreRecord record = new ScoreRecord(playerName, finalScore, finalLines, finalLevel);
                    ScoreManager.getInstance().addScore(record);
                    scoreSubmitted = true;
                    proceedToGameOver();
                }
            }
        });

        scoreInputDialog.setOnSkip(() -> {
            proceedToGameOver();
        });

        root.getChildren().add(scoreInputDialog);

        Scene scene = new Scene(root, 800, 600);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                proceedToGameOver();
            }
        });

        scoreInputDialog.focusNameInput();
        return scene;
    }

    private Scene createNotEligibleScene() {
        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.8), null, null)));

        VBox messageBox = new VBox(20);
        messageBox.setAlignment(Pos.CENTER);
        
        Text messageText = new Text("Your score didn't make it to the top 10.\nCheck out the current scoreboard!");
        messageText.setFill(Color.LIGHTGRAY);
        messageText.setFont(Font.font("Arial", 18));
        messageText.setTextAlignment(TextAlignment.CENTER);
        
        Text scoreText = new Text("Final Score: " + finalScore);
        scoreText.setFill(Color.YELLOW);
        scoreText.setFont(Font.font("Arial", 20));
        scoreText.setTextAlignment(TextAlignment.CENTER);
        
        buttonSystem = new NavigableButtonSystem();
        var scoreboardButton = buttonSystem.createNavigableButton("View Scoreboard", () -> {
            // Switch to scoreboard mode within the same state
            currentMode = Mode.SCOREBOARD;
            showNewlyAddedHighlight = false;
            // Recreate scene with new mode
            Scene newScene = createScoreboardScene();
            stateManager.getPrimaryStage().setScene(newScene);
        });
        var gameOverButton = buttonSystem.createNavigableButton("Go to Game Over", () -> proceedToGameOver());
        
        messageBox.getChildren().addAll(messageText, scoreText, scoreboardButton, gameOverButton);
        root.getChildren().add(messageBox);
        
        Scene scene = new Scene(root, 800, 600);

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                proceedToGameOver();
            } else {
                buttonSystem.handleInput(event);
            }
        });
        
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();
        
        return scene;
    }

    private Scene createScoreboardScene() {
        BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        scoreboardUI = new ScoreboardUI(showNewlyAddedHighlight);
        root.setCenter(scoreboardUI);

        HBox buttonPanel = createButtonPanel();
        root.setBottom(buttonPanel);

        Scene scene = new Scene(root, 800, 700);

        scene.setOnKeyPressed(event -> buttonSystem.handleInput(event));

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

    private void proceedToGameOver() {
        stateManager.setState("gameOver");
    }

    // Getters for score information
    public int getFinalScore() {
        return finalScore;
    }

    public int getFinalLines() {
        return finalLines;
    }

    public int getFinalLevel() {
        return finalLevel;
    }

    public Mode getCurrentMode() {
        return currentMode;
    }
}