package org.example.state;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.service.SettingManager;
import org.example.service.StateManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameOverStateTest {
    
    private StateManager stateManager;
    private GameOverState state;
    
    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // 이미 초기화된 경우 무시
        }
    }
    
    @BeforeEach
    void setUp() {
        Stage stage = mock(Stage.class);
        SettingManager settingManager = new SettingManager();
        stateManager = new StateManager(stage, settingManager);
        state = new GameOverState(stateManager);
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
        Platform.runLater(() -> {
            state.enter();
            Scene scene = state.createScene();
            
            assertNotNull(scene);
            assertNotNull(state.getScene());
        });
    }
}
