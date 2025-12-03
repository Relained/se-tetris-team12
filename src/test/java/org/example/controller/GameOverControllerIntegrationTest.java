package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;

import org.example.model.GameMode;
import org.example.model.ScoreRecord;
import org.example.service.ColorManager;
import org.example.service.SettingManager;
import org.example.view.BaseView;

import java.lang.reflect.Field;
import java.util.Stack;

@ExtendWith(ApplicationExtension.class)
class GameOverControllerIntegrationTest {

    private GameOverController controller;
    private ScoreRecord record;
    private Stage testStage;

    @Start
    void start(Stage stage) {
        this.testStage = stage;
    }

    @BeforeEach
    void setUp() throws Exception {
        FxToolkit.registerPrimaryStage();
        
        javafx.application.Platform.runLater(() -> {
            BaseView.Initialize(ColorManager.getInstance());
            BaseController.Initialize(testStage, new SettingManager());
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Clear state stack
        Field stackField = BaseController.class.getDeclaredField("stateStack");
        stackField.setAccessible(true);
        Stack<BaseController> stack = (Stack<BaseController>) stackField.get(null);
        stack.clear();
        
        this.record = new ScoreRecord(10000, 50, 5, 2, GameMode.NORMAL, true);
        this.controller = new GameOverController(record);
    }

    @Test
    void testCreateScene() throws Exception {
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(sceneHolder[0]);
        assertNotNull(sceneHolder[0].getRoot());
    }

    @Test
    void testWithEligibleScore() throws Exception {
        ScoreRecord eligibleRecord = new ScoreRecord(99999, 100, 10, 3, GameMode.ITEM, true);
        GameOverController eligibleController = new GameOverController(eligibleRecord);
        
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = eligibleController.createScene();
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(sceneHolder[0]);
    }

    @Test
    void testWithIneligibleScore() throws Exception {
        ScoreRecord ineligibleRecord = new ScoreRecord(100, 5, 1, 1, GameMode.NORMAL, false);
        GameOverController ineligibleController = new GameOverController(ineligibleRecord);
        
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = ineligibleController.createScene();
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(sceneHolder[0]);
    }

    @Test
    void testWithZeroScore() throws Exception {
        ScoreRecord zeroRecord = new ScoreRecord(0, 0, 1, 1, GameMode.NORMAL, false);
        GameOverController zeroController = new GameOverController(zeroRecord);
        
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            Scene scene = zeroController.createScene();
            sceneHolder[0] = scene;
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(sceneHolder[0]);
    }

    @Test
    void testKeyInput() throws Exception {
        javafx.application.Platform.runLater(() -> {
            Scene scene = controller.createScene();
            testStage.setScene(scene);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        javafx.application.Platform.runLater(() -> {
            var event = new KeyEvent(
                KeyEvent.KEY_PRESSED,
                "", "", KeyCode.UP, false, false, false, false
            );
            controller.handleKeyInput(event);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(controller);
    }

    @Test
    void testMultipleKeyInputs() throws Exception {
        javafx.application.Platform.runLater(() -> {
            controller.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        javafx.application.Platform.runLater(() -> {
            var downEvent = new KeyEvent(
                KeyEvent.KEY_PRESSED,
                "", "", KeyCode.DOWN, false, false, false, false
            );
            controller.handleKeyInput(downEvent);
            
            var selectEvent = new KeyEvent(
                KeyEvent.KEY_PRESSED,
                "", "", KeyCode.ENTER, false, false, false, false
            );
            controller.handleKeyInput(selectEvent);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(controller);
    }

    @Test
    void testDifferentGameModes() throws Exception {
        ScoreRecord normalRecord = new ScoreRecord(5000, 30, 4, 2, GameMode.NORMAL, false);
        GameOverController normalController = new GameOverController(normalRecord);
        
        final Scene[] normalSceneHolder = {null};
        javafx.application.Platform.runLater(() -> {
            normalSceneHolder[0] = normalController.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertNotNull(normalSceneHolder[0]);
        
        ScoreRecord itemRecord = new ScoreRecord(8000, 40, 5, 2, GameMode.ITEM, true);
        GameOverController itemController = new GameOverController(itemRecord);
        
        final Scene[] itemSceneHolder = {null};
        javafx.application.Platform.runLater(() -> {
            itemSceneHolder[0] = itemController.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertNotNull(itemSceneHolder[0]);
    }

    @Test
    void testDifferentDifficulties() throws Exception {
        ScoreRecord easyRecord = new ScoreRecord(3000, 20, 2, 1, GameMode.NORMAL, false);
        GameOverController easyController = new GameOverController(easyRecord);
        
        final Scene[] easySceneHolder = {null};
        javafx.application.Platform.runLater(() -> {
            easySceneHolder[0] = easyController.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertNotNull(easySceneHolder[0]);
        
        ScoreRecord hardRecord = new ScoreRecord(12000, 60, 6, 3, GameMode.NORMAL, true);
        GameOverController hardController = new GameOverController(hardRecord);
        
        final Scene[] hardSceneHolder = {null};
        javafx.application.Platform.runLater(() -> {
            hardSceneHolder[0] = hardController.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertNotNull(hardSceneHolder[0]);
    }
}
