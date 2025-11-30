package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.util.Pair;

import java.net.Socket;
import java.util.function.Supplier;

import org.example.view.P2PPauseView;

/**
 * Local Multiplayer 모드의 Pause 화면 Controller
 * Restart 시 LocalMultiPlayController로 돌아갑니다.
 */
public class P2PPauseController extends BaseController {
    
    private P2PPauseView pauseView;
    private Supplier<Pair<Socket, Boolean>> onGoWaitingRoom;
    private Runnable onGoMainMenu;
    private boolean isServer;
    
    public P2PPauseController(Supplier<Pair<Socket, Boolean>> onGoWaitingRoom, Runnable onGoMainMenu, boolean isServer) {
        this.pauseView = new P2PPauseView();
        this.onGoWaitingRoom = onGoWaitingRoom;
        this.onGoMainMenu = onGoMainMenu;
        this.isServer = isServer;
    }

    @Override
    protected Scene createScene() {        
        var root = pauseView.createView(
            this::handleResume,
            this::handleGoWaitingRoom,
            this::handleSettings,
            this::handleMainMenu,
            this::handleExit,
            isServer
        );
        createDefaultScene(root);
        return scene;
    }

    public void handleResume() {
        popState();
    }
    
    public void handleGoWaitingRoom() {
        Pair<Socket, Boolean> playerData = onGoWaitingRoom.get();
        setState(new WaitingRoomController(playerData.getKey(), playerData.getValue()));
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
