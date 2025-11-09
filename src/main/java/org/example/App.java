package org.example;

import javafx.application.Application;
import javafx.stage.Stage;

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
        
        // Initialize managers
        settingManager = new SettingManager();
        displayManager = settingManager.getDisplayManager();
        
        // DisplayManager에 Stage 참조 설정
        displayManager.setPrimaryStage(primaryStage);
        
        stateManager = new StateManager(primaryStage, settingManager);

        // SettingManager를 통해 DisplayManager를 활용하여 초기 창 크기 설정
        settingManager.applyScreenSize(primaryStage);

        // Start with the start screen
        stateManager.setState(new StartState(stateManager));

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
