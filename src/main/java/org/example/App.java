package org.example;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.game.state.*;

public class App extends Application {

    private GameStateManager stateManager;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tetris - Team 12");
        primaryStage.setResizable(false);

        // Initialize state manager
        stateManager = new GameStateManager(primaryStage);

        // Add all game states
        stateManager.addState("start", new StartState(stateManager));
        stateManager.addState("play", new PlayState(stateManager));
        stateManager.addState("pause", new PauseState(stateManager));
        stateManager.addState("setting", new SettingState(stateManager));
        stateManager.addState("gameOver", new GameOverState(stateManager));

        // Start with the start screen
        stateManager.setState("start");

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
