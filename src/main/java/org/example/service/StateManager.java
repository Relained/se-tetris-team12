package org.example.service;

import javafx.stage.Stage;
import javafx.scene.Scene;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class StateManager {
    private final Stage primaryStage;
    private final Map<String, GameState> states;
    private GameState currentState;
    private Stack<GameState> stateStack;
    public SettingManager settingManager;

    public StateManager(Stage primaryStage, SettingManager settingManager) {
        this.primaryStage = primaryStage;
        this.states = new HashMap<>();
        this.stateStack = new Stack<>();
        this.settingManager = settingManager;
    }

    public void addState(String name, GameState state) {
        states.put(name, state);
    }

    public void popState() {
        if (stateStack.isEmpty()) {
            return;
        }

        if (currentState != null) {
            currentState.exit();
        }
        currentState = stateStack.pop();
        currentState.resume();
        primaryStage.setScene(currentState.scene);
    }

    public void stackState(String stateName) {
        GameState newState = states.get(stateName);
        if (newState == null) {
            throw new IllegalArgumentException("State not found: " + stateName);
        }
        if (currentState != null) {
            currentState.exit();
            stateStack.push(currentState);
        }

        newState.enter();
        currentState = newState;
        Scene scene = newState.createScene();
        primaryStage.setScene(scene);
    }

    public void setState(String stateName) {
        GameState newState = states.get(stateName);
        if (newState == null) {
            throw new IllegalArgumentException("State not found: " + stateName);
        }
        if (currentState != null) {
            currentState.exit();
        }
        stateStack.clear();

        newState.enter(); 
        //반드시 enter를 먼저 호출하고 currentState를 변경 해야함.
        //그렇지 않으면 이전 state가 뭔지 잃어버려서 enter할 때 문제가 생김
        currentState = newState;
        Scene scene = newState.createScene();
        primaryStage.setScene(scene);
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}