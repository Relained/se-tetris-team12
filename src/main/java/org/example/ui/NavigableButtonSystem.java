package org.example.ui;

import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import java.util.ArrayList;

public class NavigableButtonSystem {
    private ArrayList<Button> buttons;
    private int selectedButtonIndex = 0;

    public NavigableButtonSystem() {
        buttons = new ArrayList<>();
    }

    public void resetSystem() {
        buttons.clear();
        selectedButtonIndex = 0;
    }

    public ArrayList<Button> getButtons() {
        return buttons;
    }

    public Button createNavigableButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefSize(200, 50);
        button.setFocusTraversable(false); // 탭 네비게이션 비활성화

        // 마우스 클릭 비활성화 - 클릭 이벤트를 consume하여 무효화
        button.setOnMouseClicked(event -> event.consume());
        button.setOnAction(event -> event.consume()); // 액션 이벤트도 무효화
        button.setUserData(action); // 액션을 버튼의 UserData에 저장

        buttons.add(button);
        if (buttons.size() == 1) {
            setSelectedStyle(button);
        } else {
            setDefaultStyle(button);
        }

        return button;
    }

    public void handleInput(KeyEvent event) {
        int newIdx;
        switch (event.getCode()) {
            case UP:
                newIdx = (selectedButtonIndex - 1 + buttons.size()) % buttons.size();
                updateSelectedButton(newIdx);
                break;
            case DOWN:
                newIdx = (selectedButtonIndex + 1) % buttons.size();
                updateSelectedButton(newIdx);
                break;
            case ENTER:
            case SPACE:
                executeSelectedButton();
                break;
            default:
                // 다른 키들은 무시
                break;
        }
    }

    private void updateSelectedButton(int newSelectedIndex) {
        if (newSelectedIndex < 0 || newSelectedIndex >= buttons.size()) {
            return; // 인덱스 범위 벗어남
        }
        setDefaultStyle(buttons.get(selectedButtonIndex));
        setSelectedStyle(buttons.get(newSelectedIndex));
        selectedButtonIndex = newSelectedIndex;
    }

    private void setSelectedStyle(Button button) {
        button.setStyle("-fx-font-size: 18px; " +
                "-fx-background-color: #6a6a6a; " +
                "-fx-text-fill: yellow; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2px; " +
                "-fx-effect: innershadow(three-pass-box, rgba(0,0,0,0.7), 10, 0, 0, 0);");
    }

    private void setDefaultStyle(Button button) {
        button.setStyle("-fx-font-size: 18px; " +
                "-fx-background-color: #4a4a4a; " +
                "-fx-text-fill: white;");
    }

    private void executeSelectedButton() {
        Button selectedButton = buttons.get(selectedButtonIndex);
        Runnable action = (Runnable) selectedButton.getUserData();
        action.run();
    }
}