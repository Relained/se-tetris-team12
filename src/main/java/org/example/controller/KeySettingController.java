package org.example.controller;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.example.service.KeySettingManager;
import org.example.service.StateManager;
import org.example.view.KeySettingView;

/**
 * KeySettingState의 키 설정 처리를 담당하는 Controller
 */
public class KeySettingController {
    
    private StateManager stateManager;
    private KeySettingView keySettingView;
    private KeySettingManager keySettingManager;
    private String waitingForKeyAction; // 현재 키 입력을 기다리고 있는 액션
    private boolean isWaitingForKey; // 키 입력 대기 상태인지 여부
    
    public KeySettingController(StateManager stateManager, KeySettingView keySettingView) {
        this.stateManager = stateManager;
        this.keySettingView = keySettingView;
        this.keySettingManager = KeySettingManager.getInstance();
        this.waitingForKeyAction = null;
        this.isWaitingForKey = false;
    }
    
    /**
     * Reset to Default 버튼 클릭 시 처리 - 모든 키 설정을 기본값으로 초기화
     */
    public void handleResetToDefault() {
        keySettingManager.resetToDefault();
        keySettingView.updateAllKeyBindings();
    }
    
    /**
     * Go Back 버튼 클릭 시 처리 - 키 설정을 저장하고 이전 화면으로 복귀
     */
    public void handleGoBack() {
        // 이전 상태로 복귀
        stateManager.popState();
    }
    
    /**
     * 키보드 입력 처리
     * 키 입력 대기 상태일 때는 새 키를 바인딩하고,
     * 그렇지 않으면 네비게이션 처리
     */
    public void handleKeyInput(KeyEvent event) {
        if (isWaitingForKey) {
            handleNewKeyBinding(event);
        } else {
            handleNavigation(event);
        }
    }
    
    /**
     * 네비게이션 키 입력 처리 (일반 모드)
     */
    private void handleNavigation(KeyEvent event) {
        switch (event.getCode()) {
            case UP:
                keySettingView.navigateActions(true);
                event.consume();
                break;
            case DOWN:
                keySettingView.navigateActions(false);
                event.consume();
                break;
            case ENTER:
            case SPACE:
                handleEnterKey();
                event.consume();
                break;
            case ESCAPE:
                handleGoBack();
                event.consume();
                break;
            default:
                break;
        }
    }
    
    /**
     * ENTER 키 처리 - 액션 선택 또는 버튼 실행
     */
    private void handleEnterKey() {
        if (keySettingView.isButtonSelected()) {
            // 하단 버튼 실행
            keySettingView.executeSelectedButton();
        } else {
            // 키 설정 모드 진입
            String selectedAction = keySettingView.getSelectedAction();
            if (selectedAction != null) {
                startKeySettingMode(selectedAction);
            }
        }
    }
    
    /**
     * 키 설정 모드를 시작합니다.
     * @param action 설정할 액션 이름
     */
    private void startKeySettingMode(String action) {
        isWaitingForKey = true;
        waitingForKeyAction = action;
        keySettingView.showWaitingForKey(action);
    }
    
    /**
     * 새로운 키 바인딩을 처리합니다.
     * @param event 키 이벤트
     */
    private void handleNewKeyBinding(KeyEvent event) {
        KeyCode newKey = event.getCode();
        
        // ESC 키는 취소로 처리
        if (newKey == KeyCode.ESCAPE) {
            cancelKeyBinding();
            event.consume();
            return;
        }
        
        // 특수 키들은 무시 (Shift, Control, Alt 등)
        if (isModifierKey(newKey)) {
            return;
        }
        
        // 키 바인딩 시도
        boolean success = keySettingManager.setKeyBinding(waitingForKeyAction, newKey);
        
        if (success) {
            // 성공: 뷰 업데이트
            keySettingView.updateKeyBinding(waitingForKeyAction, newKey);
            keySettingView.hideWaitingForKey();
            isWaitingForKey = false;
            waitingForKeyAction = null;
        } else {
            // 실패: 중복 키 에러 표시
            keySettingView.showDuplicateKeyError(newKey);
        }
        
        event.consume();
    }
    
    /**
     * 키 바인딩을 취소합니다.
     */
    private void cancelKeyBinding() {
        isWaitingForKey = false;
        waitingForKeyAction = null;
        keySettingView.hideWaitingForKey();
    }
    
    /**
     * 해당 키가 수정자 키(Modifier Key)인지 확인합니다.
     * @param keyCode 확인할 키코드
     * @return 수정자 키 여부
     */
    private boolean isModifierKey(KeyCode keyCode) {
        return keyCode == KeyCode.SHIFT ||
               keyCode == KeyCode.CONTROL ||
               keyCode == KeyCode.ALT ||
               keyCode == KeyCode.META ||
               keyCode == KeyCode.COMMAND ||
               keyCode == KeyCode.WINDOWS ||
               keyCode == KeyCode.CAPS ||
               keyCode == KeyCode.NUM_LOCK ||
               keyCode == KeyCode.SCROLL_LOCK;
    }
    
    /**
     * 현재 키 입력 대기 상태인지 반환합니다.
     * @return 키 입력 대기 상태 여부
     */
    public boolean isWaitingForKey() {
        return isWaitingForKey;
    }
    
    /**
     * 현재 키 입력을 기다리고 있는 액션을 반환합니다.
     * @return 액션 이름 (대기 중이 아니면 null)
     */
    public String getWaitingForKeyAction() {
        return waitingForKeyAction;
    }
}
