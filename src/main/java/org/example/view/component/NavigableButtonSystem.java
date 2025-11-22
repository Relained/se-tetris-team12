package org.example.view.component;

import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;

public class NavigableButtonSystem {
    private ArrayList<Button> buttons;
    private int selectedButtonIndex = 0;

    // 기준 크기 (MEDIUM)
    private final static int BASE_BUTTON_WIDTH = 200;
    private final static int BASE_BUTTON_HEIGHT = 50;
    private final static int BASE_FONT_SIZE = 18;
    
    // 현재 스케일 (기본값: 1.0 = MEDIUM)
    private double scale = 1.0;

    public NavigableButtonSystem() {
        buttons = new ArrayList<>();
    }
    
    /**
     * 화면 크기에 따른 스케일을 설정합니다.
     * @param scale 스케일 값 (SMALL: 0.9, MEDIUM: 1.0, LARGE: 1.1)
     */
    public void setScale(double scale) {
        this.scale = scale;
        updateAllButtonSizes();
    }
    
    /**
     * 모든 버튼의 크기와 폰트를 현재 스케일에 맞게 업데이트합니다.
     */
    private void updateAllButtonSizes() {
        double buttonWidth = BASE_BUTTON_WIDTH * scale;
        double buttonHeight = BASE_BUTTON_HEIGHT * scale;
        
        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            button.setPrefSize(buttonWidth, buttonHeight);
            
            // 스타일 업데이트 (선택 여부에 따라)
            if (i == selectedButtonIndex) {
                setSelectedStyle(button);
            } else {
                setDefaultStyle(button);
            }
        }
    }

    public ArrayList<Button> createNavigableButtonFromList(List<String> texts, List<Runnable> actions) {
        if (texts.size() != actions.size()) {
            throw new IllegalArgumentException("Texts and actions lists must have the same size.");
        }
        
        ArrayList<Button> createdButtons = new ArrayList<>();
        for (int i = 0; i < texts.size(); i++) {
            Button button = createNavigableButton(texts.get(i), actions.get(i));
            createdButtons.add(button);
        }
        
        return createdButtons;
    }

    public Button createNavigableButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefSize(BASE_BUTTON_WIDTH * scale, BASE_BUTTON_HEIGHT * scale);
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
        int fontSize = (int) (BASE_FONT_SIZE * scale);
        button.setStyle(String.format("-fx-font-size: %dpx; " +
                "-fx-background-color: #6a6a6a; " +
                "-fx-text-fill: yellow; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2px; " +
                "-fx-effect: innershadow(three-pass-box, rgba(0,0,0,0.7), 10, 0, 0, 0);", fontSize));
    }

    private void setDefaultStyle(Button button) {
        int fontSize = (int) (BASE_FONT_SIZE * scale);
        button.setStyle(String.format("-fx-font-size: %dpx; " +
                "-fx-background-color: #4a4a4a; " +
                "-fx-text-fill: white;", fontSize));
    }

    private void executeSelectedButton() {
        Button selectedButton = buttons.get(selectedButtonIndex);
        Runnable action = (Runnable) selectedButton.getUserData();
        
        action.run();
    }

    /**
     * NavigableButtonSystem에 영향을 받지 않는 독립적인 버튼을 생성합니다.
     * 선택된 스타일이 적용되며, 마우스 클릭이 비활성화됩니다.
     * 
     * @param text 버튼에 표시할 텍스트
     * @return 스타일이 적용된 독립적인 버튼
     */
    public static Button createStandaloneButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(200, 50);
        button.setFocusTraversable(false);

        // 마우스 클릭 비활성화
        button.setOnMouseClicked(event -> event.consume());
        button.setOnAction(event -> event.consume());

        // 선택된 스타일 적용
        button.setStyle("-fx-font-size: 18px; " +
                "-fx-background-color: #6a6a6a; " +
                "-fx-text-fill: yellow; " +
                "-fx-border-color: white; " +
                "-fx-border-width: 2px; " +
                "-fx-effect: innershadow(three-pass-box, rgba(0,0,0,0.7), 10, 0, 0, 0);");

        return button;
    }
}