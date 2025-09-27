package org.example.game.state;

import javafx.scene.Scene;

public abstract class GameState {
    protected GameStateManager stateManager;
    protected Scene scene;

    public GameState(GameStateManager stateManager) {
        this.stateManager = stateManager;
    }

    public abstract void enter();
    public abstract void exit();
    public abstract void update(double deltaTime);
    public abstract Scene createScene();
    public abstract void handleInput();
    public abstract void resume();
}
