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

public class SettingState extends GameState {
    public SettingState(GameStateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void enter() {
        // 설정창 진입 시 필요한 초기화 작업 수행
    }

    @Override
    public void exit() {
        // 설정창 종료 시 필요한 정리 작업 수행
    }

    @Override
    public void resume() {
        // 일시정지 창 -> 설정창으로 돌아올 때 사용
    }

    @Override
    public Scene createScene() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        Text title = new Text("Settings");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", 36));

        NavigableButtonSystem buttonSystem = new NavigableButtonSystem();

        buttonSystem.createNavigableButton("Screen Size", () -> System.err.println("Set Screen Size"));
        buttonSystem.createNavigableButton("Controls", () -> System.err.println("Set Controls"));
        buttonSystem.createNavigableButton("Reset Score Board", () -> System.err.println("Reset Score Board"));
        buttonSystem.createNavigableButton("Color Blind Setting", () -> System.err.println("Set Color Blind Setting"));
        buttonSystem.createNavigableButton("Reset All Setting", () -> System.err.println("Reset All Setting"));
        buttonSystem.createNavigableButton("Go Back", () -> stateManager.popState());

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
