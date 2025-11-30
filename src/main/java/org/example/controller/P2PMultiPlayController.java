package org.example.controller;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Pair;

import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import org.example.model.AdderBoardSync;
import org.example.model.GameMode;
import org.example.model.KeyData;
import org.example.service.DisplayManager;
import org.example.service.InGameNetworkManager;
import org.example.service.ItemTetrisSystem;
import org.example.service.SuperRotationSystem;
import org.example.service.TetrisSystem;
import org.example.view.P2PMultiPlayView;

/**
 * P2P MultiPlay의 게임 로직과 입력을 처리하는 Controller
 * 멀티플레이어 게임을 관리하며, 내 게임과 상대방 게임을 모두 처리합니다.
 */
public class P2PMultiPlayController extends BaseController {

    private P2PMultiPlayView view;
    private TetrisSystem tetrisSystem;
    private GameMode gameMode;
    private AnimationTimer gameTimer;
    private InGameNetworkManager netManager;
    private AdderBoardSync adderBoard;

    private long lastDropTime;
    private final boolean isServer;
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final Set<KeyCode> justPressedKeys = new HashSet<>();

    public P2PMultiPlayController(Socket socket, boolean isServer, GameMode gameMode, int difficulty) {
        if (gameMode == GameMode.ITEM) {
            tetrisSystem = new ItemTetrisSystem();
        } else {
            tetrisSystem = new TetrisSystem();
        }
        tetrisSystem.setDifficulty(difficulty);

        this.view = new P2PMultiPlayView();
        this.netManager = new InGameNetworkManager(
            socket, 
            this::handleDisconnect,
            this::handleOpponentGoWaitingRoom,
            this::handleOpponentGameOver,
            this::handleAdderBoardReceived,
            view::updateOpponentDisplay,
            tetrisSystem::getCompressedBoardData
        );
        this.isServer = isServer;
        this.gameMode = gameMode;
        this.lastDropTime = System.currentTimeMillis();
        this.adderBoard = new AdderBoardSync(tetrisSystem.getBoard());

        tetrisSystem.setOnPieceLocked(() -> {
            var completedLines = tetrisSystem.getCompletedLineIndices();
            if (completedLines.size() >= 2 && tetrisSystem.getPreviousSnapshot() != null) {
                int[][] lines = tetrisSystem.getPreviousSnapshot().getLines(completedLines);
                netManager.sendAdderBoard(lines);
            }
            if (!adderBoard.isEmpty()) {
                adderBoard.applyToBoard();
            }
        });

        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update(now / 1_000_000_000.0);
            }
        };
    }

    @Override
    protected Scene createScene() {
        DisplayManager.getInstance().setMultiplayerMode(true);
        var root = view.createView(
            gameMode.toString(),
            getDifficultyString(tetrisSystem.getDifficulty())
        );
        createDefaultScene(root);

        // 높이와 너비 변경 시 캔버스 크기 비율에 맞게 자동 조정
        scene.heightProperty().addListener((_, _, _) -> view.updateCanvasSize(scene));
        scene.widthProperty().addListener((_, _, _) -> view.updateCanvasSize(scene));
        
        // 초기 캔버스 크기 설정
        view.updateCanvasSize(scene);

        // 키 릴리즈 핸들 따로 추가
        scene.setOnKeyReleased(event -> handleKeyReleased(event.getCode()));

        gameTimer.start();
        return scene;
    }

    @Override
    protected void exit() {
        DisplayManager.getInstance().setMultiplayerMode(false);
    }

    @Override
    protected void resume() {
        DisplayManager.getInstance().setMultiplayerMode(true);
    }

    private String getDifficultyString(int difficulty) {
        switch (difficulty) {
            case 1:
                return "Easy";
            case 2:
                return "Normal";
            case 3:
                return "Hard";
            default:
                return "Unknown";
        }
    }

    /**
     * 게임 업데이트 로직
     */
    private void update(double deltaTime) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDropTime >= tetrisSystem.getDropInterval()) {
            tetrisSystem.update();
            lastDropTime = currentTime;
        }
        tetrisSystem.getBoard().processPendingClearsIfDue();

        // Update UI through View
        updateDisplay();

        // Check game over
        if (tetrisSystem.isGameOver()) {
            handleGameOver();
        }
    }

    /**
     * 화면 업데이트
     */
    private void updateDisplay() {
        var ghostPiece = tetrisSystem.getCurrentPiece() != null
                ? SuperRotationSystem.hardDrop(tetrisSystem.getCurrentPiece(), tetrisSystem.getBoard())
                : null;

        var nextPiece = !tetrisSystem.getNextQueue().isEmpty() 
                ? tetrisSystem.getNextQueue().get(0) 
                : null;
        
        view.updateDisplay(
                tetrisSystem.getBoard(),
                tetrisSystem.getCurrentPiece(),
                ghostPiece,
                tetrisSystem.getHoldPiece(),
                nextPiece,
                adderBoard,
                tetrisSystem.getScore(),
                tetrisSystem.getLines(),
                tetrisSystem.getLevel(),
                tetrisSystem.getRemainingTime());
    }

    @Override
    protected void handleKeyInput(KeyEvent event) {
        handleKeyPressed(event.getCode());
    }

    /**
     * 키 입력 처리 - 키가 눌렸을 때
     */
    public void handleKeyPressed(KeyCode key) {
        if (!pressedKeys.contains(key)) {
            justPressedKeys.add(key);
        }
        pressedKeys.add(key);
        handleInputs();
        justPressedKeys.clear();
    }

    /**
     * 키 입력 처리 - 키가 떼어졌을 때
     */
    public void handleKeyReleased(KeyCode key) {
        pressedKeys.remove(key);
    }

    /**
     * 입력에 따른 게임 로직 실행
     */
    private void handleInputs() {
        if (tetrisSystem == null || tetrisSystem.isGameOver())
            return;

        // SettingManager를 통해 최신 키 설정 가져오기
        KeyData data = settingManager.getCurrentSettings().controlData;

        // 한 번만 실행되는 입력 처리
        for (KeyCode key : justPressedKeys) {
            if (key == data.hardDrop) {
                tetrisSystem.hardDrop();
            } else if (key == data.rotateCounterClockwise) {
                tetrisSystem.rotateCounterClockwise();
            } else if (key == data.rotateClockwise) {
                tetrisSystem.rotateClockwise();
            } else if (key == data.hold) {
                tetrisSystem.hold();
            } else if (key == data.pause) {
                handlePause();
            }
        }

        // 연속 실행되는 입력 처리
        for (KeyCode key : pressedKeys) {
            if (key == data.moveLeft) {
                tetrisSystem.moveLeft();
            } else if (key == data.moveRight) {
                tetrisSystem.moveRight();
            } else if (key == data.softDrop) {
                tetrisSystem.moveDown();
            }
        }
    }

    private void handleOpponentGameOver() {
        gameTimer.stop();
        setState(new P2PGameOverController("You Win!", netManager.getSocket(), isServer, gameMode, tetrisSystem.getDifficulty()));
    }

    private void handleOpponentGoWaitingRoom() {
        gameTimer.stop();
        setState(new WaitingRoomController(netManager.getSocket(), isServer));
    }

    private void handleAdderBoardReceived(int[][] addedLines) {
        adderBoard.addLines(addedLines);
    }

    private void handleDisconnect() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Connection Lost");
        alert.setContentText("The other person's connection has been lost");
        alert.showAndWait();
        setState(new StartController());
    }
    
    private void handlePause() {
        stackState(new P2PPauseController(this::handleGoWaitingRoom, this::handleGoMainMenu, isServer));
    }

    private void handleGameOver() {
        netManager.sendGameOverAndShutDown();
        gameTimer.stop();
        setState(new P2PGameOverController("You Lose!", netManager.getSocket(), isServer, gameMode, tetrisSystem.getDifficulty()));
    }

    private Pair<Socket, Boolean> handleGoWaitingRoom() {
        netManager.sendGoWaitingRoomAndShutDown();
        gameTimer.stop();
        return new Pair<>(netManager.getSocket(), isServer);
    }

    private void handleGoMainMenu() {
        netManager.disconnect();
        gameTimer.stop();
    }
}
