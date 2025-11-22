package org.example.controller;

import java.util.Stack;

import org.example.service.ColorManager;
import org.example.service.DisplayManager;
import org.example.service.SettingManager;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public abstract class BaseController {

    private static Stage primaryStage;
    private static Stack<BaseController> stateStack;
    protected static SettingManager settingManager;

    protected Scene scene;

    public static void Initialize(Stage primaryStage, SettingManager settingManager) {
        BaseController.primaryStage = primaryStage;
        BaseController.settingManager = settingManager;
        BaseController.stateStack = new Stack<>();
    }

    protected void exit() {}
    protected void resume() {}
    protected abstract Scene createScene();
    protected abstract void handleKeyInput(KeyEvent event);

    protected Scene getScene() {
        return scene;
    }

    protected void createDefaultScene(Parent root) {
        scene = new Scene(root, 1000, 700);
        scene.setFill(ColorManager.getInstance().getBackgroundColor());
        scene.setOnKeyPressed(event -> handleKeyInput(event));
    }

    public static void popState() {
        if (stateStack.isEmpty()) {
            return;
        }
        stateStack.pop().exit();
        DisplayManager.getInstance().popView();
        if (stateStack.isEmpty()) {
            return;
        }
        stateStack.peek().resume();

        primaryStage.setScene(stateStack.peek().getScene());
    }

    //이전 스테이트로 돌아갈 수 있어야 할 때 사용
    public static void stackState(BaseController newState) {
        if (!stateStack.empty())
            stateStack.peek().exit();

        Scene scene = newState.createScene();
        primaryStage.setScene(scene);

        stateStack.push(newState);
    }

    //이전 스테이트로 돌아갈 필요가 없을 때 사용
    public static void setState(BaseController newState) {
        // setState가 호출되기 전에 이미 controller와 view가 생성되고 DisplayManager에 등록되는데,
        // 이를 여기서 전부 지워버려서 계속 버그가 발생했던 것임.
        // 가장 최근 뷰를 제외하고 전부 정리하도록 변경했음.
        DisplayManager.getInstance().clearAllViewsExceptLatest();
        
        while (!stateStack.empty()) {
            stateStack.pop().exit();
        }
        stackState(newState);
    }

    //현재 스테이트를 새로운 스테이트로 교체할 때 사용 (이전 스테이트는 남겨놓음)
    public static void swapState(BaseController newState) {
        if (!stateStack.empty()) {
            stateStack.pop().exit();
        }
        DisplayManager.getInstance().popView();
        stackState(newState);
    }
}
