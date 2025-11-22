package org.example.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.service.FontManager;

import java.util.function.Consumer;

public class WaitingRoomView extends BaseView {

    private static final String[] MODE_NAMES = {"Normal", "Item", "Time-Limited"};
    private static final int MODE_COUNT = 3;
    private static final int READY_BUTTON_INDEX = 0;
    
    private final boolean isServer;
    private Button readyButton;
    private Button[] gameModeButtons;
    private Button[] navigableButtons;
    private Text gameModeText;
    private int currentFocusIndex = READY_BUTTON_INDEX;
    private int selectedModeIndex = 0;

    public WaitingRoomView(boolean isServer) {
        super(false);
        this.isServer = isServer;
    }

    public VBox createView(String ipAddress, Consumer<String> onGameModeChange, Runnable onReadyToggle) {
        VBox root = new VBox(50);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(new BackgroundFill(colorManager.getBackgroundColor(), null, null)));

        root.getChildren().addAll(
            createConnectionText(ipAddress),
            isServer ? createGameModeSelection(onGameModeChange) : createGameModeDisplay(),
            createReadyButton(onReadyToggle),
            createHintText()
        );

        initNavigableButtons();
        return root;
    }

    private void initNavigableButtons() {
        if (isServer) {
            navigableButtons = new Button[]{readyButton, gameModeButtons[0], gameModeButtons[1], gameModeButtons[2]};
        } else {
            navigableButtons = new Button[]{readyButton};
        }
        updateFocusVisual();
    }

    private Text createConnectionText(String ipAddress) {
        Text text = new Text("Connected to\n" + ipAddress);
        text.setFill(colorManager.getPrimaryTextColor());
        text.setFont(FontManager.getInstance().getFont(FontManager.SIZE_TITLE_LARGE));
        return text;
    }

    private VBox createGameModeSelection(Consumer<String> onGameModeChange) {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);

        Text label = new Text("GameMode:");
        label.setFill(colorManager.getPrimaryTextColor());
        label.setFont(FontManager.getInstance().getFont(FontManager.SIZE_TITLE_MEDIUM));

        gameModeButtons = new Button[MODE_COUNT];
        for (int i = 0; i < MODE_COUNT; i++) {
            gameModeButtons[i] = createGameModeButton(MODE_NAMES[i]);
            int index = i;
            gameModeButtons[i].setOnMouseClicked(event -> event.consume());
            gameModeButtons[i].setOnAction(event -> {
                onGameModeChange.accept(MODE_NAMES[index]);
                selectGameMode(index);
            });
        }

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(gameModeButtons);

        container.getChildren().addAll(label, buttonBox);
        return container;
    }

    private VBox createGameModeDisplay() {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);

        Text label = new Text("GameMode:");
        label.setFill(colorManager.getPrimaryTextColor());
        label.setFont(FontManager.getInstance().getFont(FontManager.SIZE_TITLE_MEDIUM));

        gameModeText = new Text(MODE_NAMES[0]);
        gameModeText.setFill(colorManager.getPrimaryTextColor());
        gameModeText.setFont(FontManager.getInstance().getFont(FontManager.SIZE_TITLE_MEDIUM));

        container.getChildren().addAll(label, gameModeText);
        return container;
    }

    private Button createReadyButton(Runnable onReadyToggle) {
        readyButton = new Button("Ready");
        readyButton.setFont(FontManager.getInstance().getFont(FontManager.SIZE_BODY_LARGE));
        readyButton.setPrefWidth(200);
        readyButton.setPrefHeight(50);
        readyButton.setFocusTraversable(false);
        readyButton.setOnMouseClicked(event -> event.consume());
        readyButton.setOnAction(event -> onReadyToggle.run());
        updateToggleButtonStyle(false);
        return readyButton;
    }

    private Text createHintText() {
        Text hint = new Text("Press ESC to Go Back");
        hint.setFill(colorManager.getSecondaryTextColor());
        hint.setFont(FontManager.getInstance().getFont(FontManager.SIZE_BODY_SMALL));
        return hint;
    }

    private Button createGameModeButton(String text) {
        Button button = new Button(text);
        button.setFont(FontManager.getInstance().getFont(FontManager.SIZE_BODY_MEDIUM));
        button.setPrefWidth(150);
        button.setPrefHeight(40);
        button.setFocusTraversable(false);
        return button;
    }

    private void selectGameMode(int index) {
        selectedModeIndex = index;
        for (int i = 0; i < gameModeButtons.length; i++) {
            updateButtonStyle(gameModeButtons[i], i == index, i + 1 == currentFocusIndex);
        }
    }

    private void updateButtonStyle(Button button, boolean isSelected, boolean isFocused) {
        String bgColor = isSelected ? "#2196F3" : "#424242";
        String textColor = isSelected ? "white" : "#BDBDBD";
        String borderColor = isFocused ? "yellow" : (isSelected ? "#1976D2" : "#616161");
        String borderWidth = isFocused ? "3" : (isSelected ? "2" : "1");
        String fontWeight = isSelected ? "bold" : "normal";
        
        button.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-text-fill: " + textColor + ";" +
            "-fx-font-weight: " + fontWeight + ";" +
            "-fx-font-size: " + FontManager.SIZE_BODY_MEDIUM + "px;" +
            "-fx-background-radius: 5;" +
            "-fx-border-color: " + borderColor + ";" +
            "-fx-border-width: " + borderWidth + ";" +
            "-fx-border-radius: 5;" +
            "-fx-background-insets: 0;" +
            "-fx-focus-color: transparent;" +
            "-fx-faint-focus-color: transparent;"
        );
    }

    public void updateToggleButtonStyle(boolean isSelected) {
        if (readyButton == null) return;
        
        String bgColor = isSelected ? "#4CAF50" : "#757575";
        String borderColor = (currentFocusIndex == READY_BUTTON_INDEX) ? "yellow" : "transparent";
        String borderWidth = (currentFocusIndex == READY_BUTTON_INDEX) ? "3" : "0";
        
        readyButton.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: " + FontManager.SIZE_BODY_LARGE + "px;" +
            "-fx-background-radius: 5;" +
            "-fx-border-color: " + borderColor + ";" +
            "-fx-border-width: " + borderWidth + ";" +
            "-fx-border-radius: 5;" +
            "-fx-background-insets: 0;" +
            "-fx-focus-color: transparent;" +
            "-fx-faint-focus-color: transparent;"
        );
        readyButton.setText(isSelected ? "Ready ✓" : "Ready");
    }

    public void navigateUp() {
        currentFocusIndex = (currentFocusIndex + 1) % navigableButtons.length;
        updateFocusVisual();
    }

    public void navigateDown() {
        currentFocusIndex = (currentFocusIndex - 1 + navigableButtons.length) % navigableButtons.length;
        updateFocusVisual();
    }

    public void activateCurrentButton() {
        if (navigableButtons != null && currentFocusIndex < navigableButtons.length) {
            navigableButtons[currentFocusIndex].fire();
        }
    }

    public void setGameModeText(String mode) {
        if (gameModeText != null) {
            gameModeText.setText(mode);
        }
    }

    private void updateFocusVisual() {
        if (isServer) {
            for (int i = 0; i < gameModeButtons.length; i++) {
                updateButtonStyle(gameModeButtons[i], i == selectedModeIndex, i + 1 == currentFocusIndex);
            }
        }
        updateToggleButtonStyle(readyButton.getText().endsWith("✓"));
    }
}
