package org.example.controller;

import javafx.scene.input.KeyEvent;

import org.example.model.GameMode;
import org.example.service.StateManager;
import org.example.state.DifficultyState;
import org.example.state.GameModeState;
import org.example.view.GameModeView;

/**
 * Game Mode 선택 화면의 입력을 처리하는 Controller
 */
public class GameModeController {

    private final StateManager stateManager;
    private final GameModeView view;

    public GameModeController(StateManager stateManager, GameModeView view) {
        this.stateManager = stateManager;
        this.view = view;
    }

    public void handleNormal() {
        stateManager.stackState(new DifficultyState(stateManager, GameMode.NORMAL));
    }

    public void handleItem() {
        stateManager.stackState(new DifficultyState(stateManager, GameMode.ITEM));
    }

    public void handleGoBack() {
        stateManager.popState();
    }

    public void handleKeyInput(KeyEvent event) {
        view.getButtonSystem().handleInput(event);
    }
}
