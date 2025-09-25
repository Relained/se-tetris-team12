package org.example.game.state;

import javafx.stage.Stage;
import javafx.scene.Scene;

import java.util.HashMap;
import java.util.Map;

public class GameStateManager {
    private final Stage primaryStage;
    private final Map<String, GameState> states;
    private GameState currentState;
    private String currentStateName;

    public GameStateManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.states = new HashMap<>();
    }

    public void addState(String name, GameState state) {
        states.put(name, state);
    }

    public void setState(String stateName) {
        GameState newState = states.get(stateName);
        if (newState == null) {
            throw new IllegalArgumentException("State not found: " + stateName);
        }

        if (currentState != null) {
            currentState.exit();
        }

        currentState = newState;
        currentStateName = stateName;

        newState.enter();
        Scene scene = newState.createScene();
        primaryStage.setScene(scene);
    }

    public void update(double deltaTime) {
        if (currentState != null) {
            currentState.update(deltaTime);
        }
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public String getCurrentStateName() {
        return currentStateName;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}