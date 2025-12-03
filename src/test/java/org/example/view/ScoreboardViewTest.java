package org.example.view;

import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.model.GameMode;
import org.example.model.ScoreRecord;
import org.example.service.ColorManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ScoreboardView 클래스의 Unit Test
 */
@ExtendWith(ApplicationExtension.class)
class ScoreboardViewTest {
    
    private ScoreboardView view;
    
    @Start
    private void start(Stage stage) {
        ColorManager colorManager = ColorManager.getInstance();
        BaseView.Initialize(colorManager);
    }
    
    @BeforeEach
    void setUp() {
        view = new ScoreboardView();
    }
    
    @Test
    void testCreateView() {
        BorderPane root = view.createView(
            () -> {},
            () -> {}
        );
        
        assertNotNull(root);
        assertNotNull(root.getTop());
        assertNotNull(root.getBottom());
    }
    
    @Test
    void testCreateViewWithNullCallbacks() {
        // ScoreboardView는 null callbacks를 허용하지 않으므로 NullPointerException 예상
        assertThrows(NullPointerException.class, () -> {
            view.createView(null, null);
        });
    }
    
    @Test
    void testViewExtendsBaseView() {
        assertTrue(view instanceof BaseView);
    }
    
    @Test
    void testConstructorWithGamePlayFlag() {
        ScoreboardView afterGameView = new ScoreboardView(true, false);
        assertNotNull(afterGameView);
    }
    
    @Test
    void testConstructorWithHighlightFlag() {
        ScoreboardView highlightView = new ScoreboardView(false, true);
        assertNotNull(highlightView);
    }
    
    @Test
    void testUpdateScoreboardWithEmptyList() {
        view.createView(() -> {}, () -> {});
        
        assertDoesNotThrow(() -> view.updateScoreboard(new ArrayList<>()));
    }
    
    @Test
    void testUpdateScoreboardWithScores() {
        view.createView(() -> {}, () -> {});
        
        List<ScoreRecord> scores = new ArrayList<>();
        scores.add(new ScoreRecord(10000, 50, 5, 1, GameMode.NORMAL, false));
        scores.add(new ScoreRecord(5000, 25, 3, 1, GameMode.ITEM, false));
        
        assertDoesNotThrow(() -> view.updateScoreboard(scores));
    }
    
    @Test
    void testGetButtonSystem() {
        view.createView(() -> {}, () -> {});
        
        assertNotNull(view.getButtonSystem());
    }
    
    @Test
    void testRootBackground() {
        BorderPane root = view.createView(() -> {}, () -> {});
        
        assertTrue(root.getStyleClass().contains("root-dark"));
    }
    
    @Test
    void testMultipleViewCreations() {
        BorderPane root1 = view.createView(() -> {}, () -> {});
        BorderPane root2 = view.createView(() -> {}, () -> {});
        
        assertNotNull(root1);
        assertNotNull(root2);
    }
}
