package org.example.view;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.model.SettingData.ScreenSize;
import org.example.service.ColorManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DisplaySettingView 클래스의 Unit Test
 */
@ExtendWith(ApplicationExtension.class)
class DisplaySettingViewTest {
    
    private DisplaySettingView view;
    
    @Start
    private void start(Stage stage) {
        ColorManager colorManager = ColorManager.getInstance();
        BaseView.Initialize(colorManager);
    }
    
    @BeforeEach
    void setUp() {
        view = new DisplaySettingView();
    }
    
    @Test
    void testCreateViewWithSmallSize() {
        VBox root = view.createView(
            ScreenSize.SMALL,
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
    void testCreateViewWithMediumSize() {
        VBox root = view.createView(
            ScreenSize.MEDIUM,
            () -> {},
            () -> {},
            () -> {},
            () -> {}
        );
        
        assertNotNull(root);
    }
    
    @Test
    void testCreateViewWithLargeSize() {
        VBox root = view.createView(
            ScreenSize.LARGE,
            () -> {},
            () -> {},
            () -> {},
            () -> {}
        );
        
        assertNotNull(root);
    }
    
    @Test
    void testUpdateCurrentSize() {
        view.createView(
            ScreenSize.SMALL,
            () -> {},
            () -> {},
            () -> {},
            () -> {}
        );
        
        assertDoesNotThrow(() -> view.updateCurrentSize(ScreenSize.MEDIUM));
        assertDoesNotThrow(() -> view.updateCurrentSize(ScreenSize.LARGE));
        assertDoesNotThrow(() -> view.updateCurrentSize(ScreenSize.SMALL));
    }
    
    @Test
    void testViewExtendsBaseView() {
        assertTrue(view instanceof BaseView);
    }
    
    @Test
    void testGetButtonSystem() {
        view.createView(
            ScreenSize.MEDIUM,
            () -> {},
            () -> {},
            () -> {},
            () -> {}
        );
        
        assertNotNull(view.getButtonSystem());
    }
    
    @Test
    void testCallbacksAreNotNull() {
        boolean[] called = {false, false, false, false};
        
        VBox root = view.createView(
            ScreenSize.MEDIUM,
            () -> called[0] = true,
            () -> called[1] = true,
            () -> called[2] = true,
            () -> called[3] = true
        );
        
        assertNotNull(root);
    }
    
    @Test
    void testRootAlignment() {
        VBox root = view.createView(
            ScreenSize.MEDIUM,
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
            ScreenSize.MEDIUM,
            () -> {},
            () -> {},
            () -> {},
            () -> {}
        );
        
        assertNotNull(root.getBackground());
    }
    
    @Test
    void testUpdateCurrentSizeBeforeViewCreation() {
        assertDoesNotThrow(() -> view.updateCurrentSize(ScreenSize.LARGE));
    }
    
    @Test
    void testMultipleViewCreations() {
        VBox root1 = view.createView(
            ScreenSize.SMALL,
            () -> {},
            () -> {},
            () -> {},
            () -> {}
        );
        
        VBox root2 = view.createView(
            ScreenSize.LARGE,
            () -> {},
            () -> {},
            () -> {},
            () -> {}
        );
        
        assertNotNull(root1);
        assertNotNull(root2);
    }
    
    @Test
    void testAllScreenSizes() {
        for (ScreenSize size : ScreenSize.values()) {
            VBox root = view.createView(
                size,
                () -> {},
                () -> {},
                () -> {},
                () -> {}
            );
            
            assertNotNull(root, "Root should not be null for size: " + size);
        }
    }
}
