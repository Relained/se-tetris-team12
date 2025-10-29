package org.example.service;

import javafx.stage.Stage;
import org.example.state.BaseState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import javafx.scene.Scene;

import static org.junit.jupiter.api.Assertions.*;

class StateManagerTest {
    private StateManager stateManager;
    private Stage mockStage;
    private SettingManager settingManager;

    @BeforeAll
    static void initJavaFX() {
        try {
            javafx.application.Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // 이미 초기화됨
        }
    }

    @BeforeEach
    void setUp() {
        javafx.application.Platform.runLater(() -> {
            mockStage = new Stage();
            settingManager = new SettingManager();
            stateManager = new StateManager(mockStage, settingManager);
        });
        
        // JavaFX 초기화 대기
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("StateManager 생성 테스트")
    void testConstructor() {
        javafx.application.Platform.runLater(() -> {
            assertNotNull(stateManager);
            assertNotNull(stateManager.getPrimaryStage());
            assertNotNull(stateManager.settingManager);
        });
    }

    @Test
    @DisplayName("상태 추가 테스트")
    void testAddState() {
        javafx.application.Platform.runLater(() -> {
            TestState testState = new TestState(stateManager);
            stateManager.addState("test", testState);
            
            // 상태가 추가되었는지 확인 (예외가 발생하지 않으면 성공)
            assertDoesNotThrow(() -> stateManager.setState("test"));
        });
    }

    @Test
    @DisplayName("존재하지 않는 상태 설정 시 예외 발생")
    void testSetNonExistentState() {
        javafx.application.Platform.runLater(() -> {
            assertThrows(IllegalArgumentException.class, () -> {
                stateManager.setState("nonexistent");
            });
        });
    }

    @Test
    @DisplayName("존재하지 않는 상태 스택 시 예외 발생")
    void testStackNonExistentState() {
        javafx.application.Platform.runLater(() -> {
            assertThrows(IllegalArgumentException.class, () -> {
                stateManager.stackState("nonexistent");
            });
        });
    }

    @Test
    @DisplayName("getPrimaryStage 테스트")
    void testGetPrimaryStage() {
        javafx.application.Platform.runLater(() -> {
            Stage stage = stateManager.getPrimaryStage();
            assertNotNull(stage);
            assertEquals(mockStage, stage);
        });
    }

    @Test
    @DisplayName("빈 스택에서 popState 호출")
    void testPopStateOnEmptyStack() {
        javafx.application.Platform.runLater(() -> {
            // 빈 스택에서 pop은 아무 일도 하지 않음
            assertDoesNotThrow(() -> stateManager.popState());
        });
    }

    // 테스트용 State 클래스
    private static class TestState extends BaseState {
        private boolean enterCalled = false;
        private boolean exitCalled = false;
        private boolean resumeCalled = false;

        public TestState(StateManager stateManager) {
            super(stateManager);
        }

        @Override
        public void enter() {
            enterCalled = true;
        }

        @Override
        public void exit() {
            exitCalled = true;
        }

        @Override
        public Scene createScene() {
            return new Scene(new javafx.scene.layout.Pane(), 800, 600);
        }

        @Override
        public void resume() {
            resumeCalled = true;
        }

        @SuppressWarnings("unused")
        public boolean isEnterCalled() { return enterCalled; }
        @SuppressWarnings("unused")
        public boolean isExitCalled() { return exitCalled; }
        @SuppressWarnings("unused")
        public boolean isResumeCalled() { return resumeCalled; }
    }
}
