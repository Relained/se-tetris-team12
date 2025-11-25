package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

import org.example.view.LocalMultiDifficultyView;

/**
 * Local MultiPlay Difficulty 화면의 입력을 처리하는 Controller
 */
public class LocalMultiDifficultyController extends BaseController {

    private LocalMultiDifficultyView view;
    private boolean isItemMode;

    public LocalMultiDifficultyController(boolean isItemMode) {
        this.view = new LocalMultiDifficultyView();
        this.isItemMode = isItemMode;
    }

    @Override
    protected Scene createScene() {
        var root = view.createView(
            this::handleEasy,
            this::handleMedium,
            this::handleHard,
            this::handleGoBack
        );
        createDefaultScene(root);
        return scene;
    }

    /**
     * Easy 난이도 선택 처리
     */
    public void handleEasy() {
        setState(new LocalMultiPlayController(isItemMode, 1));
    }

    /**
     * Medium 난이도 선택 처리
     */
    public void handleMedium() {
        setState(new LocalMultiPlayController(isItemMode, 2));
    }

    /**
     * Hard 난이도 선택 처리
     */
    public void handleHard() {
        setState(new LocalMultiPlayController(isItemMode, 3));
    }

    /**
     * Go Back 버튼 클릭 시 처리
     */
    public void handleGoBack() {
        popState();
    }

    /**
     * 키보드 입력 처리
     * NavigableButtonSystem을 통해 버튼 내비게이션 처리
     */
    @Override
    public void handleKeyInput(KeyEvent event) {
        view.getButtonSystem().handleInput(event);
    }
}
