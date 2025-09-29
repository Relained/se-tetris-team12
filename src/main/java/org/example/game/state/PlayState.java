package org.example.game.state;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.example.game.logic.GameLogic;
import org.example.game.logic.SuperRotationSystem;
import org.example.ui.components.HoldPanel;
import org.example.ui.components.NextPiecePanel;
import org.example.ui.components.ScorePanel;
import org.example.ui.components.TetrisCanvas;
import org.example.game.logic.ControlData;

import java.util.HashSet;
import java.util.Set;

public class PlayState extends GameState {
    private GameLogic gameLogic;
    private TetrisCanvas gameCanvas;
    private HoldPanel holdPanel;
    private NextPiecePanel nextPanel;
    private ScorePanel scorePanel;
    private AnimationTimer gameTimer;
    private long lastDropTime;
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final Set<KeyCode> justPressedKeys = new HashSet<>();

    public PlayState(GameStateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void enter() {
        gameLogic = new GameLogic();
        lastDropTime = System.currentTimeMillis();

        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update(now / 1_000_000_000.0);
            }
        };
        gameTimer.start();
    }

    @Override
    public void exit() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    @Override
    public void resume() {
        if (gameTimer != null) {
            gameTimer.start();
        }
    }

    public void update(double deltaTime) {
        if (gameLogic == null) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDropTime >= gameLogic.getDropInterval()) {
            gameLogic.update();
            lastDropTime = currentTime;
        }

        // Update UI
        updateDisplay();

        // Check game over
        if (gameLogic.isGameOver()) {
            stateManager.setState("gameOver");
        }
    }

    private void updateDisplay() {
        if (gameCanvas != null && gameLogic != null) {
            // Calculate ghost piece position
            var ghostPiece = gameLogic.getCurrentPiece() != null ?
                    SuperRotationSystem.hardDrop(gameLogic.getCurrentPiece(), gameLogic.getBoard()) : null;

            gameCanvas.updateBoard(gameLogic.getBoard(), gameLogic.getCurrentPiece(), ghostPiece);
            holdPanel.updateHoldPiece(gameLogic.getHoldPiece());
            nextPanel.updateNextPieces(gameLogic.getNextQueue());
            scorePanel.updateStats(gameLogic.getScore(), gameLogic.getLines(), gameLogic.getLevel());
        }
    }

    @Override
    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, null, null)));

        // Game canvas (center)
        gameCanvas = new TetrisCanvas();
        root.setCenter(gameCanvas);

        // Left panel (Hold)
        holdPanel = new HoldPanel();
        root.setLeft(holdPanel);

        // Right panel (Next pieces and score)
        nextPanel = new NextPiecePanel();
        scorePanel = new ScorePanel();

        HBox rightPanel = new HBox(20);
        rightPanel.getChildren().addAll(nextPanel, scorePanel);
        rightPanel.setAlignment(Pos.TOP_CENTER);
        root.setRight(rightPanel);

        scene = new Scene(root, 1000, 700);

        // Input handling
        // Synced with OS's key repeating rate
        scene.setOnKeyPressed(event -> {
            KeyCode key = event.getCode();
            if (!pressedKeys.contains(key)) {
                justPressedKeys.add(key);
            } // Newly pressed keys only added to justPressedKeys
            pressedKeys.add(key);
            handleInputs();
            justPressedKeys.clear();
        });

        scene.setOnKeyReleased(event -> {
            pressedKeys.remove(event.getCode());
        });

        // Focus for keyboard input
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();

        return scene;
    }

    private void handleInputs() {
        if (gameLogic == null || gameLogic.isGameOver()) return;

        ControlData data = stateManager.settingManager.getCurrentSettings().controlData;

        // key events handler for just ONE execution
        for (KeyCode key : justPressedKeys) {
            if (key == data.hardDrop) {
                gameLogic.hardDrop();
            } else if (key == data.rotateCounterClockwise) {
                gameLogic.rotateCounterClockwise();
            } else if (key == data.rotateClockwise) {
                gameLogic.rotateClockwise();
            } else if (key == data.hold) {
                gameLogic.hold();
            } else if (key == data.pause) {
                stateManager.stackState("pause");
            }
        }

        // key events handler for CONTINUOUS execution
        for (KeyCode key : pressedKeys) {
            if (key == data.moveLeft) {
                gameLogic.moveLeft();
            } else if (key == data.moveRight) {
                gameLogic.moveRight();
            } else if (key == data.softDrop) {
                gameLogic.moveDown();
            }
        }
    }

    public GameLogic getGameLogic() {
        return gameLogic;
    }
}