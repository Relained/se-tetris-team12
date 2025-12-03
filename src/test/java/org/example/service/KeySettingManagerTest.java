package org.example.service;

import javafx.scene.input.KeyCode;
import org.example.model.KeyData;
import org.example.model.SettingData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * KeySettingManager 클래스의 Unit Test
 */
class KeySettingManagerTest {
    
    private KeySettingManager keySettingManager;
    private SettingManager mockSettingManager;
    private SettingData mockSettingData;
    private KeyData keyData;
    
    @BeforeEach
    void setUp() {
        // KeySettingManager 싱글톤 인스턴스 가져오기
        keySettingManager = KeySettingManager.getInstance();
        
        // Mock SettingManager 생성
        mockSettingManager = Mockito.mock(SettingManager.class);
        mockSettingData = new SettingData();
        keyData = new KeyData();
        mockSettingData.controlData = keyData;
        
        when(mockSettingManager.getCurrentSettings()).thenReturn(mockSettingData);
        
        // SettingManager 주입
        keySettingManager.setSettingManager(mockSettingManager);
    }
    
    @Test
    void testGetInstance() {
        // 싱글톤 인스턴스가 동일한지 확인
        KeySettingManager instance1 = KeySettingManager.getInstance();
        KeySettingManager instance2 = KeySettingManager.getInstance();
        assertSame(instance1, instance2);
    }
    
    @Test
    void testGetKeyData() {
        // KeyData 반환 확인
        KeyData retrievedKeyData = keySettingManager.getKeyData();
        assertNotNull(retrievedKeyData);
        assertSame(keyData, retrievedKeyData);
    }
    
    @Test
    void testGetKeyDataWithoutSettingManager() {
        // SettingManager 없이 호출 시 기본값 반환
        KeySettingManager tempManager = KeySettingManager.getInstance();
        tempManager.setSettingManager(null);
        KeyData defaultKeyData = tempManager.getKeyData();
        assertNotNull(defaultKeyData);
        
        // 원상복구
        tempManager.setSettingManager(mockSettingManager);
    }
    
    @Test
    void testSetKeyBindingMoveLeft() {
        boolean result = keySettingManager.setKeyBinding("moveLeft", KeyCode.A);
        assertTrue(result);
        assertEquals(KeyCode.A, keyData.moveLeft);
    }
    
    @Test
    void testSetKeyBindingMoveRight() {
        boolean result = keySettingManager.setKeyBinding("moveRight", KeyCode.D);
        assertTrue(result);
        assertEquals(KeyCode.D, keyData.moveRight);
    }
    
    @Test
    void testSetKeyBindingSoftDrop() {
        boolean result = keySettingManager.setKeyBinding("softDrop", KeyCode.S);
        assertTrue(result);
        assertEquals(KeyCode.S, keyData.softDrop);
    }
    
    @Test
    void testSetKeyBindingHardDrop() {
        boolean result = keySettingManager.setKeyBinding("hardDrop", KeyCode.W);
        assertTrue(result);
        assertEquals(KeyCode.W, keyData.hardDrop);
    }
    
    @Test
    void testSetKeyBindingRotateCounterClockwise() {
        boolean result = keySettingManager.setKeyBinding("rotateCounterClockwise", KeyCode.Q);
        assertTrue(result);
        assertEquals(KeyCode.Q, keyData.rotateCounterClockwise);
    }
    
    @Test
    void testSetKeyBindingRotateClockwise() {
        boolean result = keySettingManager.setKeyBinding("rotateClockwise", KeyCode.E);
        assertTrue(result);
        assertEquals(KeyCode.E, keyData.rotateClockwise);
    }
    
    @Test
    void testSetKeyBindingHold() {
        boolean result = keySettingManager.setKeyBinding("hold", KeyCode.SHIFT);
        assertTrue(result);
        assertEquals(KeyCode.SHIFT, keyData.hold);
    }
    
    @Test
    void testSetKeyBindingPause() {
        boolean result = keySettingManager.setKeyBinding("pause", KeyCode.P);
        assertTrue(result);
        assertEquals(KeyCode.P, keyData.pause);
    }
    
    @Test
    void testSetKeyBindingInvalidAction() {
        // 잘못된 액션 이름
        boolean result = keySettingManager.setKeyBinding("invalidAction", KeyCode.A);
        assertFalse(result);
    }
    
    @Test
    void testSetKeyBindingDuplicateKey() {
        // moveLeft를 A로 설정
        keySettingManager.setKeyBinding("moveLeft", KeyCode.A);
        
        // moveRight도 A로 설정 시도 - 실패해야 함
        boolean result = keySettingManager.setKeyBinding("moveRight", KeyCode.A);
        assertFalse(result);
        
        // moveRight는 기본값 유지
        assertEquals(KeyCode.RIGHT, keyData.moveRight);
    }
    
    @Test
    void testSetKeyBindingSameActionAllowed() {
        // 같은 액션에 대해 키 재설정은 허용
        keySettingManager.setKeyBinding("moveLeft", KeyCode.A);
        assertEquals(KeyCode.A, keyData.moveLeft);
        
        boolean result = keySettingManager.setKeyBinding("moveLeft", KeyCode.J);
        assertTrue(result);
        assertEquals(KeyCode.J, keyData.moveLeft);
    }
    
