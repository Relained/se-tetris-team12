package org.example;

import javafx.application.Application;
import javafx.stage.Stage;

import org.example.service.StateManager;
import org.example.service.SettingManager;
import org.example.state.*;

public class App extends Application {

    private StateManager stateManager;
    private SettingManager settingManager;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tetris");
        
        // 창 크기 조정 가능하도록 설정
        primaryStage.setResizable(true);
        
        // 최소 크기 설정 (게임이 정상적으로 표시될 수 있는 최소 크기)
        primaryStage.setMinWidth(650);
        primaryStage.setMinHeight(800);
        
        // 기본 크기 설정
        primaryStage.setWidth(650);
        primaryStage.setHeight(800);

        // Initialize state manager
        settingManager = new SettingManager();
        stateManager = new StateManager(primaryStage, settingManager);

        // Add all game states
        stateManager.addState("start", new StartState(stateManager));
        stateManager.addState("play", new PlayState(stateManager));
        stateManager.addState("pause", new PauseState(stateManager));
        stateManager.addState("setting", new SettingState(stateManager));
        stateManager.addState("color_setting", new ColorSettingState(stateManager));
        stateManager.addState("key_setting", new KeySettingState(stateManager));
        stateManager.addState("gameover", new GameOverState(stateManager));
        stateManager.addState("difficulty", new DifficultyState(stateManager));

        // Start with the start screen
        stateManager.setState("start");

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
