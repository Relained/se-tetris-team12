package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import org.example.view.LocalMultiGameModeView;

/**
 * Local MultiPlay Game Mode 선택 화면의 입력을 처리하는 Controller
 */
public class LocalMultiGameModeController extends BaseController {

    private final LocalMultiGameModeView view;

    public LocalMultiGameModeController() {
        this.view = new LocalMultiGameModeView();
    }

    @Override
    protected Scene createScene() {
        var root = view.createView(
            this::handleNormal,
            this::handleItem,
            this::handleGoBack
        );
        createDefaultScene(root);
        return scene;
    }

    /**
     * Normal 모드 선택 시 처리
     */
    public void handleNormal() {
        stackState(new LocalMultiDifficultyController(false));
    }

    /**
     * Item 모드 선택 시 처리
     */
    public void handleItem() {
        stackState(new LocalMultiDifficultyController(true));
    }

    /**
     * Go Back 버튼 클릭 시 처리
     */
    public void handleGoBack() {
        popState();
    }

    @Override
    public void handleKeyInput(KeyEvent event) {
        view.getButtonSystem().handleInput(event);
    }
}
