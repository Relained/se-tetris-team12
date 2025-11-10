package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

import org.example.view.DifficultyView;
import org.example.model.GameMode;

/**
 * Difficulty 화면의 입력을 처리하는 Controller
 */
public class DifficultyController extends BaseController {

    private DifficultyView difficultyView;
    private GameMode gameMode;

    public DifficultyController(GameMode gameMode) {
        this.difficultyView = new DifficultyView();
        this.gameMode = gameMode;
    }

    @Override
    protected Scene createScene() {
        var root = difficultyView.createView(
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
        setState(new PlayController(gameMode, 1));
    }

    /**
     * Medium 난이도 선택 처리
     */
    public void handleMedium() {
        setState(new PlayController(gameMode, 2));
    }

    /**
     * Hard 난이도 선택 처리
     */
    public void handleHard() {
        setState(new PlayController(gameMode, 3));
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
    public void handleKeyInput(KeyEvent event) {
        difficultyView.getButtonSystem().handleInput(event);
    }
}
