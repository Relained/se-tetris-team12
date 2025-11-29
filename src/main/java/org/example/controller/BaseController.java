package org.example.controller;

import java.util.Stack;

import org.example.App;
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
        DisplayManager displayManager = DisplayManager.getInstance();
        int width = displayManager.getWidth(displayManager.getCurrentSize());
        int height = displayManager.getHeight(displayManager.getCurrentSize());

        scene = new Scene(root, width, height);
        scene.setFill(ColorManager.getInstance().getBackgroundColor());
        scene.setOnKeyPressed(event -> handleKeyInput(event));

        // CSS 스타일시트 로드
        String css = App.class.getResource("/styles/tetris.css").toExternalForm();
        scene.getStylesheets().add(css);

        // 스크린 사이즈 클래스 적용
        displayManager.applyScreenSizeClass(root);
    }

    public static void popState() {
        if (stateStack.isEmpty()) {
            return;
        }
        stateStack.pop().exit();
        if (stateStack.isEmpty()) {
            return;
        }
        BaseController currentState = stateStack.peek();
        currentState.resume();

        Scene currentScene = currentState.getScene();
        primaryStage.setScene(currentScene);

        // popState로 돌아올 때도 스크린 사이즈 클래스 재적용
        DisplayManager displayManager = DisplayManager.getInstance();
        displayManager.applyScreenSizeClass(currentScene.getRoot());
    }

    //이전 스테이트로 돌아갈 수 있어야 할 때 사용
    public static void stackState(BaseController newState) {
        if (!stateStack.empty())
            stateStack.peek().exit();

        Scene scene = newState.createScene();
        primaryStage.setScene(scene);

        // stackState로 새로운 화면 진입 시에도 스크린 사이즈 클래스 재적용
        DisplayManager displayManager = DisplayManager.getInstance();
        displayManager.applyScreenSizeClass(scene.getRoot());

        stateStack.push(newState);
    }

    //이전 스테이트로 돌아갈 필요가 없을 때 사용
    public static void setState(BaseController newState) {
        // 모든 stack을 비우므로 등록된 View들도 모두 제거
        DisplayManager.getInstance().clearAllViews();
        
        while (!stateStack.empty()) {
            stateStack.pop().exit();
        }
        stackState(newState);
    }
}
