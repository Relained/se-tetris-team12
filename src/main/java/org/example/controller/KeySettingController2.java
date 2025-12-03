package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.example.model.KeyData;
import org.example.view.KeySettingView2;

import java.util.HashMap;
import java.util.Map;

/**
 * Multiplayer Key Setting Controller
 * Player 1/2의 멀티플레이 전용 키 설정을 동시에 관리합니다.
 */
public class KeySettingController2 extends BaseController {
    
    private KeySettingView2 view;
    private KeyData keyData;
    
    public KeySettingController2() {
        this.view = new KeySettingView2();
        this.keyData = settingManager.getCurrentSettings().controlData;
    }
    
    @Override
    public Scene createScene() {
        // 두 플레이어의 키 바인딩 가져오기
        Map<String, KeyCode> player1Bindings = getPlayer1KeyBindings();
        Map<String, KeyCode> player2Bindings = getPlayer2KeyBindings();
        
        VBox root = view.createView(
            player1Bindings,
            player2Bindings,
            this::resetToDefault,
            this::handleGoBack
        );
        
        createDefaultScene(root);
        return scene;
    }
    
    @Override
    protected void handleKeyInput(KeyEvent event) {
        handleKeyPressed(event.getCode());
    }
    
    /**
     * Player 1의 키 바인딩을 Map으로 반환합니다.
     */
    private Map<String, KeyCode> getPlayer1KeyBindings() {
        Map<String, KeyCode> bindings = new HashMap<>();
        String[] actions = KeySettingView2.getActions();
        
        for (String action : actions) {
            KeyCode key = getKeyBinding(1, action);
            if (key != null) {
                bindings.put(action, key);
            }
        }
        
        return bindings;
    }
    
    /**
     * Player 2의 키 바인딩을 Map으로 반환합니다.
     */
    private Map<String, KeyCode> getPlayer2KeyBindings() {
        Map<String, KeyCode> bindings = new HashMap<>();
        String[] actions = KeySettingView2.getActions();
        
        for (String action : actions) {
            KeyCode key = getKeyBinding(2, action);
            if (key != null) {
                bindings.put(action, key);
            }
        }
        
        return bindings;
    }
    
    /**
     * 특정 플레이어의 액션 키 바인딩을 반환합니다.
     */
    private KeyCode getKeyBinding(int playerNumber, String action) {
        // pause는 공유
        if ("pause".equals(action)) {
            return keyData.pause;
        }
        
        if (playerNumber == 1) {
            switch (action) {
                case "moveLeft": return keyData.multi1MoveLeft;
                case "moveRight": return keyData.multi1MoveRight;
                case "softDrop": return keyData.multi1SoftDrop;
                case "hardDrop": return keyData.multi1HardDrop;
                case "rotateCounterClockwise": return keyData.multi1RotateCounterClockwise;
                case "rotateClockwise": return keyData.multi1RotateClockwise;
                case "hold": return keyData.multi1Hold;
                default: return null;
            }
        } else { // playerNumber == 2
            switch (action) {
                case "moveLeft": return keyData.multi2MoveLeft;
                case "moveRight": return keyData.multi2MoveRight;
                case "softDrop": return keyData.multi2SoftDrop;
                case "hardDrop": return keyData.multi2HardDrop;
                case "rotateCounterClockwise": return keyData.multi2RotateCounterClockwise;
                case "rotateClockwise": return keyData.multi2RotateClockwise;
                case "hold": return keyData.multi2Hold;
                default: return null;
            }
        }
    }
    
    /**
     * 특정 플레이어의 액션 키 바인딩을 설정합니다.
     */
    private boolean setKeyBinding(int playerNumber, String action, KeyCode keyCode) {
        // 중복 키 체크
        if (isDuplicateKey(playerNumber, keyCode, action)) {
            return false;
        }
        
        // pause는 공유
        if ("pause".equals(action)) {
            keyData.pause = keyCode;
            return true;
        }
        
        if (playerNumber == 1) {
            switch (action) {
                case "moveLeft": keyData.multi1MoveLeft = keyCode; break;
                case "moveRight": keyData.multi1MoveRight = keyCode; break;
                case "softDrop": keyData.multi1SoftDrop = keyCode; break;
                case "hardDrop": keyData.multi1HardDrop = keyCode; break;
                case "rotateCounterClockwise": keyData.multi1RotateCounterClockwise = keyCode; break;
                case "rotateClockwise": keyData.multi1RotateClockwise = keyCode; break;
                case "hold": keyData.multi1Hold = keyCode; break;
                default: return false;
            }
        } else { // playerNumber == 2
            switch (action) {
                case "moveLeft": keyData.multi2MoveLeft = keyCode; break;
                case "moveRight": keyData.multi2MoveRight = keyCode; break;
                case "softDrop": keyData.multi2SoftDrop = keyCode; break;
                case "hardDrop": keyData.multi2HardDrop = keyCode; break;
                case "rotateCounterClockwise": keyData.multi2RotateCounterClockwise = keyCode; break;
                case "rotateClockwise": keyData.multi2RotateClockwise = keyCode; break;
                case "hold": keyData.multi2Hold = keyCode; break;
                default: return false;
            }
        }
        
        return true;
    }
    
