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
 * ScoreInputView 클래스의 Unit Test
 */
@ExtendWith(ApplicationExtension.class)
class ScoreInputViewTest {
    
    private ScoreInputView view;
    
    @Start
    private void start(Stage stage) {
        ColorManager colorManager = ColorManager.getInstance();
        BaseView.Initialize(colorManager);
    }
    
    @BeforeEach
    void setUp() {
        view = new ScoreInputView();
    }
    
    @Test
    void testCreateView() {
        VBox root = view.createView(
            1,
            10000,
            50,
            5,
            () -> {},
            () -> {}
        );
        
        assertNotNull(root);
        assertNotNull(root.getChildren());
        assertTrue(root.getChildren().size() > 0);
    }
    
    @Test
    void testCreateViewWithDifferentRanks() {
        VBox root1 = view.createView(1, 10000, 50, 5, () -> {}, () -> {});
        assertNotNull(root1);
        
        VBox root5 = view.createView(5, 5000, 25, 3, () -> {}, () -> {});
        assertNotNull(root5);
        
        VBox root10 = view.createView(10, 1000, 10, 1, () -> {}, () -> {});
        assertNotNull(root10);
    }
    
    @Test
    void testViewExtendsBaseView() {
        assertTrue(view instanceof BaseView);
    }
    
    @Test
    void testGetPlayerName() {
        view.createView(1, 10000, 50, 5, () -> {}, () -> {});
        
        String playerName = view.getPlayerName();
        assertNotNull(playerName);
    }
    
    @Test
    void testFocusNameInput() {
        view.createView(1, 10000, 50, 5, () -> {}, () -> {});
        
        assertDoesNotThrow(() -> view.focusNameInput());
    }
    
    @Test
    void testRootAlignment() {
        VBox root = view.createView(1, 10000, 50, 5, () -> {}, () -> {});
        
        assertNotNull(root.getAlignment());
    }
    
    @Test
    void testRootBackground() {
        VBox root = view.createView(1, 10000, 50, 5, () -> {}, () -> {});
        
        assertTrue(root.getStyleClass().contains("score-input-container"));
    }
    
    @Test
    void testCreateViewWithZeroValues() {
        VBox root = view.createView(1, 0, 0, 0, () -> {}, () -> {});
        
        assertNotNull(root);
    }
    
    @Test
    void testCreateViewWithHighValues() {
        VBox root = view.createView(1, 999999, 9999, 99, () -> {}, () -> {});
        
        assertNotNull(root);
    }
    
    @Test
    void testMultipleViewCreations() {
        VBox root1 = view.createView(1, 10000, 50, 5, () -> {}, () -> {});
        VBox root2 = view.createView(2, 5000, 25, 3, () -> {}, () -> {});
        
        assertNotNull(root1);
        assertNotNull(root2);
    }
}
