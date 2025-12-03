package org.example.controller;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
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
import org.example.service.ColorManager;
import org.example.service.SettingManager;
import org.example.view.BaseView;

import java.lang.reflect.Field;
import java.util.Stack;

@ExtendWith(ApplicationExtension.class)
class PlayControllerIntegrationTest {

    private PlayController controller;
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
        
        controller = new PlayController(GameMode.NORMAL, 2);
    }

    @Test
    void testConstructorNormalMode() {
        assertNotNull(controller);
        assertEquals(GameMode.NORMAL, controller.getGameMode());
    }

    @Test
    void testConstructorItemMode() {
        PlayController itemController = new PlayController(GameMode.ITEM, 1);
        
        assertNotNull(itemController);
        assertEquals(GameMode.ITEM, itemController.getGameMode());
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
    void testGameLogicInitialized() {
        assertNotNull(controller.getGameLogic());
    }

    @Test
    void testDifficultySettings() {
        PlayController easy = new PlayController(GameMode.NORMAL, 1);
        PlayController normal = new PlayController(GameMode.NORMAL, 2);
        PlayController hard = new PlayController(GameMode.NORMAL, 3);
        
        assertEquals(1, easy.getGameLogic().getDifficulty());
        assertEquals(2, normal.getGameLogic().getDifficulty());
        assertEquals(3, hard.getGameLogic().getDifficulty());
    }

    @Test
    void testUpdate() throws Exception {
        javafx.application.Platform.runLater(() -> {
            controller.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        javafx.application.Platform.runLater(() -> {
            controller.update(0.016); // ~60 FPS
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(controller);
    }

    @Test
    void testHandleKeyPressed() throws Exception {
        javafx.application.Platform.runLater(() -> {
            controller.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        javafx.application.Platform.runLater(() -> {
            controller.handleKeyPressed(KeyCode.LEFT);
            controller.handleKeyPressed(KeyCode.RIGHT);
            controller.handleKeyPressed(KeyCode.DOWN);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(controller);
    }

    @Test
    void testHandleKeyReleased() throws Exception {
        javafx.application.Platform.runLater(() -> {
            controller.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        javafx.application.Platform.runLater(() -> {
            controller.handleKeyPressed(KeyCode.LEFT);
            controller.handleKeyReleased(KeyCode.LEFT);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(controller);
    }

    @Test
    void testResetLastDropTime() throws Exception {
        javafx.application.Platform.runLater(() -> {
            controller.createScene();
            controller.resetLastDropTime();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(controller);
    }

    @Test
    void testUpdateMultipleTimes() throws Exception {
        javafx.application.Platform.runLater(() -> {
            controller.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        for (int i = 0; i < 5; i++) {
            javafx.application.Platform.runLater(() -> {
                controller.update(0.016);
            });
            WaitForAsyncUtils.waitForFxEvents();
        }
        
        assertNotNull(controller);
    }

    @Test
    void testExitAndResume() throws Exception {
        javafx.application.Platform.runLater(() -> {
            controller.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        javafx.application.Platform.runLater(() -> {
            controller.exit();
            controller.resume();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(controller);
    }

    @Test
    void testDifferentDifficulties() throws Exception {
        PlayController easy = new PlayController(GameMode.NORMAL, 1);
        final Scene[] easySceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            easySceneHolder[0] = easy.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertNotNull(easySceneHolder[0]);
        
        PlayController hard = new PlayController(GameMode.NORMAL, 3);
        final Scene[] hardSceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            hardSceneHolder[0] = hard.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();
        assertNotNull(hardSceneHolder[0]);
    }

    @Test
    void testItemModePlayController() throws Exception {
        PlayController itemController = new PlayController(GameMode.ITEM, 2);
        final Scene[] sceneHolder = {null};
        
        javafx.application.Platform.runLater(() -> {
            sceneHolder[0] = itemController.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertNotNull(sceneHolder[0]);
        assertNotNull(itemController.getGameLogic());
        assertEquals(GameMode.ITEM, itemController.getGameMode());
    }

    @Test
    void testGameStateInitially() throws Exception {
        javafx.application.Platform.runLater(() -> {
            controller.createScene();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertFalse(controller.getGameLogic().isGameOver());
        assertEquals(0, controller.getGameLogic().getScore());
        assertEquals(0, controller.getGameLogic().getLines());
        assertTrue(controller.getGameLogic().getLevel() > 0);
        assertNotNull(controller.getGameLogic().getCurrentPiece());
        assertFalse(controller.getGameLogic().getNextQueue().isEmpty());
    }
}
