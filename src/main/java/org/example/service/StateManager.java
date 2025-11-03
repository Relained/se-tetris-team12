package org.example.service;

import javafx.stage.Stage;
import javafx.scene.Scene;

import java.util.Stack;

import org.example.state.BaseState;

public class StateManager {
    private final Stage primaryStage;
    public SettingManager settingManager;
    private Stack<BaseState> stateStack;

    public StateManager(Stage primaryStage, SettingManager settingManager) {
        this.primaryStage = primaryStage;
        this.settingManager = settingManager;
        this.stateStack = new Stack<>();
    }

    public void popState() {
        if (stateStack.isEmpty()) {
            return;
        }
        stateStack.pop().exit();
        if (stateStack.isEmpty()) {
            return;
        }
        stateStack.peek().resume();
        primaryStage.setScene(stateStack.peek().getScene());
    }

    //이전 스테이트로 돌아갈 수 있어야 할 때 사용
    public void stackState(BaseState newState) {
        if (!stateStack.empty())
            stateStack.peek().exit();

        Scene scene = newState.createScene();
        primaryStage.setScene(scene);

        stateStack.push(newState);
    }

    //이전 스테이트로 돌아갈 필요가 없을 때 사용
    public void setState(BaseState newState) {
        while (!stateStack.empty()) {
            stateStack.pop().exit();
        }
        stackState(newState);
    }

    public BaseState getCurrentState() {
        if (stateStack.isEmpty()) {
            return null;
        }
        return stateStack.peek();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}