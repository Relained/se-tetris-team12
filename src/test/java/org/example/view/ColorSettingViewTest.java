package org.example.view;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.model.SettingData.ColorBlindMode;
import org.example.service.ColorManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ColorSettingView 클래스의 Unit Test
 */
@ExtendWith(ApplicationExtension.class)
class ColorSettingViewTest {
    
    private ColorSettingView view;
    
    @Start
    private void start(Stage stage) {
        ColorManager colorManager = ColorManager.getInstance();
        BaseView.Initialize(colorManager);
    }
    
    @BeforeEach
    void setUp() {
        WaitForAsyncUtils.waitForFxEvents();
        view = new ColorSettingView();
    }
    
    @Test
    void testCreateViewWithDefaultMode() {
        VBox root = view.createView(
            ColorBlindMode.Default,
            () -> {},
            () -> {},
            () -> {},
            () -> {},
            () -> {}
        );
        
        assertNotNull(root);
        assertNotNull(root.getChildren());
        assertTrue(root.getChildren().size() > 0);
    }
    
    @Test
    void testCreateViewWithProtanopiaMode() {
        VBox root = view.createView(
            ColorBlindMode.PROTANOPIA,
            () -> {},
            () -> {},
            () -> {},
            () -> {},
            () -> {}
        );
        
        assertNotNull(root);
    }
    
    @Test
    void testCreateViewWithDeuteranopiaMode() {
        VBox root = view.createView(
            ColorBlindMode.DEUTERANOPIA,
            () -> {},
            () -> {},
            () -> {},
            () -> {},
            () -> {}
        );
        
        assertNotNull(root);
    }
    
    @Test
    void testCreateViewWithTritanopiaMode() {
        VBox root = view.createView(
            ColorBlindMode.TRITANOPIA,
            () -> {},
            () -> {},
            () -> {},
            () -> {},
            () -> {}
        );
        
        assertNotNull(root);
    }
    
    @Test
    void testUpdateCurrentMode() {
        view.createView(
            ColorBlindMode.Default,
            () -> {},
            () -> {},
            () -> {},
            () -> {},
            () -> {}
        );
        
        assertDoesNotThrow(() -> view.updateCurrentMode(ColorBlindMode.PROTANOPIA));
        assertDoesNotThrow(() -> view.updateCurrentMode(ColorBlindMode.DEUTERANOPIA));
        assertDoesNotThrow(() -> view.updateCurrentMode(ColorBlindMode.TRITANOPIA));
        assertDoesNotThrow(() -> view.updateCurrentMode(ColorBlindMode.Default));
    }
    
    @Test
    void testViewExtendsBaseView() {
        assertTrue(view instanceof BaseView);
    }
    
    @Test
    void testGetButtonSystem() {
        view.createView(
            ColorBlindMode.Default,
            () -> {},
            () -> {},
            () -> {},
            () -> {},
            () -> {}
        );
        
        assertNotNull(view.getButtonSystem());
    }
    
    @Test
    void testCallbacksAreNotNull() {
        boolean[] called = {false, false, false, false, false};
        
        VBox root = view.createView(
            ColorBlindMode.Default,
            () -> called[0] = true,
            () -> called[1] = true,
            () -> called[2] = true,
            () -> called[3] = true,
            () -> called[4] = true
        );
        
        assertNotNull(root);
    }
    
    @Test
    void testRootAlignment() {
        VBox root = view.createView(
            ColorBlindMode.Default,
            () -> {},
            () -> {},
            () -> {},
            () -> {},
            () -> {}
        );
        
        assertNotNull(root.getAlignment());
    }
    
    @Test
    void testRootBackground() {
        VBox root = view.createView(
            ColorBlindMode.Default,
            () -> {},
            () -> {},
            () -> {},
            () -> {},
            () -> {}
        );
        
        assertTrue(root.getStyleClass().contains("root-dark"));
    }
    
    @Test
    void testUpdateCurrentModeBeforeViewCreation() {
        // View가 생성되기 전에 updateCurrentMode 호출 시 예외가 발생하지 않아야 함
        assertDoesNotThrow(() -> view.updateCurrentMode(ColorBlindMode.PROTANOPIA));
    }
    
    @Test
    void testMultipleViewCreations() {
        VBox root1 = view.createView(
            ColorBlindMode.Default,
            () -> {},
            () -> {},
            () -> {},
            () -> {},
            () -> {}
        );
        
        VBox root2 = view.createView(
            ColorBlindMode.PROTANOPIA,
            () -> {},
            () -> {},
            () -> {},
            () -> {},
            () -> {}
        );
        
        assertNotNull(root1);
        assertNotNull(root2);
    }
    
    @Test
    void testAllColorBlindModes() {
        for (ColorBlindMode mode : ColorBlindMode.values()) {
            VBox root = view.createView(
                mode,
                () -> {},
                () -> {},
                () -> {},
                () -> {},
                () -> {}
            );
            
            assertNotNull(root, "Root should not be null for mode: " + mode);
        }
    }
}
