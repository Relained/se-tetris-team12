package org.example.controller;

import javafx.scene.input.KeyEvent;

import org.example.service.StateManager;
import org.example.state.PlayState;
import org.example.view.DifficultyView;
import org.example.model.GameMode;

/**
 * Difficulty 화면의 입력을 처리하는 Controller
 */
public class DifficultyController {

    private StateManager stateManager;
    private DifficultyView difficultyView;
    private GameMode gameMode;

    public DifficultyController(StateManager stateManager, DifficultyView difficultyView, GameMode gameMode) {
        this.stateManager = stateManager;
        this.difficultyView = difficultyView;
        this.gameMode = gameMode;
    }

    /**
     * Easy 난이도 선택 처리
     */
    public void handleEasy() {
        stateManager.setState(new PlayState(stateManager, gameMode, 1));
    }

    /**
     * Medium 난이도 선택 처리
     */
    public void handleMedium() {
        stateManager.setState(new PlayState(stateManager, gameMode, 2));
    }

    /**
     * Hard 난이도 선택 처리
     */
    public void handleHard() {
        stateManager.setState(new PlayState(stateManager, gameMode, 3));
    }

    /**
     * Go Back 버튼 클릭 시 처리
     */
    public void handleGoBack() {
        stateManager.popState();
    }

    /**
     * 키보드 입력 처리
     * NavigableButtonSystem을 통해 버튼 내비게이션 처리
     */
    public void handleKeyInput(KeyEvent event) {
        difficultyView.getButtonSystem().handleInput(event);
    }
}
