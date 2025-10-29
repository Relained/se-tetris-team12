package org.example;

import javafx.application.Application;
import javafx.stage.Stage;

import org.example.model.GameMode;
import org.example.service.StateManager;
import org.example.service.SettingManager;
import org.example.service.DisplayManager;
import org.example.state.*;

public class App extends Application {

    private StateManager stateManager;
    private SettingManager settingManager;
    private DisplayManager displayManager;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tetris");
        
        // 창 크기 조정 가능하도록 설정
        primaryStage.setResizable(true);
        
        // 최소 크기 설정 (게임이 정상적으로 표시될 수 있는 최소 크기)
        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(800);
        
        // Initialize managers
        settingManager = new SettingManager();
        displayManager = settingManager.getDisplayManager();
        
        // DisplayManager에 Stage 참조 설정
        displayManager.setPrimaryStage(primaryStage);
        
        stateManager = new StateManager(primaryStage, settingManager);

        // SettingManager를 통해 DisplayManager를 활용하여 초기 창 크기 설정
        settingManager.applyScreenSize(primaryStage);

        // Add all game states
        stateManager.addState("start", new StartState(stateManager));
        stateManager.addState("play", new PlayState(stateManager, GameMode.NORMAL));
        stateManager.addState("playItem", new PlayState(stateManager, GameMode.ITEM));
        stateManager.addState("pause", new PauseState(stateManager));
        stateManager.addState("setting", new SettingState(stateManager));
        stateManager.addState("color_setting", new ColorSettingState(stateManager));
        stateManager.addState("key_setting", new KeySettingState(stateManager));
        stateManager.addState("display_setting", new DisplaySettingState(stateManager));
        stateManager.addState("gameover", new GameOverState(stateManager));
        stateManager.addState("difficulty", new DifficultyState(stateManager));
        stateManager.addState("scoreboard", new ScoreboardState(stateManager));

        // Start with the start screen
        stateManager.setState("start");

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
