package org.example.game.state;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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
        // 메인 컨테이너 (좌우 분할)
        HBox root = new HBox(5);
        root.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, null, null)));
        root.setPadding(new Insets(20));

        // 좌측: 게임 캔버스 영역
        VBox leftContainer = new VBox();
        leftContainer.setAlignment(Pos.CENTER);
        
        gameCanvas = new TetrisCanvas();
        leftContainer.getChildren().add(gameCanvas);
        
        // 우측: 모든 UI 패널들을 VBox로 세로 배치 (Hold, Next, Score 순서)
        VBox rightContainer = new VBox(5);
        rightContainer.setAlignment(Pos.TOP_CENTER);
        rightContainer.setPadding(new Insets(0, 0, 0, 20));
        
        // Hold, Next, Score 패널들 생성 및 추가
        holdPanel = new HoldPanel();
        nextPanel = new NextPiecePanel();
        scorePanel = new ScorePanel();
        
        rightContainer.getChildren().addAll(holdPanel, nextPanel, scorePanel);
        
        // 좌측 영역이 더 많은 공간을 차지하도록 설정
        HBox.setHgrow(leftContainer, Priority.ALWAYS);
        
        root.getChildren().addAll(leftContainer, rightContainer);

        scene = new Scene(root, 1000, 700);

        // 높이와 너비 변경 시 캔버스 크기 비율에 맞게 자동 조정
        scene.heightProperty().addListener((_, _, _) -> updateCanvasSize());
        scene.widthProperty().addListener((_, _, _) -> updateCanvasSize());
        
        // 초기 캔버스 크기 설정
        updateCanvasSize();

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
    
    private void updateCanvasSize() {
        if (gameCanvas == null || scene == null) return;
        
        // 사용 가능한 공간 계산 (여백 고려)
        double availableWidth = scene.getWidth() * 0.65 - 40; // 좌측 65% 영역에서 여백 제외
        double availableHeight = scene.getHeight() - 40; // 상하 여백 제외
        
        // 최소 크기 보장
        availableWidth = Math.max(300, availableWidth);
        availableHeight = Math.max(400, availableHeight);
        
        // 캔버스 크기를 비율에 맞게 조정
        gameCanvas.setCanvasSize(availableWidth, availableHeight);
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