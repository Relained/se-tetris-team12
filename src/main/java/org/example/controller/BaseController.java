package org.example.controller;

import java.util.Stack;

import org.example.service.SettingManager;

import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class BaseController {

    protected static Stage primaryStage;
    public static SettingManager settingManager;
    private static Stack<BaseController> stateStack;

    protected Scene scene;

    public static void Initialize(Stage primaryStage, SettingManager settingManager) {
        BaseController.primaryStage = primaryStage;
        BaseController.settingManager = settingManager;
        BaseController.stateStack = new Stack<>();
    }

    protected void exit() {
        // Cleanup if needed
    }

    protected void resume() {

    }

    protected Scene getScene() {
        return scene;
    }

    protected abstract Scene createScene();

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
    public void stackState(BaseController newState) {
        if (!stateStack.empty())
            stateStack.peek().exit();

        Scene scene = newState.createScene();
        primaryStage.setScene(scene);

        stateStack.push(newState);
    }

    //이전 스테이트로 돌아갈 필요가 없을 때 사용
    public void setState(BaseController newState) {
        while (!stateStack.empty()) {
            stateStack.pop().exit();
        }
        stackState(newState);
    }
}
