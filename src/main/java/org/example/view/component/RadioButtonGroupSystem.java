package org.example.view.component;

import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 여러 개의 라디오 버튼 그룹을 관리하는 네비게이션 시스템
 * - 위/아래 키: 그룹 간 이동
 * - 좌/우 키: 그룹 내에서 옵션 선택
 * - Enter/Space: 버튼 실행
 */
public class RadioButtonGroupSystem {

    // CSS 클래스 상수
    private static final String CSS_RADIO_UNFOCUSED = "radio-unfocused";
    private static final String CSS_RADIO_SELECTED_UNFOCUSED = "radio-selected-unfocused";
    private static final String CSS_RADIO_FOCUSED = "radio-focused";
    private static final String CSS_RADIO_SELECTED_FOCUSED = "radio-selected-focused";
    
    // 여러 라디오 그룹 관리
    private ArrayList<RadioButtonGroupWrapper<?>> radioGroups;
    private int focusedGroupIndex = 0; // 현재 포커스된 그룹 인덱스
    
    // 순환 네비게이션 비활성화 (상위 시스템과 연동 시)
    private boolean wrapNavigation = true;
    
    public RadioButtonGroupSystem() {
        radioGroups = new ArrayList<>();
    }
    
    /**
     * 순환 네비게이션을 설정합니다.
     * @param wrap true면 첫 번째에서 위로 이동 시 마지막으로, false면 이동하지 않음
     */
    public void setWrapNavigation(boolean wrap) {
        this.wrapNavigation = wrap;
    }
    
    /**
     * 현재 첫 번째 그룹에 포커스되어 있는지 확인합니다.
     */
    public boolean isAtFirst() {
        return focusedGroupIndex == 0;
    }
    
    /**
     * 현재 마지막 그룹에 포커스되어 있는지 확인합니다.
     */
    public boolean isAtLast() {
        return focusedGroupIndex == radioGroups.size() - 1;
    }
    
    /**
     * JavaFX RadioButton + ToggleGroup을 래핑하는 클래스
     * ToggleGroup이 하나만 선택되도록 자동으로 관리합니다.
     */
    public static class RadioButtonGroupWrapper<T> {
        private final ToggleGroup toggleGroup;
        private final ArrayList<RadioButton> radioButtons;
        private final ArrayList<T> options;
        private Consumer<T> onSelectionChanged;
        private boolean isFocused = false;
        
