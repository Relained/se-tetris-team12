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
    
    // 현재 스케일 (기본값: 1.0 = MEDIUM)
    private double scale = 1.0;
    
    // 2D 그리드 레이아웃 지원
    private int gridColumns = 1; // 기본값: 1열 (수직 리스트)
    private boolean horizontalNavigation = false; // 좌우 네비게이션 활성화 여부
    
    // 순환 네비게이션 활성화 여부 (기본: true)
    private boolean wrapNavigation = true;
    
    // 버튼 스타일 접두사 (기본: "nav-button")
    private String stylePrefix = "nav-button";

    public NavigableButtonSystem() {
        buttons = new ArrayList<>();
    }
    
    /**
     * 버튼 스타일 접두사를 설정합니다.
     * @param prefix 스타일 접두사 (예: "nav-button", "nav-button-red")
     */
    public void setStylePrefix(String prefix) {
        this.stylePrefix = prefix;
    }
    
    /**
     * 그리드 열 수를 설정합니다. (2D 레이아웃 지원)
     * @param columns 열 개수 (1 = 수직 리스트, 2+ = 그리드)
     */
    public void setGridColumns(int columns) {
        this.gridColumns = Math.max(1, columns);
        this.horizontalNavigation = (columns > 1);
    }
    
    /**
     * 수평 네비게이션 활성화 여부를 설정합니다.
     * @param enabled true면 좌우 키로 이동 가능
     */
    public void setHorizontalNavigation(boolean enabled) {
        this.horizontalNavigation = enabled;
    }
    
    /**
     * 순환 네비게이션 활성화 여부를 설정합니다.
     * @param wrap true면 경계에서 순환, false면 경계에서 멈춤
     */
    public void setWrapNavigation(boolean wrap) {
        this.wrapNavigation = wrap;
    }
    
    /**
     * 첫 번째 버튼이 선택되어 있는지 확인합니다.
     */
    public boolean isAtFirstButton() {
        return selectedButtonIndex == 0;
    }
    
    /**
     * 마지막 버튼이 선택되어 있는지 확인합니다.
     */
    public boolean isAtLastButton() {
        return selectedButtonIndex == buttons.size() - 1;
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
                newIdx = navigateUp();
                if (newIdx != selectedButtonIndex) {
                    updateSelectedButton(newIdx);
                }
                break;
            case DOWN:
                newIdx = navigateDown();
                if (newIdx != selectedButtonIndex) {
                    updateSelectedButton(newIdx);
                }
                break;
            case LEFT:
                if (horizontalNavigation) {
                    newIdx = navigateLeft();
                    if (newIdx != selectedButtonIndex) {
                        updateSelectedButton(newIdx);
                    }
                }
                break;
            case RIGHT:
                if (horizontalNavigation) {
                    newIdx = navigateRight();
                    if (newIdx != selectedButtonIndex) {
                        updateSelectedButton(newIdx);
                    }
                }
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
    
    /**
     * 위로 이동할 때의 새 인덱스를 계산합니다.
     * @return 새 인덱스 (이동 불가 시 현재 인덱스 반환)
     */
    private int navigateUp() {
        if (gridColumns == 1) {
            // 수직 리스트: 단순히 이전 버튼
            if (selectedButtonIndex == 0) {
                if (wrapNavigation) {
                    return buttons.size() - 1;
                }
                return selectedButtonIndex; // 첫 번째에서 위로 이동 불가
            }
            return selectedButtonIndex - 1;
        } else {
            // 그리드: 현재 행에서 위 행으로
            int currentRow = selectedButtonIndex / gridColumns;
            int currentCol = selectedButtonIndex % gridColumns;
            
            if (currentRow == 0) {
                if (!wrapNavigation) {
                    return selectedButtonIndex; // 첫 번째 행에서 위로 이동 불가
                }
            }
            
            int newRow = (currentRow - 1 + getGridRows()) % getGridRows();
            int newIdx = newRow * gridColumns + currentCol;
            
            // 유효한 버튼 인덱스인지 확인
            if (newIdx >= buttons.size()) {
                // 마지막 행이 불완전한 경우, 같은 열의 마지막 유효한 버튼으로
                newIdx = buttons.size() - 1;
            }
            return newIdx;
        }
    }
    
    /**
     * 아래로 이동할 때의 새 인덱스를 계산합니다.
     * @return 새 인덱스 (이동 불가 시 현재 인덱스 반환)
     */
    private int navigateDown() {
        if (gridColumns == 1) {
            // 수직 리스트: 단순히 다음 버튼
            if (selectedButtonIndex == buttons.size() - 1) {
                if (wrapNavigation) {
                    return 0;
                }
                return selectedButtonIndex; // 마지막에서 아래로 이동 불가
            }
            return selectedButtonIndex + 1;
        } else {
            // 그리드: 현재 행에서 아래 행으로
            int currentRow = selectedButtonIndex / gridColumns;
            int currentCol = selectedButtonIndex % gridColumns;
            
            if (currentRow == getGridRows() - 1) {
                if (!wrapNavigation) {
                    return selectedButtonIndex; // 마지막 행에서 아래로 이동 불가
                }
            }
            
            int newRow = (currentRow + 1) % getGridRows();
            int newIdx = newRow * gridColumns + currentCol;
            
            // 유효한 버튼 인덱스인지 확인
            if (newIdx >= buttons.size()) {
                // 순환하여 첫 번째 행의 같은 열로
                newIdx = currentCol;
                if (newIdx >= buttons.size()) {
                    newIdx = 0;
                }
            }
            return newIdx;
        }
    }
    
    /**
     * 왼쪽으로 이동할 때의 새 인덱스를 계산합니다.
     */
    private int navigateLeft() {
        if (gridColumns == 1) {
            // 수직 리스트에서는 이전 버튼으로
            return (selectedButtonIndex - 1 + buttons.size()) % buttons.size();
        } else {
            // 그리드: 같은 행에서 왼쪽으로
            int currentRow = selectedButtonIndex / gridColumns;
            int currentCol = selectedButtonIndex % gridColumns;
            int newCol = (currentCol - 1 + gridColumns) % gridColumns;
            int newIdx = currentRow * gridColumns + newCol;
            
            // 유효한 버튼 인덱스인지 확인
            if (newIdx >= buttons.size()) {
                // 현재 행의 마지막 유효한 버튼으로
                newIdx = Math.min(currentRow * gridColumns + gridColumns - 1, buttons.size() - 1);
            }
            return newIdx;
        }
    }
    
    /**
     * 오른쪽으로 이동할 때의 새 인덱스를 계산합니다.
     */
    private int navigateRight() {
        if (gridColumns == 1) {
            // 수직 리스트에서는 다음 버튼으로
            return (selectedButtonIndex + 1) % buttons.size();
        } else {
            // 그리드: 같은 행에서 오른쪽으로
            int currentRow = selectedButtonIndex / gridColumns;
            int currentCol = selectedButtonIndex % gridColumns;
            int newCol = (currentCol + 1) % gridColumns;
            int newIdx = currentRow * gridColumns + newCol;
            
            // 유효한 버튼 인덱스인지 확인
            if (newIdx >= buttons.size()) {
                // 현재 행의 첫 번째 버튼으로 순환
                newIdx = currentRow * gridColumns;
            }
            return newIdx;
        }
    }
    
    /**
     * 그리드의 행 수를 계산합니다.
     */
    private int getGridRows() {
        return (buttons.size() + gridColumns - 1) / gridColumns;
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
        button.getStyleClass().remove(stylePrefix);
        String selectedClass = stylePrefix + "--selected";
        if (!button.getStyleClass().contains(selectedClass)) {
            button.getStyleClass().add(selectedClass);
        }
    }

    private void setDefaultStyle(Button button) {
        String selectedClass = stylePrefix + "--selected";
        button.getStyleClass().remove(selectedClass);
        if (!button.getStyleClass().contains(stylePrefix)) {
            button.getStyleClass().add(stylePrefix);
        }
    }

    private void executeSelectedButton() {
        Button selectedButton = buttons.get(selectedButtonIndex);
        Runnable action = (Runnable) selectedButton.getUserData();
        
        action.run();
    }
    
    /**
     * 모든 버튼의 포커스를 해제합니다. (기본 스타일 적용)
     */
    public void unfocusAll() {
        for (Button button : buttons) {
            setDefaultStyle(button);
        }
    }
    
    /**
     * 특정 인덱스의 버튼으로 포커스를 이동합니다.
     * @param index 이동할 버튼의 인덱스
     * @return 성공 여부
     */
    public boolean focusButton(int index) {
        if (index < 0 || index >= buttons.size()) {
            return false;
        }
        updateSelectedButton(index);
        return true;
    }
    
    /**
     * 특정 텍스트를 가진 버튼으로 포커스를 이동합니다.
     * @param buttonText 버튼의 텍스트
     * @return 성공 여부
     */
    public boolean focusButtonByText(String buttonText) {
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).getText().equals(buttonText)) {
                updateSelectedButton(i);
                return true;
            }
        }
        return false;
    }
    
    /**
     * 현재 선택된 버튼의 인덱스를 반환합니다.
     */
    public int getSelectedIndex() {
        return selectedButtonIndex;
    }
    
    /**
     * 현재 선택된 버튼을 반환합니다.
     */
    public Button getSelectedButton() {
        if (selectedButtonIndex >= 0 && selectedButtonIndex < buttons.size()) {
            return buttons.get(selectedButtonIndex);
        }
        return null;
    }
    
    /**
     * 버튼 리스트를 반환합니다.
     */
    public ArrayList<Button> getButtons() {
        return buttons;
    }
    
    /**
     * 버튼 개수를 반환합니다.
     */
    public int getButtonCount() {
        return buttons.size();
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

        // 선택된 스타일 적용 (CSS 클래스)
        button.getStyleClass().add("nav-button--selected");

        return button;
    }
}