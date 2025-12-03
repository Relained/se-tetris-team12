package org.example.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import org.example.model.GameMode;

import java.util.function.Consumer;

public class WaitingRoomView extends BaseView {

    private static final int READY_BUTTON_INDEX = 0;

    private final boolean isServer;
    private Button readyButton;
    private ToggleGroup gameModeGroup;
    private ToggleGroup difficultyGroup;
    private RadioButton[] gameModeRadios;
    private RadioButton[] difficultyRadios;
    private Button[] navigableButtons;
    private Text gameModeClientText;
    private Text difficultyClientText;
    private int currentFocusIndex = READY_BUTTON_INDEX;
    private VBox chatMessagesBox;
    private ScrollPane chatScrollPane;
    private TextField chatInputField;

    public WaitingRoomView(boolean isServer) {
        super(false);
        this.isServer = isServer;
    }

    public VBox createView(String ipAddress,
                        Consumer<String> onGameModeChange,
                        Consumer<Integer> onDifficultyChange,
                        Runnable onReadyToggle,
                        Consumer<String> onChatSubmit,
                        Runnable onLeave) {
        VBox root = new VBox(50);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root-dark");

        root.getChildren().addAll(
            createConnectionText(ipAddress),
            isServer ? createGameModeSelection(onGameModeChange) : createGameModeDisplay(),
            isServer ? createDifficultySelection(onDifficultyChange) : new VBox(),
            createReadyButton(onReadyToggle),
            // createChatSection(onChatSubmit),
            createLeaveButton(onLeave)
        );

        initNavigableButtons();
        return root;
    }

    private void initNavigableButtons() {
        if (isServer) {
            navigableButtons = new Button[]{readyButton};
        } else {
            navigableButtons = new Button[]{readyButton};
        }
        updateFocusVisual();
    }

    private Text createConnectionText(String ipAddress) {
        Text text = new Text("Connected to:\n" + ipAddress);
        text.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        text.getStyleClass().addAll("text-primary", "text-title-medium");
        return text;
    }

