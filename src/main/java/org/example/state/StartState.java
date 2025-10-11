package org.example.state;

import org.example.service.StateManager;
import org.example.view.component.NavigableButtonSystem;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class StartState extends GameState {
    public StartState(StateManager stateManager) {
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

        NavigableButtonSystem buttonSystem = new NavigableButtonSystem();
        var startButton = buttonSystem.createNavigableButton("Start Game", () -> stateManager.setState("play"));
        var exitButton = buttonSystem.createNavigableButton("Exit", () -> System.exit(0));

        Text controls = new Text("Controls:\n" +
                "← → Move\n" +
                "↓ Soft Drop\n" +
                "Space Hard Drop\n" +
                "Z/X Rotate\n" +
                "C Hold\n" +
                "ESC Pause");
        controls.setFill(Color.LIGHTGRAY);
        controls.setFont(Font.font("Arial", 14));

        root.getChildren().addAll(title, subtitle, startButton, exitButton, controls);

        scene = new Scene(root, 1000, 700);

        scene.setOnKeyPressed(event -> buttonSystem.handleInput(event));

        return scene;
    }
}