package org.example.controller;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.example.service.ColorManager;
import org.example.service.KeySettingManager;
import org.example.service.SettingManager;
import org.example.view.BaseView;
import org.example.view.KeySettingView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * KeySettingController 클래스의 Unit Test with Mockito
 */
@ExtendWith(ApplicationExtension.class)
class KeySettingControllerTest {
    
    private KeySettingController controller;
    
    @Mock
    private KeySettingView mockView;
    
    @Mock
    private KeySettingManager mockKeySettingManager;
    
    @Start
    private void start(Stage stage) {
        ColorManager colorManager = ColorManager.getInstance();
        BaseView.Initialize(colorManager);
        
        SettingManager settingManager = new SettingManager();
        BaseController.Initialize(stage, settingManager);
    }
    
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = new KeySettingController();
        
        // Mock 객체를 Controller의 private field에 주입
        Field viewField = KeySettingController.class.getDeclaredField("keySettingView");
        viewField.setAccessible(true);
        viewField.set(controller, mockView);
        
        Field managerField = KeySettingController.class.getDeclaredField("keySettingManager");
        managerField.setAccessible(true);
        managerField.set(controller, mockKeySettingManager);
    }
    
    @Test
    void testInitialWaitingState() {
        assertFalse(controller.isWaitingForKey());
        assertNull(controller.getWaitingForKeyAction());
    }
    
    @Test
    void testControllerExtendsBaseController() {
        assertTrue(controller instanceof BaseController);
    }
    
    // ========== Mockito 기반 Private Method 테스트 ==========
    
    @Test
    void testStartKeySettingModeWithMock() throws Exception {
        var method = controller.getClass().getDeclaredMethod("startKeySettingMode", String.class);
        method.setAccessible(true);
        
        method.invoke(controller, "Move Left");
        
        assertTrue(controller.isWaitingForKey());
        assertEquals("Move Left", controller.getWaitingForKeyAction());
        verify(mockView).showWaitingForKey("Move Left");
    }
    
    @Test
    void testCancelKeyBindingWithMock() throws Exception {
        var startMethod = controller.getClass().getDeclaredMethod("startKeySettingMode", String.class);
        startMethod.setAccessible(true);
        startMethod.invoke(controller, "Move Right");
        
        var method = controller.getClass().getDeclaredMethod("cancelKeyBinding");
        method.setAccessible(true);
        method.invoke(controller);
        
        assertFalse(controller.isWaitingForKey());
        assertNull(controller.getWaitingForKeyAction());
        verify(mockView).hideWaitingForKey();
    }
    
    @Test
    void testHandleNewKeyBindingSuccessWithMock() throws Exception {
        var startMethod = controller.getClass().getDeclaredMethod("startKeySettingMode", String.class);
        startMethod.setAccessible(true);
        startMethod.invoke(controller, "Soft Drop");
        
        when(mockKeySettingManager.setKeyBinding("Soft Drop", KeyCode.W)).thenReturn(true);
        
        var method = controller.getClass().getDeclaredMethod("handleNewKeyBinding", KeyEvent.class);
        method.setAccessible(true);
        
        KeyEvent keyEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED, "", "", KeyCode.W,
            false, false, false, false
        );
        
        method.invoke(controller, keyEvent);
        
        verify(mockView).updateKeyBinding("Soft Drop", KeyCode.W);
        verify(mockView).hideWaitingForKey();
        assertFalse(controller.isWaitingForKey());
    }
    
    @Test
    void testHandleNewKeyBindingDuplicateWithMock() throws Exception {
        var startMethod = controller.getClass().getDeclaredMethod("startKeySettingMode", String.class);
        startMethod.setAccessible(true);
        startMethod.invoke(controller, "Hard Drop");
        
        when(mockKeySettingManager.setKeyBinding("Hard Drop", KeyCode.LEFT)).thenReturn(false);
        
        var method = controller.getClass().getDeclaredMethod("handleNewKeyBinding", KeyEvent.class);
        method.setAccessible(true);
        
        KeyEvent keyEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED, "", "", KeyCode.LEFT,
            false, false, false, false
        );
        
        method.invoke(controller, keyEvent);
        
        verify(mockView).showDuplicateKeyError(KeyCode.LEFT);
        assertTrue(controller.isWaitingForKey());
    }
    
    @Test
    void testHandleNewKeyBindingWithEscapeKeyMock() throws Exception {
        var startMethod = controller.getClass().getDeclaredMethod("startKeySettingMode", String.class);
        startMethod.setAccessible(true);
        startMethod.invoke(controller, "Soft Drop");
        
        var method = controller.getClass().getDeclaredMethod("handleNewKeyBinding", KeyEvent.class);
        method.setAccessible(true);
        
        KeyEvent escapeEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE,
            false, false, false, false
        );
        
        method.invoke(controller, escapeEvent);
        
        assertFalse(controller.isWaitingForKey());
        verify(mockView).hideWaitingForKey();
    }
    
    @Test
    void testHandleNewKeyBindingWithModifierKeyMock() throws Exception {
        var startMethod = controller.getClass().getDeclaredMethod("startKeySettingMode", String.class);
        startMethod.setAccessible(true);
        startMethod.invoke(controller, "Hard Drop");
        
        var method = controller.getClass().getDeclaredMethod("handleNewKeyBinding", KeyEvent.class);
        method.setAccessible(true);
        
        KeyEvent shiftEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED, "", "", KeyCode.SHIFT,
            false, false, false, false
        );
        
        method.invoke(controller, shiftEvent);
        
        assertTrue(controller.isWaitingForKey());
        verify(mockKeySettingManager, never()).setKeyBinding(any(), any());
    }
    
    @Test
    void testHandleEnterKeyWithButtonSelected() throws Exception {
        when(mockView.isButtonSelected()).thenReturn(true);
        
        var method = controller.getClass().getDeclaredMethod("handleEnterKey");
        method.setAccessible(true);
        method.invoke(controller);
        
        verify(mockView).executeSelectedButton();
    }
    
    @Test
    void testHandleEnterKeyWithActionSelected() throws Exception {
        when(mockView.isButtonSelected()).thenReturn(false);
        when(mockView.getSelectedAction()).thenReturn("Rotate Clockwise");
        
        var method = controller.getClass().getDeclaredMethod("handleEnterKey");
        method.setAccessible(true);
        method.invoke(controller);
        
        assertTrue(controller.isWaitingForKey());
        assertEquals("Rotate Clockwise", controller.getWaitingForKeyAction());
    }
    
    @Test
    void testHandleResetToDefaultWithMock() {
        controller.handleResetToDefault();
        
        verify(mockKeySettingManager).resetToDefault();
        verify(mockView).updateAllKeyBindings();
    }
    
    @Test
    void testMultipleKeySettingModesWithMock() throws Exception {
        var method = controller.getClass().getDeclaredMethod("startKeySettingMode", String.class);
        method.setAccessible(true);
        
        method.invoke(controller, "Move Left");
        assertEquals("Move Left", controller.getWaitingForKeyAction());
        verify(mockView).showWaitingForKey("Move Left");
        
        method.invoke(controller, "Rotate Clockwise");
        assertEquals("Rotate Clockwise", controller.getWaitingForKeyAction());
        verify(mockView).showWaitingForKey("Rotate Clockwise");
    }
    
    @Test
    void testHandleNavigationCallsViewMethods() throws Exception {
        var method = controller.getClass().getDeclaredMethod("handleNavigation", KeyEvent.class);
        method.setAccessible(true);
        
        KeyEvent upEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED, "", "", KeyCode.UP,
            false, false, false, false
        );
        
        method.invoke(controller, upEvent);
        verify(mockView).navigateActions(true);
        
        KeyEvent downEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN,
            false, false, false, false
        );
        
        method.invoke(controller, downEvent);
        verify(mockView).navigateActions(false);
    }
    
    @Test
    void testIsModifierKeyMethod() throws Exception {
        var method = controller.getClass().getDeclaredMethod("isModifierKey", KeyCode.class);
        method.setAccessible(true);
        
        assertTrue((Boolean) method.invoke(controller, KeyCode.SHIFT));
        assertTrue((Boolean) method.invoke(controller, KeyCode.CONTROL));
        assertTrue((Boolean) method.invoke(controller, KeyCode.ALT));
        assertTrue((Boolean) method.invoke(controller, KeyCode.META));
        
        assertFalse((Boolean) method.invoke(controller, KeyCode.W));
        assertFalse((Boolean) method.invoke(controller, KeyCode.ENTER));
    }
    
    @Test
    void testIsModifierKeyWithAllModifiers() throws Exception {
        var method = controller.getClass().getDeclaredMethod("isModifierKey", KeyCode.class);
        method.setAccessible(true);
        
        KeyCode[] modifiers = {
            KeyCode.SHIFT, KeyCode.CONTROL, KeyCode.ALT, KeyCode.META,
            KeyCode.COMMAND, KeyCode.WINDOWS, KeyCode.CAPS,
            KeyCode.NUM_LOCK, KeyCode.SCROLL_LOCK
        };
        
        for (KeyCode modifier : modifiers) {
            assertTrue((Boolean) method.invoke(controller, modifier));
        }
    }
    
    @Test
    void testIsModifierKeyWithRegularKeys() throws Exception {
        var method = controller.getClass().getDeclaredMethod("isModifierKey", KeyCode.class);
        method.setAccessible(true);
        
        KeyCode[] regularKeys = {
            KeyCode.A, KeyCode.W, KeyCode.SPACE, KeyCode.ENTER,
            KeyCode.UP, KeyCode.DOWN, KeyCode.DIGIT1
        };
        
        for (KeyCode key : regularKeys) {
            assertFalse((Boolean) method.invoke(controller, key));
        }
    }
}
