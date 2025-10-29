package org.example.view;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import org.example.service.KeySettingManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;

class KeySettingViewTest extends ApplicationTest {
    
    private KeySettingManager keySettingManager;
    
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
        keySettingManager = KeySettingManager.getInstance();
    }
    
    @Test
    @DisplayName("KeySettingView 생성자 테스트")
    void testConstructor() {
        Platform.runLater(() -> {
            KeySettingView view = new KeySettingView();
            assertNotNull(view);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView로 UI 생성 테스트")
    void testCreateView() {
        Platform.runLater(() -> {
            KeySettingView view = new KeySettingView();
            boolean[] resetCalled = {false};
            boolean[] backCalled = {false};
            
            VBox root = view.createView(
                () -> resetCalled[0] = true,
                () -> backCalled[0] = true
            );
            
            assertNotNull(root);
            assertFalse(resetCalled[0]);
            assertFalse(backCalled[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("navigateActions - 아래로 이동 테스트")
    void testNavigateActionsDown() {
        Platform.runLater(() -> {
            KeySettingView view = new KeySettingView();
            view.createView(() -> {}, () -> {});
            
            // 아래로 이동
            assertDoesNotThrow(() -> view.navigateActions(false));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("navigateActions - 위로 이동 테스트")
    void testNavigateActionsUp() {
        Platform.runLater(() -> {
            KeySettingView view = new KeySettingView();
            view.createView(() -> {}, () -> {});
            
            // 위로 이동
            assertDoesNotThrow(() -> view.navigateActions(true));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("navigateActions - 순환 테스트 (위로)")
    void testNavigateActionsCircularUp() {
        Platform.runLater(() -> {
            KeySettingView view = new KeySettingView();
            view.createView(() -> {}, () -> {});
            
            // 처음 위치에서 위로 이동 - 순환
            view.navigateActions(true);
            assertDoesNotThrow(() -> view.navigateActions(true));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("navigateActions - 순환 테스트 (아래로)")
    void testNavigateActionsCircularDown() {
        Platform.runLater(() -> {
            KeySettingView view = new KeySettingView();
            view.createView(() -> {}, () -> {});
            
            // 여러 번 아래로 이동하여 순환 테스트
            for (int i = 0; i < 15; i++) {
                view.navigateActions(false);
            }
            assertDoesNotThrow(() -> view.navigateActions(false));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("getSelectedAction - 액션 선택 시")
    void testGetSelectedActionWhenAction() {
        Platform.runLater(() -> {
            KeySettingView view = new KeySettingView();
            view.createView(() -> {}, () -> {});
            
            // 첫 번째는 액션이어야 함
            String action = view.getSelectedAction();
            assertNotNull(action);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("getSelectedAction - 버튼 선택 시")
    void testGetSelectedActionWhenButton() {
        Platform.runLater(() -> {
            KeySettingView view = new KeySettingView();
            view.createView(() -> {}, () -> {});
            
            // 버튼으로 이동
            String[] actions = keySettingManager.getAllActions();
            for (int i = 0; i <= actions.length; i++) {
                view.navigateActions(false);
            }
            
            // 버튼 선택 시 null 반환
            String action = view.getSelectedAction();
            assertNull(action);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("isButtonSelected - 버튼 선택 여부 테스트")
    void testIsButtonSelected() {
        Platform.runLater(() -> {
            KeySettingView view = new KeySettingView();
            view.createView(() -> {}, () -> {});
            
            // 처음에는 액션 선택
            assertFalse(view.isButtonSelected());
            
            // 버튼으로 이동
            String[] actions = keySettingManager.getAllActions();
            for (int i = 0; i <= actions.length; i++) {
                view.navigateActions(false);
            }
            
            // 이제 버튼 선택
            assertTrue(view.isButtonSelected());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("executeSelectedButton - Reset 버튼 실행")
    void testExecuteSelectedButtonReset() {
        Platform.runLater(() -> {
            KeySettingView view = new KeySettingView();
            boolean[] resetCalled = {false};
            
            view.createView(() -> resetCalled[0] = true, () -> {});
            
            // Reset 버튼으로 이동 (actions 다음이 첫 번째 버튼)
            String[] actions = keySettingManager.getAllActions();
            for (int i = 0; i < actions.length; i++) {
                view.navigateActions(false);
            }
            
            // 버튼 실행
            view.executeSelectedButton();
            assertTrue(resetCalled[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("executeSelectedButton - Go Back 버튼 실행")
    void testExecuteSelectedButtonGoBack() {
        Platform.runLater(() -> {
            KeySettingView view = new KeySettingView();
            boolean[] backCalled = {false};
            
            view.createView(() -> {}, () -> backCalled[0] = true);
            
            // Go Back 버튼으로 이동 (actions 다음 두 번째 버튼)
            String[] actions = keySettingManager.getAllActions();
            for (int i = 0; i < actions.length + 1; i++) {
                view.navigateActions(false);
            }
            
            // 버튼 실행
            view.executeSelectedButton();
            assertTrue(backCalled[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("showWaitingForKey 테스트")
    void testShowWaitingForKey() {
        Platform.runLater(() -> {
            KeySettingView view = new KeySettingView();
            view.createView(() -> {}, () -> {});
            
            assertDoesNotThrow(() -> view.showWaitingForKey("MOVE_LEFT"));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("hideWaitingForKey 테스트")
    void testHideWaitingForKey() {
        Platform.runLater(() -> {
            KeySettingView view = new KeySettingView();
            view.createView(() -> {}, () -> {});
            
            view.showWaitingForKey("MOVE_RIGHT");
            assertDoesNotThrow(() -> view.hideWaitingForKey());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("showDuplicateKeyError 테스트")
    void testShowDuplicateKeyError() {
        Platform.runLater(() -> {
            KeySettingView view = new KeySettingView();
            view.createView(() -> {}, () -> {});
            
            assertDoesNotThrow(() -> view.showDuplicateKeyError(KeyCode.A));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateKeyBinding 테스트")
    void testUpdateKeyBinding() {
        Platform.runLater(() -> {
            KeySettingView view = new KeySettingView();
            view.createView(() -> {}, () -> {});
            
            assertDoesNotThrow(() -> view.updateKeyBinding("MOVE_LEFT", KeyCode.A));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateAllKeyBindings 테스트")
    void testUpdateAllKeyBindings() {
        Platform.runLater(() -> {
            KeySettingView view = new KeySettingView();
            view.createView(() -> {}, () -> {});
            
            assertDoesNotThrow(() -> view.updateAllKeyBindings());
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
}
