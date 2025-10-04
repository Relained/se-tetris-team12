package org.example.game.state;

import org.example.ui.NavigableButtonSystem;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class PauseState extends GameState {
    public PauseState(GameStateManager stateManager) {
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
        // 설정창 -> 일시정지 창으로 돌아올 때 사용
    }

    @Override
    public Scene createScene() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        Text title = new Text("PAUSED");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", 36));

        NavigableButtonSystem buttonSystem = new NavigableButtonSystem();

        buttonSystem.createNavigableButton("Resume", () -> stateManager.popState());
        buttonSystem.createNavigableButton("Restart", () -> stateManager.setState("play"));
        buttonSystem.createNavigableButton("Settings", () -> stateManager.stackState("setting"));
        buttonSystem.createNavigableButton("Main Menu", () -> stateManager.setState("start"));

        root.getChildren().add(title);
        root.getChildren().addAll(buttonSystem.getButtons());

        scene = new Scene(root, 1000, 700);

        // Handle keyboard input
        scene.setOnKeyPressed(event -> buttonSystem.handleInput(event));

        // Focus for keyboard input
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();

        return scene;
    }
}