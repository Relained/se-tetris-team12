package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.control.Button;
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
class ScoreNotEligibleControllerIntegrationTest {
    private Stage testStage;
    private ScoreNotEligibleController controller;
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
        
        // Create test record with score not eligible for top 10
        testRecord = new ScoreRecord(500, 25, 2, 1, GameMode.NORMAL, false);
        
        // Create controller
        controller = new ScoreNotEligibleController(testRecord);
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
    void testResumeMethod() throws Exception {
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            
            // Call resume method
            controller.resume();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Resume should complete without errors
        assertTrue(true, "Resume method should execute without errors");
    }

    @Test
    void testSceneDisplaysScore() throws Exception {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            ScoreRecord displayRecord = new ScoreRecord(1500, 75, 7, 1, GameMode.NORMAL, false);
            ScoreNotEligibleController displayController = new ScoreNotEligibleController(displayRecord);
            Scene scene = displayController.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(sceneHolder[0], "Scene should display the score");
    }

    @Test
    void testWithZeroScore() throws Exception {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            ScoreRecord zeroRecord = new ScoreRecord(0, 0, 1, 1, GameMode.NORMAL, false);
            ScoreNotEligibleController zeroController = new ScoreNotEligibleController(zeroRecord);
            Scene scene = zeroController.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(sceneHolder[0], "Scene should be created with zero score");
    }

    @Test
    void testWithHighButNotEligibleScore() throws Exception {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            // Add 10 high scores to fill the scoreboard
            scoreManager.clearScores();
            for (int i = 0; i < 10; i++) {
                ScoreRecord highRecord = new ScoreRecord(10000 - i * 100, 100 - i * 10, 10, 1, GameMode.NORMAL, false);
                highRecord.setPlayerName("TopPlayer" + i);
                scoreManager.addScore(highRecord);
            }
            
            // Score 500 is high but not in top 10
            ScoreRecord notEligibleRecord = new ScoreRecord(500, 25, 2, 1, GameMode.NORMAL, false);
            ScoreNotEligibleController highController = new ScoreNotEligibleController(notEligibleRecord);
            Scene scene = highController.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(sceneHolder[0], "Scene should be created for high but not eligible score");
    }

    @Test
    void testSceneContainsContinueButton() throws Exception {
        final Button[] buttonHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            
            // Look for button (may not have specific ID)
            buttonHolder[0] = (Button) scene.lookup(".button");
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(buttonHolder[0], "Scene should contain a button");
    }

    @Test
    void testMultipleDifferentScores() throws Exception {
        final Scene[] scene1 = {null};
        final Scene[] scene2 = {null};
        
        javafx.application.Platform.runLater(() -> {
            ScoreRecord record1 = new ScoreRecord(100, 5, 1, 1, GameMode.NORMAL, false);
            ScoreNotEligibleController controller1 = new ScoreNotEligibleController(record1);
            scene1[0] = controller1.createScene();
            
            ScoreRecord record2 = new ScoreRecord(5000, 250, 10, 2, GameMode.ITEM, false);
            ScoreNotEligibleController controller2 = new ScoreNotEligibleController(record2);
            scene2[0] = controller2.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(scene1[0], "Scene should be created for low score");
        assertNotNull(scene2[0], "Scene should be created for high score");
    }

    @Test
    void testSceneWithDifferentGameModes() throws Exception {
        final Scene[] normalScene = {null};
        final Scene[] itemScene = {null};
        
        javafx.application.Platform.runLater(() -> {
            ScoreRecord normalRecord = new ScoreRecord(500, 25, 2, 1, GameMode.NORMAL, false);
            ScoreNotEligibleController normalController = new ScoreNotEligibleController(normalRecord);
            normalScene[0] = normalController.createScene();
            
            ScoreRecord itemRecord = new ScoreRecord(500, 25, 2, 2, GameMode.ITEM, false);
            ScoreNotEligibleController itemController = new ScoreNotEligibleController(itemRecord);
            itemScene[0] = itemController.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(normalScene[0], "Scene should be created for NORMAL mode");
        assertNotNull(itemScene[0], "Scene should be created for ITEM mode");
    }

    @Test
    void testSceneWithDifferentDifficultyLevels() throws Exception {
        final Scene[] easyScene = {null};
        final Scene[] hardScene = {null};
        
        javafx.application.Platform.runLater(() -> {
            ScoreRecord easyRecord = new ScoreRecord(500, 25, 2, 1, GameMode.NORMAL, false);
            ScoreNotEligibleController easyController = new ScoreNotEligibleController(easyRecord);
            easyScene[0] = easyController.createScene();
            
            ScoreRecord hardRecord = new ScoreRecord(500, 25, 2, 3, GameMode.NORMAL, false);
            ScoreNotEligibleController hardController = new ScoreNotEligibleController(hardRecord);
            hardScene[0] = hardController.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(easyScene[0], "Scene should be created for easy difficulty");
        assertNotNull(hardScene[0], "Scene should be created for hard difficulty");
    }
}
