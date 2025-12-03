package org.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import org.example.service.KeySettingManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Key Setting 화면의 UI를 담당하는 View 클래스
 * 모든 네비게이션 가능한 항목을 하나의 시스템으로 관리
 */
public class KeySettingView extends BaseView {
    
    private Text title;
    private Text statusText;
    private KeySettingManager keySettingManager;
    private Map<String, Text> keyBindingTexts;
    
    // 통합 네비게이션 시스템
    private List<NavigableItem> navigableItems;
    private int selectedIndex = 0;
    
    /**
     * 네비게이션 가능한 항목을 나타내는 클래스
     */
    private static class NavigableItem {
        String type; // "action" 또는 "button"
        String name; // 액션 이름 또는 버튼 이름
        HBox visualElement; // 시각적 요소
        Runnable action; // 버튼의 경우 실행할 액션
        
        NavigableItem(String type, String name, HBox element, Runnable action) {
            this.type = type;
            this.name = name;
            this.visualElement = element;
            this.action = action;
        }
    }
    
    public KeySettingView() {
        super(false); // NavigableButtonSystem 사용 안 함 - 커스텀 네비게이션 사용
        this.keySettingManager = KeySettingManager.getInstance();
        this.keyBindingTexts = new HashMap<>();
        this.navigableItems = new ArrayList<>();
    }
    
    /**
     * Key Setting 화면의 UI를 구성하고 반환합니다.
     */
    public VBox createView(Runnable onResetToDefault, Runnable onGoBack) {
        // 초기화
        navigableItems.clear();
        selectedIndex = 0;
        
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.getStyleClass().add("root-dark");

        // 제목
        title = new Text("Key Settings");
        title.getStyleClass().addAll("text-title-medium", "text-primary");

        // 상태 텍스트
        statusText = new Text("Use ↑↓ to navigate, ENTER to change key, ESC to go back");
        statusText.getStyleClass().addAll("text-body-medium", "text-secondary");

        // 키 바인딩 컨테이너
        VBox keyBindingsContainer = new VBox(15);
        keyBindingsContainer.setAlignment(Pos.CENTER);
        
        // 모든 액션에 대한 키 바인딩 표시 및 네비게이션 항목 등록
        String[] actions = keySettingManager.getAllActions();
        for (String action : actions) {
            HBox keyBindingRow = createKeyBindingRow(action);
            keyBindingsContainer.getChildren().add(keyBindingRow);
            // 네비게이션 항목으로 등록
            navigableItems.add(new NavigableItem("action", action, keyBindingRow, null));
        }

        // 하단 버튼들을 HBox로 생성하여 네비게이션 항목에 추가
        HBox resetButtonBox = createButtonRow("Reset to Default", onResetToDefault);
        HBox goBackButtonBox = createButtonRow("Go Back", onGoBack);
        
        navigableItems.add(new NavigableItem("button", "Reset to Default", resetButtonBox, onResetToDefault));
        navigableItems.add(new NavigableItem("button", "Go Back", goBackButtonBox, onGoBack));

        // 첫 번째 항목을 선택 상태로 설정
        if (!navigableItems.isEmpty()) {
            setSelectedStyle(navigableItems.get(0).visualElement);
        }

        root.getChildren().addAll(
            title,
            statusText,
            keyBindingsContainer,
            resetButtonBox,
            goBackButtonBox
        );

        return root;
    }
    
