package org.example.controller;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.example.model.KeyData;
import org.example.service.ColorManager;
import org.example.service.SettingManager;
import org.example.view.BaseView;
import org.example.view.KeySettingView2;
import org.example.view.component.NavigableButtonSystem;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * KeySettingController2 클래스의 Unit Test with Mockito
 */
@ExtendWith(ApplicationExtension.class)
class KeySettingController2Test {
    
    private KeySettingController2 controller;
    private SettingManager settingManager;
    
    @Mock
    private KeySettingView2 mockView;
    
    @Mock
    private NavigableButtonSystem mockButtonSystem;
    
    @Start
    private void start(Stage stage) {
        ColorManager colorManager = ColorManager.getInstance();
        BaseView.Initialize(colorManager);
        
        settingManager = new SettingManager();
        BaseController.Initialize(stage, settingManager);
    }
    
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = new KeySettingController2();
        
        // Mock 객체를 Controller의 private field에 주입
        Field viewField = KeySettingController2.class.getDeclaredField("view");
        viewField.setAccessible(true);
        viewField.set(controller, mockView);
        
        // Mock ButtonSystem 설정
        when(mockView.getButtonSystem()).thenReturn(mockButtonSystem);
    }
    
    @Test
    void testControllerExtendsBaseController() {
        assertTrue(controller instanceof BaseController);
    }
    
    @Test
    void testDefaultPlayer1KeyBindings() {
        KeyData keyData = settingManager.getCurrentSettings().controlData;
        
        assertEquals(KeyCode.LEFT, keyData.multi1MoveLeft);
        assertEquals(KeyCode.RIGHT, keyData.multi1MoveRight);
        assertEquals(KeyCode.DOWN, keyData.multi1SoftDrop);
    }
    
    @Test
    void testDefaultPlayer2KeyBindings() {
        KeyData keyData = settingManager.getCurrentSettings().controlData;
        
        assertEquals(KeyCode.A, keyData.multi2MoveLeft);
        assertEquals(KeyCode.D, keyData.multi2MoveRight);
        assertEquals(KeyCode.S, keyData.multi2SoftDrop);
    }
    
    // ========== Mockito 기반 Private Method 테스트 ==========
    
    @Test
    void testHandleKeyPressedNavigationModeWithMock() throws Exception {
        when(mockView.isWaitingForKey()).thenReturn(false);
        
        var method = controller.getClass().getDeclaredMethod("handleKeyPressed", KeyCode.class);
        method.setAccessible(true);
        
        method.invoke(controller, KeyCode.UP);
        
        verify(mockButtonSystem).handleInput(any(KeyEvent.class));
    }
    
    @Test
    void testHandleKeyPressedWaitingModeWithMock() throws Exception {
        when(mockView.isWaitingForKey()).thenReturn(true);
        when(mockView.getWaitingPlayer()).thenReturn(1);
        when(mockView.getWaitingAction()).thenReturn("moveLeft");
        
        var method = controller.getClass().getDeclaredMethod("handleKeyPressed", KeyCode.class);
        method.setAccessible(true);
        
        method.invoke(controller, KeyCode.W);
        
        verify(mockView).updateKeyBinding(anyInt(), anyString(), any(KeyCode.class));
    }
    
    @Test
    void testHandleNavigationWithMock() throws Exception {
        var method = controller.getClass().getDeclaredMethod("handleNavigation", KeyCode.class);
        method.setAccessible(true);
        
        method.invoke(controller, KeyCode.UP);
        
        verify(mockButtonSystem).handleInput(any(KeyEvent.class));
    }
    
    @Test
    void testHandleNewKeyBindingSuccessWithMock() throws Exception {
        when(mockView.getWaitingPlayer()).thenReturn(1);
        when(mockView.getWaitingAction()).thenReturn("moveLeft");
        
        var method = controller.getClass().getDeclaredMethod("handleNewKeyBinding", KeyCode.class);
        method.setAccessible(true);
        
        method.invoke(controller, KeyCode.W);
        
        verify(mockView).updateKeyBinding(1, "moveLeft", KeyCode.W);
    }
    
    @Test
    void testHandleNewKeyBindingWithEscapeKeyMock() throws Exception {
        when(mockView.getWaitingPlayer()).thenReturn(1);
        when(mockView.getWaitingAction()).thenReturn("moveRight");
        
        var method = controller.getClass().getDeclaredMethod("handleNewKeyBinding", KeyCode.class);
        method.setAccessible(true);
        
        method.invoke(controller, KeyCode.ESCAPE);
        
        verify(mockView).cancelKeyBinding();
    }
    
    @Test
    void testHandleNewKeyBindingWithInvalidModifierKeyMock() throws Exception {
        when(mockView.getWaitingPlayer()).thenReturn(2);
        when(mockView.getWaitingAction()).thenReturn("hardDrop");
        
        var method = controller.getClass().getDeclaredMethod("handleNewKeyBinding", KeyCode.class);
        method.setAccessible(true);
        
        method.invoke(controller, KeyCode.CONTROL);
        
        verify(mockView, never()).updateKeyBinding(anyInt(), anyString(), any(KeyCode.class));
    }
    
    @Test
    void testHandleNewKeyBindingDuplicateKeyWithMock() throws Exception {
        when(mockView.getWaitingPlayer()).thenReturn(1);
        when(mockView.getWaitingAction()).thenReturn("moveLeft");
        
        var method = controller.getClass().getDeclaredMethod("handleNewKeyBinding", KeyCode.class);
        method.setAccessible(true);
        
        method.invoke(controller, KeyCode.RIGHT);
        
        verify(mockView).showDuplicateKeyError(KeyCode.RIGHT);
    }
    
    @Test
    void testResetToDefaultWithMock() throws Exception {
        var method = controller.getClass().getDeclaredMethod("resetToDefault");
        method.setAccessible(true);
        
        method.invoke(controller);
        
        KeyData keyData = settingManager.getCurrentSettings().controlData;
        assertEquals(KeyCode.LEFT, keyData.multi1MoveLeft);
        assertEquals(KeyCode.A, keyData.multi2MoveLeft);
        
        verify(mockView).updateAllKeyBindings(any(), any());
    }
    
    @Test
    void testSetKeyBindingWithValidKeyMock() throws Exception {
        var method = controller.getClass().getDeclaredMethod("setKeyBinding", int.class, String.class, KeyCode.class);
        method.setAccessible(true);
        
        boolean result = (boolean) method.invoke(controller, 1, "moveLeft", KeyCode.J);
        assertTrue(result);
        
        KeyData keyData = settingManager.getCurrentSettings().controlData;
        assertEquals(KeyCode.J, keyData.multi1MoveLeft);
    }
    
    @Test
    void testSetKeyBindingWithDuplicateKeyMock() throws Exception {
        var method = controller.getClass().getDeclaredMethod("setKeyBinding", int.class, String.class, KeyCode.class);
        method.setAccessible(true);
        
        boolean result = (boolean) method.invoke(controller, 1, "moveLeft", KeyCode.RIGHT);
        assertFalse(result);
    }
    
    @Test
    void testIsDuplicateKeyWithDifferentActions() throws Exception {
        var method = controller.getClass().getDeclaredMethod("isDuplicateKey", int.class, KeyCode.class, String.class);
        method.setAccessible(true);
        
        boolean isDuplicate = (boolean) method.invoke(controller, 1, KeyCode.RIGHT, "moveLeft");
        assertTrue(isDuplicate);
    }
    
    @Test
    void testIsDuplicateKeyWithSameAction() throws Exception {
        var method = controller.getClass().getDeclaredMethod("isDuplicateKey", int.class, KeyCode.class, String.class);
        method.setAccessible(true);
        
        boolean isDuplicate = (boolean) method.invoke(controller, 1, KeyCode.LEFT, "moveLeft");
        assertFalse(isDuplicate);
    }
    
    @Test
    void testGetKeyBindingForPlayer1AllActions() throws Exception {
        var method = controller.getClass().getDeclaredMethod("getKeyBinding", int.class, String.class);
        method.setAccessible(true);
        
        // 몇 가지 액션만 테스트
        assertNotNull(method.invoke(controller, 1, "moveLeft"));
        assertNotNull(method.invoke(controller, 1, "moveRight"));
        assertNotNull(method.invoke(controller, 1, "softDrop"));
    }
    
    @Test
    void testGetKeyBindingForPlayer2AllActions() throws Exception {
        var method = controller.getClass().getDeclaredMethod("getKeyBinding", int.class, String.class);
        method.setAccessible(true);
        
        // 몇 가지 액션만 테스트
        assertNotNull(method.invoke(controller, 2, "moveLeft"));
        assertNotNull(method.invoke(controller, 2, "moveRight"));
        assertNotNull(method.invoke(controller, 2, "softDrop"));
    }
    
    @Test
    void testGetPlayer1KeyBindingsReturnsMap() throws Exception {
        var method = controller.getClass().getDeclaredMethod("getPlayer1KeyBindings");
        method.setAccessible(true);
        
        @SuppressWarnings("unchecked")
        var result = (java.util.Map<String, KeyCode>) method.invoke(controller);
        
        assertNotNull(result);
        // Map이 비어있지 않은지 확인
        assertFalse(result.isEmpty());
    }
    
    @Test
    void testGetPlayer2KeyBindingsReturnsMap() throws Exception {
        var method = controller.getClass().getDeclaredMethod("getPlayer2KeyBindings");
        method.setAccessible(true);
        
        @SuppressWarnings("unchecked")
        var result = (java.util.Map<String, KeyCode>) method.invoke(controller);
        
        assertNotNull(result);
        // Map이 비어있지 않은지 확인
        assertFalse(result.isEmpty());
    }
    
    @Test
    void testIsInvalidModifierKeyMethod() throws Exception {
        var method = controller.getClass().getDeclaredMethod("isInvalidModifierKey", KeyCode.class);
        method.setAccessible(true);
        
        assertTrue((Boolean) method.invoke(controller, KeyCode.CONTROL));
        assertTrue((Boolean) method.invoke(controller, KeyCode.ALT));
        assertTrue((Boolean) method.invoke(controller, KeyCode.META));
        
        assertFalse((Boolean) method.invoke(controller, KeyCode.SHIFT));
        assertFalse((Boolean) method.invoke(controller, KeyCode.W));
        assertFalse((Boolean) method.invoke(controller, KeyCode.ENTER));
    }
}
