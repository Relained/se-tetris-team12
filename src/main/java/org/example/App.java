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
        
        // Initialize managers
        settingManager = new SettingManager();
        stateManager = new StateManager(primaryStage, settingManager);

        // 저장된 설정에 따라 초기 창 크기 설정
        applyScreenSize(primaryStage, settingManager.getCurrentSettings().screenSize);

        // Add all game states
        stateManager.addState("start", new StartState(stateManager));
        stateManager.addState("play", new PlayState(stateManager));
        stateManager.addState("pause", new PauseState(stateManager));
        stateManager.addState("setting", new SettingState(stateManager));
        stateManager.addState("color_setting", new ColorSettingState(stateManager));
        stateManager.addState("display_setting", new DisplaySettingState(stateManager));
        stateManager.addState("gameover", new GameOverState(stateManager));

        // Start with the start screen
        stateManager.setState("start");

        primaryStage.show();
    }
    
    /**
     * 화면 크기 설정을 적용합니다.
     * @param stage 적용할 Stage
     * @param screenSize 설정할 화면 크기
     */
    private void applyScreenSize(Stage stage, org.example.model.SettingData.ScreenSize screenSize) {
        switch (screenSize) {
            case SMALL:
                stage.setWidth(800);
                stage.setHeight(600);
                break;
            case MEDIUM:
                stage.setWidth(1000);
                stage.setHeight(700);
                break;
            case LARGE:
                stage.setWidth(1200);
                stage.setHeight(800);
                break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
