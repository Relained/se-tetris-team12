package org.example.view;

import javafx.application.Platform;
import javafx.scene.layout.VBox;
import org.example.model.SettingData.ColorBlindMode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;

class ColorSettingViewTest extends ApplicationTest {
    
    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // 이미 초기화된 경우 무시
        }
    }
    
    @Test
    @DisplayName("ColorSettingView 생성자 테스트")
    void testConstructor() {
        Platform.runLater(() -> {
            ColorSettingView view = new ColorSettingView();
            assertNotNull(view);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - DEFAULT 모드로 UI 생성")
    void testCreateViewWithDefault() {
        Platform.runLater(() -> {
            ColorSettingView view = new ColorSettingView();
            boolean[] defaultCalled = {false};
            
            VBox root = view.createView(
                ColorBlindMode.Default,
                () -> defaultCalled[0] = true,
                () -> {},
                () -> {},
                () -> {},
                () -> {}
            );
            
            assertNotNull(root);
            assertFalse(defaultCalled[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - PROTANOPIA 모드로 UI 생성")
    void testCreateViewWithProtanopia() {
        Platform.runLater(() -> {
            ColorSettingView view = new ColorSettingView();
            boolean[] protanopiaCalled = {false};
            
            VBox root = view.createView(
                ColorBlindMode.PROTANOPIA,
                () -> {},
                () -> protanopiaCalled[0] = true,
                () -> {},
                () -> {},
                () -> {}
            );
            
            assertNotNull(root);
            assertFalse(protanopiaCalled[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - DEUTERANOPIA 모드로 UI 생성")
    void testCreateViewWithDeuteranopia() {
        Platform.runLater(() -> {
            ColorSettingView view = new ColorSettingView();
            boolean[] deuteranopiaCalled = {false};
            
            VBox root = view.createView(
                ColorBlindMode.DEUTERANOPIA,
                () -> {},
                () -> {},
                () -> deuteranopiaCalled[0] = true,
                () -> {},
                () -> {}
            );
            
            assertNotNull(root);
            assertFalse(deuteranopiaCalled[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - TRITANOPIA 모드로 UI 생성")
    void testCreateViewWithTritanopia() {
        Platform.runLater(() -> {
            ColorSettingView view = new ColorSettingView();
            boolean[] tritanopiaCalled = {false};
            
            VBox root = view.createView(
                ColorBlindMode.TRITANOPIA,
                () -> {},
                () -> {},
                () -> {},
                () -> tritanopiaCalled[0] = true,
                () -> {}
            );
            
            assertNotNull(root);
            assertFalse(tritanopiaCalled[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateCurrentMode - DEFAULT 업데이트")
    void testUpdateCurrentModeDefault() {
        Platform.runLater(() -> {
            ColorSettingView view = new ColorSettingView();
            view.createView(ColorBlindMode.PROTANOPIA, () -> {}, () -> {}, () -> {}, () -> {}, () -> {});
            
            assertDoesNotThrow(() -> view.updateCurrentMode(ColorBlindMode.Default));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateCurrentMode - PROTANOPIA 업데이트")
    void testUpdateCurrentModeProtanopia() {
        Platform.runLater(() -> {
            ColorSettingView view = new ColorSettingView();
            view.createView(ColorBlindMode.Default, () -> {}, () -> {}, () -> {}, () -> {}, () -> {});
            
            assertDoesNotThrow(() -> view.updateCurrentMode(ColorBlindMode.PROTANOPIA));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateCurrentMode - DEUTERANOPIA 업데이트")
    void testUpdateCurrentModeDeuteranopia() {
        Platform.runLater(() -> {
            ColorSettingView view = new ColorSettingView();
            view.createView(ColorBlindMode.Default, () -> {}, () -> {}, () -> {}, () -> {}, () -> {});
            
            assertDoesNotThrow(() -> view.updateCurrentMode(ColorBlindMode.DEUTERANOPIA));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateCurrentMode - TRITANOPIA 업데이트")
    void testUpdateCurrentModeTritanopia() {
        Platform.runLater(() -> {
            ColorSettingView view = new ColorSettingView();
            view.createView(ColorBlindMode.Default, () -> {}, () -> {}, () -> {}, () -> {}, () -> {});
            
            assertDoesNotThrow(() -> view.updateCurrentMode(ColorBlindMode.TRITANOPIA));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateCurrentMode - null title 테스트")
    void testUpdateCurrentModeWithNullTitle() {
        Platform.runLater(() -> {
            ColorSettingView view = new ColorSettingView();
            // createView를 호출하지 않아 title이 null인 상태
            
            // null title일 때 분기 커버
            assertDoesNotThrow(() -> view.updateCurrentMode(ColorBlindMode.PROTANOPIA));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("모든 버튼 콜백 테스트")
    void testAllButtonCallbacks() {
        Platform.runLater(() -> {
            ColorSettingView view = new ColorSettingView();
            boolean[] defaultCalled = {false};
            boolean[] protanopiaCalled = {false};
            boolean[] deuteranopiaCalled = {false};
            boolean[] tritanopiaCalled = {false};
            boolean[] backCalled = {false};
            
            view.createView(
                ColorBlindMode.Default,
                () -> defaultCalled[0] = true,
                () -> protanopiaCalled[0] = true,
                () -> deuteranopiaCalled[0] = true,
                () -> tritanopiaCalled[0] = true,
                () -> backCalled[0] = true
            );
            
            // 모든 콜백이 초기에는 호출되지 않아야 함
            assertFalse(defaultCalled[0]);
            assertFalse(protanopiaCalled[0]);
            assertFalse(deuteranopiaCalled[0]);
            assertFalse(tritanopiaCalled[0]);
            assertFalse(backCalled[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("NavigableButtonSystem 사용 테스트")
    void testNavigableButtonSystem() {
        Platform.runLater(() -> {
            ColorSettingView view = new ColorSettingView();
            VBox root = view.createView(
                ColorBlindMode.Default,
                () -> {},
                () -> {},
                () -> {},
                () -> {},
                () -> {}
            );
            
            // buttonSystem이 사용되는지 확인 (5개 버튼 생성)
            assertNotNull(root);
            assertTrue(root.getChildren().size() >= 6); // title + 5 buttons
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
}
