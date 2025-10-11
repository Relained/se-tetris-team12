package org.example.game.state;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.example.model.ScoreRecord;
import org.example.ui.components.ScoreInputDialog;
import org.example.ui.NavigableButtonSystem;

public class ScoreInputState extends GameState {
    private int finalScore;
    private int finalLines;
    private int finalLevel;
    private ScoreInputDialog scoreInputDialog;
    private boolean scoreSubmitted = false;
    private boolean showingNotEligibleMessage = false;
    private NavigableButtonSystem buttonSystem;

    public ScoreInputState(GameStateManager stateManager, int score, int lines, int level) {
        super(stateManager);
        this.finalScore = score;
        this.finalLines = lines;
        this.finalLevel = level;
    }

    @Override
    public void enter() {
        scoreSubmitted = false;
        showingNotEligibleMessage = false;

        // 점수가 상위 10위 안에 들지 못하면 메시지 표시 후 스코어보드로 이동
        if (!ScoreManager.getInstance().isScoreEligibleForSaving(finalScore)) {
            showingNotEligibleMessage = true;
            return;
        }
    }

    @Override
    public void exit() {
        // Cleanup if needed
    }

    @Override
    public void resume() {
        // Not applicable for score input state
    }

    @Override
    public Scene createScene() {
        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.8), null, null)));

        // 상위 10위에 들지 못했을 때 메시지 표시
        if (showingNotEligibleMessage) {
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
            var scoreboardButton = buttonSystem.createNavigableButton("View Scoreboard", () -> proceedToScoreboard());
            var gameOverButton = buttonSystem.createNavigableButton("Go to Game Over", () -> proceedToGameOver());
            
            messageBox.getChildren().addAll(messageText, scoreText, scoreboardButton, gameOverButton);
            root.getChildren().add(messageBox);
            
            scene = new Scene(root, 800, 600);

            // Handle keyboard input
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    proceedToGameOver();
                } else {
                    buttonSystem.handleInput(event);
                }
            });
            
            // Focus for keyboard input
            scene.getRoot().setFocusTraversable(true);
            scene.getRoot().requestFocus();
            
            return scene;
        }

        // Original score input logic
        scoreInputDialog = new ScoreInputDialog(finalScore, finalLines, finalLevel);
        
        // Set up event handlers
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

        scene = new Scene(root, 800, 600);

        // Handle keyboard input
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                proceedToGameOver();
            }
        });

        // Focus the input field
        scoreInputDialog.focusNameInput();

        return scene;
    }

    private void proceedToGameOver() {
        stateManager.setState("gameOver");
    }

    private void proceedToScoreboard() {
        // Create a scoreboard state without newly added highlights
        ScoreboardState scoreboardState = new ScoreboardState(stateManager, false);
        stateManager.addState("scoreboardNoHighlight", scoreboardState);
        stateManager.setState("scoreboardNoHighlight");
    }

    public int getFinalScore() {
        return finalScore;
    }

    public int getFinalLines() {
        return finalLines;
    }

    public int getFinalLevel() {
        return finalLevel;
    }
}