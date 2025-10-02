package org.example.game.state;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.example.game.state.ScoreManager;
import org.example.model.ScoreRecord;
import org.example.ui.components.ScoreInputDialog;

public class ScoreInputState extends GameState {
    private int finalScore;
    private int finalLines;
    private int finalLevel;
    private ScoreInputDialog scoreInputDialog;
    private boolean scoreSubmitted = false;

    public ScoreInputState(GameStateManager stateManager, int score, int lines, int level) {
        super(stateManager);
        this.finalScore = score;
        this.finalLines = lines;
        this.finalLevel = level;
    }

    @Override
    public void enter() {
        scoreSubmitted = false;
        
        // 하이스코어가 아니라면 바로 게임오버로 전환
        if (!ScoreManager.getInstance().isHighScore(finalScore)) {
            proceedToGameOver();
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