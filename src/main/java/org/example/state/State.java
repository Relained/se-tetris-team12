package org.example.state;

import javafx.scene.Scene;
import org.example.service.StateManager;

public abstract class State {
    protected Scene scene;
    protected StateManager stateManager;
    
    public State(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public abstract void enter();
    public abstract void exit();
    public abstract Scene createScene();
    public abstract void resume();
    
    /**
     * 현재 씬을 반환합니다.
     * @return 현재 Scene 객체
     */
    public Scene getScene() {
        return scene;
    }
}
