package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.example.view.ClientConnectionView;

/**
 * Client Waiting Room 화면의 입력을 처리하는 Controller
 */
public class ClientConnectionController extends BaseController {

    private final ClientConnectionView view;

    public ClientConnectionController() {
        this.view = new ClientConnectionView();
    }

    @Override
    protected Scene createScene() {
        var root = view.createView(this::handleIpSubmit);
        createDefaultScene(root);
        return scene;
    }

    public void handleGoBack() {
        popState();
    }

    private void handleIpSubmit(String ipAddress) {
        // if (!NetworkManager.isValidIPv4(ipAddress)) {
        //     view.setTitleToInvalidIP();
        //     return;
        // }
        
        // view.setTitleToConnecting();

        swapState(new WaitingRoomController(ipAddress));
    }

    @Override
    public void handleKeyInput(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            handleGoBack();
        }
    }
}
