package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import org.example.model.GameMode;
import org.example.view.GameModeView;

/**
 * Game Mode 선택 화면의 입력을 처리하는 Controller
 */
public class GameModeController extends BaseController {

    private final GameModeView view;

    public GameModeController() {
        this.view = new GameModeView();
    }

    @Override
    protected Scene createScene() {
        VBox root = view.createView(
            () -> handleNormal(),
            () -> handleItem(),
            () -> handleGoBack()
        );

        scene = new Scene(root, 1000, 700);
        scene.setFill(org.example.service.ColorManager.getInstance().getBackgroundColor());
        scene.setOnKeyPressed(event -> handleKeyInput(event));
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();
        return scene;
    }

    public void handleNormal() {
        stackState(new DifficultyController(GameMode.NORMAL));
    }

    public void handleItem() {
        stackState(new DifficultyController(GameMode.ITEM));
    }

    public void handleGoBack() {
        popState();
    }

    public void handleKeyInput(KeyEvent event) {
        view.getButtonSystem().handleInput(event);
    }
}
