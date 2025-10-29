package org.example.service;

import javafx.scene.input.KeyCode;
import org.example.model.KeyData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class KeySettingManagerTest {
    
    private KeySettingManager keySettingManager;
    private SettingManager settingManager;
    private static final String KEY_SETTING_FILE = "keysetting.ser";
    
    @BeforeEach
    void setUp() {
        keySettingManager = KeySettingManager.getInstance();
        settingManager = new SettingManager();
        keySettingManager.setSettingManager(settingManager);
    }
    
    @AfterEach
    void tearDown() {
        // 테스트 후 설정 파일 삭제
        File file = new File(KEY_SETTING_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
    
    @Test
    @DisplayName("싱글톤 인스턴스 확인")
    void testSingleton() {
        KeySettingManager instance1 = KeySettingManager.getInstance();
        KeySettingManager instance2 = KeySettingManager.getInstance();
        
        assertSame(instance1, instance2);
    }
    
    @Test
    @DisplayName("KeyData 가져오기")
    void testGetKeyData() {
        KeyData data = keySettingManager.getKeyData();
        
        assertNotNull(data);
    }
    
    @Test
    @DisplayName("키 바인딩 설정 - 성공")
    void testSetKeyBindingSuccess() {
        boolean result = keySettingManager.setKeyBinding("moveLeft", KeyCode.A);
        
        assertTrue(result);
        assertEquals(KeyCode.A, keySettingManager.getKeyBinding("moveLeft"));
    }
    
    @Test
    @DisplayName("키 바인딩 설정 - 중복 키 실패")
    void testSetKeyBindingDuplicateFail() {
        keySettingManager.setKeyBinding("moveLeft", KeyCode.A);
        boolean result = keySettingManager.setKeyBinding("moveRight", KeyCode.A);
        
        assertFalse(result);
    }
    
    @Test
    @DisplayName("키 바인딩 설정 - 잘못된 액션 이름")
    void testSetKeyBindingInvalidAction() {
        boolean result = keySettingManager.setKeyBinding("invalidAction", KeyCode.A);
        
        assertFalse(result);
    }
    
    @Test
    @DisplayName("모든 액션에 대한 키 바인딩 설정")
    void testSetAllKeyBindings() {
        assertTrue(keySettingManager.setKeyBinding("moveLeft", KeyCode.A));
        assertTrue(keySettingManager.setKeyBinding("moveRight", KeyCode.D));
        assertTrue(keySettingManager.setKeyBinding("softDrop", KeyCode.S));
        assertTrue(keySettingManager.setKeyBinding("hardDrop", KeyCode.W));
        assertTrue(keySettingManager.setKeyBinding("rotateCounterClockwise", KeyCode.Q));
        assertTrue(keySettingManager.setKeyBinding("rotateClockwise", KeyCode.E));
        assertTrue(keySettingManager.setKeyBinding("hold", KeyCode.C));
        assertTrue(keySettingManager.setKeyBinding("pause", KeyCode.P));
        
        assertEquals(KeyCode.A, keySettingManager.getKeyBinding("moveLeft"));
        assertEquals(KeyCode.D, keySettingManager.getKeyBinding("moveRight"));
        assertEquals(KeyCode.S, keySettingManager.getKeyBinding("softDrop"));
        assertEquals(KeyCode.W, keySettingManager.getKeyBinding("hardDrop"));
        assertEquals(KeyCode.Q, keySettingManager.getKeyBinding("rotateCounterClockwise"));
        assertEquals(KeyCode.E, keySettingManager.getKeyBinding("rotateClockwise"));
        assertEquals(KeyCode.C, keySettingManager.getKeyBinding("hold"));
        assertEquals(KeyCode.P, keySettingManager.getKeyBinding("pause"));
    }
    
    @Test
    @DisplayName("키 바인딩 가져오기 - 잘못된 액션")
    void testGetKeyBindingInvalidAction() {
        KeyCode result = keySettingManager.getKeyBinding("invalidAction");
        
        assertNull(result);
    }
    
    @Test
    @DisplayName("기본값으로 리셋")
    void testResetToDefault() {
        keySettingManager.setKeyBinding("moveLeft", KeyCode.A);
        keySettingManager.resetToDefault();
        
        KeyData data = keySettingManager.getKeyData();
        KeyData defaultData = new KeyData();
        
        assertEquals(defaultData.moveLeft, data.moveLeft);
    }
    
    @Test
    @DisplayName("키 설정 저장 및 로드")
    void testSaveAndLoadKeySettings() {
        keySettingManager.setKeyBinding("moveLeft", KeyCode.A);
        keySettingManager.setKeyBinding("moveRight", KeyCode.D);
        settingManager.saveSettingData();
        
        // 새로운 매니저 생성 및 로드
        SettingManager newSettingManager = new SettingManager();
        KeySettingManager newKeyManager = KeySettingManager.getInstance();
        newKeyManager.setSettingManager(newSettingManager);
        
        boolean loaded = newSettingManager.loadSettingData();
        
        assertTrue(loaded);
        assertEquals(KeyCode.A, newKeyManager.getKeyBinding("moveLeft"));
        assertEquals(KeyCode.D, newKeyManager.getKeyBinding("moveRight"));
    }
    
    @Test
    @DisplayName("모든 액션 이름 가져오기")
    void testGetAllActions() {
        String[] actions = keySettingManager.getAllActions();
        
        assertNotNull(actions);
        assertEquals(8, actions.length);
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
    @DisplayName("액션 표시 이름 가져오기")
    void testGetActionDisplayName() {
        assertEquals("Move Left", keySettingManager.getActionDisplayName("moveLeft"));
        assertEquals("Move Right", keySettingManager.getActionDisplayName("moveRight"));
        assertEquals("Soft Drop", keySettingManager.getActionDisplayName("softDrop"));
        assertEquals("Hard Drop", keySettingManager.getActionDisplayName("hardDrop"));
        assertEquals("Rotate CCW", keySettingManager.getActionDisplayName("rotateCounterClockwise"));
        assertEquals("Rotate CW", keySettingManager.getActionDisplayName("rotateClockwise"));
        assertEquals("Hold", keySettingManager.getActionDisplayName("hold"));
        assertEquals("Pause", keySettingManager.getActionDisplayName("pause"));
    }
    
    @Test
    @DisplayName("잘못된 액션의 표시 이름")
    void testGetActionDisplayNameInvalid() {
        String displayName = keySettingManager.getActionDisplayName("invalidAction");
        
        assertEquals("invalidAction", displayName);
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
