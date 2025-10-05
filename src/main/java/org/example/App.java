package org.example;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.game.state.*;
import org.example.game.logic.SettingManager;

public class App extends Application {

    private GameStateManager stateManager;
    private SettingManager settingManager;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tetris - Team 12");
        
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
        stateManager = new GameStateManager(primaryStage, settingManager);

        // Add all game states
        stateManager.addState("start", new StartState(stateManager));
        stateManager.addState("play", new PlayState(stateManager));
        stateManager.addState("pause", new PauseState(stateManager));
        stateManager.addState("setting", new SettingState(stateManager));
        stateManager.addState("color_setting", new ColorSettingState(stateManager));
        stateManager.addState("gameOver", new GameOverState(stateManager));

        // Start with the start screen
        stateManager.setState("start");

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