        public RadioButtonGroupWrapper(List<T> options, int initialIndex) {
            this.toggleGroup = new ToggleGroup();
            this.radioButtons = new ArrayList<>();
            this.options = new ArrayList<>(options);
            
            // ToggleGroup의 선택 변경 리스너 설정
            toggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
                if (newToggle != null && onSelectionChanged != null) {
                    int index = radioButtons.indexOf(newToggle);
                    if (index >= 0 && index < this.options.size()) {
                        onSelectionChanged.accept(this.options.get(index));
                    }
                }
                updateButtonStyles();
            });
        }
        
        /**
         * 선택 변경 리스너를 설정합니다.
         */
        public void setOnSelectionChanged(Consumer<T> listener) {
            this.onSelectionChanged = listener;
        }
        
        /**
         * 현재 선택된 옵션을 반환합니다.
         */
        public T getSelectedOption() {
            Toggle selected = toggleGroup.getSelectedToggle();
            if (selected != null) {
                int index = radioButtons.indexOf(selected);
                if (index >= 0 && index < options.size()) {
                    return options.get(index);
                }
            }
            return options.isEmpty() ? null : options.get(0);
        }
        
        /**
         * 현재 선택된 인덱스를 반환합니다.
         */
        public int getSelectedIndex() {
            Toggle selected = toggleGroup.getSelectedToggle();
            if (selected != null) {
                return radioButtons.indexOf(selected);
            }
            return 0;
        }
        
        /**
         * 다음 옵션으로 토글합니다.
         */
        public void selectNext() {
            int currentIndex = getSelectedIndex();
            int nextIndex = (currentIndex + 1) % radioButtons.size();
            radioButtons.get(nextIndex).setSelected(true);
        }
        
        /**
         * 이전 옵션으로 토글합니다.
         */
        public void selectPrevious() {
            int currentIndex = getSelectedIndex();
            int prevIndex = (currentIndex - 1 + radioButtons.size()) % radioButtons.size();
            radioButtons.get(prevIndex).setSelected(true);
        }
        
        /**
         * 특정 인덱스로 선택을 변경합니다.
         */
        public void setSelectedIndex(int index) {
            if (index >= 0 && index < radioButtons.size()) {
                radioButtons.get(index).setSelected(true);
            }
        }
        
        /**
         * 포커스 상태를 설정합니다.
         */
        public void setFocused(boolean focused) {
            this.isFocused = focused;
            updateButtonStyles();
        }
        
        /**
         * 모든 라디오 버튼의 스타일을 업데이트합니다.
         */
        private void updateButtonStyles() {
            for (RadioButton btn : radioButtons) {
                if (btn.isSelected()) {
                    setSelectedStyle(btn);
                } else {
                    setUnselectedStyle(btn);
                }
            }
        }
        
        private void setSelectedStyle(RadioButton button) {
            clearRadioStyleClasses(button);
            if (isFocused) {
                button.getStyleClass().add(CSS_RADIO_SELECTED_FOCUSED);
            } else {
                button.getStyleClass().add(CSS_RADIO_SELECTED_UNFOCUSED);
            }
        }

        private void setUnselectedStyle(RadioButton button) {
            clearRadioStyleClasses(button);
            if (isFocused) {
                button.getStyleClass().add(CSS_RADIO_FOCUSED);
            } else {
                button.getStyleClass().add(CSS_RADIO_UNFOCUSED);
            }
        }

        private void clearRadioStyleClasses(RadioButton button) {
            button.getStyleClass().removeAll(
                CSS_RADIO_UNFOCUSED,
                CSS_RADIO_SELECTED_UNFOCUSED,
                CSS_RADIO_FOCUSED,
                CSS_RADIO_SELECTED_FOCUSED
            );
        }
        
        /**
         * 라디오 버튼 리스트를 반환합니다.
         */
        public ArrayList<RadioButton> getButtons() {
            return radioButtons;
        }
        
        /**
         * ToggleGroup을 반환합니다.
         */
        public ToggleGroup getToggleGroup() {
            return toggleGroup;
        }
    }
    
    /**
     * 라디오 버튼 그룹을 생성합니다.
     * @param <T> 옵션 타입
     * @param options 선택할 옵션 리스트
     * @param initialIndex 초기 선택 인덱스
     * @param labelExtractor 옵션에서 라벨을 추출하는 함수
     * @return 생성된 RadioButtonGroupWrapper
     */
    public <T> RadioButtonGroupWrapper<T> createRadioButtonGroup(
            List<T> options,
            int initialIndex,
            java.util.function.Function<T, String> labelExtractor) {
        
        RadioButtonGroupWrapper<T> group = new RadioButtonGroupWrapper<>(options, initialIndex);
        
        for (int i = 0; i < options.size(); i++) {
            T option = options.get(i);
            String label = labelExtractor.apply(option);
            RadioButton button = createRadioButton(label, group.toggleGroup);
            group.radioButtons.add(button);
        }
        
        // 초기 선택 설정
        if (initialIndex >= 0 && initialIndex < group.radioButtons.size()) {
            group.radioButtons.get(initialIndex).setSelected(true);
        } else if (!group.radioButtons.isEmpty()) {
            group.radioButtons.get(0).setSelected(true);
        }
        
        // 첫 번째 그룹이면 자동으로 포커스
        if (radioGroups.isEmpty()) {
            group.setFocused(true);
        }
        
        radioGroups.add(group);
        
        return group;
    }
    
    /**
     * 문자열 리스트로 라디오 버튼 그룹을 생성합니다.
     */
    public RadioButtonGroupWrapper<String> createRadioButtonGroup(List<String> options, int initialIndex) {
        return createRadioButtonGroup(options, initialIndex, s -> s);
    }
    
    /**
     * 개별 라디오 버튼을 생성합니다 (내부 사용).
     */
    private RadioButton createRadioButton(String text, ToggleGroup group) {
        RadioButton button = new RadioButton(text);
        button.setFocusTraversable(false);
        button.setToggleGroup(group);
        
        // 마우스 클릭 비활성화 - 키보드로만 조작
        button.setOnMouseClicked(event -> event.consume());
        
        return button;
    }
    
    /**
     * 키 입력을 처리합니다.
     */
    public void handleInput(KeyEvent event) {
        if (radioGroups.isEmpty()) return;
        
        switch (event.getCode()) {
            case UP:
                navigateUp();
                break;
            case DOWN:
                navigateDown();
                break;
            case LEFT:
                getCurrentFocusedGroup().selectPrevious();
                break;
            case RIGHT:
                getCurrentFocusedGroup().selectNext();
                break;
            default:
                break;
        }
    }
    
    /**
     * 위로 이동 (이전 그룹으로)
     * @return 이동 성공 여부 (경계에서 순환하지 않을 경우 false)
     */
    private boolean navigateUp() {
        if (focusedGroupIndex == 0) {
            if (wrapNavigation) {
                setFocusedGroup(radioGroups.size() - 1);
                return true;
            }
            return false; // 첫 번째에서 위로 이동 불가
        }
        setFocusedGroup(focusedGroupIndex - 1);
        return true;
    }
    
    /**
     * 아래로 이동 (다음 그룹으로)
     * @return 이동 성공 여부 (경계에서 순환하지 않을 경우 false)
     */
    private boolean navigateDown() {
        if (focusedGroupIndex == radioGroups.size() - 1) {
            if (wrapNavigation) {
                setFocusedGroup(0);
                return true;
            }
            return false; // 마지막에서 아래로 이동 불가
        }
        setFocusedGroup(focusedGroupIndex + 1);
        return true;
    }
    
    /**
     * 모든 라디오 그룹의 포커스를 해제합니다.
     */
    public void unfocusAll() {
        for (RadioButtonGroupWrapper<?> group : radioGroups) {
            group.setFocused(false);
        }
    }
    
    /**
     * 특정 그룹에 포커스를 설정합니다.
     */
    public void setFocusedGroup(int index) {
        if (index < 0 || index >= radioGroups.size()) return;
        
        // 다른 모든 그룹 포커스 해제
        for (int i = 0; i < radioGroups.size(); i++) {
            if (i != index) {
                radioGroups.get(i).setFocused(false);
            }
        }
        
        // 지정된 그룹 포커스
        focusedGroupIndex = index;
        radioGroups.get(focusedGroupIndex).setFocused(true);
    }
    
    /**
     * 모든 라디오 그룹 중 첫 번째에 포커스를 설정합니다.
     */
    public void focusFirst() {
        if (!radioGroups.isEmpty()) {
            setFocusedGroup(0);
        }
    }
    
    /**
     * 모든 라디오 그룹 중 마지막에 포커스를 설정합니다.
     */
    public void focusLast() {
        if (!radioGroups.isEmpty()) {
            setFocusedGroup(radioGroups.size() - 1);
        }
    }
    
    /**
     * 현재 포커스된 그룹을 반환합니다.
     */
    private RadioButtonGroupWrapper<?> getCurrentFocusedGroup() {
        if (focusedGroupIndex >= 0 && focusedGroupIndex < radioGroups.size()) {
            return radioGroups.get(focusedGroupIndex);
        }
        return null;
    }
    
    /**
     * 그룹의 개수를 반환합니다.
     */
    public int getGroupCount() {
        return radioGroups.size();
    }
    
    /**
     * 현재 포커스된 그룹의 인덱스를 반환합니다.
     */
    public int getFocusedGroupIndex() {
        return focusedGroupIndex;
    }
    
    /**
     * 모든 라디오 그룹을 반환합니다.
     */
    public ArrayList<RadioButtonGroupWrapper<?>> getRadioGroups() {
        return radioGroups;
    }
}