    @Test
    void testGetKeyBindingMoveLeft() {
        keyData.moveLeft = KeyCode.A;
        assertEquals(KeyCode.A, keySettingManager.getKeyBinding("moveLeft"));
    }
    
    @Test
    void testGetKeyBindingMoveRight() {
        keyData.moveRight = KeyCode.D;
        assertEquals(KeyCode.D, keySettingManager.getKeyBinding("moveRight"));
    }
    
    @Test
    void testGetKeyBindingSoftDrop() {
        keyData.softDrop = KeyCode.S;
        assertEquals(KeyCode.S, keySettingManager.getKeyBinding("softDrop"));
    }
    
    @Test
    void testGetKeyBindingHardDrop() {
        keyData.hardDrop = KeyCode.W;
        assertEquals(KeyCode.W, keySettingManager.getKeyBinding("hardDrop"));
    }
    
    @Test
    void testGetKeyBindingRotateCounterClockwise() {
        keyData.rotateCounterClockwise = KeyCode.Q;
        assertEquals(KeyCode.Q, keySettingManager.getKeyBinding("rotateCounterClockwise"));
    }
    
    @Test
    void testGetKeyBindingRotateClockwise() {
        keyData.rotateClockwise = KeyCode.E;
        assertEquals(KeyCode.E, keySettingManager.getKeyBinding("rotateClockwise"));
    }
    
    @Test
    void testGetKeyBindingHold() {
        keyData.hold = KeyCode.SHIFT;
        assertEquals(KeyCode.SHIFT, keySettingManager.getKeyBinding("hold"));
    }
    
    @Test
    void testGetKeyBindingPause() {
        keyData.pause = KeyCode.P;
        assertEquals(KeyCode.P, keySettingManager.getKeyBinding("pause"));
    }
    
    @Test
    void testGetKeyBindingInvalidAction() {
        assertNull(keySettingManager.getKeyBinding("invalidAction"));
    }
    
    @Test
    void testResetToDefault() {
        // 키를 변경
        keyData.moveLeft = KeyCode.A;
        keyData.moveRight = KeyCode.D;
        
        // 기본값으로 리셋
        keySettingManager.resetToDefault();
        
        // 새로운 KeyData가 할당되었는지 확인
        KeyData newKeyData = mockSettingData.controlData;
        assertNotNull(newKeyData);
        
        // 기본값으로 돌아왔는지 확인
        assertEquals(KeyCode.LEFT, newKeyData.moveLeft);
        assertEquals(KeyCode.RIGHT, newKeyData.moveRight);
    }
    
    @Test
    void testGetAllActions() {
        String[] actions = keySettingManager.getAllActions();
        
        assertNotNull(actions);
        assertEquals(8, actions.length);
        
        // 모든 액션이 포함되어 있는지 확인
        assertTrue(containsAction(actions, "moveLeft"));
        assertTrue(containsAction(actions, "moveRight"));
        assertTrue(containsAction(actions, "softDrop"));
        assertTrue(containsAction(actions, "hardDrop"));
        assertTrue(containsAction(actions, "rotateCounterClockwise"));
        assertTrue(containsAction(actions, "rotateClockwise"));
        assertTrue(containsAction(actions, "hold"));
        assertTrue(containsAction(actions, "pause"));
    }
    
    @Test
    void testGetActionDisplayNameMoveLeft() {
        assertEquals("Move Left", keySettingManager.getActionDisplayName("moveLeft"));
    }
    
    @Test
    void testGetActionDisplayNameMoveRight() {
        assertEquals("Move Right", keySettingManager.getActionDisplayName("moveRight"));
    }
    
    @Test
    void testGetActionDisplayNameSoftDrop() {
        assertEquals("Soft Drop", keySettingManager.getActionDisplayName("softDrop"));
    }
    
    @Test
    void testGetActionDisplayNameHardDrop() {
        assertEquals("Hard Drop", keySettingManager.getActionDisplayName("hardDrop"));
    }
    
    @Test
    void testGetActionDisplayNameRotateCounterClockwise() {
        assertEquals("Rotate CCW", keySettingManager.getActionDisplayName("rotateCounterClockwise"));
    }
    
    @Test
    void testGetActionDisplayNameRotateClockwise() {
        assertEquals("Rotate CW", keySettingManager.getActionDisplayName("rotateClockwise"));
    }
    
    @Test
    void testGetActionDisplayNameHold() {
        assertEquals("Hold", keySettingManager.getActionDisplayName("hold"));
    }
    
    @Test
    void testGetActionDisplayNamePause() {
        assertEquals("Pause", keySettingManager.getActionDisplayName("pause"));
    }
    
    @Test
    void testGetActionDisplayNameUnknown() {
        assertEquals("unknownAction", keySettingManager.getActionDisplayName("unknownAction"));
    }
    
    private boolean containsAction(String[] actions, String action) {
        for (String a : actions) {
            if (a.equals(action)) {
                return true;
            }
        }
        return false;
    }
}