    private VBox createGameModeSelection(Consumer<String> onGameModeChange) {
        VBox container = new VBox(15);
        container.setAlignment(Pos.CENTER);

        gameModeGroup = new ToggleGroup();
        gameModeRadios = new RadioButton[GameMode.values().length];
        HBox radioBox = new HBox(20);
        radioBox.setAlignment(Pos.CENTER);

        GameMode[] modes = GameMode.values();
        for (int i = 0; i < modes.length; i++) {
            gameModeRadios[i] = new RadioButton(modes[i].toString());
            gameModeRadios[i].setToggleGroup(gameModeGroup);
            gameModeRadios[i].setFocusTraversable(false);
            gameModeRadios[i].setOnMouseClicked(event -> event.consume());

            int index = i;
            gameModeRadios[i].selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    onGameModeChange.accept(modes[index].toString());
                    updateGameModeRadioStyles();
                }
            });

            radioBox.getChildren().add(gameModeRadios[i]);
        }

        gameModeRadios[0].setSelected(true);
        updateGameModeRadioStyles();

        container.getChildren().add(radioBox);
        return container;
    }

    private VBox createGameModeDisplay() {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);

        // Create HBox to display mode and difficulty with centered separator
        HBox displayBox = new HBox();
        displayBox.setAlignment(Pos.CENTER);
        displayBox.setPrefWidth(400);
        displayBox.setMinWidth(400);

        // Left side: GameMode (with right alignment in its region)
        HBox leftBox = new HBox();
        leftBox.setAlignment(Pos.CENTER_RIGHT);
        leftBox.setPrefWidth(150);
        leftBox.setMinWidth(150);

        gameModeClientText = new Text(GameMode.NORMAL.toString());
        gameModeClientText.getStyleClass().addAll("text-title-medium", "text-red");
        leftBox.getChildren().add(gameModeClientText);

        // Center: Separator (always in center)
        HBox centerBox = new HBox();
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPrefWidth(100);
        centerBox.setMinWidth(100);

        Text separator = new Text("|");
        separator.getStyleClass().addAll("text-primary", "text-title-medium");
        centerBox.getChildren().add(separator);

        // Right side: Difficulty (with left alignment in its region)
        HBox rightBox = new HBox();
        rightBox.setAlignment(Pos.CENTER_LEFT);
        rightBox.setPrefWidth(150);
        rightBox.setMinWidth(150);

        difficultyClientText = new Text("Easy");
        difficultyClientText.getStyleClass().addAll("text-title-medium", "text-easy");
        rightBox.getChildren().add(difficultyClientText);

        displayBox.getChildren().addAll(leftBox, centerBox, rightBox);
        container.getChildren().add(displayBox);

        return container;
    }

    private VBox createDifficultySelection(Consumer<Integer> onDifficultyChange) {
        VBox container = new VBox(15);
        container.setAlignment(Pos.CENTER);

        difficultyGroup = new ToggleGroup();
        difficultyRadios = new RadioButton[3];
        String[] difficultyNames = {"Easy", "Normal", "Hard"};
        HBox radioBox = new HBox(20);
        radioBox.setAlignment(Pos.CENTER);

        for (int i = 0; i < 3; i++) {
            difficultyRadios[i] = new RadioButton(difficultyNames[i]);
            difficultyRadios[i].setToggleGroup(difficultyGroup);
            difficultyRadios[i].setFocusTraversable(false);
            difficultyRadios[i].setOnMouseClicked(event -> event.consume());

            int index = i + 1;
            difficultyRadios[i].selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    updateDifficultyRadioStyles();
                    if (onDifficultyChange != null) {
                        onDifficultyChange.accept(index); // 1=Easy, 2=Normal, 3=Hard
                    }
                }
            });

            radioBox.getChildren().add(difficultyRadios[i]);
        }

        difficultyRadios[0].setSelected(true); // Default: Easy
        updateDifficultyRadioStyles();

        container.getChildren().add(radioBox);
        return container;
    }

    private Button createReadyButton(Runnable onReadyToggle) {
        readyButton = new Button("Ready");
        readyButton.getStyleClass().addAll("success-button--inactive", "text-body-large");
        readyButton.setPrefWidth(200);
        readyButton.setPrefHeight(50);
        readyButton.setFocusTraversable(false);
        readyButton.setOnMouseClicked(event -> event.consume());
        readyButton.setOnAction(event -> onReadyToggle.run());
        updateToggleButtonStyle(false);
        return readyButton;
    }

    private VBox createChatSection(Consumer<String> onChatSubmit) {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.setMaxWidth(500);

        chatMessagesBox = new VBox(5);
        chatMessagesBox.setPadding(new javafx.geometry.Insets(10));
        chatMessagesBox.getStyleClass().add("chat-messages");

        chatScrollPane = new ScrollPane(chatMessagesBox);
        chatScrollPane.setPrefHeight(200);
        chatScrollPane.setMaxHeight(200);
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.getStyleClass().add("chat-scroll-pane");
        chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        chatInputField = new TextField();
        chatInputField.setPromptText("Type a message...");
        chatInputField.setPrefWidth(500);
        chatInputField.setPrefHeight(40);
        chatInputField.getStyleClass().add("chat-input");
        chatInputField.setTextFormatter(new TextFormatter<String>(change -> 
            change.getControlNewText().length() <= 70 ? change : null));
        chatInputField.setOnAction(event -> {
            String message = chatInputField.getText().trim();
            if (!message.isEmpty()) {
                onChatSubmit.accept(message);
                chatInputField.clear();
            }
        });

        container.getChildren().addAll(chatScrollPane, chatInputField);
        return container;
    }

    private Button createLeaveButton(Runnable onLeave) {
        Button leaveButton = new Button("Leave Room");
        leaveButton.getStyleClass().addAll("danger-button", "text-body-large");
        leaveButton.setPrefWidth(200);
        leaveButton.setPrefHeight(50);
        leaveButton.setFocusTraversable(false);
        leaveButton.setOnAction(event -> onLeave.run());
        return leaveButton;
    }

    private void updateGameModeRadioStyles() {
        if (gameModeRadios == null) return;

        String[] colors = {"#FF6B6B", "#4ECDC4", "#FFE66D"}; // Red, Teal, Yellow for Normal, Item, Time Attack

        for (int i = 0; i < gameModeRadios.length; i++) {
            RadioButton radio = gameModeRadios[i];
            String textColor = radio.isSelected() ? colors[i] : "#888888";
            String fontWeight = radio.isSelected() ? "bold" : "normal";

            radio.setStyle(String.format(
                "-fx-text-fill: %s; -fx-font-weight: %s; -fx-font-size: 16px;",
                textColor, fontWeight
            ));
        }
    }

    private void updateDifficultyRadioStyles() {
        if (difficultyRadios == null) return;

        String[] colors = {"#4CAF50", "#2196F3", "#F44336"}; // Green, Blue, Red for Easy, Normal, Hard

        for (int i = 0; i < difficultyRadios.length; i++) {
            RadioButton radio = difficultyRadios[i];
            String textColor = radio.isSelected() ? colors[i] : "#888888";
            String fontWeight = radio.isSelected() ? "bold" : "normal";

            radio.setStyle(String.format(
                "-fx-text-fill: %s; -fx-font-weight: %s; -fx-font-size: 16px;",
                textColor, fontWeight
            ));
        }
    }

    public void updateToggleButtonStyle(boolean isSelected) {
        if (readyButton == null) return;
        
        readyButton.getStyleClass().removeAll("success-button", "success-button--inactive");
        if (isSelected) {
            readyButton.getStyleClass().add("success-button");
        } else {
            readyButton.getStyleClass().add("success-button--inactive");
        }
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

    private void updateFocusVisual() {
        if (isServer) {
            updateGameModeRadioStyles();
            updateDifficultyRadioStyles();
        }
        updateToggleButtonStyle(readyButton.getText().endsWith("✓"));
    }

    public void addChatMessage(String message) {
        if (chatMessagesBox == null) return;

        Text messageText = new Text(message);
        messageText.getStyleClass().addAll("text-white", "text-body-medium");
        messageText.setWrappingWidth(460);

        chatMessagesBox.getChildren().add(messageText);

        // Auto-scroll to bottom
        chatScrollPane.layout();
        chatScrollPane.setVvalue(1.0);
    }

    public void setGameModeText(String mode, String difficulty) {
        if (gameModeClientText != null) {
            gameModeClientText.setText(mode);
            updateGameModeClientColor(mode);
        }
        if (difficultyClientText != null) {
            difficultyClientText.setText(difficulty);
            updateDifficultyClientColor(difficulty);
        }
    }

    private void updateGameModeClientColor(String mode) {
        if (gameModeClientText == null) return;
        gameModeClientText.getStyleClass().removeAll("text-red", "text-orange", "text-yellow");
        String styleClass = switch (mode.toUpperCase()) {
            case "NORMAL" -> "text-red";
            case "ITEM" -> "text-orange";
            case "TIME ATTACK" -> "text-yellow";
            default -> "text-primary";
        };
        gameModeClientText.getStyleClass().add(styleClass);
    }

    private void updateDifficultyClientColor(String difficulty) {
        if (difficultyClientText == null) return;
        difficultyClientText.getStyleClass().removeAll("text-easy", "text-normal", "text-hard");
        String styleClass = switch (difficulty.toUpperCase()) {
            case "EASY" -> "text-easy";
            case "NORMAL" -> "text-normal";
            case "HARD" -> "text-hard";
            default -> "text-primary";
        };
        difficultyClientText.getStyleClass().add(styleClass);
    }

}
