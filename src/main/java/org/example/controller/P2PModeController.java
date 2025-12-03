package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import org.example.view.P2PModeView;

/**
 * P2P Mode에서 Server/Client 선택 화면의 입력을 처리하는 Controller
 */
public class P2PModeController extends BaseController {

    private final P2PModeView view;

    public P2PModeController() {
        this.view = new P2PModeView();
    }

    @Override
    protected Scene createScene() {
        var root = view.createView(
            this::handleServer,
            this::handleClient,
            this::handleGoBack
        );
        createDefaultScene(root);
        return scene;
    }

    public void handleServer() {
        stackState(new ServerConnectionController());
    }

    public void handleClient() {
        stackState(new ClientConnectionController());
    }

    public void handleGoBack() {
        popState();
    }

    @Override
    public void handleKeyInput(KeyEvent event) {
        view.getButtonSystem().handleInput(event);
    }
}
