package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.util.Pair;

import java.net.Socket;
import java.util.function.Supplier;

import org.example.view.PauseView;

/**
 * Local Multiplayer 모드의 Pause 화면 Controller
 * Restart 시 LocalMultiPlayController로 돌아갑니다.
 */
public class P2PPauseController extends BaseController {
    
    private PauseView pauseView;
    private Supplier<Pair<Socket, Boolean>> onGoWaitingRoom;
    private Runnable onGoMainMenu;
    
    public P2PPauseController(Supplier<Pair<Socket, Boolean>> onGoWaitingRoom, Runnable onGoMainMenu) {
        this.pauseView = new PauseView();
        this.onGoWaitingRoom = onGoWaitingRoom;
        this.onGoMainMenu = onGoMainMenu;
    }

    @Override
    protected Scene createScene() {        
        var root = pauseView.createView(
            this::handleResume,      // Resume 버튼
            this::handleGoWaitingRoom,   // 대기실로 돌아가기 버튼
            this::handleSettings,    // Settings 버튼
            this::handleMainMenu,    // Main Menu 버튼
            this::handleExit         // Exit 버튼
        );
        createDefaultScene(root);
        return scene;
    }

    public void handleResume() {
        popState();
    }
    
    public void handleGoWaitingRoom() {
        Pair<Socket, Boolean> playerData = onGoWaitingRoom.get();
        swapState(new WaitingRoomController(playerData.getKey(), playerData.getValue()));
    }

    public void handleSettings() {
        stackState(new SettingController());
    }
    
    public void handleMainMenu() {
        onGoMainMenu.run();
        setState(new StartController());
    }

    public void handleExit() {
        System.exit(0);
    }
    
    @Override
    public void handleKeyInput(KeyEvent event) {
        pauseView.getButtonSystem().handleInput(event);
    }
}