    /**
     * 특정 플레이어 내에서 중복 키인지 확인합니다.
     */
    private boolean isDuplicateKey(int playerNumber, KeyCode keyCode, String excludeAction) {
        String[] actions = KeySettingView2.getActions();
        for (String action : actions) {
            if (!action.equals(excludeAction)) {
                KeyCode existingKey = getKeyBinding(playerNumber, action);
                if (existingKey != null && existingKey.equals(keyCode)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 키보드 입력을 처리합니다.
     */
    private void handleKeyPressed(KeyCode keyCode) {
        if (view.isWaitingForKey()) {
            handleNewKeyBinding(keyCode);
        } else {
            handleNavigation(keyCode);
        }
    }
    
    /**
     * 네비게이션 키 입력을 처리합니다.
     */
    private void handleNavigation(KeyCode keyCode) {
        if (keyCode == KeyCode.ESCAPE) {
            handleGoBack();
            return;
        }
        
        // NavigableButtonSystem에 입력 전달
        KeyEvent event = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            keyCode,
            false,
            false,
            false,
            false
        );
        view.getButtonSystem().handleInput(event);
    }
    
    /**
     * 새로운 키 바인딩을 처리합니다.
     */
    private void handleNewKeyBinding(KeyCode keyCode) {
        Integer playerNumber = view.getWaitingPlayer();
        String action = view.getWaitingAction();
        
        if (playerNumber == null || action == null) {
            return;
        }
        
        // ESC로 취소
        if (keyCode == KeyCode.ESCAPE) {
            view.cancelKeyBinding();
            return;
        }
        
        // 유효하지 않은 modifier 키 체크
        if (isInvalidModifierKey(keyCode)) {
            return;
        }
        
        // 키 바인딩 설정 시도
        if (setKeyBinding(playerNumber, action, keyCode)) {
            // 성공: UI 업데이트
            view.updateKeyBinding(playerNumber, action, keyCode);
            
            // 설정 저장
            settingManager.saveSettingData();
        } else {
            // 실패: 중복 키 에러 표시
            view.showDuplicateKeyError(keyCode);
        }
    }
    
    /**
     * 유효하지 않은 modifier 키인지 확인합니다.
     * SHIFT는 Hold 키로 사용하므로 허용합니다.
     */
    private boolean isInvalidModifierKey(KeyCode keyCode) {
        return keyCode == KeyCode.CONTROL || 
               keyCode == KeyCode.ALT || 
               keyCode == KeyCode.META;
    }
    
    /**
     * 기본값으로 리셋합니다.
     */
    private void resetToDefault() {
        // Player 1 기본 키 (화살표 키 기반)
        keyData.multi1MoveLeft = KeyCode.LEFT;
        keyData.multi1MoveRight = KeyCode.RIGHT;
        keyData.multi1SoftDrop = KeyCode.DOWN;
        keyData.multi1HardDrop = KeyCode.ENTER;
        keyData.multi1RotateCounterClockwise = KeyCode.QUOTE;
        keyData.multi1RotateClockwise = KeyCode.UP;
        keyData.multi1Hold = KeyCode.SHIFT;
        
        // Player 2 기본 키 (WASD 기반)
        keyData.multi2MoveLeft = KeyCode.A;
        keyData.multi2MoveRight = KeyCode.D;
        keyData.multi2SoftDrop = KeyCode.S;
        keyData.multi2HardDrop = KeyCode.SPACE;
        keyData.multi2RotateCounterClockwise = KeyCode.Z;
        keyData.multi2RotateClockwise = KeyCode.W;
        keyData.multi2Hold = KeyCode.C;
        
        // pause는 공유 (KeyData.pause)
        keyData.pause = KeyCode.ESCAPE;
        
        // 설정 저장
        settingManager.saveSettingData();
        
        // UI 업데이트
        view.updateAllKeyBindings(getPlayer1KeyBindings(), getPlayer2KeyBindings());
    }
    
    /**
     * 뒤로 가기를 처리합니다.
     */
    private void handleGoBack() {
        // KeySettingSelectView로 돌아가기
        popState();
    }
}

