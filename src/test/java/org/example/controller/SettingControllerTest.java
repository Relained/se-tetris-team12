package org.example.controller;

import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.example.service.ColorManager;
import org.example.service.SettingManager;
import org.example.view.BaseView;
import org.example.view.SettingView;
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
import static org.mockito.Mockito.*;

/**
 * SettingController 클래스의 Unit Test with Mockito
 */
@ExtendWith(ApplicationExtension.class)
class SettingControllerTest {
    
    private SettingController controller;
    private SettingManager realSettingManager;
    
    @Mock
    private SettingView mockView;
    
    @Mock
    private NavigableButtonSystem mockButtonSystem;
    
    @Start
    private void start(Stage stage) {
        ColorManager colorManager = ColorManager.getInstance();
        BaseView.Initialize(colorManager);
        
        realSettingManager = new SettingManager();
        BaseController.Initialize(stage, realSettingManager);
    }
    
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = new SettingController();
        
        // Mock 객체를 Controller의 private field에 주입
        Field viewField = SettingController.class.getDeclaredField("settingView");
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
    void testHandleKeyInput() {
        KeyEvent event = mock(KeyEvent.class);
        
        controller.handleKeyInput(event);
        
        verify(mockButtonSystem).handleInput(event);
    }
    
    @Test
    void testHandleScreenSizeMethodExists() {
        assertDoesNotThrow(() -> {
            controller.getClass().getDeclaredMethod("handleScreenSize");
        });
    }
    
    @Test
    void testHandleControlsMethodExists() {
        assertDoesNotThrow(() -> {
            controller.getClass().getDeclaredMethod("handleControls");
        });
    }
    
    @Test
    void testHandleColorBlindSettingMethodExists() {
        assertDoesNotThrow(() -> {
            controller.getClass().getDeclaredMethod("handleColorBlindSetting");
        });
    }
    
    @Test
    void testHandleResetScoreBoardMethodExists() {
        assertDoesNotThrow(() -> {
            controller.getClass().getDeclaredMethod("handleResetScoreBoard");
        });
    }
    
    @Test
    void testHandleResetAllSettingMethodExists() {
        assertDoesNotThrow(() -> {
            controller.getClass().getDeclaredMethod("handleResetAllSetting");
        });
    }
    
    @Test
    void testHandleGoBackMethodExists() {
        assertDoesNotThrow(() -> {
            controller.getClass().getDeclaredMethod("handleGoBack");
        });
    }
    
    @Test
    void testExitMethodExists() throws Exception {
        var method = controller.getClass().getDeclaredMethod("exit");
        method.setAccessible(true);
        assertNotNull(method);
    }
    
    @Test
    void testHandleResetScoreBoard() {
        controller.handleResetScoreBoard();
        
        // SettingManager의 메서드가 호출되는지 확인할 수는 없지만
        // 메서드가 정상적으로 실행되는지 확인
        assertDoesNotThrow(() -> controller.handleResetScoreBoard());
    }
    
    @Test
    void testHandleResetAllSetting() {
        controller.handleResetAllSetting();
        
        // 메서드가 정상적으로 실행되는지 확인
        assertDoesNotThrow(() -> controller.handleResetAllSetting());
    }
    
    @Test
    void testCreateScene() {
        // Given: 실제 SettingController 생성 (mock 없이)
        SettingController realController = new SettingController();
        
        // When: Scene 생성
        var scene = realController.createScene();
        
        // Then: Scene이 생성되어야 함
        assertNotNull(scene);
        assertNotNull(scene.getRoot());
    }
    
    @Test
    void testExit() {
        // Given: 실제 SettingController 생성
        SettingController realController = new SettingController();
        realController.createScene();
        
        // When: exit 메소드 호출 (리플렉션)
        // Then: 예외 없이 처리되어야 함
        assertDoesNotThrow(() -> {
            var exitMethod = realController.getClass().getDeclaredMethod("exit");
            exitMethod.setAccessible(true);
            exitMethod.invoke(realController);
        });
    }
    
    @Test
    void testHandleScreenSizeMethodExists2() {
        // When & Then: handleScreenSize 메소드 존재 확인
        assertDoesNotThrow(() -> {
            var method = controller.getClass().getDeclaredMethod("handleScreenSize");
            assertNotNull(method);
        });
    }
    
    @Test
    void testHandleControlsMethodExists2() {
        // When & Then: handleControls 메소드 존재 확인
        assertDoesNotThrow(() -> {
            var method = controller.getClass().getDeclaredMethod("handleControls");
            assertNotNull(method);
        });
    }
    
    @Test
    void testHandleColorBlindSettingMethodExists2() {
        // When & Then: handleColorBlindSetting 메소드 존재 확인
        assertDoesNotThrow(() -> {
            var method = controller.getClass().getDeclaredMethod("handleColorBlindSetting");
            assertNotNull(method);
        });
    }
    
    @Test
    void testHandleGoBackMethodExists2() {
        // When & Then: handleGoBack 메소드 존재 확인
        assertDoesNotThrow(() -> {
            var method = controller.getClass().getDeclaredMethod("handleGoBack");
            assertNotNull(method);
        });
    }
}
