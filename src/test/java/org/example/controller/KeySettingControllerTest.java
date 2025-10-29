package org.example.controller;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.example.service.KeySettingManager;
import org.example.service.SettingManager;
import org.example.service.StateManager;
import org.example.view.KeySettingView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class KeySettingControllerTest extends ApplicationTest {
    
    private KeySettingController controller;
    private StateManager stateManager;
    private KeySettingView keySettingView;
    private KeySettingManager keySettingManager;
    
    @BeforeEach
    void setUp() {
        stateManager = mock(StateManager.class);
        SettingManager settingManager = new SettingManager();
        stateManager.settingManager = settingManager;
        keySettingManager = KeySettingManager.getInstance();
        keySettingManager.setSettingManager(settingManager);
        keySettingView = mock(KeySettingView.class);
        controller = new KeySettingController(stateManager, keySettingView);
    }
    
    @Test
    @DisplayName("컨트롤러 생성 시 null이 아닌지 확인")
    void testControllerNotNull() {
        assertNotNull(controller);
    }
    
    @Test
    @DisplayName("초기 상태는 키 입력 대기 중이 아님")
    void testInitialState() {
        assertFalse(controller.isWaitingForKey());
        assertNull(controller.getWaitingForKeyAction());
    }
    
    @Test
    @DisplayName("Reset to Default 핸들러")
    void testHandleResetToDefault() {
        controller.handleResetToDefault();
        
        verify(keySettingView).updateAllKeyBindings();
    }
    
    @Test
    @DisplayName("Go Back 핸들러 - 이전 상태로 복귀")
    void testHandleGoBack() {
        controller.handleGoBack();
        
        verify(stateManager).popState();
    }
    
    @Test
    @DisplayName("handleKeyInput - UP 키 네비게이션")
    void testHandleKeyInputUp() {
        KeyEvent upEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.UP,
            false, false, false, false
        );
        
        controller.handleKeyInput(upEvent);
        
        verify(keySettingView).navigateActions(true);
    }
    
    @Test
    @DisplayName("handleKeyInput - DOWN 키 네비게이션")
    void testHandleKeyInputDown() {
        KeyEvent downEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.DOWN,
            false, false, false, false
        );
        
        controller.handleKeyInput(downEvent);
        
        verify(keySettingView).navigateActions(false);
    }
    
    @Test
    @DisplayName("handleKeyInput - ESCAPE 키로 뒤로 가기")
    void testHandleKeyInputEscape() {
        KeyEvent escapeEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.ESCAPE,
            false, false, false, false
        );
        
        controller.handleKeyInput(escapeEvent);
        
        verify(stateManager).popState();
    }
    
    @Test
    @DisplayName("handleKeyInput - ENTER 키로 버튼 실행")
    void testHandleKeyInputEnterOnButton() {
        when(keySettingView.isButtonSelected()).thenReturn(true);
        
        KeyEvent enterEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.ENTER,
            false, false, false, false
        );
        
        controller.handleKeyInput(enterEvent);
        
        verify(keySettingView).executeSelectedButton();
    }
    
    @Test
    @DisplayName("handleKeyInput - ENTER 키로 키 설정 모드 진입")
    void testHandleKeyInputEnterOnAction() {
        when(keySettingView.isButtonSelected()).thenReturn(false);
        when(keySettingView.getSelectedAction()).thenReturn("moveLeft");
        
        KeyEvent enterEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.ENTER,
            false, false, false, false
        );
        
        controller.handleKeyInput(enterEvent);
        
        assertTrue(controller.isWaitingForKey());
        assertEquals("moveLeft", controller.getWaitingForKeyAction());
        verify(keySettingView).showWaitingForKey("moveLeft");
    }
    
    @Test
    @DisplayName("handleKeyInput - SPACE 키로 키 설정 모드 진입")
    void testHandleKeyInputSpaceOnAction() {
        when(keySettingView.isButtonSelected()).thenReturn(false);
        when(keySettingView.getSelectedAction()).thenReturn("moveRight");
        
        KeyEvent spaceEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.SPACE,
            false, false, false, false
        );
        
        controller.handleKeyInput(spaceEvent);
        
        assertTrue(controller.isWaitingForKey());
        assertEquals("moveRight", controller.getWaitingForKeyAction());
        verify(keySettingView).showWaitingForKey("moveRight");
    }
    
    @Test
    @DisplayName("handleKeyInput - 키 설정 모드에서 새 키 바인딩 성공")
    void testHandleKeyInputNewKeyBindingSuccess() {
        // 키 설정 모드 진입
        when(keySettingView.isButtonSelected()).thenReturn(false);
        when(keySettingView.getSelectedAction()).thenReturn("moveLeft");
        
        KeyEvent enterEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.ENTER,
            false, false, false, false
        );
        controller.handleKeyInput(enterEvent);
        
        // 새 키 입력
        KeyEvent newKeyEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.A,
            false, false, false, false
        );
        controller.handleKeyInput(newKeyEvent);
        
        verify(keySettingView).updateKeyBinding("moveLeft", KeyCode.A);
        verify(keySettingView).hideWaitingForKey();
        assertFalse(controller.isWaitingForKey());
        assertNull(controller.getWaitingForKeyAction());
    }
    
    @Test
    @DisplayName("handleKeyInput - 키 설정 모드에서 ESC로 취소")
    void testHandleKeyInputCancelKeyBinding() {
        // 키 설정 모드 진입
        when(keySettingView.isButtonSelected()).thenReturn(false);
        when(keySettingView.getSelectedAction()).thenReturn("moveLeft");
        
        KeyEvent enterEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.ENTER,
            false, false, false, false
        );
        controller.handleKeyInput(enterEvent);
        
        assertTrue(controller.isWaitingForKey());
        
        // ESC로 취소
        KeyEvent escapeEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.ESCAPE,
            false, false, false, false
        );
        controller.handleKeyInput(escapeEvent);
        
        verify(keySettingView).hideWaitingForKey();
        assertFalse(controller.isWaitingForKey());
        assertNull(controller.getWaitingForKeyAction());
    }
    
    @Test
    @DisplayName("handleKeyInput - 키 설정 모드에서 중복 키 에러")
    void testHandleKeyInputDuplicateKey() {
        // 먼저 LEFT 키를 moveLeft에 설정
        keySettingManager.setKeyBinding("moveLeft", KeyCode.LEFT);
        
        // 키 설정 모드 진입 (moveRight 설정 시도)
        when(keySettingView.isButtonSelected()).thenReturn(false);
        when(keySettingView.getSelectedAction()).thenReturn("moveRight");
        
        KeyEvent enterEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.ENTER,
            false, false, false, false
        );
        controller.handleKeyInput(enterEvent);
        
        // 이미 사용 중인 LEFT 키 입력
        KeyEvent leftKeyEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.LEFT,
            false, false, false, false
        );
        controller.handleKeyInput(leftKeyEvent);
        
        verify(keySettingView).showDuplicateKeyError(KeyCode.LEFT);
        // 키 설정 모드는 유지됨
        assertTrue(controller.isWaitingForKey());
    }
    
    @Test
    @DisplayName("handleKeyInput - 수정자 키는 무시됨")
    void testHandleKeyInputModifierKeyIgnored() {
        // 키 설정 모드 진입
        when(keySettingView.isButtonSelected()).thenReturn(false);
        when(keySettingView.getSelectedAction()).thenReturn("moveLeft");
        
        KeyEvent enterEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.ENTER,
            false, false, false, false
        );
        controller.handleKeyInput(enterEvent);
        
        // SHIFT 키 입력 (무시되어야 함)
        KeyEvent shiftEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.SHIFT,
            false, false, false, false
        );
        controller.handleKeyInput(shiftEvent);
        
        // 키 설정 모드는 유지됨 (updateKeyBinding이 호출되지 않음)
        assertTrue(controller.isWaitingForKey());
        verify(keySettingView, never()).updateKeyBinding(anyString(), any(KeyCode.class));
        verify(keySettingView, never()).hideWaitingForKey();
    }
    
    @Test
    @DisplayName("handleKeyInput - 여러 수정자 키 테스트")
    void testHandleKeyInputVariousModifierKeys() {
        // 키 설정 모드 진입
        when(keySettingView.isButtonSelected()).thenReturn(false);
        when(keySettingView.getSelectedAction()).thenReturn("rotateClockwise");
        
        KeyEvent enterEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.ENTER,
            false, false, false, false
        );
        controller.handleKeyInput(enterEvent);
        
        // 여러 수정자 키 테스트
        KeyCode[] modifierKeys = {
            KeyCode.CONTROL, KeyCode.ALT, KeyCode.META,
            KeyCode.CAPS, KeyCode.NUM_LOCK
        };
        
        for (KeyCode modifierKey : modifierKeys) {
            KeyEvent modEvent = new KeyEvent(
                KeyEvent.KEY_PRESSED,
                "",
                "",
                modifierKey,
                false, false, false, false
            );
            controller.handleKeyInput(modEvent);
            
            // 키 설정 모드는 유지됨
            assertTrue(controller.isWaitingForKey());
        }
    }
    
    @Test
    @DisplayName("handleKeyInput - 연속적인 키 설정")
    void testHandleKeyInputSequentialKeySettings() {
        // 첫 번째 키 설정
        when(keySettingView.isButtonSelected()).thenReturn(false);
        when(keySettingView.getSelectedAction()).thenReturn("moveLeft");
        
        KeyEvent enterEvent1 = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.ENTER,
            false, false, false, false
        );
        controller.handleKeyInput(enterEvent1);
        
        KeyEvent aKeyEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.A,
            false, false, false, false
        );
        controller.handleKeyInput(aKeyEvent);
        
        verify(keySettingView).updateKeyBinding("moveLeft", KeyCode.A);
        assertFalse(controller.isWaitingForKey());
        
        // 두 번째 키 설정
        when(keySettingView.getSelectedAction()).thenReturn("moveRight");
        
        KeyEvent enterEvent2 = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.ENTER,
            false, false, false, false
        );
        controller.handleKeyInput(enterEvent2);
        
        KeyEvent dKeyEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED,
            "",
            "",
            KeyCode.D,
            false, false, false, false
        );
        controller.handleKeyInput(dKeyEvent);
        
        verify(keySettingView).updateKeyBinding("moveRight", KeyCode.D);
        assertFalse(controller.isWaitingForKey());
    }
}
