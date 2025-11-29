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

    private P2PMultiPlayView multiPlayView;
    private TetrisSystem myTetrisSystem;
    private GameMode gameMode;
    private AnimationTimer gameTimer;
    private InGameNetworkManager netManager;

    private long lastDropTime;
    private final boolean isServer;
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final Set<KeyCode> justPressedKeys = new HashSet<>();

    public P2PMultiPlayController(Socket socket, boolean isServer, GameMode gameMode, int difficulty) {
        if (gameMode == GameMode.ITEM) {
            myTetrisSystem = new ItemTetrisSystem();
        } else {
            myTetrisSystem = new TetrisSystem();
        }
        myTetrisSystem.setDifficulty(difficulty);

        this.multiPlayView = new P2PMultiPlayView();
        this.netManager = new InGameNetworkManager(
            socket, 
            isServer,
            this::handleDisconnect,
            multiPlayView::updateOpponentDisplay,
            myTetrisSystem::getCompressedBoardData
        );
        this.isServer = isServer;
        this.gameMode = gameMode;
        this.lastDropTime = System.currentTimeMillis();

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
        var root = multiPlayView.createView(
            gameMode.toString(),
            getDifficultyString(myTetrisSystem.getDifficulty())
        );
        createDefaultScene(root);

        // 높이와 너비 변경 시 캔버스 크기 비율에 맞게 자동 조정
        scene.heightProperty().addListener((_, _, _) -> multiPlayView.updateCanvasSize(scene));
        scene.widthProperty().addListener((_, _, _) -> multiPlayView.updateCanvasSize(scene));
        
        // 초기 캔버스 크기 설정
        multiPlayView.updateCanvasSize(scene);

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
    public void update(double deltaTime) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDropTime >= myTetrisSystem.getDropInterval()) {
            myTetrisSystem.update();
            lastDropTime = currentTime;
        }
        myTetrisSystem.getBoard().processPendingClearsIfDue();

        // Update UI through View
        updateDisplay();

        // Check game over
        if (myTetrisSystem.isGameOver()) {
            handleGameOver();
        }
    }

    /**
     * 화면 업데이트
     */
    private void updateDisplay() {
        var ghostPiece = myTetrisSystem.getCurrentPiece() != null
                ? SuperRotationSystem.hardDrop(myTetrisSystem.getCurrentPiece(), myTetrisSystem.getBoard())
                : null;

        multiPlayView.updateDisplay(
                myTetrisSystem.getBoard(),
                myTetrisSystem.getCurrentPiece(),
                ghostPiece,
                myTetrisSystem.getHoldPiece(),
                myTetrisSystem.getNextQueue(),
                myTetrisSystem.getScore(),
                myTetrisSystem.getLines(),
                myTetrisSystem.getLevel());
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
        if (myTetrisSystem == null || myTetrisSystem.isGameOver())
            return;

        // SettingManager를 통해 최신 키 설정 가져오기
        KeyData data = settingManager.getCurrentSettings().controlData;

        // 한 번만 실행되는 입력 처리
        for (KeyCode key : justPressedKeys) {
            if (key == data.hardDrop) {
                myTetrisSystem.hardDrop();
            } else if (key == data.rotateCounterClockwise) {
                myTetrisSystem.rotateCounterClockwise();
            } else if (key == data.rotateClockwise) {
                myTetrisSystem.rotateClockwise();
            } else if (key == data.hold) {
                myTetrisSystem.hold();
            } else if (key == data.pause) {
                handlePause();
            }
        }

        // 연속 실행되는 입력 처리
        for (KeyCode key : pressedKeys) {
            if (key == data.moveLeft) {
                myTetrisSystem.moveLeft();
            } else if (key == data.moveRight) {
                myTetrisSystem.moveRight();
            } else if (key == data.softDrop) {
                myTetrisSystem.moveDown();
            }
        }
    }

    /**
     * 일시정지 처리
     */
    public void handlePause() {
        stackState(new P2PPauseController(null, null));
    }

    public void handleDisconnect() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Connection Lost");
        alert.setHeaderText(null);
        alert.setContentText("Disconnected from opponent.");
        alert.showAndWait();
        popState();
    }

    // 그냥 disconnect 하는게 아니라, tcpSocket은 살려야됨
    // udp는 끊고, 스레드는 전부 종료
    public void handleGameOver() {
        netManager.disconnect();
        gameTimer.stop();
        popState();
    }

    // 얘도 마찬가지
    public Pair<Socket, Boolean> onGoWaitingRoom() {
        netManager.disconnect();
        gameTimer.stop();
        return new Pair<>(netManager.getSocket(), isServer);
    }

    public void onGoMainMenu() {
        netManager.disconnect();
        gameTimer.stop();
    }

    public TetrisSystem getMyGameLogic() {
        return myTetrisSystem;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void resetLastDropTime() {
        this.lastDropTime = System.currentTimeMillis();
    }
}