    /**
     * 개별 키 바인딩 행을 생성합니다.
     */
    private HBox createKeyBindingRow(String action) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(10));
        
        // 액션 이름 표시
        Text actionLabel = new Text(keySettingManager.getActionDisplayName(action) + ":");
        actionLabel.getStyleClass().addAll("text-body-large", "text-primary");
        actionLabel.setWrappingWidth(200);

        // 현재 키 바인딩 표시
        KeyCode currentKey = keySettingManager.getKeyBinding(action);
        Text keyText = new Text(currentKey != null ? currentKey.getName() : "Not Set");
        keyText.getStyleClass().addAll("text-body-large", "text-secondary");
        keyText.setWrappingWidth(150);
        keyBindingTexts.put(action, keyText);
        
        row.getChildren().addAll(actionLabel, keyText);
        setDefaultStyle(row);
        
        return row;
    }
    
    /**
     * 버튼 행을 생성합니다.
     */
    private HBox createButtonRow(String label, Runnable action) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(10, 20, 10, 20));
        row.setMinWidth(200);
        row.setMinHeight(50);
        
        Text text = new Text(label);
        text.getStyleClass().addAll("text-body-large", "text-primary");
        
        row.getChildren().add(text);
        setDefaultStyle(row);
        
        return row;
    }
    
    /**
     * 선택된 스타일을 설정합니다.
     */
    private void setSelectedStyle(HBox row) {
        row.getStyleClass().remove("key-setting-row");
        if (!row.getStyleClass().contains("key-setting-row--selected")) {
            row.getStyleClass().add("key-setting-row--selected");
        }
    }

    /**
     * 기본 스타일을 설정합니다.
     */
    private void setDefaultStyle(HBox row) {
        row.getStyleClass().remove("key-setting-row--selected");
        if (!row.getStyleClass().contains("key-setting-row")) {
            row.getStyleClass().add("key-setting-row");
        }
    }

    /**
     * 상태 텍스트의 색상 스타일을 변경합니다.
     */
    private void setStatusTextStyle(String colorClass) {
        statusText.getStyleClass().removeAll("text-secondary", "text-orange", "text-red");
        statusText.getStyleClass().add(colorClass);
    }

    /**
     * 키보드 네비게이션을 처리합니다 (일반 모드).
     */
    public void navigateActions(boolean isUp) {
        if (navigableItems.isEmpty()) {
            return;
        }
        
        // 현재 선택된 항목의 스타일을 기본으로 변경
        if (selectedIndex >= 0 && selectedIndex < navigableItems.size()) {
            setDefaultStyle(navigableItems.get(selectedIndex).visualElement);
        }
        
        // 인덱스 업데이트
        if (isUp) {
            selectedIndex = (selectedIndex - 1 + navigableItems.size()) % navigableItems.size();
        } else {
            selectedIndex = (selectedIndex + 1) % navigableItems.size();
        }
        
        // 새로 선택된 항목에 선택 스타일 적용
        if (selectedIndex >= 0 && selectedIndex < navigableItems.size()) {
            setSelectedStyle(navigableItems.get(selectedIndex).visualElement);
        }
    }
    
    /**
     * 현재 선택된 액션을 반환합니다.
     * @return 액션 이름 (버튼 선택 시 null)
     */
    public String getSelectedAction() {
        if (selectedIndex >= 0 && selectedIndex < navigableItems.size()) {
            NavigableItem item = navigableItems.get(selectedIndex);
            if ("action".equals(item.type)) {
                return item.name;
            }
        }
        return null;
    }
    
    /**
     * 현재 하단 버튼이 선택되었는지 확인합니다.
     * @return 버튼 선택 여부
     */
    public boolean isButtonSelected() {
        if (selectedIndex >= 0 && selectedIndex < navigableItems.size()) {
            NavigableItem item = navigableItems.get(selectedIndex);
            return "button".equals(item.type);
        }
        return false;
    }
    
    /**
     * 현재 선택된 버튼을 실행합니다.
     */
    public void executeSelectedButton() {
        if (selectedIndex >= 0 && selectedIndex < navigableItems.size()) {
            NavigableItem item = navigableItems.get(selectedIndex);
            if ("button".equals(item.type) && item.action != null) {
                item.action.run();
            }
        }
    }
    
    /**
     * 키 입력 대기 상태를 표시합니다.
     */
    public void showWaitingForKey(String action) {
        statusText.setText("Press a key for " + keySettingManager.getActionDisplayName(action) +
                          " (ESC to cancel)");
        setStatusTextStyle("text-orange");
    }

    /**
     * 키 입력 대기 상태 표시를 숨기고 기본 메시지로 복구합니다.
     */
    public void hideWaitingForKey() {
        statusText.setText("Use ↑↓ to navigate, ENTER to change key, ESC to go back");
        setStatusTextStyle("text-secondary");
    }

    /**
     * 중복 키 에러를 표시합니다.
     */
    public void showDuplicateKeyError(KeyCode keyCode) {
        statusText.setText("Key " + keyCode.getName() + " is already assigned! Try another key.");
        setStatusTextStyle("text-red");
        
        // 3초 후 메시지 숨기기
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> hideWaitingForKey());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    /**
     * 특정 액션의 키 바인딩 표시를 업데이트합니다.
     */
    public void updateKeyBinding(String action, KeyCode keyCode) {
        Text keyText = keyBindingTexts.get(action);
        if (keyText != null) {
            keyText.setText(keyCode.getName());
        }
    }
    
    /**
     * 모든 키 바인딩 표시를 업데이트합니다.
     */
    public void updateAllKeyBindings() {
        String[] actions = keySettingManager.getAllActions();
        for (String action : actions) {
            KeyCode keyCode = keySettingManager.getKeyBinding(action);
            if (keyCode != null) {
                updateKeyBinding(action, keyCode);
            }
        }
    }
}
