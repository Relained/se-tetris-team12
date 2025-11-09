package org.example;

import javafx.application.Application;
import javafx.stage.Stage;

import org.example.controller.BaseController;
import org.example.controller.StartController;
import org.example.service.SettingManager;
import org.example.service.DisplayManager;

public class App extends Application {

    private SettingManager settingManager;
    private DisplayManager displayManager;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tetris");
        
        // 창 크기 조정 가능하도록 설정
        primaryStage.setResizable(true);
        
        // 최소 크기 설정 (게임이 정상적으로 표시될 수 있는 최소 크기)
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(800);
        
        // Initialize managers
        settingManager = new SettingManager();
        displayManager = settingManager.getDisplayManager();
        
        // DisplayManager에 Stage 참조 설정
        displayManager.setPrimaryStage(primaryStage);
        
        // BaseController 초기화 (static 의존성 주입)
        BaseController.Initialize(primaryStage, settingManager);

        // SettingManager를 통해 DisplayManager를 활용하여 초기 창 크기 설정
        settingManager.applyScreenSize(primaryStage);

        // Start with the start screen
        StartController startController = new StartController();
        startController.setState(startController);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
