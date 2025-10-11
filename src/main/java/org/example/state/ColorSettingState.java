package org.example.state;

import org.example.model.SettingData.ColorBlindMode;
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

public class ColorSettingState extends GameState {
    private ColorBlindMode selectedMode;
    private Text title;
    
    public ColorSettingState(StateManager stateManager) {
        super(stateManager);
    }

    @Override
    public void enter() {
        selectedMode = stateManager.settingManager.getCurrentSettings().colorBlindMode;
    }

    @Override
    public void exit() {
        stateManager.settingManager.setColorSetting(selectedMode);
    }

    @Override
    public void resume() {
        // 일시정지 창 -> 설정창으로 돌아올 때 사용
    }

    private void setColor(ColorBlindMode mode) {
        selectedMode = mode;
        title.setText("Color Settings\nCurrent: " + selectedMode.name());
    }

    @Override
    public Scene createScene() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        title = new Text("Color Settings\nCurrent: " + selectedMode.name());
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", 36));

        NavigableButtonSystem buttonSystem = new NavigableButtonSystem();

        buttonSystem.createNavigableButton("Default", () -> setColor(ColorBlindMode.Default));
        buttonSystem.createNavigableButton("PROTANOPIA", () -> setColor(ColorBlindMode.PROTANOPIA));
        buttonSystem.createNavigableButton("DEUTERANOPIA", () -> setColor(ColorBlindMode.DEUTERANOPIA));
        buttonSystem.createNavigableButton("TRITANOPIA", () -> setColor(ColorBlindMode.TRITANOPIA));
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
