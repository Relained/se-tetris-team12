package org.example.game.state;

import org.example.ui.NavigableButtonSystem;

import javafx.scene.Scene;

public abstract class GameState {
    protected GameStateManager stateManager;
    protected Scene scene;

    public GameState(GameStateManager stateManager) {
        this.stateManager = stateManager;
    }

    public abstract void enter();
    public abstract void exit();
    public abstract Scene createScene();
    public abstract void resume();
}
