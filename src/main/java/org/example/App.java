package org.example;

import javafx.application.Application;
import javafx.stage.Stage;

import org.example.controller.BaseController;
import org.example.controller.StartController;
import org.example.service.SettingManager;
import org.example.view.BaseView;
import org.example.service.ColorManager;
import org.example.service.DisplayManager;

public class App extends Application {

    private SettingManager settingManager;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tetris");
        
        primaryStage.setResizable(false);
        
        // Initialize managers
        settingManager = new SettingManager();
        
        // DisplayManager에 Stage 참조 설정
        DisplayManager.getInstance().setPrimaryStage(primaryStage);
        
        // Base 초기화 (static 의존성 주입)
        BaseController.Initialize(primaryStage, settingManager);
        BaseView.Initialize(ColorManager.getInstance());

        // SettingManager를 통해 DisplayManager를 활용하여 초기 창 크기 설정
        settingManager.applyScreenSize();

        // Start with the start screen
        BaseController.setState(new StartController());

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
