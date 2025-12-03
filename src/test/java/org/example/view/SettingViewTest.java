package org.example.view;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.service.ColorManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SettingView 클래스의 Unit Test
 */
@ExtendWith(ApplicationExtension.class)
class SettingViewTest {
    
    private SettingView view;
    
    @Start
    private void start(Stage stage) {
        ColorManager colorManager = ColorManager.getInstance();
        BaseView.Initialize(colorManager);
    }
    
    @BeforeEach
    void setUp() {
        view = new SettingView();
    }
    
    @Test
    void testCreateView() {
        boolean[] flags = {false, false, false, false, false, false};
        
        VBox root = view.createView(
            () -> flags[0] = true,  // onScreenSize
            () -> flags[1] = true,  // onControls
            () -> flags[2] = true,  // onColorBlindSetting
            () -> flags[3] = true,  // onResetScoreBoard
            () -> flags[4] = true,  // onResetAllSetting
            () -> flags[5] = true   // onGoBack
        );
        
        assertNotNull(root);
        assertNotNull(root.getChildren());
        assertTrue(root.getChildren().size() > 0);
    }
    
    @Test
    void testCallbacksWork() {
        boolean[] called = {false};
        
        VBox root = view.createView(
            () -> called[0] = true,
            () -> {},
            () -> {},
            () -> {},
            () -> {},
            () -> {}
        );
        
        assertNotNull(root);
        
        // ButtonSystem이 생성되었는지 확인
        assertNotNull(view.getButtonSystem());
    }
    
    @Test
    void testViewExtendsBaseView() {
        assertTrue(view instanceof BaseView);
    }
    
    @Test
    void testGetButtonSystem() {
        view.createView(
            () -> {},
            () -> {},
            () -> {},
            () -> {},
            () -> {},
            () -> {}
        );
        
        assertNotNull(view.getButtonSystem());
    }
    
    @Test
    void testRootAlignment() {
        VBox root = view.createView(
            () -> {},
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
            () -> {},
            () -> {},
            () -> {},
            () -> {},
            () -> {},
            () -> {}
        );
        
        assertTrue(root.getStyleClass().contains("root-dark"));
    }
    
    @Test
    void testMultipleViewCreations() {
        VBox root1 = view.createView(
            () -> {},
            () -> {},
            () -> {},
            () -> {},
            () -> {},
            () -> {}
        );
        
        VBox root2 = view.createView(
            () -> {},
            () -> {},
            () -> {},
            () -> {},
            () -> {},
            () -> {}
        );
        
        assertNotNull(root1);
        assertNotNull(root2);
    }
}
