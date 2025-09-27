package org.example.game.state;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class StartState extends GameState {
    public StartState(GameStateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void enter() {
        // Setup complete when scene is created
    }

    @Override
    public void exit() {
        // Cleanup if needed
    }

    @Override
    public void resume() {
        
    }

    @Override
    public void update(double deltaTime) {
        // Start state doesn't need updates
    }

    @Override
    public Scene createScene() {
        VBox root = new VBox(40);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        Text title = new Text("TETRIS");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", 48));

        Text subtitle = new Text("Team 12 Edition");
        subtitle.setFill(Color.LIGHTGRAY);
        subtitle.setFont(Font.font("Arial", 16));

        Button startButton = new Button("Start Game");
        startButton.setPrefSize(200, 50);
        startButton.setStyle("-fx-font-size: 18px; -fx-background-color: #4a4a4a; -fx-text-fill: white;");
        startButton.setOnAction(e -> stateManager.setState("play"));

        Button exitButton = new Button("Exit");
        exitButton.setPrefSize(200, 50);
        exitButton.setStyle("-fx-font-size: 18px; -fx-background-color: #4a4a4a; -fx-text-fill: white;");
        exitButton.setOnAction(e -> System.exit(0));

        Text controls = new Text("Controls:\n" +
                "← → Move\n" +
                "↓ Soft Drop\n" +
                "Space Hard Drop\n" +
                "Z/X Rotate\n" +
                "C Hold\n" +
                "P Pause");
        controls.setFill(Color.LIGHTGRAY);
        controls.setFont(Font.font("Arial", 14));

        root.getChildren().addAll(title, subtitle, startButton, exitButton, controls);

        scene = new Scene(root, 800, 600);

        // Handle keyboard input
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                stateManager.setState("play");
            } else if (event.getCode() == KeyCode.ESCAPE) {
                System.exit(0);
            }
        });

        return scene;
    }

    @Override
    public void handleInput() {
        // Input handled in scene key events
    }
}