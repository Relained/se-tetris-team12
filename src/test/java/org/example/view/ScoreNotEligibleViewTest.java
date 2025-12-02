package org.example.view;

import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.service.ColorManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ScoreNotEligibleView 클래스의 Unit Test
 */
@ExtendWith(ApplicationExtension.class)
class ScoreNotEligibleViewTest {
    
    private ScoreNotEligibleView view;
    
    @Start
    private void start(Stage stage) {
        ColorManager colorManager = ColorManager.getInstance();
        BaseView.Initialize(colorManager);
    }
    
    @BeforeEach
    void setUp() {
        view = new ScoreNotEligibleView();
    }
    
    @Test
    void testCreateView() {
        StackPane root = view.createView(5000, () -> {});
        
        assertNotNull(root);
        assertNotNull(root.getChildren());
        assertTrue(root.getChildren().size() > 0);
    }
    
    @Test
    void testViewExtendsBaseView() {
        assertTrue(view instanceof BaseView);
    }
    
    @Test
    void testGetButtonSystem() {
        view.createView(5000, () -> {});
        
        assertNotNull(view.getButtonSystem());
    }
    
    @Test
    void testRootAlignment() {
        StackPane root = view.createView(5000, () -> {});
        
        assertNotNull(root.getAlignment());
    }
    
    @Test
    void testRootBackground() {
        StackPane root = view.createView(5000, () -> {});
        
        assertNotNull(root.getBackground());
    }
    
    @Test
    void testCreateViewWithZeroScore() {
        StackPane root = view.createView(0, () -> {});
        
        assertNotNull(root);
    }
    
    @Test
    void testCreateViewWithHighScore() {
        StackPane root = view.createView(999999, () -> {});
        
        assertNotNull(root);
    }
    
    @Test
    void testMultipleViewCreations() {
        StackPane root1 = view.createView(5000, () -> {});
        StackPane root2 = view.createView(3000, () -> {});
        
        assertNotNull(root1);
        assertNotNull(root2);
        assertNotSame(root1, root2);
    }
    
    @Test
    void testButtonSystemNotNull() {
        view.createView(5000, () -> {});
        
        assertNotNull(view.getButtonSystem());
    }
}
