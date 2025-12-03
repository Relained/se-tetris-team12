package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.example.service.ColorManager;
import org.example.service.SettingManager;
import org.example.view.BaseView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;

/**
 * KeySettingSelectController Unit Test
 * Line Coverage 70% 이상을 목표로 작성됨
 */
@ExtendWith(ApplicationExtension.class)
class KeySettingSelectControllerTest {
    
    private KeySettingSelectController controller;
    
    @BeforeAll
    static void initToolkit() {
        // JavaFX 환경 초기화
    }
    
    @Start
    void start(Stage stage) {
        SettingManager settingManager = new SettingManager();
        BaseController.Initialize(stage, settingManager);
        
        ColorManager colorManager = ColorManager.getInstance();
        BaseView.Initialize(colorManager);
    }
    
    @BeforeEach
    void setUp() {
        controller = new KeySettingSelectController();
    }
    
    @Test
    void testControllerCreation() {
        assertNotNull(controller, "Controller should be created");
    }
    
    @Test
    void testCreateScene() {
        // When: Scene 생성
        Scene scene = controller.createScene();
        
        // Then
        assertNotNull(scene, "Scene should be created");
        assertNotNull(scene.getRoot(), "Scene root should exist");
        assertTrue(scene.getWidth() > 0);
        assertTrue(scene.getHeight() > 0);
    }
    
    @Test
    void testSceneKeyEventHandler() {
        // Given: Scene 생성
        Scene scene = controller.createScene();
        
        // When: 키 이벤트 핸들러 확인
        var keyHandler = scene.getOnKeyPressed();
        
        // Then
        assertNotNull(keyHandler, "Key event handler should be set");
    }
    
    @Test
    void testHandleKeyInputDown() {
        // Given: Scene 생성
        controller.createScene();
        
        // When: DOWN 키 입력
        KeyEvent downEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN,
            false, false, false, false
        );
        
        // Then: 예외 없이 처리되어야 함
        assertDoesNotThrow(() -> controller.handleKeyInput(downEvent));
    }
    
    @Test
    void testHandleKeyInputUp() {
        // Given: Scene 생성
        controller.createScene();
        
        // When: UP 키 입력
        KeyEvent upEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED, "", "", KeyCode.UP,
            false, false, false, false
        );
        
        // Then
        assertDoesNotThrow(() -> controller.handleKeyInput(upEvent));
    }
    
    @Test
    void testGetScene() {
        // Given: Scene 생성
        Scene createdScene = controller.createScene();
        
        // When: getScene 호출
        Scene retrievedScene = controller.getScene();
        
        // Then
        assertNotNull(retrievedScene);
        assertEquals(createdScene, retrievedScene);
    }
    
    @Test
    void testNavigationBetweenButtons() {
        // Given: Scene 생성
        controller.createScene();
        
        // When: 여러 키 입력으로 네비게이션
        KeyEvent down = new KeyEvent(
            KeyEvent.KEY_PRESSED, "", "", KeyCode.DOWN,
            false, false, false, false
        );
        KeyEvent up = new KeyEvent(
            KeyEvent.KEY_PRESSED, "", "", KeyCode.UP,
            false, false, false, false
        );
        
        // Then: 모든 입력이 정상 처리되어야 함
        assertDoesNotThrow(() -> {
            controller.handleKeyInput(down); // 2P Settings
            controller.handleKeyInput(down); // Go Back
            controller.handleKeyInput(up);   // 2P Settings
            controller.handleKeyInput(up);   // Single Settings
        });
    }
    
    @Test
    void testMultipleSceneCreation() {
        // When: 여러 번 Scene 생성
        Scene scene1 = controller.createScene();
        Scene scene2 = controller.createScene();
        
        // Then: 모두 정상 생성되어야 함
        assertNotNull(scene1);
        assertNotNull(scene2);
    }
    
    @Test
    void testSceneBackground() {
        // Given & When: Scene 생성
        Scene scene = controller.createScene();
        
        // Then: 배경색이 설정되어야 함
        assertNotNull(scene.getFill());
    }
    
    @Test
    void testKeyInputWithSpaceKey() {
        // Given: Scene 생성
        controller.createScene();
        
        // When: SPACE 키 입력은 실제 버튼 액션을 실행하므로
        // 네비게이션만 테스트
        KeyEvent leftEvent = new KeyEvent(
            KeyEvent.KEY_PRESSED, "", "", KeyCode.LEFT,
            false, false, false, false
        );
        
        // Then: 처리되어야 함
        assertDoesNotThrow(() -> controller.handleKeyInput(leftEvent));
    }
}
