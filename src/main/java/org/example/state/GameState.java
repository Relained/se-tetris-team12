package org.example.state;

import org.example.service.StateManager;

import javafx.scene.Scene;

public abstract class GameState {
    protected StateManager stateManager;
    protected Scene scene;

    public GameState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public abstract void enter();
    public abstract void exit();
    public abstract Scene createScene();
    public abstract void resume();
}
