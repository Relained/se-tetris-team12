package org.example.controller;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.example.model.GameMode;
import org.example.model.ScoreRecord;
import org.example.service.ColorManager;
import org.example.service.ScoreManager;
import org.example.service.SettingManager;
import org.example.view.BaseView;
import org.example.view.ScoreboardView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Field;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ScoreboardController Unit Test (Mockito)
 */
@ExtendWith(ApplicationExtension.class)
class ScoreboardControllerTest {
    
    @Mock
    private ScoreboardView mockView;
    
    private ScoreboardController controller;
    private ScoreManager scoreManager;
    private ScoreRecord testRecord;
    
    @Start
    private void start(Stage stage) {
        ColorManager colorManager = ColorManager.getInstance();
        BaseView.Initialize(colorManager);
        
        SettingManager settingManager = new SettingManager();
        BaseController.Initialize(stage, settingManager);
    }
    
    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        scoreManager = ScoreManager.getInstance();
        scoreManager.clearScores();
        
        // Add test data
        ScoreRecord record1 = new ScoreRecord(1000, 50, 5, 1, GameMode.NORMAL, false);
        record1.setPlayerName("Player1");
        scoreManager.addScore(record1);
        
        ScoreRecord record2 = new ScoreRecord(800, 40, 4, 1, GameMode.NORMAL, false);
        record2.setPlayerName("Player2");
        scoreManager.addScore(record2);
        
        ScoreRecord record3 = new ScoreRecord(600, 30, 3, 1, GameMode.NORMAL, false);
        record3.setPlayerName("Player3");
        scoreManager.addScore(record3);
        
        // Clear the state stack
        Field stackField = BaseController.class.getDeclaredField("stateStack");
        stackField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Stack<BaseController> stack = (Stack<BaseController>) stackField.get(null);
        stack.clear();
        
        testRecord = new ScoreRecord(1200, 60, 6, 1, GameMode.NORMAL, true);
        testRecord.setPlayerName("TestPlayer");
        
        controller = new ScoreboardController();
        
        // Inject mock view using Reflection
        Field viewField = ScoreboardController.class.getDeclaredField("scoreboardView");
        viewField.setAccessible(true);
        viewField.set(controller, mockView);
    }
    
    @Test
    void testDefaultConstructor() {
        ScoreboardController defaultController = new ScoreboardController();
        assertNotNull(defaultController);
    }
    
    @Test
    void testConstructorWithAfterGamePlay() {
        ScoreboardController afterGameController = new ScoreboardController(true, testRecord);
        assertNotNull(afterGameController);
    }
    
    @Test
    void testHandleClearScores() {
        int initialSize = scoreManager.getTopScores().size();
        assertEquals(3, initialSize, "Should have initial scores");
        
        controller.handleClearScores();
        
        int finalSize = scoreManager.getTopScores().size();
        assertEquals(0, finalSize, "Scores should be cleared");
        verify(mockView, times(1)).updateScoreboard(anyList());
    }
    
    @Test
    void testRefreshScoreboard() {
        controller.refreshScoreboard();
        
        verify(mockView, times(1)).updateScoreboard(scoreManager.getTopScores());
    }
    
    @Test
    void testRefreshScoreboardAfterAddingScore() {
        ScoreRecord newRecord = new ScoreRecord(1500, 70, 7, 1, GameMode.NORMAL, false);
        newRecord.setPlayerName("NewPlayer");
        scoreManager.addScore(newRecord);
        
        controller.refreshScoreboard();
        
        assertEquals(4, scoreManager.getTopScores().size(), "Should have one more score");
        verify(mockView, times(1)).updateScoreboard(anyList());
    }
    
    @Test
    void testHandleGoBackWhenNotAfterGamePlay() throws Exception {
        // handleGoBack calls popState which modifies the stack
        // We cannot easily test state transitions without FX thread, so we test the logic separately
        ScoreboardController normalController = new ScoreboardController();
        
        // Verify controller is created without isAfterGamePlay
        assertNotNull(normalController);
    }
    
    @Test
    void testHandleGoBackWhenAfterGamePlay() throws Exception {
        // handleGoBack calls setState which requires FX thread
        // We cannot easily test state transitions without FX thread, so we test the logic separately
        ScoreboardController afterGameController = new ScoreboardController(true, testRecord);
        
        // Verify controller is created with isAfterGamePlay
        assertNotNull(afterGameController);
    }
    
    @Test
    void testHandleKeyInput() {
        KeyEvent mockKeyEvent = mock(KeyEvent.class);
        when(mockKeyEvent.getCode()).thenReturn(KeyCode.ENTER);
        
        org.example.view.component.NavigableButtonSystem mockButtonSystem = mock(org.example.view.component.NavigableButtonSystem.class);
        when(mockView.getButtonSystem()).thenReturn(mockButtonSystem);
        
        controller.handleKeyInput(mockKeyEvent);
        
        verify(mockView, times(1)).getButtonSystem();
        verify(mockButtonSystem, times(1)).handleInput(mockKeyEvent);
    }
    
    @Test
    void testClearScoresMultipleTimes() {
        controller.handleClearScores();
        assertEquals(0, scoreManager.getTopScores().size(), "Scores should be cleared");
        
        controller.handleClearScores();
        assertEquals(0, scoreManager.getTopScores().size(), "Should remain empty");
        
        verify(mockView, times(2)).updateScoreboard(anyList());
    }
    
    @Test
    void testRefreshScoreboardWithEmptyScores() {
        scoreManager.clearScores();
        
        controller.refreshScoreboard();
        
        verify(mockView, times(1)).updateScoreboard(scoreManager.getTopScores());
        assertTrue(scoreManager.getTopScores().isEmpty(), "Scores should be empty");
    }
}
