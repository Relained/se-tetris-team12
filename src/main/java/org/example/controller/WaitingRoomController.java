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
    private GameMode selectedGameMode;
    private String ipAddress;
    private boolean isReady;
    private long lastToggleTime;
    private static final long TOGGLE_COOLDOWN_MS = 1000; // 1초 쿨다운

    public WaitingRoomController(String ipAddress) {
        this.view = new WaitingRoomView();
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
            this::handleReadyToggle,
            this::handleGoBack
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

    public void handleGoBack() {
        popState();
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
        view.getButtonSystem().handleInput(event);
    }
}
