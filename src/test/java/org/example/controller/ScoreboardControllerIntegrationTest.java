package org.example.controller;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.model.ScoreRecord;
import org.example.model.GameMode;
import org.example.service.ScoreManager;
import org.example.service.SettingManager;
import org.example.service.ColorManager;
import org.example.view.BaseView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.lang.reflect.Field;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class ScoreboardControllerIntegrationTest {
    private Stage testStage;
    private ScoreboardController controller;
    private ScoreManager scoreManager;
    private ScoreRecord testRecord;

    @Start
    void start(Stage stage) {
        testStage = stage;
    }

    @BeforeEach
    void setUp() throws Exception {
        FxToolkit.registerPrimaryStage();
        
        // Initialize on FX thread
        javafx.application.Platform.runLater(() -> {
            BaseView.Initialize(ColorManager.getInstance());
            BaseController.Initialize(testStage, new SettingManager());
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Initialize ScoreManager
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
        Stack<BaseController> stack = (Stack<BaseController>) stackField.get(null);
        stack.clear();
        
        // Create test record
        testRecord = new ScoreRecord(1200, 60, 6, 1, GameMode.NORMAL, true);
        testRecord.setPlayerName("TestPlayer");
        
        // Create controller
        controller = new ScoreboardController();
    }

    @Test
    void testCreateSceneSuccessfully() throws Exception {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(sceneHolder[0]);
        assertTrue(sceneHolder[0].getWidth() > 0, "Scene width should be positive");
        assertTrue(sceneHolder[0].getHeight() > 0, "Scene height should be positive");
    }

    @Test
    void testCreateSceneWithAfterGamePlay() throws Exception {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            ScoreboardController afterGameController = new ScoreboardController(true, testRecord);
            Scene scene = afterGameController.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(sceneHolder[0]);
    }

    @Test
    void testHandleClearScores() throws Exception {
        final int[] initialSize = {0};
        final int[] finalSize = {0};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            
            initialSize[0] = scoreManager.getTopScores().size();
            
            controller.handleClearScores();
            
            finalSize[0] = scoreManager.getTopScores().size();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(3, initialSize[0], "Should have initial scores");
        assertEquals(0, finalSize[0], "Scores should be cleared");
    }

    @Test
    void testRefreshScoreboard() throws Exception {
        final int[] initialSize = {0};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            
            initialSize[0] = scoreManager.getTopScores().size();
            
            ScoreRecord newRecord = new ScoreRecord(1500, 70, 7, 1, GameMode.NORMAL, false);
            newRecord.setPlayerName("NewPlayer");
            scoreManager.addScore(newRecord);
            
            controller.refreshScoreboard();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(3, initialSize[0], "Should have initial scores before refresh");
    }

    @Test
    void testScoreboardDisplaysTopScores() throws Exception {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            // Add many scores
            for (int i = 0; i < 15; i++) {
                ScoreRecord record = new ScoreRecord(100 * i, 10, 1, 1, GameMode.NORMAL, false);
                record.setPlayerName("Player" + i);
                scoreManager.addScore(record);
            }
            
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(sceneHolder[0], "Scene should be created even with many scores");
    }

    @Test
    void testScoreboardHighlightsNewScore() throws Exception {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            ScoreRecord newRecord = new ScoreRecord(1200, 60, 6, 1, GameMode.NORMAL, true);
            newRecord.setPlayerName("NewPlayer");
            scoreManager.addScore(newRecord);
            
            ScoreboardController highlightController = new ScoreboardController(true, newRecord);
            Scene scene = highlightController.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(sceneHolder[0], "Scene should be created with highlighted score");
    }

    @Test
    void testScoreboardWithNoScores() throws Exception {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            scoreManager.clearScores();
            
            ScoreboardController emptyController = new ScoreboardController();
            Scene scene = emptyController.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(sceneHolder[0], "Scene should be created even with no scores");
    }

    @Test
    void testScoreboardWithMaxScores() throws Exception {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            scoreManager.clearScores();
            
            // Add exactly 10 scores (max display)
            for (int i = 0; i < 10; i++) {
                ScoreRecord record = new ScoreRecord(1000 - i * 100, 50 - i * 5, 5, 1, GameMode.NORMAL, false);
                record.setPlayerName("Player" + i);
                scoreManager.addScore(record);
            }
            
            ScoreboardController maxController = new ScoreboardController();
            Scene scene = maxController.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(sceneHolder[0], "Scene should be created with max scores");
    }

    @Test
    void testScoreboardAfterGamePlayWithSubmittedScore() throws Exception {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            ScoreRecord submittedRecord = new ScoreRecord(1100, 55, 5, 1, GameMode.NORMAL, true);
            submittedRecord.setPlayerName("Submitted");
            
            ScoreboardController afterGameController = new ScoreboardController(true, submittedRecord);
            Scene scene = afterGameController.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(sceneHolder[0], "Scene should be created after game with submitted score");
    }

    @Test
    void testScoreboardAfterGamePlayWithSkippedScore() throws Exception {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            ScoreRecord skippedRecord = new ScoreRecord(900, 45, 4, 1, GameMode.NORMAL, false);
            
            ScoreboardController afterGameController = new ScoreboardController(true, skippedRecord);
            Scene scene = afterGameController.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(sceneHolder[0], "Scene should be created after game with skipped score");
    }
}
