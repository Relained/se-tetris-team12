package org.example.controller;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;

import java.net.Socket;

import org.example.model.GameMode;
import org.example.service.WaitingRoomNetworkManager;
import org.example.view.WaitingRoomView;

/**
 * Waiting Room 화면의 입력을 처리하는 Controller
 */
public class WaitingRoomController extends BaseController {

    private final WaitingRoomView view;
    private final boolean isServer;
    private boolean isReady;
    private long lastToggleTime;
    private long lastChatSubmitTime;
    private GameMode selectedGameMode;
    private WaitingRoomNetworkManager netManager;
    private static final long TOGGLE_COOLDOWN_MS = 1000; // 1초 쿨다운
    private static final long CHAT_COOLDOWN_MS = 500; // 500ms 쿨다운

    public WaitingRoomController(Socket socket, boolean isServer) {
        this.view = new WaitingRoomView(isServer);
        this.isServer = isServer;
        this.isReady = false;
        this.lastToggleTime = 0;
        this.lastChatSubmitTime = 0;
        this.selectedGameMode = GameMode.NORMAL;
        this.netManager = new WaitingRoomNetworkManager(socket, isServer, 
            this::handleDisconnect, 
            this::handleGameStart, 
            this::setGameModeText,
            this::handleOpponentReadyChanged,
            this::handleChatReceived);
    }

    @Override
    protected Scene createScene() {
        var root = view.createView(
            netManager.getRemoteIPAddress(),
            this::handleGameModeChange,
            this::handleReadyToggle,
            this::handleChatSubmit,
            this::handleGoBack
        );
        createDefaultScene(root);

        return scene;
    }

    private void handleGameModeChange(String newMode) {
        GameMode newGameMode = switch (newMode) {
            case "Item" -> GameMode.ITEM;
            case "Time-Limited" -> GameMode.TIME_LIMITED;
            default -> GameMode.NORMAL;
        };
        if (selectedGameMode == newGameMode) {
            return;
        }
        selectedGameMode = newGameMode;
        if (isServer) {
            netManager.sendGameModeChange(selectedGameMode);
        }
    }

    private void handleReadyToggle() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastToggleTime < TOGGLE_COOLDOWN_MS) {
            return;
        }
        lastToggleTime = currentTime;

        isReady = !isReady;
        view.updateToggleButtonStyle(isReady);
        
        if (isServer) {
            netManager.setServerReady(isReady);
        }
        netManager.sendReadyState(isReady);
    }

    private void handleChatSubmit(String message) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastChatSubmitTime < CHAT_COOLDOWN_MS) {
            return;
        }
        lastChatSubmitTime = currentTime;

        netManager.sendChatMessage(message);
        view.addChatMessage("[You]: " + message);
    }

    private void handleChatReceived(String message) {
        view.addChatMessage("[Opponent]: " + message);
    }

    private void handleOpponentReadyChanged(boolean ready) {
        if (ready) {
            view.addChatMessage("[System]: Opponent is ready");
        } else {
            view.addChatMessage("[System]: Opponent cancelled ready");
        }
    }

    private void handleGameStart() {
        System.out.println("[Game starting...]");
        swapState(new P2PMultiPlayController(netManager.getSocket(), isServer, selectedGameMode, 2));
    }

    private void handleGoBack() {
        netManager.disconnect();
        popState();
    }

    private void handleDisconnect() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Connection Lost");
        alert.setContentText("The other person's connection has been lost");
        alert.showAndWait();
        Platform.runLater(BaseController::popState);
    }

    private void setGameModeText(GameMode mode) {
        this.selectedGameMode = mode;
        view.setGameModeText(mode.toString());
    }

    @Override
    public void handleKeyInput(KeyEvent event) {
        event.consume();
        switch (event.getCode()) {
            case ESCAPE:
                handleGoBack();
                break;
            case DOWN:
                view.navigateDown();
                break;
            case UP:
                view.navigateUp();
                break;
            case ENTER:
            case SPACE:
                view.activateCurrentButton();
                break;
            default:
                break;
        }
    }
}
