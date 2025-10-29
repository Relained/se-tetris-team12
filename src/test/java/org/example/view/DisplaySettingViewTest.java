package org.example.view;

import javafx.application.Platform;
import javafx.scene.layout.VBox;
import org.example.model.SettingData.ScreenSize;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;

class DisplaySettingViewTest extends ApplicationTest {
    
    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // 이미 초기화된 경우 무시
        }
    }
    
    @Test
    @DisplayName("DisplaySettingView 생성자 테스트")
    void testConstructor() {
        Platform.runLater(() -> {
            DisplaySettingView view = new DisplaySettingView();
            assertNotNull(view);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - SMALL 크기로 UI 생성")
    void testCreateViewWithSmall() {
        Platform.runLater(() -> {
            DisplaySettingView view = new DisplaySettingView();
            boolean[] smallCalled = {false};
            
            VBox root = view.createView(
                ScreenSize.SMALL,
                () -> smallCalled[0] = true,
                () -> {},
                () -> {},
                () -> {}
            );
            
            assertNotNull(root);
            assertFalse(smallCalled[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - MEDIUM 크기로 UI 생성")
    void testCreateViewWithMedium() {
        Platform.runLater(() -> {
            DisplaySettingView view = new DisplaySettingView();
            boolean[] mediumCalled = {false};
            
            VBox root = view.createView(
                ScreenSize.MEDIUM,
                () -> {},
                () -> mediumCalled[0] = true,
                () -> {},
                () -> {}
            );
            
            assertNotNull(root);
            assertFalse(mediumCalled[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("createView - LARGE 크기로 UI 생성")
    void testCreateViewWithLarge() {
        Platform.runLater(() -> {
            DisplaySettingView view = new DisplaySettingView();
            boolean[] largeCalled = {false};
            
            VBox root = view.createView(
                ScreenSize.LARGE,
                () -> {},
                () -> {},
                () -> largeCalled[0] = true,
                () -> {}
            );
            
            assertNotNull(root);
            assertFalse(largeCalled[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateCurrentSize - SMALL 업데이트")
    void testUpdateCurrentSizeSmall() {
        Platform.runLater(() -> {
            DisplaySettingView view = new DisplaySettingView();
            view.createView(ScreenSize.MEDIUM, () -> {}, () -> {}, () -> {}, () -> {});
            
            assertDoesNotThrow(() -> view.updateCurrentSize(ScreenSize.SMALL));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateCurrentSize - MEDIUM 업데이트")
    void testUpdateCurrentSizeMedium() {
        Platform.runLater(() -> {
            DisplaySettingView view = new DisplaySettingView();
            view.createView(ScreenSize.SMALL, () -> {}, () -> {}, () -> {}, () -> {});
            
            assertDoesNotThrow(() -> view.updateCurrentSize(ScreenSize.MEDIUM));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateCurrentSize - LARGE 업데이트")
    void testUpdateCurrentSizeLarge() {
        Platform.runLater(() -> {
            DisplaySettingView view = new DisplaySettingView();
            view.createView(ScreenSize.MEDIUM, () -> {}, () -> {}, () -> {}, () -> {});
            
            assertDoesNotThrow(() -> view.updateCurrentSize(ScreenSize.LARGE));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("updateCurrentSize - null title 테스트")
    void testUpdateCurrentSizeWithNullTitle() {
        Platform.runLater(() -> {
            DisplaySettingView view = new DisplaySettingView();
            // createView를 호출하지 않아 title이 null인 상태
            
            // null title일 때 분기 커버
            assertDoesNotThrow(() -> view.updateCurrentSize(ScreenSize.LARGE));
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("모든 버튼 콜백 테스트")
    void testAllButtonCallbacks() {
        Platform.runLater(() -> {
            DisplaySettingView view = new DisplaySettingView();
            boolean[] smallCalled = {false};
            boolean[] mediumCalled = {false};
            boolean[] largeCalled = {false};
            boolean[] backCalled = {false};
            
            view.createView(
                ScreenSize.MEDIUM,
                () -> smallCalled[0] = true,
                () -> mediumCalled[0] = true,
                () -> largeCalled[0] = true,
                () -> backCalled[0] = true
            );
            
            // 모든 콜백이 초기에는 호출되지 않아야 함
            assertFalse(smallCalled[0]);
            assertFalse(mediumCalled[0]);
            assertFalse(largeCalled[0]);
            assertFalse(backCalled[0]);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    @DisplayName("NavigableButtonSystem 사용 테스트")
    void testNavigableButtonSystem() {
        Platform.runLater(() -> {
            DisplaySettingView view = new DisplaySettingView();
            VBox root = view.createView(
                ScreenSize.MEDIUM,
                () -> {},
                () -> {},
                () -> {},
                () -> {}
            );
            
            // buttonSystem이 사용되는지 확인 (4개 버튼 생성)
            assertNotNull(root);
            assertTrue(root.getChildren().size() >= 5); // title + 4 buttons
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
}
