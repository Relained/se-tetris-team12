package org.example.state;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.service.SettingManager;
import org.example.service.StateManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SettingStateTest extends ApplicationTest {
    
    private StateManager stateManager;
    private SettingState state;
    
    @BeforeEach
    void setUp() {
        Stage stage = mock(Stage.class);
        SettingManager settingManager = new SettingManager();
        stateManager = new StateManager(stage, settingManager);
        state = new SettingState(stateManager);
    }
    
    @Test
    @DisplayName("State 생성 시 null이 아닌지 확인")
    void testStateNotNull() {
        assertNotNull(state);
    }
    
    @Test
    @DisplayName("enter() 호출 시 예외 발생하지 않음")
    void testEnter() {
        assertDoesNotThrow(() -> state.enter());
    }
    
    @Test
    @DisplayName("exit() 호출 시 예외 발생하지 않음")
    void testExit() {
        assertDoesNotThrow(() -> state.exit());
    }
    
    @Test
    @DisplayName("resume() 호출 시 예외 발생하지 않음")
    void testResume() {
        assertDoesNotThrow(() -> state.resume());
    }
    
    @Test
    @DisplayName("createScene() - Scene 생성 확인")
    void testCreateScene() {
        javafx.application.Platform.runLater(() -> {
            state.enter();
            Scene scene = state.createScene();
            
            assertNotNull(scene);
            assertNotNull(state.getScene());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createScene() - Scene 크기")
    void testCreateSceneSize() {
        javafx.application.Platform.runLater(() -> {
            state.enter();
            Scene scene = state.createScene();
            
            assertEquals(1000, scene.getWidth());
            assertEquals(700, scene.getHeight());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createScene() - 키 이벤트 핸들러 설정됨")
    void testCreateSceneKeyHandler() {
        javafx.application.Platform.runLater(() -> {
            state.enter();
            Scene scene = state.createScene();
            
            assertNotNull(scene.getOnKeyPressed());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("enter() - View와 Controller 초기화")
    void testEnterInitializesComponents() {
        state.enter();
        
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                Scene scene = state.createScene();
                assertNotNull(scene);
            });
        });
    }
    
    @Test
    @DisplayName("여러 번 enter() 호출")
    void testMultipleEnter() {
        assertDoesNotThrow(() -> {
            state.enter();
            state.enter();
            state.enter();
        });
    }
    
    @Test
    @DisplayName("enter() 없이 createScene() 호출")
    void testCreateSceneWithoutEnter() {
        javafx.application.Platform.runLater(() -> {
            assertThrows(NullPointerException.class, () -> state.createScene());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("getScene() - Scene 생성 전 null 반환")
    void testGetSceneBeforeCreate() {
        assertNull(state.getScene());
    }
    
    @Test
    @DisplayName("getScene() - Scene 생성 후 반환")
    void testGetSceneAfterCreate() {
        javafx.application.Platform.runLater(() -> {
            state.enter();
            state.createScene();
            
            assertNotNull(state.getScene());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("resume() - enter 후 호출")
    void testResumeAfterEnter() {
        javafx.application.Platform.runLater(() -> {
            state.enter();
            state.createScene();
            
            assertDoesNotThrow(() -> state.resume());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
}