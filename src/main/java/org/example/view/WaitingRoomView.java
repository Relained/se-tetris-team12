package org.example.view;

import javafx.geometry.Pos;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.service.FontManager;

import java.util.List;
import java.util.function.Consumer;

public class WaitingRoomView extends BaseView {

    private Button readyButton;

    public WaitingRoomView() {
        super(true);
    }

    public VBox createView(String ipAddress, 
                          Consumer<String> onGameModeChange,
                          Runnable onReadyToggle,
                          Runnable onGoBack) {
        VBox root = new VBox(50);
        root.setAlignment(Pos.CENTER);
        root.setBackground(new Background(
            new BackgroundFill(colorManager.getBackgroundColor(), null, null)
        ));

        // 연결된 IP 주소 표시
        Text connectedText = new Text("Connected to\n" + ipAddress);
        connectedText.setFill(colorManager.getPrimaryTextColor());
        connectedText.setFont(FontManager.getInstance().getFont(FontManager.SIZE_TITLE_LARGE));

        // 게임 모드 선택 라디오 버튼
        Text gameModeLabel = new Text("GameMode:");
        gameModeLabel.setFill(colorManager.getPrimaryTextColor());
        gameModeLabel.setFont(FontManager.getInstance().getFont(FontManager.SIZE_TITLE_MEDIUM));

        ToggleGroup gameModeGroup = new ToggleGroup();
        
        RadioButton normalRadio = createRadioButton("Normal", gameModeGroup);
        RadioButton itemRadio = createRadioButton("Item", gameModeGroup);
        RadioButton timeLimitedRadio = createRadioButton("Time-Limited", gameModeGroup);

        normalRadio.setSelected(true);

        // 라디오 버튼 변경 이벤트
        gameModeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            RadioButton selected = (RadioButton) newToggle;
            onGameModeChange.accept(selected.getText());
        });

        HBox radioBox = new HBox(20);
        radioBox.setAlignment(Pos.CENTER);
        radioBox.getChildren().addAll(normalRadio, itemRadio, timeLimitedRadio);

        // Ready 토글 버튼
        readyButton = new Button("Ready");
        readyButton.setFont(FontManager.getInstance().getFont(FontManager.SIZE_BODY_LARGE));
        readyButton.setPrefWidth(200);
        readyButton.setPrefHeight(50);
        updateToggleButtonStyle(false);

        readyButton.setOnMouseClicked(event -> event.consume());
        readyButton.setOnAction(event -> onReadyToggle.run());

        // Go Back 버튼
        var created = buttonSystem.createNavigableButtonFromList(
            List.of("Go Back"),
            List.of(onGoBack)
        );

        // 레이아웃 구성
        root.getChildren().addAll(
            connectedText,
            gameModeLabel,
            radioBox,
            readyButton
        );
        root.getChildren().addAll(created);

        return root;
    }

    private RadioButton createRadioButton(String text, ToggleGroup group) {
        RadioButton radio = new RadioButton(text);
        radio.setToggleGroup(group);
        radio.setFont(FontManager.getInstance().getFont(FontManager.SIZE_BODY_MEDIUM));
        radio.setTextFill(colorManager.getPrimaryTextColor());
        
        // 라디오 버튼 스타일
        radio.setStyle(
            "-fx-text-fill: " + toHexString(colorManager.getPrimaryTextColor()) + ";" +
            "-fx-font-size: " + FontManager.SIZE_BODY_MEDIUM + "px;"
        );
        
        return radio;
    }

    public void updateToggleButtonStyle(boolean isSelected) {
        if (readyButton == null) return;
        
        if (isSelected) {
            readyButton.setStyle(
                "-fx-background-color: #4CAF50;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 5;" +
                "-fx-border-radius: 5;"
            );
            readyButton.setText("Ready ✓");
        } else {
            readyButton.setStyle(
                "-fx-background-color: #757575;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 5;" +
                "-fx-border-radius: 5;"
            );
            readyButton.setText("Ready");
        }
    }

    private String toHexString(javafx.scene.paint.Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }
}
