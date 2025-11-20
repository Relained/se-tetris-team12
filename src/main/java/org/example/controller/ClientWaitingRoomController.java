package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import org.example.view.ClientWaitingRoomView;

/**
 * Client Waiting Room 화면의 입력을 처리하는 Controller
 */
public class ClientWaitingRoomController extends BaseController {

    private final ClientWaitingRoomView view;

    public ClientWaitingRoomController() {
        this.view = new ClientWaitingRoomView();
    }

    @Override
    protected Scene createScene() {
        var root = view.createView(this::handleGoBack);
        createDefaultScene(root);
        return scene;
    }

    public void handleGoBack() {
        popState();
    }

    @Override
    public void handleKeyInput(KeyEvent event) {
        view.getButtonSystem().handleInput(event);
    }
}
