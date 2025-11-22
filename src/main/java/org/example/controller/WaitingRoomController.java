package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import org.example.model.GameMode;
import org.example.view.WaitingRoomView;

/**
 * Waiting Room 화면의 입력을 처리하는 Controller
 */
public class WaitingRoomController extends BaseController {

    private final WaitingRoomView view;
    private final boolean isServer;
    private GameMode selectedGameMode;
    private String ipAddress;
    private boolean isReady;
    private long lastToggleTime;
    private static final long TOGGLE_COOLDOWN_MS = 1000; // 1초 쿨다운

    public WaitingRoomController(String ipAddress, boolean isServer) {
        this.view = new WaitingRoomView(isServer);
        this.isServer = isServer;
        this.selectedGameMode = GameMode.NORMAL;
        this.isReady = false;
        this.lastToggleTime = 0;
        this.ipAddress = ipAddress;
    }

    @Override
    protected Scene createScene() {
        var root = view.createView(
            ipAddress,
            this::handleGameModeChange,
            this::handleReadyToggle
        );
        createDefaultScene(root);

        return scene;
    }

    public void handleGameModeChange(String newMode) {
        selectedGameMode = switch (newMode) {
            case "Item" -> GameMode.ITEM;
            case "Time-Limited" -> GameMode.TIME_LIMITED;
            default -> GameMode.NORMAL;
        };
    }

    public void setGameMode(GameMode mode) {
        this.selectedGameMode = mode;
        view.setGameModeText(mode.toString());
    }

    public void handleReadyToggle() {
        long currentTime = System.currentTimeMillis();
        
        // 쿨다운 체크
        if (currentTime - lastToggleTime < TOGGLE_COOLDOWN_MS) {
            return;
        }
        
        // 토글 수행
        lastToggleTime = currentTime;
        isReady = !isReady;
        view.updateToggleButtonStyle(isReady);
    }

    public GameMode getSelectedGameMode() {
        return selectedGameMode;
    }

    public boolean isReady() {
        return isReady;
    }

    @Override
    protected void exit() {

    }

    @Override
    public void handleKeyInput(KeyEvent event) {
        event.consume();
        switch (event.getCode()) {
            case ESCAPE:
                popState();
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
